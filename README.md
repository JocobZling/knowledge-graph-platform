# 个人知识图谱与问题追踪系统

前后端分离 Demo，后端使用 Spring Boot 3、MyBatis Plus、PostgreSQL，前端使用 Vue 3、Vite、Element Plus、Axios、ECharts Graph。

## 目录结构

```text
knowledge-graph-platform
├─ backend
├─ frontend
└─ docs
```

## 数据库初始化

1. 创建 PostgreSQL 数据库：

```sql
CREATE DATABASE knowledge_graph;
```

2. 执行脚本：

```bash
psql -U postgres -d knowledge_graph -f docs/database.sql
```

3. 如账号密码不是 `postgres/postgres`，修改 `backend/src/main/resources/application.yml`。

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

## 已实现功能

- Dashboard 指标与近期列表
- 知识卡片新增、查询、编辑、删除
- 问题记录新增、查询、编辑、删除、标记解决
- 项目新增、查询、编辑、删除
- Prompt 新增、查询、编辑、删除
- 图谱节点聚合展示和关系维护接口
- 时间轴列表
- 全局关键词搜索

## 文档

- API 文档：`docs/api.md`
- 数据库脚本与示例数据：`docs/database.sql`
- MVP 需求摘要：`docs/requirement.md`


## Daily Brief 日报模块

Daily Brief 是本项目的情报中心，用于归档每天生成的 AI、测试开发、Agent 生态和技术行业日报。

### Markdown 目录

示例文件位于：

```text
daily/ai-testing/2026/06/2026-06-29.md
daily/ai-tech/2026/06/2026-06-29.md
```

每篇 Markdown 建议包含 Front Matter：`title`、`date`、`type`、`category`、`tags`、`summary`、`source`、`status`。

### 数据库初始化

重新执行：

```bash
psql -U postgres -d knowledge_graph -f docs/database.sql
```

### 同步日报

后端启动后调用：

```bash
curl -X POST http://localhost:8080/api/daily-brief/sync
```

同步会扫描 `daily/` 目录并解析 Markdown Front Matter；同一天同 type 重复执行会更新，不会重复插入。

### 前端入口

```text
http://localhost:5173/daily
http://localhost:5173/daily/:id
```

如果后端没有启动，前端会使用 `frontend/public/daily-reports/` 中的本地 Markdown 示例作为预览数据。
