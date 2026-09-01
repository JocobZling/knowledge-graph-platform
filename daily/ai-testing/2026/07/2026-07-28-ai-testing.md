---
title: "AI Testing Daily Brief"
date: "2026-07-28"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - MCP Testing
  - Copilot Governance
  - Enterprise Controls
  - Protocol Compatibility
source: "ChatGPT"
status: "published"
summary: "今日新增聚焦GitHub将Copilot桌面端与云端Agent纳入统一企业策略，以及MCP 2026-07-28规范带来的无状态协议、扩展机制和升级回归要求。"
---

# AI Testing Daily Brief - 2026-07-28

## 今日摘要

今天最明确的产品级新增来自 GitHub：Copilot app 获得独立访问策略，同时 Copilot app 与 Copilot cloud agent 开始统一执行企业 `managed-settings.json`。企业可以分别控制桌面端与 CLI 的可用范围，并集中限制插件、插件市场、命令审批绕过和默认模型选择。对测试开发而言，Agent 治理对象已经从单个 IDE 扩展到桌面端、CLI、VS Code 和云端任务。

MCP `2026-07-28` 规范进入计划发布日。该版本引入无状态协议核心，移除 `initialize` 握手和协议级 Session，并将 MCP Apps、Tasks、扩展框架、授权强化和正式弃用策略纳入协议演进体系。当前官方仓库在执行本日报时仍主要展示 RC 与待完成发布事项，因此本文将其标注为“计划发布日/待最终 GA 页面确认”，不把 RC 直接写成已正式发布。

本次已读取最近 7 篇现有日报与 `data/ai-testing/topic-index.json` 完成去重。IssueBench、Gemini 3.5 Flash Cyber、MCPEvol-Bench、BioSecBench、MCP Conformance、Eval Engineering Skill、IssueTrojanBench 和 DynamicMCPBench 等近期主题未重复进入正文。

WayToAGI 缓存状态为 success，抓取时间为 2026-07-27 12:04，距本次执行不足 48 小时。本次只将其作为发现线索，正文事实均回到 GitHub 与 MCP 官方来源核验。

## 今日重点

### 1. Copilot治理开始覆盖桌面端与云端Agent

- **一句话总结：** GitHub Copilot app 与 Copilot cloud agent 现在可以统一执行企业管理设置，Copilot app 还获得独立于 Copilot CLI 的访问策略。
- **为什么值得关注：** 过去企业可能只控制 IDE 或 CLI，而桌面 Agent、云端 Agent 和插件市场形成新的治理空白。GitHub 现在允许企业统一限制插件来源、是否允许绕过命令/文件/URL审批，以及默认模型选择。
- **对智能测试或测试开发的影响：** Agent 准入测试需要验证同一策略在 VS Code、CLI、桌面 app 和 cloud agent 中是否一致生效，不能只在一个客户端验收后默认其他客户端等价。
- **建议动作：** 建立一组跨客户端策略回归：禁用未批准插件市场、禁止绕过审批、限制外部 URL，并在四类客户端中分别验证执行结果与审计记录。

来源：https://github.blog/changelog/2026-07-27-enterprise-managed-settings-now-apply-to-the-github-copilot-app/

### 2. Copilot app访问策略与功能策略需要分开测试

- **一句话总结：** Copilot app 现在具有独立访问策略，企业可选择全局启用、全局禁用或交由组织决定。
- **为什么值得关注：** “能否打开客户端”与“客户端中能做什么”是两个不同控制层。访问策略负责准入，`managed-settings.json` 负责插件、市场和审批等行为限制。
- **对智能测试或测试开发的影响：** 只验证用户无法登录并不足以证明策略完整；还要验证策略切换、组织继承、缓存刷新和已打开会话的处理方式。
- **建议动作：** 设计三组用户：企业禁用、组织决定、企业启用；分别验证新登录、重新登录、客户端重启和既有会话后的实际权限。

来源：https://github.blog/changelog/2026-07-27-manage-github-copilot-app-access-with-a-dedicated-policy/

### 3. MCP无状态升级需要重做状态、认证和兼容性测试

