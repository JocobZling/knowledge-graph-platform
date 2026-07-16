# WayToAGI 信息源缓存

该目录用于缓存 WayToAGI 飞书知识库页面，供每日《智能测试日报》读取。

## 为什么需要缓存

飞书公开 Wiki 对普通 HTTP 抓取器可能产生循环重定向。同步脚本使用真实 Chromium 浏览器渲染页面，并在浏览器抓取失败时尝试 Jina Reader，避免日报任务因单一抓取方式失败而完全跳过该信息源。

## 自动执行

GitHub Action：`.github/workflows/sync-waytoagi.yml`

- 每天北京时间 08:10 自动执行。
- 成功后生成或更新 `data/sources/waytoagi/latest.md`。
- `status.json` 记录抓取时间、方式、正文长度和内容哈希。
- 日报任务在 09:00 运行时优先读取 `latest.md`，只使用 48 小时内的成功缓存。

## 本地测试

```bash
python -m pip install "playwright>=1.45,<2"
python -m playwright install chromium
python scripts/sync_waytoagi.py
```

## 页面需要登录时

先在本地执行：

```bash
python scripts/sync_waytoagi.py --login
```

浏览器中完成飞书登录后回到终端按 Enter，会生成：

```text
.secrets/feishu-storage-state.json
```

把该文件的完整 JSON 内容保存为 GitHub Actions Secret：

```text
FEISHU_STORAGE_STATE_JSON
```

不要将 `.secrets/feishu-storage-state.json` 提交到仓库，其中可能包含登录凭证和 Cookie。

## 日报使用规则

1. 先读取 `status.json`，确认状态为 `success`。
2. 缓存时间不超过 48 小时时读取 `latest.md`。
3. 从页面中发现的线索仍需回到原始官方来源进行二次核验。
4. 缓存失败或过期时，继续使用其他可靠来源，不得虚构 WayToAGI 内容。
