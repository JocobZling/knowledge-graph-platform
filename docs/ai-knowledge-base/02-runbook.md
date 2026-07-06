# Runbook

## 已知本地环境

根据 `docs/session-status.md`：

- Maven 3.9.16：`D:\codexWorkspace\tools\apache-maven-3.9.16`
- PostgreSQL 17.6：`D:\codexWorkspace\tools\pgsql`
- PostgreSQL data：`D:\codexWorkspace\postgres-data`
- Maven 本地仓库：`D:\codexWorkspace\m2-repository`
- PostgreSQL 日志：`D:\codexWorkspace\logs\postgres.log`

## 启动数据库

```cmd
scripts\start-postgres.cmd
```

停止数据库：

```cmd
scripts\stop-postgres.cmd
```

检查数据库：

```cmd
scripts\check-backend-db.cmd
```

初始化或重新导入数据库：

```cmd
scripts\init-knowledge-db.cmd
```

## 启动后端

```cmd
scripts\run-backend.cmd
```

后端地址：

```text
http://localhost:8080
```

构建后端：

```cmd
scripts\build-backend.cmd
```

注意：如果 Maven 编译在 `PromptController.java` 附近异常，检查文件开头是否有 BOM。

## 启动前端

```cmd
cd frontend
npm install
npm.cmd run dev
```

前端地址：

```text
http://localhost:5173
```

PowerShell 中如果 `npm run build` 被执行策略拦截，使用：

```cmd
npm.cmd run build
```

## Daily Brief 同步

后端启动后：

```cmd
curl -X POST http://localhost:8080/api/daily-brief/sync
```

同步逻辑会扫描 `daily/` 目录，解析 Markdown Front Matter，并将同一天同类型日报更新而不是重复插入。

## 常见排查

- 前端接口失败：先确认后端 `:8080` 是否启动，再确认 Vite `/api` 代理。
- 数据为空：确认 PostgreSQL 已启动并执行过 `docs/database.sql`。
- Git fetch/pull/add 权限错误：参考 `docs/git-operations.md`，Codex 沙箱写 `.git` 可能需要批准。
- GitHub 443 连接失败：先处理网络、代理或 VPN，再重试 Git 命令。