- **一句话总结：** MCP `2026-07-28` 计划移除 `initialize` 和协议级 Session，将协议版本、客户端信息和能力放入每次请求，并通过显式 Handle 管理业务状态。
- **为什么值得关注：** 无状态不等于无业务状态。购物车、浏览器会话、交易批次等状态需要通过显式 ID 在线程中传递；遗漏、串用或伪造 Handle 都可能形成新的错误与越权风险。
- **对智能测试或测试开发的影响：** MCP Server 升级需要覆盖版本协商、请求幂等、显式 Handle、认证上下文、并行请求、缓存、旧客户端兼容和错误码变化。
- **建议动作：** 在测试环境中同时运行旧版与新版客户端，构造 Handle 丢失、Handle 串用、重复请求、并行调用和错误版本头，验证 Server 是否安全失败并保留审计证据。

来源：https://blog.modelcontextprotocol.io/posts/2026-07-28-release-candidate/

## 行业新闻

### 1. GitHub为Copilot app提供独立访问策略

- **摘要：** 企业和组织可以独立控制 Copilot app，不再依赖 Copilot CLI 策略；支持全局启用、全局禁用或组织自行决定。
- **影响：** Agent客户端准入从共用开关转向按客户端治理。
- **发布时间：** 2026-07-27
- **来源：** GitHub Changelog
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是

### 2. 企业管理设置覆盖Copilot app与cloud agent

- **摘要：** `managed-settings.json` 可统一限制插件、插件市场、审批绕过及默认模型选择，并由桌面 app 和 cloud agent 执行。
- **影响：** 跨客户端策略一致性成为新的Agent治理和回归测试对象。
- **发布时间：** 2026-07-27
- **来源：** GitHub Changelog
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是

### 3. MCP `2026-07-28`进入计划发布日

- **摘要：** 新版规范计划引入无状态核心、扩展框架、Tasks、MCP Apps、授权强化和正式弃用政策。执行本日报时，最终 GA 页面仍待确认。
- **影响：** MCP Server需要开展破坏性变更、版本协商和显式状态Handle回归。
- **计划发布时间：** 2026-07-28
- **来源：** MCP官方博客与GitHub
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是

今日暂无更多经权威来源核验、且与近 7 日归档不重复的高价值新增。

## 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| GitHub Copilot app policy | 新增独立客户端访问策略 | 与 Copilot CLI 分离，可按企业或组织控制 | 验证客户端级准入、继承与策略切换 |
| GitHub enterprise managed settings | 扩展至 Copilot app 与 cloud agent | 插件、市场、审批绕过和默认模型统一治理 | 建立跨客户端策略一致性回归 |
| MCP `2026-07-28` | 计划发布新规范 | 无状态核心、显式 Handle、扩展框架、授权强化 | 重做协议版本、状态、认证与兼容性测试 |

## Agent Ecosystem

### Cross-Client Policy Enforcement

同一企业策略需要在不同 Agent 客户端中产生一致结果。测试不仅要比较配置文件，还应比较最终行为、拒绝原因和审计记录。

### Stateless Protocol, Explicit State

协议层不再维护 Session 后，业务状态必须显式传递。Handle 应具备作用域、主体绑定、有效期和不可猜测性，并防止跨用户、跨任务复用。

### Policy Propagation Testing

GitHub说明管理设置会在客户端重启、重新登录或约一小时内更新，cloud agent 在下一次任务中读取变更。因此策略测试需要覆盖传播延迟和旧会话边界。

## 开源推荐

### MCP Inspector

- **GitHub：** https://github.com/modelcontextprotocol/inspector
- **Star：** 约 10.4k，2026-07-28 联网核验
- **License：** MIT
- **核心能力：** 可视化连接 MCP Server，检查工具、资源、Prompt、认证和请求响应；适合手工探索协议行为与参数边界。
- **推荐指数：** 4.6/5
- **推荐理由：** 在无状态协议升级阶段，Inspector适合快速检查版本头、工具Schema、认证流程和错误返回。但它不能替代自动化Conformance与业务Oracle，应作为交互式诊断工具使用。

## 企业实践

### GitHub：用“客户端准入＋统一行为策略”治理Coding Agent

- **企业：** GitHub
- **做法：** 将 Copilot app 访问策略从 CLI 中拆分，同时通过 `managed-settings.json` 在 VS Code、CLI、Copilot app 和 cloud agent 中统一执行插件、市场、审批和模型策略。
- **效果：** 企业可以分别控制谁能使用客户端，以及客户端允许使用哪些能力；策略覆盖面从本地IDE扩展到桌面端和云端任务。
- **可借鉴点：** 企业Agent平台应将身份准入、客户端准入、工具准入和动作审批拆成不同控制层，并建立跨客户端一致性测试。

