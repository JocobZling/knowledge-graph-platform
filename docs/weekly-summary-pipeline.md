# 每周对话总结自动化流水线

## 目标

将每周 ChatGPT 对话总结改造成更稳定的 GitHub Actions 流水线，避免只依赖 ChatGPT 自动化提醒导致漏发、漏执行。

## 当前方案

每周日 09:30（Asia/Shanghai）由 GitHub Actions 自动触发：

1. 拉取仓库代码。
2. 执行 `scripts/generate_weekly_summary.py`。
3. 生成 Markdown 周总结。
4. 写入固定目录。
5. 自动提交到 GitHub。

## 文件位置

```text
.github/workflows/weekly-summary.yml        # GitHub Actions 工作流
scripts/generate_weekly_summary.py          # 周总结生成脚本
daily/weekly-summary/YYYY/MM/               # 周总结归档目录
docs/weekly-summary-pipeline.md             # 本说明文档
```

## 生成路径规则

例如 2026-07-05 执行时，会生成：

```text
daily/weekly-summary/2026/07/2026-07-05-weekly-summary.md
```

## Markdown 格式

每篇周总结固定包含 YAML Front Matter：

```yaml
---
title: "Weekly Conversation Summary"
date: "2026-07-05"
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
summary: "本周对话总结，包含主题总览、关键决策、项目进展和下周行动计划。"
---
```

正文固定包含：

1. 本周总览
2. 按对话逐个总结
3. 本周重要决策
4. 长期推进项目更新
5. 本周知识沉淀
6. 可复用资产
7. 下周行动清单
8. 下周值得关注

## 重要限制

GitHub Actions 不能天然读取 ChatGPT 历史对话。

因此，自动生成高质量周总结需要一个“本周对话来源”。当前支持三种方式：

### 方式一：手动触发并粘贴来源

进入 GitHub 仓库：

```text
Actions → Weekly Conversation Summary → Run workflow
```

在 `source_content` 中粘贴本周对话要点或导出的对话内容。

### 方式二：维护来源文件

在仓库中新增或更新：

```text
data/weekly-summary-source.md
```

每周把重要对话要点写进去，GitHub Actions 自动执行时会读取该文件。

### 方式三：后续接入 Codex 或导出脚本

可以让 Codex 定期把本周重要对话、日报、项目记录汇总到：

```text
data/weekly-summary-source.md
```

然后由 GitHub Actions 负责定时生成和提交。

## OpenAI API 配置

如需由脚本自动调用模型生成完整总结，需要在 GitHub 仓库配置 Secret：

```text
OPENAI_API_KEY
```

可选配置：

```text
OPENAI_MODEL
```

默认模型：

```text
gpt-4.1-mini
```

如果没有配置 `OPENAI_API_KEY`，脚本不会失败，会生成一篇占位说明，提醒补充来源和密钥。

## 为什么比 ChatGPT 自动化更稳定

| 项目 | ChatGPT 自动化 | GitHub Actions 流水线 |
|---|---|---|
| 定时稳定性 | 可能受通知/客户端影响 | GitHub 原生定时任务 |
| 文件归档 | 不稳定 | 自动 commit 到仓库 |
| 可追踪性 | 较弱 | 有 Actions 日志和 Git 历史 |
| 失败排查 | 不透明 | 可查看失败日志 |
| 后续扩展 | 有限制 | 可接入 Codex、脚本、API |

## 建议后续优化

1. 新增 `data/weekly-summary-source.md`，作为本周对话输入源。
2. 配置 `OPENAI_API_KEY`，让脚本具备自动生成能力。
3. 让 AI Testing 日报、每周总结、项目周报共用一套 Markdown 归档规范。
4. 后续让 Codex 增加“来源内容收集脚本”，把每天重要对话摘要写入来源文件。
