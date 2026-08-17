---
title: "AI Testing Daily Brief"
date: "2026-08-17"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - OpenAI Agents SDK
  - Deterministic Testing
  - Sandbox Testing
  - Codex Security
  - Agent Security
  - MCP Testing
  - Test Automation
source: "ChatGPT + official sources + GitHub"
status: "published"
summary: "今日新增聚焦OpenAI Agents SDK JS v0.16.1/v0.16.0引入的确定性Agent测试工具、模型调用超时与沙箱网络隔离，以及Codex Security v0.1.13/v0.1.14将安全发现发布到Linear并强化审批默认值；最近24小时主流Agent测试领域出现了可直接转化为企业测试工程的实质更新。"
---

# AI Testing Daily Brief - 2026-08-17

## 1. 今日摘要

今天最值得关注的是 **OpenAI Agents SDK for JavaScript/TypeScript v0.16.1**。该版本于 2026-08-16 22:25 UTC 发布，新增模型调用超时、run-scoped sandbox 工作目录、Docker sandbox 禁用网络能力，并修复 exact call approval 决策处理；前一版本 v0.16.0 则刚引入 `ScriptedModel`、`scriptedSandboxSession()`、`ScriptedRealtimeTransport` 等 provider-neutral 确定性测试工具。对 Agent Testing 来说，这意味着“Mock LLM / Sandbox / Realtime Transport”开始成为 SDK 一等能力，而不是测试项目自行拼装。来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.1 及 https://github.com/openai/openai-agents-js/releases/tag/v0.16.0

第二个高价值新增来自 **Codex Security 0.1.13 / 0.1.14**。0.1.13 于 2026-08-16 发布，新增将完成的安全扫描结果直接发布到 Linear、持久化 finding publication association、并发批量发布和 assignee 支持；0.1.14 随后补充 reviewed scan approval defaults。相较前几日日报已经报道的“威胁建模—验证—修复”流程，这是新的治理落地点：AI Security Finding 开始进入企业 Issue/Workflow 系统。来源：https://github.com/openai/codex-security/releases/tag/npm-v0.1.13 及 https://github.com/openai/codex-security/releases/tag/npm-v0.1.14

本次已读取 2026-08-10 至 2026-08-16 最近 7 篇 `daily/ai-testing` 日报及 `data/ai-testing/topic-index.json` 完成主题去重。近 7 日已覆盖硬件 Keystore、Skill-Use、ActBench、Replay Gap、Counterfactual Oracle、Tangent、AEROBAT、多 Agent 传播风险、Tool Architecture、Runtime Contract、GitSkills、CAP-Bench、AgentProcessBench、Evo-Bench、VideoVIBE 与 A²E，因此今天没有重复进入重点栏目。

`topic-index.json` 当前仍停留在 `latest_report_date=2026-07-15`，所以继续以实际日报作为主要去重基线。WayToAGI 缓存状态为 `success`，抓取时间为 2026-08-16 09:59:02 +08:00，未超过 48 小时，本次已读取 `latest.md`，仅用于线索发现；进入正文的事实均回到 OpenAI 官方 GitHub、发布记录或官方资料核验。

## 2. 今日重点

### 1）Agent 测试开始正式进入“确定性 Harness”阶段

- **一句话总结：** v0.16.0 把模型、Sandbox、Realtime Transport 的可脚本化替身做成官方测试 API，让 Agent 流程可以在不依赖真实模型和外部环境的情况下稳定回归。
- **关注原因：** `ScriptedModel`、`scriptedSandboxSession()` 和 `ScriptedRealtimeTransport` 可以控制模型响应、Sandbox 行为与实时传输，减少 live model、WebSocket、WebRTC、真实 Sandbox 带来的随机性和成本。
- **对智能测试/测试开发的影响：** Agent 测试可以明确拆成两类：Harness/Workflow 的确定性结构测试，以及真实模型下的行为评测。前者适合 CI 高频运行，后者用于模型升级、Prompt/Skill/Harness 变更后的抽样或专项回归。
- **建议动作：** 将现有一条 Agent E2E 拆为两层：先用 ScriptedModel 固定工具选择与返回顺序，验证 Retry、Handoff、Tool Error、Max Turn、Approval；再用真实模型只验证策略与语义能力。

来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.0

### 2）Sandbox 测试需要把“网络是否存在”变成显式配置

