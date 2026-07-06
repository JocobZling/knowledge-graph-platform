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

## 同步规则

`POST /api/daily-brief/sync` 应满足：

- 扫描 `daily/`。
- 解析 Front Matter 和 Markdown 正文。
- 写入或更新 `daily_brief`。
- 同一天同 type 重复执行时更新，不产生重复数据。
- Front Matter 缺失字段时要兼容处理。

## 内容安全

- 日报可包含公开资料摘要和个人分析。
- 不要把公司内部资料、私钥、token、客户隐私写入 Markdown 示例。
- 引入外部链接时，保留来源字段或正文链接，方便后续追溯。

