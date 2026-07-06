# AGENTS.md

本文件是本仓库给 Codex / Cursor / Claude Code / 其他 AI 编程代理的入口规则。执行任何改动前，先读本文件，再按任务类型阅读 `docs/ai-knowledge-base/` 中的对应文档。

## 强制规则

- 不提交 `.env`。
- 不提交真实数据库密码。
- 不提交公司内部代码密钥。
- 使用 `.env.example` 记录变量名和示例值。
- 敏感配置全部走环境变量。
- Codex 执行前先检查 `git status --short` 和 `git diff --stat`。
- 默认项目根目录使用 `D:\codexWorkspace`。
- 后续新建、克隆、初始化项目时，优先放在 `D:\codexWorkspace\<项目名>`。
- 不把长期项目放在 `C:\Users\92479\Documents\Codex` 下；该目录仅作为临时/会话工作区使用。

## 本仓库上下文

- 项目名：`knowledge-graph-platform`
- 仓库路径：`D:\codexWorkspace\knowledge-graph-platform`
- 后端：Spring Boot 3.3.5 + Java 17 + MyBatis Plus + PostgreSQL
- 前端：Vue 3 + Vite + Element Plus + Axios + ECharts
- 主要模块：知识卡片、问题记录、项目中心、Prompt、图谱关系、时间轴、全局搜索、Daily Brief 日报

## AI 知识库入口

优先阅读：

1. `docs/ai-knowledge-base/README.md`
2. `docs/ai-knowledge-base/00-project-map.md`
3. 按任务类型继续阅读后端、前端、数据、安全或任务流程文档

如果任务涉及后端启动、数据库、Maven 或 PostgreSQL，必须先读：

- `docs/session-status.md`
- `docs/ai-knowledge-base/02-runbook.md`

如果任务涉及界面或样式，必须先读：

- `agent.md`
- `docs/design/CODEX_DESIGN_BOOK_AI_WORKSPACE.md`
- `docs/ai-knowledge-base/04-frontend-guide.md`

## 默认工作流

1. 检查工作区状态，保护用户已有改动。
2. 阅读相关知识库文档和现有代码。
3. 小范围修改，遵循现有结构和命名。
4. 能验证就验证；不能验证要说明原因。
5. 最终回复列出变更文件、验证结果、未完成风险。

