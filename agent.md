## Frontend UI Rules

- 开发前必须阅读 DESIGN.md。
- 不允许生成 Element Plus 默认后台模板风格。
- 页面必须有统一布局、统一间距、统一卡片样式。
- 优先封装公共组件，而不是每个页面重复写样式。
- 新页面必须复用：
  - AppLayout
  - PageHeader
  - BaseCard
  - StatusTag
  - EmptyState
- 样式变量统一放在 src/styles/variables.css。
- 页面样式不要散落过多，优先使用公共 class。
