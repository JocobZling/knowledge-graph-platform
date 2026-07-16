#!/usr/bin/env python3
"""Build a deduplicated topic index from AI Testing daily reports."""

from __future__ import annotations

import json
import re
import unicodedata
from datetime import date, timedelta
from difflib import SequenceMatcher
from pathlib import Path
from urllib.parse import parse_qsl, urlencode, urlsplit, urlunsplit

ROOT = Path(__file__).resolve().parents[1]
SOURCE_GLOB = "daily/ai-testing/**/*.md"
OUTPUT = ROOT / "data" / "ai-testing" / "topic-index.json"
DATE_RE = re.compile(r"\b(20\d{2}-\d{2}-\d{2})\b")
NEWS_RE = re.compile(r"^###\s+(?:\d+[.、]\s*)?(.+?)\s*$", re.M)
FIELD_RE = re.compile(r"^\s*[-*]\s*([^：:]+)[：:]\s*(.+?)\s*$")
LINK_RE = re.compile(r"\[([^]]+)\]\((https?://[^)\s]+)\)")

ENTITIES = (
    ("playwright", "Playwright Test Agents"),
    ("github copilot browser", "GitHub Copilot Browser Tools"),
    ("copilot browser", "GitHub Copilot Browser Tools"),
    ("github copilot", "GitHub Copilot"), ("codex", "Codex"),
    ("claude code", "Claude Code"), ("vera-bench", "Vera-Bench"),
    ("vera", "Vera"), ("model context protocol", "MCP"), ("mcp", "MCP"),
    ("logichunter", "LogicHunter"), ("logic hunter", "LogicHunter"),
    ("testagent", "TestAgent"), ("gimitest", "Gimitest"), ("cread", "CREAD"),
    ("gpt-", "GPT"), ("openai", "OpenAI"), ("anthropic", "Anthropic"),
    ("deepeval", "DeepEval"), ("garak", "garak"), ("agentlab", "AgentLab"),
    ("hallu", "HalluSquatting"), ("itu", "ITU"), ("google", "Google"),
    ("微软", "Microsoft"),
)
EVENTS = (
    (("漏洞", "攻击", "风险", "安全", "security", "exploit"), "security-event"),
    (("发布", "上线", "正式可用", "launch", "release"), "release"),
    (("修复", "回滚", "恢复", "fix", "regression"), "update"),
    (("论文", "研究", "arxiv", "study", "research"), "research"),
    (("案例", "采用", "使用", "adoption"), "case-study"),
    (("框架", "倡议", "规范", "治理", "compliance"), "governance"),
    (("工具", "tool"), "tool"),
)
FEATURES = (
    (("planner", "generator", "healer"), "planner-generator-healer"),
    (("browser", "tools"), "browser-tools"), (("data injection",), "data-injection"),
    (("prompt injection", "提示注入"), "prompt-injection"),
    (("供应链", "supply chain"), "supply-chain"),
    (("tool calling", "工具调用"), "tool-calling"),
    (("evidence", "证据"), "evidence-verification"),
    (("continuous compliance", "持续合规"), "continuous-compliance"),
    (("unit test", "单元测试"), "unit-test-generation"),
    (("autonomous driving", "自动驾驶"), "autonomous-driving"),
    (("reinforcement learning", "强化学习"), "rl-policy-testing"),
    (("agent framework", "agent 框架", "agent框架"), "agent-framework"),
    (("quality flywheel", "质量飞轮"), "quality-flywheel"),
    (("red team", "红队"), "automated-red-teaming"),
    (("benchmark", "基准"), "benchmark"), (("evaluation", "评估", "评测"), "evaluation"),
    (("governance", "治理"), "governance"), (("security", "安全"), "security"),
)
STOP = {"the", "and", "for", "from", "with", "into", "are", "is", "to", "of",
        "a", "an", "ai", "agent", "agents", "testing", "test", "tests"}


