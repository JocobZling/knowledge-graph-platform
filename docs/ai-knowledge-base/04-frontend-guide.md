# Frontend Guide

## 技术栈

- Vue 3
- Vite
- Vue Router
- Element Plus
- Axios
- ECharts

## 运行和构建

```cmd
cd frontend
npm install
npm.cmd run dev
npm.cmd run build
```

Vite 配置：

```text
server.port = 5173
/api -> http://localhost:8080
```

## 页面和组件约定

优先复用已有组件：

- `AppLayout`
- `PageHeader`
- `BaseCard`
- `StatusTag`
- `EmptyState`
- `MarkdownViewer`
- `DailyBriefCard`

新增页面放在 `frontend/src/views/`，公共组件放在 `frontend/src/components/`。页面路由集中维护在 `frontend/src/router/index.js`。

## API 调用

统一使用：

```js
import http from '../api/http'
```

`http.js` 会解包后端统一响应；业务页面拿到的是 `data` 字段。

## UI 规则

- 遵循 `agent.md` 和 `docs/design/CODEX_DESIGN_BOOK_AI_WORKSPACE.md`。
- 不生成 Element Plus 默认后台模板风格。
- 页面要有统一布局、间距、卡片和状态标签。
- 样式变量优先放在 `frontend/src/styles/variables.css`。
- 新页面优先使用公共 class 和公共组件，避免每页重复大段样式。
- Daily Brief 列表使用内容卡片，不做传统后台表格。
- Daily Brief 详情以阅读体验优先，正文宽度保持舒适。

## 本地 fallback

当后端未启动时，Daily Brief 前端可使用 `frontend/public/daily-reports/` 中的本地 Markdown 示例作为预览数据。修改此逻辑时要保留无后端预览能力，除非任务明确要求移除。

该 fallback 由 `index.json` 显式列出文件，属于独立的预览快照；它不等同于 `daily/` 下的完整归档。需要验证新增归档内容时，应启动后端并执行 Daily Brief 同步。

## Daily Brief 元数据展示与筛选

- 将 `category` 视为面向读者的主题名；`type` 是回退值和数据类型标识。
- 卡片、详情页与主题筛选都使用 `category || type`，避免同一篇内容同时出现语义重复的主题。
- 主题筛选的选项键会忽略大小写、空格、连字符和下划线；标签仍按原值展示。
- 卡片标签会按上述主题归一规则去重，因此新增内容应同时保留稳定的 `type` 与清晰的 `category`。