- **一句话总结：** v0.16.1 新增 Docker Sandbox 禁网、run-scoped 工作目录与模型调用超时，说明 Agent 测试环境正在从“有一个 Sandbox”升级成“Sandbox 边界本身可配置、可回归”。
- **关注原因：** Agent 一旦能够访问网络、共享目录或长期工作区，测试结果可能受到外部状态、残留文件和第三方服务影响。新的 run-scoped sandbox path 可以降低跨运行污染；禁用网络则允许构造真正离线的安全评测；模型调用 timeout 则让卡死与超时成为可判定失败。
- **对智能测试/测试开发的影响：** Sandbox 回归建议固定记录 `filesystem_scope / network_enabled / timeout / workspace_id / persisted_state`。同一 Agent 在有网与无网、共享目录与 run-scoped 目录下表现不同，应被视为环境差异，而不是模型随机性。
- **建议动作：** 对一个 Browser/MCP/Code Agent 各跑一组 `network=on/off`，同时验证 DNS、HTTP、包管理器、外部 API 与本地文件访问；命中非授权外联直接判安全失败。

来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.1

### 3）AI 安全扫描开始进入“发现发布链路”测试

- **一句话总结：** Codex Security 0.1.13 将扫描结果发布到 Linear，并持久化发布关联；0.1.14 又补充 reviewed scan approval defaults，安全 Agent 的测试对象已经从扫描准确率扩展到 Finding 生命周期。
- **关注原因：** 当安全 Agent 可以自动创建 Issue、分配责任人并批量发布 Findings 后，错误不再只是一个误报文本，而可能变成错误工单、重复工单、错误负责人、错误状态流转或审批绕过。
- **对智能测试/测试开发的影响：** Security Agent 测试应新增 `Finding → Publication → Issue → Assignee → Status → Re-scan` 端到端链路；必须验证幂等、重复发布、批量失败恢复、权限、审批与关联持久化。
- **建议动作：** 构造同一 Finding 重复发布两次、部分批量失败、Finding 已关闭后重扫、无 Linear 权限、错误 assignee 五类 Case，确保不会形成重复工单或静默丢失。

来源：https://github.com/openai/codex-security/releases/tag/npm-v0.1.13

## 3. 行业新闻

### 1. OpenAI Agents SDK JS v0.16.1 发布

- **摘要：** 新增模型调用超时、run-scoped sandbox 工作目录、Docker sandbox 禁网、Modal sandbox 资源选项，并修复 exact call approval 决策处理。
- **影响：** Agent Runtime 的网络、目录、超时和审批行为可以成为直接可配置、可自动化验证的测试维度。
- **发布时间：** 2026-08-16 22:25 UTC
- **来源：** OpenAI 官方 GitHub
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是，Agent 测试平台与 Sandbox 团队建议立即关注。

来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.1

### 2. OpenAI Agents SDK JS v0.16.0 引入确定性测试工具

- **摘要：** 新增 `ScriptedModel`、`scriptedSandboxSession()` 与 `ScriptedRealtimeTransport`，支持不依赖 live model / sandbox / WebRTC / WebSocket 的确定性测试。
- **影响：** Agent Harness 单元和集成测试可以更接近传统软件测试，降低随机模型与真实环境对 CI 的干扰。
- **发布时间：** 2026-08-15 03:07 UTC
- **来源：** OpenAI 官方 GitHub
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是。

来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.0

### 3. Codex Security 0.1.13 / 0.1.14 发布

- **摘要：** 0.1.13 新增安全扫描结果直接发布到 Linear、publication association、批量发布和 assignee 支持；0.1.14 调整 reviewed scan approval defaults。
- **影响：** AI Security Testing 需要覆盖安全 Finding 的下游发布、审批、幂等和状态生命周期。
- **发布时间：** 2026-08-16
- **来源：** OpenAI 官方 GitHub
- **重要程度：** 高
- **热度：** 中
- **是否建议立即学习：** DevSecOps / Security Agent 团队建议关注。

来源：https://github.com/openai/codex-security/releases/tag/npm-v0.1.13

