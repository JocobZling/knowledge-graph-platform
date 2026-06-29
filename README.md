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
