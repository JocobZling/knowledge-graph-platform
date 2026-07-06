# Security And Git

## 敏感信息规则

禁止提交：

- `.env`
- 真实数据库密码
- 公司内部代码密钥
- token、cookie、私钥、证书
- 客户数据或内部不可公开材料

允许提交：

- `.env.example`
- 无害示例值
- 环境变量名
- 本地启动说明

## 环境变量

后续配置应走环境变量。示例变量见根目录 `.env.example`：

```text
DB_URL
DB_USERNAME
DB_PASSWORD
CODEX_WORKSPACE
MAVEN_REPO
PGDATA
```

## Git 前置检查

执行任何改动前：

```cmd
git status --short
git diff --stat
```

提交前：

```cmd
git diff --stat
git diff --cached --stat
git status --short
```

不要提交无关文件，不要覆盖用户已有改动。

## 本仓库 Git 注意事项

参考 `docs/git-operations.md`：

- Codex 沙箱内 `git fetch`、`git pull`、`git add` 可能因为 `.git` 写权限失败。
- 遇到 `.git/FETCH_HEAD` 或 `.git/index.lock` 权限错误时，需要按 Codex 规则请求提升权限。
- PowerShell 下前端构建优先使用 `npm.cmd run build`。
- 网络连接 GitHub 失败时，先处理网络、代理或 VPN。

## 推荐提交信息

```text
docs(ai): add repository knowledge base
feat(daily): add daily brief filter
fix(api): handle missing daily brief metadata
chore(config): move database config to environment variables
```

