package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.DailyBrief;
import com.example.knowledgegraph.entity.DailyBriefRelation;
import com.example.knowledgegraph.service.DailyBriefRelationService;
import com.example.knowledgegraph.service.DailyBriefService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/daily-brief")
public class DailyBriefController {
    private final DailyBriefService service;
    private final DailyBriefRelationService relationService;

    public DailyBriefController(DailyBriefService service, DailyBriefRelationService relationService) {
        this.service = service;
        this.relationService = relationService;
    }

    @GetMapping("/list")
    public ApiResponse<List<DailyBrief>> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) String category,
                                              @RequestParam(required = false) String tag,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(required = false) String status) {
        LambdaQueryWrapper<DailyBrief> wrapper = new LambdaQueryWrapper<DailyBrief>()
                .and(keyword != null && !keyword.isBlank(), (query) -> query
                        .like(DailyBrief::getTitle, keyword)
                        .or().like(DailyBrief::getSummary, keyword)
                        .or().like(DailyBrief::getContent, keyword)
                        .or().apply("tags::text like {0}", "%" + keyword + "%")
                        .or().like(DailyBrief::getCategory, keyword)
                        .or().like(DailyBrief::getType, keyword))
                .eq(type != null && !type.isBlank(), DailyBrief::getType, type)
                .eq(category != null && !category.isBlank(), DailyBrief::getCategory, category)
                .apply(tag != null && !tag.isBlank(), "tags::text like {0}", "%" + tag + "%")
                .ge(startDate != null && !startDate.isBlank(), DailyBrief::getBriefDate, parseDate(startDate))
                .le(endDate != null && !endDate.isBlank(), DailyBrief::getBriefDate, parseDate(endDate))
                .eq(status != null && !status.isBlank(), DailyBrief::getStatus, status)
                .ne(status == null || status.isBlank(), DailyBrief::getStatus, "archived")
                .orderByDesc(DailyBrief::getBriefDate)
                .orderByDesc(DailyBrief::getUpdatedTime);
        return ApiResponse.success(service.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<DailyBrief> detail(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }

    @PostMapping("/create")
    public ApiResponse<DailyBrief> create(@RequestBody DailyBrief item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedTime(now);
        item.setUpdatedTime(now);
        fillDefaults(item);
        service.save(item);
        return ApiResponse.success(item);
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody DailyBrief item) {
        item.setUpdatedTime(LocalDateTime.now());
        fillDefaults(item);
        return ApiResponse.success(service.updateById(item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        DailyBrief item = service.getById(id);
        if (item == null) return ApiResponse.success(false);
        item.setStatus("archived");
        item.setUpdatedTime(LocalDateTime.now());
        return ApiResponse.success(service.updateById(item));
    }

    @PostMapping("/sync")
    public ApiResponse<Integer> sync() {
        return ApiResponse.success(service.syncFromMarkdown());
    }

    @PostMapping("/relation/create")
    public ApiResponse<DailyBriefRelation> createRelation(@RequestBody DailyBriefRelation relation) {
        relation.setCreatedTime(LocalDateTime.now());
        relationService.save(relation);
        return ApiResponse.success(relation);
    }

    private void fillDefaults(DailyBrief item) {
        if (item.getBriefDate() == null) item.setBriefDate(LocalDate.now());
        if (item.getType() == null || item.getType().isBlank()) item.setType("daily");
        if (item.getCategory() == null || item.getCategory().isBlank()) item.setCategory(item.getType());
        if (item.getTags() == null || item.getTags().isBlank()) item.setTags("[]");
        if (item.getSource() == null || item.getSource().isBlank()) item.setSource("ChatGPT");
        if (item.getStatus() == null || item.getStatus().isBlank()) item.setStatus("published");
    }

    private LocalDate parseDate(String value) {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