def section(text: str, name: str) -> str:
    start = re.search(rf"^##\s+{re.escape(name)}\s*$", text, re.M)
    if not start:
        return ""
    following = re.search(r"^##\s+.+$", text[start.end():], re.M)
    end = start.end() + following.start() if following else len(text)
    return text[start.end():end].strip()


def clean(value: str) -> str:
    return re.sub(r"\s+", " ", value).strip()


def get_report_date(path: Path, text: str) -> str:
    front = re.search(r"^---\s*$([\s\S]*?)^---\s*$", text, re.M)
    if front:
        found = re.search(r'^date:\s*["\']?(20\d{2}-\d{2}-\d{2})', front.group(1), re.M)
        if found:
            return found.group(1)
    found = DATE_RE.search(path.name)
    if not found:
        raise ValueError(f"Cannot determine report date: {path}")
    return found.group(1)


def canonical_url(raw: str) -> str:
    parts = urlsplit(raw.strip().rstrip(".,;，。；"))
    host, path = parts.netloc.lower(), re.sub(r"/{2,}", "/", parts.path)
    if host in {"www.arxiv.org", "export.arxiv.org"}:
        host = "arxiv.org"
    if host == "arxiv.org":
        paper = re.match(r"/(?:abs|html|pdf)/(\d{4}\.\d{4,5})(?:v\d+)?(?:\.pdf)?/?$", path)
        if paper:
            return f"https://arxiv.org/abs/{paper.group(1)}"
    ignored = {"utm_source", "utm_medium", "utm_campaign", "utm_term", "utm_content", "ref"}
    query = urlencode([(k, v) for k, v in parse_qsl(parts.query) if k.lower() not in ignored])
    return urlunsplit((parts.scheme.lower() or "https", host, path.rstrip("/") or "/", query, ""))


def parse_news(body: str) -> list[dict[str, str]]:
    headings, result = list(NEWS_RE.finditer(body)), []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(body)
        fields = {}
        for line in body[heading.end():end].splitlines():
            match = FIELD_RE.match(line)
            if match:
                fields[clean(match.group(1))] = clean(match.group(2))
        result.append({"title": clean(heading.group(1)), "source": fields.get("来源", ""),
                       "published": fields.get("时间", ""), "keywords": fields.get("关键词", ""),
                       "core_fact": fields.get("摘要", "")})
    return result


def parse_links(body: str) -> list[dict[str, str]]:
    result, seen = [], set()
    for label, url in LINK_RE.findall(body):
        url = canonical_url(url)
        if url not in seen:
            result.append({"label": clean(label), "url": url})
            seen.add(url)
    return result


def tokens(value: str) -> set[str]:
    return {word for word in re.findall(r"[a-z0-9]+", value.lower()) if len(word) > 1 and word not in STOP}


def entity(text: str) -> str:
    lowered = text.lower()
    for needle, value in ENTITIES:
        if needle in lowered:
            return value
    names = [item for item in re.findall(r"\b[A-Z][A-Za-z0-9.-]*(?:\s+[A-Z][A-Za-z0-9.-]*){0,2}\b", text)
             if item.lower() not in STOP]
    if names:
        return names[0]
    return clean(re.split(r"发布|推出|提供|使用|提出|强调|发现|修复|启动", text, maxsplit=1)[0])[:40] or "unknown"


def attach_urls(news: list[dict[str, str]], links: list[dict[str, str]]) -> None:
    for index, item in enumerate(news):
        if not links:
            item["source_url"] = ""
            continue
        text = " ".join((item["title"], item["core_fact"], item["keywords"], item["source"]))
        subject = entity(item["title"]).lower()
        def score(link: dict[str, str]) -> float:
            link_text = f'{link["label"]} {link["url"]}'.lower()
            overlap = len(tokens(text) & tokens(link_text)) / max(1, min(len(tokens(text)), len(tokens(link_text))))
            return overlap * .7 + SequenceMatcher(None, item["title"].lower(), link["label"].lower()).ratio() * .3 + (.45 if subject in link_text else 0)
        best = max(links, key=score)
        if score(best) < .28 and index < len(links):
            best = links[index]
        item["source_url"] = best["url"]


