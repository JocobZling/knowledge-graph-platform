# Data And Content

## PostgreSQL

数据库名：

```text
knowledge_graph
```

初始化脚本：

```text
docs/database.sql
```

本地导入：

```cmd
scripts\init-knowledge-db.cmd
```

## 主要数据域

- `knowledge_card`：知识卡片。
- `issue_record`：问题记录。
- `project_record`：项目记录。
- `prompt_record`：Prompt 记录。
- `graph_relation`：图谱关系。
- `timeline_event`：时间轴事件。
- `daily_brief`：日报索引和正文。
- `daily_brief_item`：日报结构化条目，预留扩展。
- `daily_brief_relation`：日报与知识、项目、问题、Prompt 等对象的关联。

## Daily Brief Markdown

源文件位于：

```text
daily/<type>/<year>/<month>/<YYYY-MM-DD>.md
```

示例：

```text
daily/ai-testing/2026/07/2026-07-03-ai-testing.md
daily/ai-tech/2026/07/2026-07-03-ai-tech.md
```

推荐 Front Matter：

```yaml
---
title: AI Testing Daily Brief
date: 2026-07-03
type: ai-testing
category: AI Testing
tags:
  - AI Testing
  - Agent
summary: 今日摘要
source: ChatGPT
status: published
---
```

当前归档类型包括 `ai-tech`、`ai-testing` 和 `weekly-summary`。`type` 用于同步去重和程序识别；`category` 用于前端主题展示，推荐使用清晰、可读的名称，例如 `AI Tech`、`AI Testing`、`Weekly Summary`。

## 周总结归档

周总结沿用相同的 Markdown 与 Front Matter 规范：

```text
daily/weekly-summary/<year>/<month>/<YYYY-MM-DD>-weekly-summary.md
```

生成流水线详见 [`docs/weekly-summary-pipeline.md`](../weekly-summary-pipeline.md)：GitHub Actions 每周日 09:30（Asia/Shanghai）运行，也支持手动传入 `source_content`。脚本优先使用手动内容，其次读取 `data/weekly-summary-source.md`；配置仓库 Secret `OPENAI_API_KEY` 后才会调用模型生成完整总结。缺少来源或密钥时，脚本会生成可追踪的占位归档，而不是静默失败。

## 同步规则

`POST /api/daily-brief/sync` 应满足：

- 扫描 `daily/`，并兼容 `frontend/public/daily-reports/` 的本地预览 Markdown。
- 解析 Front Matter 和 Markdown 正文。
- 以 `briefDate + type` 写入或更新 `daily_brief`。
- 同一天同 type 重复执行时更新，不产生重复数据。
- Front Matter 缺失字段时要兼容处理。

`daily/` 是完整归档的事实源；`frontend/public/daily-reports/index.json` 只服务于前端无后端预览，新增 `daily/` 文件不会自动加入该 fallback 索引。

## 内容安全

- 日报可包含公开资料摘要和个人分析。
- 不要把公司内部资料、私钥、token、客户隐私写入 Markdown 示例。
- 引入外部链接时，保留来源字段或正文链接，方便后续追溯。


## 2026-07-06 Daily Brief Sync Snapshot

- Source Markdown files were present under `daily/` for `ai-tech` and `ai-testing` through `2026-07-06`.
- `POST /api/daily-brief/sync` was executed after backend startup.
- Sync response was `{"code":0,"message":"success","data":12}`, meaning 12 Daily Brief records were inserted or updated.
- `GET /api/daily-brief/list` returned `code: 0`; newest record verified in the response was `2026-07-06` / `ai-testing`.
- Frontend `/daily` was verified with `HTTP/1.1 200 OK` from Vite on `http://localhost:5173/`.
