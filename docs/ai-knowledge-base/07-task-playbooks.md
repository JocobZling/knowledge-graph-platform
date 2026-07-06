# Task Playbooks

## 开始任何任务

1. 读 `AGENTS.md`。
2. 执行 `git status --short` 和 `git diff --stat`。
3. 按任务类型读本知识库对应文档。
4. 找到现有实现，遵循原结构修改。
5. 小步验证，最后说明验证结果。

## 新增后端模块

1. 查 `docs/database.sql` 和相邻模块。
2. 新增或更新表结构。
3. 按 Entity、Mapper、Service、Controller 分层添加代码。
4. 保持统一 `ApiResponse`。
5. 更新 `docs/api.md` 和本知识库。
6. 运行 `scripts\build-backend.cmd`。

## 新增前端页面

1. 读 `agent.md`、设计书和 `04-frontend-guide.md`。
2. 复用公共组件和样式变量。
3. 在 `router/index.js` 添加路由。
4. API 调用统一走 `api/http.js`。
5. 使用 `npm.cmd run build` 验证。

## 修改 Daily Brief

1. 读 `docs/daily-brief-requirement.md` 和 `05-data-and-content.md`。
2. 确认 Markdown 文件、数据库表和前端 fallback 三者是否都受影响。
3. 保持 `/api/daily-brief/sync` 可重复执行。
4. 保持 `/daily` 和 `/daily/:id` 在后端不可用时仍能预览本地示例。

## 修 Bug

1. 先复现或定位最小失败点。
2. 优先找相邻模块的既有模式。
3. 只修改与问题直接相关的文件。
4. 增加必要的防御逻辑或验证。
5. 说明根因、改动和验证。

## 交付说明模板

最终回复包含：

```text
已完成：
- ...

验证：
- ...

注意：
- ...
```

如果未运行测试，必须说明原因。