**今日暂无更多经原始官方来源核验、且与近 7 日归档不重复的高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| OpenAI Agents SDK JS v0.16.1 | Sandbox 与 Runtime 强化 | 模型 timeout、run-scoped working directory、Docker 禁网、exact approval 修复 | 可直接测试超时、目录隔离、网络边界、审批一致性 |
| OpenAI Agents SDK JS v0.16.0 | 新增官方测试工具 | ScriptedModel、ScriptedSandbox、ScriptedRealtimeTransport | 支持 Agent Harness 的确定性 CI 回归 |
| Codex Security 0.1.13 / 0.1.14 | Finding 发布与审批治理 | Linear 发布、批量持久化、assignee、reviewed approval defaults | 覆盖扫描→发布→工单→审批全链路 |

## 5. Agent Ecosystem

### Deterministic Agent Testing

Agent 测试可以明确区分：

`Deterministic Harness Test → Live Model Evaluation → Production Shadow / Canary`

第一层验证 Workflow、State、Tool、Retry、Approval 和异常处理；第二层才验证模型能力与概率行为。

### Sandbox Boundary as Test Metadata

以后 Agent Trace 最好把以下环境字段当作一等元数据：

`network / workspace_scope / timeout / persistence / filesystem_mount / credential_scope`

否则两个看似相同的测试，实际运行边界可能完全不同。

### Finding Lifecycle Testing

Security Agent 不再只产出一个 Finding；当它开始自动发布到 Issue Tracker 后，真正的测试链路变成：

`Detection → Validation → Publication → Assignment → Remediation → Re-scan → Closure`

## 6. 开源推荐：OpenAI Agents SDK JavaScript/TypeScript

- **项目：** `openai/openai-agents-js`
- **GitHub：** https://github.com/openai/openai-agents-js
- **Star：** 3,625，2026-08-17 通过 GitHub API 核验
- **License：** MIT
- **核心能力：** Multi-Agent、Tool、Guardrail、Handoff、MCP、Realtime、Sandbox Agent、Tracing，以及 v0.16.0 新增的确定性测试工具
- **推荐指数：** 4.8 / 5
- **推荐理由：** 今天的版本直接补齐了 Agent 测试开发长期缺失的一块：无需真实模型即可测试 Runner / Sandbox / Realtime 的结构性逻辑，同时 v0.16.1 又把网络隔离、工作目录和 timeout 变成可控边界，特别适合作为 Agent Testing Lab 的参考实现。

来源：https://github.com/openai/openai-agents-js

## 7. 企业实践

### OpenAI：将 Agent SDK 的“可测试性”下沉到框架层

**企业：** OpenAI

**做法**

- 提供 provider-neutral ScriptedModel，避免 CI 必须调用真实模型；
- 提供 scripted Sandbox 与 Realtime Transport，隔离外部环境依赖；
- 将模型 timeout 纳入 Runtime；
- 将 Sandbox 工作目录改为 run-scoped；
- Docker Sandbox 可显式关闭网络；
- Tool Approval 使用 exact decision 语义。

**效果**

官方发布说明没有给出故障率、成本下降或测试稳定性等量化数据，因此不对生产收益做外推。工程层面的直接收益是，Runner、Sandbox、Realtime、Approval 等路径可以更稳定地进入自动化 CI。

**可借鉴点**

企业自研 Agent 平台不应把“模型输出不可控”当成无法写自动化测试的理由。模型层可以脚本化，Harness 与 Tool 层应尽量做到完全确定性验证，再把概率测试留给真正需要模型能力的环节。

## 8. 今日工具推荐：`@openai/agents/testing`

### 适用场景

- Agent Runner 单元/集成测试；
- Tool Calling 与 Handoff 回归；
- Retry、Max Turn、Approval 测试；
- Sandbox 状态变化；
- Realtime Agent 测试；
- CI 中避免 live model 依赖。

### 快速开始

```bash
npm install @openai/agents
```

第一轮建议只选一条简单 Agent 流程，使用官方 testing utilities 构造固定响应：

1. 第一次模型返回 Tool Call；
2. Tool 返回固定结果；
3. 第二次模型返回 Final Answer；
4. 断言 Tool 名称、参数、调用次数与最终状态；
5. 再加入 Tool Error、Timeout、Approval Reject、Max Turn 四组失败 Case。

随后才用真实模型跑同一业务 Case，比较行为层差异。

来源：https://github.com/openai/openai-agents-js/releases/tag/v0.16.0

## 9. 今日学习：为什么 Agent 测试要同时保留 Scripted Model 和 Live Model？

Scripted Model 解决的是**软件逻辑是否正确**：状态机、Tool 参数传递、异常处理、Retry、Handoff、审批和持久化都可以稳定复现。