def slug(value: str) -> str:
    value = unicodedata.normalize("NFKC", value).lower().replace("&", " and ")
    return re.sub(r"[^a-z0-9\u3400-\u9fff]+", "-", value).strip("-") or "unknown"


def identity(item: dict[str, str]) -> tuple[str, str, str]:
    text = " ".join((item["title"], item["core_fact"], item["keywords"], item["source_url"]))
    subject, lowered = entity(item["title"]), text.lower()
    event = next((value for needles, value in EVENTS if any(word in lowered for word in needles)), "news")
    feature = next((value for needles, value in FEATURES
                    if all(word in lowered for word in needles) or (len(needles) > 1 and any(word in lowered for word in needles))), None)
    if not feature:
        useful = [word for word in re.findall(r"[a-z0-9]+", lowered)
                  if len(word) > 2 and word not in STOP and word not in tokens(subject)]
        feature = "-".join(dict.fromkeys(useful))[:60] or (re.findall(r"[\u3400-\u9fff]{2,8}", text) or ["general"])[0]
    return subject, event, f"{slug(subject)}-{slug(event)}-{slug(feature)}"


def build(paths: list[Path]) -> dict[str, object]:
    occurrences, dates = [], []
    for path in paths:
        text = path.read_text(encoding="utf-8-sig")
        reported = get_report_date(path, text)
        dates.append(reported)
        summary = clean(section(text, "今日摘要"))
        news = parse_news(section(text, "重点新闻"))
        attach_urls(news, parse_links(section(text, "相关链接")))
        for item in news:
            subject, event, topic_id = identity(item)
            published = DATE_RE.search(item["published"])
            occurrences.append({"topic_id": topic_id, "entity": subject, "event_type": event,
                                "title": item["title"], "published_at": published.group(1) if published else reported,
                                "reported_at": reported, "source_url": item["source_url"],
                                "core_fact": item["core_fact"] or summary})
    occurrences.sort(key=lambda item: (item["reported_at"], item["title"]))
    topics, by_id, by_url = [], {}, {}
    for item in occurrences:
        record = by_url.get(item["source_url"]) if item["source_url"] else None
        record = record or by_id.get(item["topic_id"])
        if record is None:
            record = {key: item[key] for key in ("topic_id", "entity", "event_type", "title", "published_at", "source_url", "core_fact")}
            record.update(first_reported=item["reported_at"], last_reported=item["reported_at"], appeared_count=0, appeared_dates=[])
            topics.append(record)
        record["first_reported"] = min(record["first_reported"], item["reported_at"])
        record["last_reported"] = max(record["last_reported"], item["reported_at"])
        record["appeared_count"] += 1
        if item["reported_at"] not in record["appeared_dates"]:
            record["appeared_dates"].append(item["reported_at"])
        by_id[item["topic_id"]] = record
        if item["source_url"]:
            by_url[item["source_url"]] = record
    latest = max(dates) if dates else None
    recent_start = (date.fromisoformat(latest) - timedelta(days=6)).isoformat() if latest else None
    for topic in topics:
        topic["appeared_dates"].sort()
        topic["reported_in_last_7_days"] = bool(recent_start and topic["last_reported"] >= recent_start)
    topics.sort(key=lambda item: (item["last_reported"], item["topic_id"]), reverse=True)
    return {"schema_version": 1, "source_glob": SOURCE_GLOB, "latest_report_date": latest,
            "recent_7_days_start": recent_start, "report_count": len(paths),
            "topic_count": len(topics), "topics": topics}


def main() -> None:
    index = build(sorted(ROOT.glob(SOURCE_GLOB)))
    urls = [item["source_url"] for item in index["topics"] if item["source_url"]]
    ids = [item["topic_id"] for item in index["topics"]]
    if len(urls) != len(set(urls)) or len(ids) != len(set(ids)):
        raise ValueError("Topic index still contains duplicate identifiers")
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(json.dumps(index, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"Indexed {index['topic_count']} topics from {index['report_count']} reports -> {OUTPUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
