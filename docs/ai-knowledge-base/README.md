# AI Knowledge Base

这是 `knowledge-graph-platform` 的专属 AI 知识库。目标是让后续 AI 代理不用重新猜项目结构、运行方式、设计约定和安全边界。

## 阅读顺序

所有任务先读：

1. `AGENTS.md`
2. `00-project-map.md`
3. 本文件

按任务继续阅读：

- 后端/API：`01-architecture.md`、`02-runbook.md`、`03-backend-guide.md`
- 前端/UI：`01-architecture.md`、`04-frontend-guide.md`
- Daily Brief/内容：`05-data-and-content.md`
- Git/安全/交付：`06-security-and-git.md`
- 新功能/修 Bug/验收：`07-task-playbooks.md`

## 知识库维护规则

- 项目结构、启动命令、接口约定发生变化时，同步更新这里。
- 不在知识库中记录真实密码、token、内网密钥或客户数据。
- 用 `.env.example` 记录变量名，用环境变量承载敏感配置。
- 文档要写“当前事实”和“下一步建议”，避免写成泛泛原则。

## 当前项目摘要

这是一个个人知识图谱与问题追踪系统，采用前后端分离：

- 后端提供统一 `/api` 接口和 PostgreSQL 数据持久化。
- 前端提供 Dashboard、CRUD 管理、图谱、搜索、时间轴、Daily Brief 阅读。
- Daily Brief 使用本地 Markdown + Front Matter，并可同步进入数据库。

