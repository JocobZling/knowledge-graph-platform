# Frontend Design Guide

## Style
- 整体风格：简洁、现代、知识管理工具感
- 参考风格：Notion、Linear、Raycast、Vercel Dashboard
- 避免：默认后台管理系统风、过重边框、杂乱色彩

## Layout
- 使用左侧 Sidebar + 顶部 Header + 内容区
- 内容区最大宽度 1200px
- 页面留白充足
- 卡片统一圆角 12px
- 页面背景使用浅灰，内容卡片使用白色

## Color
- 主色：#2563eb
- 背景：#f8fafc
- 卡片：#ffffff
- 文字主色：#0f172a
- 文字次色：#64748b
- 边框：#e2e8f0

## Components
- 所有列表页使用统一 PageHeader
- 所有内容区使用 BaseCard
- 所有状态使用 StatusTag
- 所有表单使用统一间距
- 所有按钮优先使用 primary / default / text 三种

## UX
- 空状态要有提示
- 删除要二次确认
- 加载中要有 loading
- 表单提交后要有成功提示
- 错误提示要自然，不直接暴露后端异常
