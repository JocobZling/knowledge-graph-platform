# Project Map

## 根目录

```text
knowledge-graph-platform/
  backend/                  Spring Boot 后端
  frontend/                 Vue 3 + Vite 前端
  daily/                    Daily Brief Markdown 源文件
  docs/                     需求、接口、数据库、运行状态和设计文档
  docs/ai-knowledge-base/   本 AI 知识库
  scripts/                  本地运行、构建、数据库和 Git 辅助脚本
  AGENTS.md                 AI 代理入口规则
  .env.example              环境变量示例，不含真实密码
```

## 后端关键路径

```text
backend/src/main/java/com/example/knowledgegraph/
  common/       ApiResponse、全局异常处理
  config/       WebConfig、JSONB 类型处理
  controller/   REST API
  dto/          接口数据对象
  entity/       数据库实体
  mapper/       MyBatis Plus Mapper
  service/      Service 接口
  service/impl/ Service 实现
```

## 前端关键路径

```text
frontend/src/
  App.vue
  main.js
  api/http.js
  router/index.js
  components/
  views/
  styles/variables.css
  style.css
```

## 文档事实源

- `docs/requirement.md`：MVP 范围。
- `docs/api.md`：接口清单。
- `docs/database.sql`：数据库结构和示例数据。
- `docs/daily-brief-requirement.md`：Daily Brief 模块需求。
- `docs/session-status.md`：后端工具链和数据库环境状态。
- `docs/git-operations.md`：Git、构建和网络问题记录。
- `docs/design/CODEX_DESIGN_BOOK_AI_WORKSPACE.md`：当前 UI 设计书。

## 当前应保护的工作区状态

每次开始前都要重新执行：

```cmd
git status --short
git diff --stat
```

不要覆盖用户已有改动。发现无关改动时，保持原样；发现相关改动时，先读懂再继续。