## 今日工具推荐

### MCP Inspector

**适用场景：**

- MCP Server首次接入；
- 协议升级后的交互式检查；
- 工具Schema和参数边界调试；
- OAuth与认证流程检查；
- 资源、Prompt和工具返回诊断。

**快速开始：**

```bash
npx @modelcontextprotocol/inspector
```

连接测试Server后，建议依次验证：

1. 协议版本与发现能力；
2. 工具列表及Schema；
3. 必填和可选参数；
4. 错误参数与错误版本；
5. 认证失败和权限不足；
6. 多次调用及显式Handle传递。

## 今日学习

### 为什么策略一致性不能只比较配置文件？

多个客户端读取同一份策略，不代表最终行为一定一致。不同客户端可能在插件加载、审批提示、URL访问、命令执行和缓存刷新上存在不同实现。策略测试应使用相同任务和相同身份，在每个客户端实际触发受限动作，比较是否阻断、拒绝理由、审计日志和生效时间。只有“配置一致＋行为一致＋证据一致”，才能认为跨客户端治理真正生效。

## 趋势观察

未来 3 个月，企业 Agent 治理会从单客户端配置转向跨 IDE、CLI、桌面端和云端 Agent 的统一策略验证；MCP 测试会从连接成功进一步转向显式状态、协议版本和破坏性升级回归。

## 30分钟 Action

### 建立第一版跨客户端策略回归表

1. 选择一条禁止访问外部URL的企业策略。
2. 准备同一测试任务和同一测试账号。
3. 分别在 VS Code、Copilot CLI、Copilot app 和 cloud agent 执行。
4. 记录是否阻断、拒绝原因、审计日志和策略生效时间。
5. 修改策略允许一个白名单域名。
6. 重启或重新登录客户端，并等待cloud agent下一任务。
7. 验证白名单域名可访问、其他域名仍被阻断。
8. 将差异固化为客户端版本回归用例。

## 值得跟进

- MCP `2026-07-28`最终GA公告与正式Tag；
- RC到GA之间是否存在规范差异；
- Tier 1 SDK正式支持时间；
- GitHub managed settings支持的完整Key列表；
- cloud agent与交互式客户端审批策略差异；
- 策略传播时间和旧会话处理；
- 显式Handle的主体绑定与过期机制；
- Browser Agent无状态任务的页面状态恢复；
- MCP Inspector对新版协议的完整支持情况。

## 我的备注

对金融测试平台而言，客户端准入与业务操作权限必须分开。测试人员可以使用Agent客户端，不代表该Agent可以改数、发送MQ、执行账务调整或访问生产数据。客户端策略只解决“从哪里使用”，业务权限仍应由服务端身份、数据范围和动作审批控制。

MCP无状态升级对金融链路的影响较直接。商户号、清算日期、汇总流水号和批次信息应作为显式业务参数或Handle传递，并绑定用户、环境和有效期。任何Handle缺失、跨用户复用或过期，都应拒绝执行，不能由Agent自行猜测。

Browser Agent同样需要跨客户端策略测试。桌面端和云端Agent可能使用不同浏览器环境、网络出口和登录状态；即使策略文件相同，也必须分别验证域名白名单、下载、上传、剪贴板和凭据访问。

安全日志审查中，策略变更本身应进入审计中心，记录修改人、旧值、新值、生效客户端和生效时间。测试计划先行时，应提前定义准入层、能力层、业务层和审批层的边界，避免把一份客户端配置误当作完整安全控制。

## 相关链接

- [GitHub Copilot app dedicated policy](https://github.blog/changelog/2026-07-27-manage-github-copilot-app-access-with-a-dedicated-policy/)
- [Enterprise managed settings for Copilot app and cloud agent](https://github.blog/changelog/2026-07-27-enterprise-managed-settings-now-apply-to-the-github-copilot-app/)
- [MCP 2026-07-28 release candidate](https://blog.modelcontextprotocol.io/posts/2026-07-28-release-candidate/)
- [MCP Inspector](https://github.com/modelcontextprotocol/inspector)
