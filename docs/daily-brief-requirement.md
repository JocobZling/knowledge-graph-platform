# Daily Brief 日报模块需求文档 v1

## 1. 模块定位

Daily Brief 是个人知识图谱网站中的「情报中心」模块，用于存放每天生成的 AI / 测试开发 / Agent 生态 / 技术行业日报。

该模块不只是展示 Markdown，而是要支持：

* Markdown 日报存储
* 日报列表展示
* 日报详情阅读
* 标签与分类筛选
* 搜索
* 与知识卡片、项目、图谱建立关联
* 后续支持结构化解析与 AI 总结

---

## 2. 第一阶段目标

第一阶段先完成 MVP：

* 支持本地 Markdown 文件作为日报来源
* 支持 Front Matter 元数据解析
* 支持日报列表
* 支持日报详情页
* 支持按类型、标签、日期筛选
* 支持关键词搜索
* 支持与知识图谱基础关联
* 保留后续扩展结构化新闻条目的能力

---

## 3. Markdown 文件目录规范

日报文件统一放在前端或后端可读取的目录中。

推荐目录：

```text
daily/
├── ai-testing/
│   └── 2026/
│       └── 06/
│           ├── 2026-06-29.md
│           └── 2026-06-30.md
│
├── ai-tech/
│   └── 2026/
│       └── 06/
│           └── 2026-06-29.md
│
└── agent-ecosystem/
    └── 2026/
        └── 06/
            └── 2026-06-29.md
```

---

## 4. Markdown Front Matter 规范

每篇日报必须包含 Front Matter。

示例：

```md
---
title: AI Testing Daily Brief
date: 2026-06-29
type: ai-testing
category: AI Testing
tags:
  - AI Testing
  - Codex
  - Agent
  - MCP
summary: 今日 AI Testing 与 Agent 生态重点动态。
source: ChatGPT
status: published
---

# AI Testing Daily Brief

## 今日重点

...

## 行业动态

...

## Agent Ecosystem

...

## 值得关注

...
```

---

## 5. Front Matter 字段说明

```text
title：日报标题
date：日报日期，格式 YYYY-MM-DD
type：日报类型，例如 ai-testing / ai-tech / agent-ecosystem
category：分类名称
tags：标签数组
summary：摘要
source：来源，例如 ChatGPT
status：状态，draft / published / archived
```

---

## 6. 数据库存储策略

Markdown 原文可以保留在文件系统中，但需要在数据库中建立索引，方便列表、搜索、筛选和关联。

---

## 7. 数据库表设计

### 7.1 daily_brief