Live Model 解决的是**智能行为是否可靠**：模型会不会选错 Tool、漏步骤、误解上下文或越权。

如果所有测试都用真实模型，CI 会混入随机性、成本和模型版本漂移；如果所有测试都用 Scripted Model，又看不到真正的 Agent 行为风险。

更合理的结构是：

> 大量确定性 Harness 测试 + 少量高价值 Live Model 回归 + 独立安全/行为 Benchmark。

## 10. 趋势观察

**未来 3 个月，Agent Testing 会更明显地回归传统软件工程：模型层被脚本化，Harness/Tool/Sandbox 大量走确定性 CI；真实模型则集中用于行为、策略和安全评测。与此同时，Security Agent 的验收边界会从“Finding 是否正确”扩展到 Finding 在企业工作流中的完整生命周期。**

## 11. 30 分钟 Action

### 给一个现有 Agent 建立第一条“无真实模型”回归

1. 选一条包含 1—2 个 Tool 的稳定流程。
2. 固定模型响应：Tool Call → Tool Result → Final Answer。
3. 验证 Tool 名称、参数与调用顺序。
4. 注入 Tool 500 / Timeout。
5. 注入 Approval Reject。
6. 验证 Max Turn 与错误结束状态。
7. 再跑一次真实模型。
8. 把差异归类为 `Harness Bug / Model Behavior / Tool Bug`。

建议记录：

```text
model_mode: scripted | live
harness_version
tool_schema_version
sandbox_network
workspace_scope
expected_trace
actual_trace
business_oracle
```

## 12. 值得跟进

- `@openai/agents/testing` API 是否继续扩展到更多 Agent Runtime 部件；
- ScriptedModel 能否稳定模拟 streaming / parallel tool calling；
- run-scoped sandbox path 对并发测试隔离的实际效果；
- Docker sandbox 禁网是否涵盖 DNS、代理和 package registry；
- 模型调用 timeout 与 Retry 策略的组合行为；
- exact call approval 的 Trace 和审计字段；
- Codex Security Finding → Linear 的幂等与批量失败恢复；
- Codex Security publication association 是否支持后续 Jira/GitHub Issues 等适配；
- MCP Tool 在无网络 Sandbox 中的降级行为；
- Browser Agent 在 network-off 环境中的资源加载与故障诊断；
- 知识图谱节点：`Deterministic Agent Testing`、`ScriptedModel`、`Sandbox Boundary Testing`、`Run-Scoped Workspace`、`Finding Lifecycle`、`Security Finding Publication`。

## 13. 我的备注

今天这个更新非常适合智能测试平台，因为它把一个长期困扰 Agent 自动化的问题拆清楚了：**不是所有 Agent 测试都必须调用真实模型。**

金融测试中的清算核对 Agent，可以先用 Scripted Model 固定：

`查汇总 → 查明细 → 计算 → 回读状态`

这样状态机、参数传递、异常处理、重复调用和幂等都能做成稳定 CI。只有“Agent 是否根据复杂上下文正确选择下一步”这一部分才需要真实模型。

Browser Agent 同样可以分层：页面操作框架、Session、截图、Network Trace、Retry 都可以做确定性测试；Vision/DOM 理解与策略选择再交给真实模型评估。

MCP Server 准入可以增加 Sandbox 环境矩阵：

`network on/off × read/write × temp workspace/persistent workspace × short/long timeout`

这样可以很快发现 Server 是否隐式依赖公网、共享磁盘或长期状态。

安全日志 AI 精筛也适合分两层。上下文拼装、规则映射、结果回调、状态聚合和异常处理全部使用固定模型响应测试；真实模型只负责验证误判降低能力。这样模型效果回归和工程并发/状态问题不会再混成一个问题。

测试计划先行可以增加一个字段：

> **本条用例是在验证 Harness，还是在验证 Model？**

如果是在验证 Harness，就尽量脚本化模型；如果是在验证 Model，就冻结 Harness、Tool、Sandbox 和 Oracle。这样测试结果的归因会清楚很多。

## 📦 GitHub Archive

Repository: JocobZling/knowledge-graph-platform
File: daily/ai-testing/2026/08/2026-08-17-ai-testing.md
Status: 已上传
Commit: <commit-sha>
Commit Message: docs(ai-testing): add daily brief for 2026-08-17
