package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.DailyBrief;
import com.example.knowledgegraph.mapper.DailyBriefMapper;
import com.example.knowledgegraph.service.DailyBriefService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class DailyBriefServiceImpl extends ServiceImpl<DailyBriefMapper, DailyBrief> implements DailyBriefService {
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    @Override
    public int syncFromMarkdown() {
        int count = 0;
        for (Path root : markdownRoots()) {
            if (!Files.exists(root)) continue;
            try (Stream<Path> paths = Files.walk(root)) {
                List<Path> files = paths
                        .filter((path) -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".md"))
                        .toList();
                for (Path file : files) {
                    upsert(parseMarkdown(root, file));
                    count++;
                }
            } catch (IOException ex) {
                throw new IllegalStateException("日报同步失败");
            }
        }
        return count;
    }

    private List<Path> markdownRoots() {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        List<Path> roots = new ArrayList<>();
        roots.add(cwd.resolve("daily"));
        roots.add(cwd.resolve("../daily").normalize());
        roots.add(cwd.resolve("frontend/public/daily-reports"));
        roots.add(cwd.resolve("../frontend/public/daily-reports").normalize());
        return roots.stream().distinct().toList();
    }

    private DailyBrief parseMarkdown(Path root, Path file) throws IOException {
        String raw = Files.readString(file, StandardCharsets.UTF_8);
        ParsedMarkdown parsed = splitFrontMatter(raw);
        Map<String, List<String>> meta = parseFrontMatter(parsed.frontMatter());
        String fallbackType = inferType(root, file);
        LocalDate fallbackDate = inferDate(file);
        LocalDate nowDate = LocalDate.now();

        DailyBrief brief = new DailyBrief();
        brief.setTitle(first(meta, "title", file.getFileName().toString().replaceFirst("\\.md$", "")));
        brief.setBriefDate(parseDate(first(meta, "date", null), fallbackDate == null ? nowDate : fallbackDate));
        brief.setType(first(meta, "type", fallbackType));
        brief.setCategory(first(meta, "category", first(meta, "type", fallbackType)));
        brief.setTags(toJsonArray(values(meta, "tags")));
        brief.setSummary(first(meta, "summary", ""));
        brief.setSource(first(meta, "source", "ChatGPT"));
        brief.setStatus(first(meta, "status", "published"));
        brief.setFilePath(root.relativize(file).toString().replace("\\", "/"));
        brief.setContent(parsed.content());
        return brief;
    }

    private ParsedMarkdown splitFrontMatter(String raw) {
        String normalized = raw.replace("\r\n", "\n");
        if (!normalized.startsWith("---\n")) return new ParsedMarkdown("", normalized);
        int end = normalized.indexOf("\n---", 4);
        if (end < 0) return new ParsedMarkdown("", normalized);
        String frontMatter = normalized.substring(4, end).trim();
        String content = normalized.substring(end + 4).stripLeading();
        return new ParsedMarkdown(frontMatter, content);
    }

    private Map<String, List<String>> parseFrontMatter(String frontMatter) {
        Map<String, List<String>> meta = new HashMap<>();
        String currentKey = null;
        for (String line : frontMatter.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isBlank()) continue;
            if (trimmed.startsWith("- ") && currentKey != null) {
                meta.computeIfAbsent(currentKey, (key) -> new ArrayList<>()).add(unquote(trimmed.substring(2).trim()));
                continue;
            }
            int colon = trimmed.indexOf(':');
            if (colon < 0) continue;
            currentKey = trimmed.substring(0, colon).trim();
            String value = trimmed.substring(colon + 1).trim();
            if (!value.isEmpty()) {
                meta.computeIfAbsent(currentKey, (key) -> new ArrayList<>()).add(unquote(value));
            }
        }
        return meta;
    }

    private void upsert(DailyBrief incoming) {
        DailyBrief existing = getOne(new LambdaQueryWrapper<DailyBrief>()
                .eq(DailyBrief::getBriefDate, incoming.getBriefDate())
                .eq(DailyBrief::getType, incoming.getType())
                .last("limit 1"));
        LocalDateTime now = LocalDateTime.now();
        incoming.setUpdatedTime(now);
        if (existing == null) {
            incoming.setCreatedTime(now);
            save(incoming);
        } else {
            incoming.setId(existing.getId());
            incoming.setCreatedTime(existing.getCreatedTime());
            updateById(incoming);
        }
    }

    private String inferType(Path root, Path file) {
        Path relative = root.relativize(file);
        if (relative.getNameCount() > 1) return relative.getName(0).toString();
        return "daily";
    }

    private LocalDate inferDate(Path file) {
        Matcher matcher = DATE_PATTERN.matcher(file.getFileName().toString());
        if (!matcher.find()) return null;
        return LocalDate.parse(matcher.group(1));
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.isBlank()) return fallback;
        return LocalDate.parse(value);
    }

    private String first(Map<String, List<String>> meta, String key, String fallback) {
        List<String> values = meta.get(key);
        return values == null || values.isEmpty() ? fallback : values.get(0);
    }

    private List<String> values(Map<String, List<String>> meta, String key) {
        return meta.getOrDefault(key, List.of());
    }

    private String unquote(String value) {
        if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private String toJsonArray(List<String> values) {
        if (values == null || values.isEmpty()) return "[]";
        return "[" + values.stream().map(this::jsonString).reduce((a, b) -> a + "," + b).orElse("") + "]";
    }

    private String jsonString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private record ParsedMarkdown(String frontMatter, String content) {
    }
}