```sql
CREATE TABLE daily_brief (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    brief_date DATE NOT NULL,
    type VARCHAR(100),
    category VARCHAR(100),
    tags JSONB,
    summary TEXT,
    source VARCHAR(100),
    status VARCHAR(50) DEFAULT 'published',
    file_path VARCHAR(500),
    content TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

说明：

```text
file_path：Markdown 文件路径
content：Markdown 正文内容，第一阶段可以直接存库，方便读取
```

---

### 7.2 daily_brief_item

用于后续扩展。第一阶段可以先建表，不强制使用。

```sql
CREATE TABLE daily_brief_item (
    id BIGSERIAL PRIMARY KEY,
    brief_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    source_name VARCHAR(100),
    source_url TEXT,
    category VARCHAR(100),
    importance INT DEFAULT 3,
    summary TEXT,
    analysis TEXT,
    tags JSONB,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

说明：

```text
brief_id：所属日报 id
title：单条新闻标题
source_name：来源名称
source_url：来源链接
importance：重要程度，1-5
summary：摘要
analysis：个人分析或 AI 分析
tags：标签
```

---

### 7.3 daily_brief_relation

用于日报和知识图谱节点建立关系。

```sql
CREATE TABLE daily_brief_relation (
    id BIGSERIAL PRIMARY KEY,
    brief_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

relation_type 示例：

```text
RELATED_TO
MENTIONS
GENERATED_KNOWLEDGE
PART_OF
```

target_type 示例：

```text
knowledge
project
issue
prompt
tag
```

---

## 8. 后端接口设计

接口统一以 `/api/daily-brief` 开头。

### 8.1 日报列表

```text
GET /api/daily-brief/list
```

查询参数：

```text
keyword
type
category
tag
startDate
endDate
status
page
size
```

返回字段：

```json
{
  "id": 1,
  "title": "AI Testing Daily Brief",
  "briefDate": "2026-06-29",
  "type": "ai-testing",
  "category": "AI Testing",
  "tags": ["AI Testing", "Codex", "Agent"],
  "summary": "今日 AI Testing 与 Agent 生态重点动态。",
  "status": "published"
}
```

---

### 8.2 日报详情

```text
GET /api/daily-brief/{id}
```

返回：

```json
{
  "id": 1,
  "title": "AI Testing Daily Brief",
  "briefDate": "2026-06-29",
  "type": "ai-testing",
  "category": "AI Testing",
  "tags": ["AI Testing", "Codex", "Agent"],
  "summary": "今日 AI Testing 与 Agent 生态重点动态。",
  "content": "# AI Testing Daily Brief\n\n...",
  "status": "published"
}
```

---

### 8.3 新增日报

```text
POST /api/daily-brief/create
```

请求：

```json
{
  "title": "AI Testing Daily Brief",
  "briefDate": "2026-06-29",
  "type": "ai-testing",
  "category": "AI Testing",
  "tags": ["AI Testing", "Codex", "Agent"],
  "summary": "今日 AI Testing 与 Agent 生态重点动态。",
  "content": "# AI Testing Daily Brief\n\n...",
  "status": "published"
}
```

---

### 8.4 更新日报

```text
PUT /api/daily-brief/update
```

---

### 8.5 删除日报

```text
DELETE /api/daily-brief/{id}
```

第一阶段使用逻辑删除或状态归档：

```text
status = archived
```

---

### 8.6 从 Markdown 文件同步日报

```text
POST /api/daily-brief/sync
```

功能：

* 扫描 daily 目录
* 读取 Markdown
* 解析 Front Matter
* 将元数据和正文写入 daily_brief 表
* 如果同一天同 type 已存在，则更新
* 如果不存在，则新增

---

### 8.7 日报关联知识节点

```text
POST /api/daily-brief/relation/create
```

请求：

```json
{
  "briefId": 1,
  "targetType": "knowledge",
  "targetId": 10,
  "relationType": "MENTIONS"
}
```

---

## 9. 前端页面设计

### 9.1 路由

```text
/daily
日报列表

/daily/:id
日报详情

/daily/calendar
日报日历视图
```

---

### 9.2 日报列表页

功能：

* 按日期倒序展示
* 支持搜索
* 支持按 type 筛选
* 支持按 category 筛选
* 支持按 tag 筛选
* 支持按日期范围筛选
* 支持点击进入详情

展示字段：

```text
标题
日期
摘要
标签
类型
状态
```

设计要求：

* 不要做成传统后台表格
* 使用卡片式列表
* 类似 Notion / Linear 风格
* 每条日报是一张内容卡片
* 标签轻量展示
* 日期清晰展示

---

### 9.3 日报详情页

功能：

* Markdown 渲染
* 展示 Front Matter 信息
* 展示标签
* 展示关联知识点
* 展示关联项目
* 支持返回列表

设计要求：

* 阅读体验优先
* 正文最大宽度 860px
* 标题层级清晰
* Markdown 样式美观
* 代码块可读
* 链接明显但不刺眼

---

### 9.4 日历视图

第一阶段可选。

用于展示每日是否有日报。

```text
2026-06
日 一 二 三 四 五 六
...
```

点击日期进入对应日报。

---

## 10. 前端组件建议

```text
DailyBriefList.vue
DailyBriefCard.vue
DailyBriefDetail.vue
DailyBriefFilter.vue
DailyBriefCalendar.vue
MarkdownViewer.vue
TagList.vue
```

---

## 11. Markdown 渲染建议

前端建议使用：

```text
markdown-it
highlight.js
```

如果项目已有 Markdown 渲染库，优先复用。

要求：

* 支持标题
* 支持列表
* 支持代码块
* 支持链接
* 支持表格
* 支持引用
* 支持高亮代码

---

## 12. 与知识图谱模块的关系

日报不是孤立内容，需要能和已有模块关联。

### 关联对象

```text
Knowledge Card
Issue
Project
Prompt
Tag
Graph Node
```

### 示例

```text
日报：2026-06-29 AI Testing Daily

提到：
- Codex
- Agent
- MCP
- Browser Agent

则可以关联：
- Knowledge: Codex
- Knowledge: Agent
- Knowledge: MCP
- Project: AI Testing Daily
```

---

## 13. 搜索设计

日报内容需要纳入全局搜索。

搜索范围：

```text
title
summary
content
tags
category
type
```

第一阶段可以使用数据库 like 查询。

后续可升级为：

* PostgreSQL Full Text Search
* 向量检索
* AI 问答

---

## 14. 第一阶段验收标准

### 后端

* daily_brief 表创建成功
* daily_brief_item 表创建成功
* daily_brief_relation 表创建成功
* 支持日报 CRUD
* 支持 Markdown 同步接口
* 支持列表筛选
* 支持详情查询
* 支持统一返回格式

### 前端

* `/daily` 可以展示日报列表
* `/daily/:id` 可以展示 Markdown 详情
* 支持标签展示
* 支持搜索
* 支持筛选
* UI 风格与知识图谱网站整体一致
* 不使用默认后台表格风格

### 数据

* 至少内置 2 篇 Markdown 示例日报
* 示例日报包含 Front Matter
* 示例日报可以被同步进入数据库
* 页面可以正常阅读

---

## 15. Codex 执行要求

请基于当前项目实现 Daily Brief 模块。

执行顺序：

1. 阅读 `AGENTS.md`
2. 阅读 `DESIGN.md`
3. 阅读当前项目结构
4. 新增数据库表 SQL
5. 新增后端 Entity / Mapper / Service / Controller
6. 新增 Markdown Front Matter 解析逻辑
7. 新增 `/api/daily-brief/sync` 接口
8. 新增前端页面 `/daily`
9. 新增前端页面 `/daily/:id`
10. 接入全局导航
11. 增加示例 Markdown 文件
12. 更新 README

---

## 16. Codex 注意事项

* 不要破坏已有模块
* 不要修改已有接口返回格式
* 不要引入过重依赖
* 如果必须新增依赖，需要说明原因
* 前端不要生成默认后台管理系统风格
* 页面必须遵守 DESIGN.md
* Markdown 正文展示要重视阅读体验
* 后端要兼容 Front Matter 缺失字段的情况
* sync 接口需要支持重复执行，不应产生重复数据

---

## 17. 推荐 Codex Prompt

```text
请基于 docs/daily-brief-requirement.md 实现 Daily Brief 日报模块。

要求：
1. 先阅读 AGENTS.md、DESIGN.md 和现有项目结构。
2. 后端新增 Daily Brief 相关表、Entity、Mapper、Service、Controller。
3. 新增 Markdown Front Matter 解析与同步接口。
4. 前端新增 /daily 日报列表页和 /daily/:id 日报详情页。
5. 日报列表使用卡片式布局，不要使用默认后台表格风格。
6. 日报详情页使用 Markdown 渲染，保证阅读体验。
7. 新增至少 2 篇示例 Markdown 日报。
8. sync 接口可重复执行，不产生重复数据。
9. 所有接口保持统一返回格式。
10. 修改完成后输出：
   - 修改文件清单
   - 数据库变更说明
   - 启动方式
   - 验证步骤
```
