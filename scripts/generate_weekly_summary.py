import os
from datetime import datetime
from pathlib import Path

try:
    from zoneinfo import ZoneInfo
except ImportError:  # pragma: no cover
    ZoneInfo = None


def today_shanghai() -> datetime:
    if ZoneInfo:
        return datetime.now(ZoneInfo("Asia/Shanghai"))
    return datetime.now()


def read_source_content() -> str:
    # 1. 手动触发 workflow_dispatch 时可直接传入 source_content
    content = os.getenv("SOURCE_CONTENT", "").strip()
    if content:
        return content

    # 2. 自动执行时优先读取仓库内暂存的本周对话要点
    source_file = Path("data/weekly-summary-source.md")
    if source_file.exists():
        return source_file.read_text(encoding="utf-8").strip()

    # 3. 没有来源时生成占位说明，避免任务静默失败
    return """
本周尚未提供对话原始内容。

说明：GitHub Actions 无法直接读取 ChatGPT 历史对话，需要通过以下任一方式提供来源：
1. 手动运行 workflow_dispatch，并粘贴本周对话要点；
2. 每周前更新 data/weekly-summary-source.md；
3. 后续接入你自己的对话导出/同步脚本。
""".strip()


SYSTEM_PROMPT = """
你是一个严谨的个人知识管理与工作复盘助手。请根据用户提供的本周对话内容，生成结构化周总结。
要求：
1. 使用中文。
2. 输出完整 Markdown。
3. 不编造未提供的信息；来源不足时明确标注“待补充”。
4. 风格清晰、适合归档到 GitHub。
5. 内容结构必须包含：本周总览、按对话逐个总结、本周重要决策、长期推进项目更新、本周知识沉淀、可复用资产、下周行动清单、下周值得关注。
""".strip()


USER_TEMPLATE = """
请整理本周我与 ChatGPT 的全部对话，先生成本周总览（主要主题、时间投入方向、一句话成果），再按每个独立对话分别总结主题、核心内容、关键结论、最终确定的方案、重要产出（文档、Prompt、代码、脚本等）、待办事项（TODO）和建议下一步；随后汇总本周重要决策，更新长期推进项目（包括最新进展、完成度、下一步计划），最后生成按优先级排序的下周行动清单（必做事项、建议推进事项、可探索的新方向）。

本周对话内容如下：

{source_content}
""".strip()


def generate_with_openai(source_content: str) -> str | None:
    api_key = os.getenv("OPENAI_API_KEY", "").strip()
    if not api_key:
        return None

    from openai import OpenAI

    client = OpenAI(api_key=api_key)
    response = client.chat.completions.create(
        model=os.getenv("OPENAI_MODEL", "gpt-4.1-mini"),
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": USER_TEMPLATE.format(source_content=source_content)},
        ],
        temperature=0.2,
    )
    return response.choices[0].message.content.strip()


def fallback_summary(source_content: str) -> str:
    return f"""
## 本周总览

- **本周一句话成果**：待补充。本次自动任务已运行，但缺少可直接用于总结的完整对话来源。
- **主要主题**：待补充。
- **时间投入方向**：待补充。

## 按对话逐个总结

### 对话 01

- **主题**：待补充
- **核心内容**：待补充
- **关键结论**：待补充
- **最终确定的方案**：待补充
- **重要产出**：待补充
- **待办事项 TODO**：补充本周对话来源后重新运行工作流
- **建议下一步**：将本周对话要点写入 `data/weekly-summary-source.md`，或手动运行 workflow_dispatch 并粘贴内容。

## 本周重要决策

待补充。

## 长期推进项目更新

| 项目 | 本周进展 | 完成度 | 下一步计划 | 风险 |
|---|---|---:|---|---|
| 周总结自动化 | GitHub Actions 自动归档流程已触发 | 60% | 接入稳定的对话来源 | ChatGPT 历史对话无法被 GitHub Actions 直接读取 |

## 本周知识沉淀

- GitHub Actions 可以稳定负责定时触发、生成文件和提交仓库。
- ChatGPT 历史对话需要额外导出或同步，不能由 GitHub Actions 原生读取。

## 可复用资产

- `.github/workflows/weekly-summary.yml`
- `scripts/generate_weekly_summary.py`

## 下周行动清单

### P0（必须完成）

1. 确定本周对话来源的稳定输入方式。
2. 配置仓库 Secrets：`OPENAI_API_KEY`。

### P1（建议完成）

1. 建立 `data/weekly-summary-source.md` 作为临时输入文件。
2. 将每日对话要点或重要结论追加到该文件。

### P2（探索）

1. 后续接入自动导出 ChatGPT 对话内容的脚本或人工半自动流程。

## 下周值得关注

- AI Testing 日报与周总结是否可以共享同一套 GitHub Actions 归档能力。
- Codex 是否可以定期维护输入文件与归档结构。

---

## 本次输入来源快照

```text
{source_content[:4000]}
```
""".strip()


def add_front_matter(body: str, now: datetime) -> str:
    date_str = now.strftime("%Y-%m-%d")
    summary_line = "本周对话总结，包含主题总览、关键决策、项目进展和下周行动计划。"
    return f"""---
title: "Weekly Conversation Summary"
date: "{date_str}"
type: "weekly-summary"
category: "Weekly Review"
tags:
  - Weekly
  - Conversation
  - AI
  - Testing
  - Knowledge
source: "GitHub Actions + ChatGPT/Codex Pipeline"
status: "published"
summary: "{summary_line}"
---

# Weekly Conversation Summary - {date_str}

{body}
""".strip() + "\n"


def main() -> None:
    now = today_shanghai()
    date_str = now.strftime("%Y-%m-%d")
    year = now.strftime("%Y")
    month = now.strftime("%m")

    source_content = read_source_content()
    generated = generate_with_openai(source_content)
    body = generated if generated else fallback_summary(source_content)
    markdown = add_front_matter(body, now)

    output_dir = Path("daily") / "weekly-summary" / year / month
    output_dir.mkdir(parents=True, exist_ok=True)
    output_file = output_dir / f"{date_str}-weekly-summary.md"
    output_file.write_text(markdown, encoding="utf-8")
    print(f"Generated {output_file}")


if __name__ == "__main__":
    main()
