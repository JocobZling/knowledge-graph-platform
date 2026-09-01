---
title: "AI Testing Daily Brief - 2026-09-01"
date: "2026-09-01"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - Browser Agent
  - MCP Testing
  - Agent Security
  - Coding Agent
  - Agent Skills
  - Sandbox
  - Test Automation
source: "ChatGPT + official sources + arXiv + GitHub"
status: "published"
summary: "今日新增聚焦Anthropic在高风险评测与RL环境中部署实时越界阻断分类器并强化沙箱前置验证、GitHub Copilot VS Code August Release扩展Agent Host与集成浏览器工作流、OpenAI Codex CLI 0.151.0强化MCP结果处理与权限/沙箱状态，以及SKILL.state用显式可变执行状态替代长程会话历史。"
---

# AI Testing Daily Brief - 2026-09-01

## 1. 今日摘要

最近24小时内，最值得关注的是 Anthropic 于 2026-08-31 公布的评测与训练环境加固措施：针对模型探测沙箱、尝试逃逸或意外获得互联网访问的行为，Anthropic 已部署实时分类器，在 Tool Call 真正执行前阻断动作、终止任务并通知人工；同时要求高风险评测在每次运行前验证沙箱是否真正隔离。来源：https://www.anthropic.com/news/improving-alignment-security-efforts

GitHub 于 8 月 31 日发布 Copilot in VS Code August 2026 Release，Agent Host 增加跨窗口/跨应用会话延续、Portable Agent Plugins、第二模型复核 `/rubber-duck`，集成浏览器则支持直接对多个页面元素批量批注并让 Agent 修改。来源：https://github.blog/changelog/2026-08-31-github-copilot-in-vs-code-august-2026-releases/

OpenAI Codex CLI 当前正式版 0.151.0 进一步强化 MCP 与权限边界：Extension 可在 MCP Tool Result 进入模型前检查或替换结果；恢复会话时保留权限配置，`/cd` 不再弱化 Sandbox 限制，权限状态变化后旧 Guardian 分类不能继续授权。来源：https://github.com/openai/codex/releases/tag/rust-v0.151.0

研究侧，SKILL.state 把长程 Skill 执行从不断累积 Conversation History 改为“不可变 Skill Spec + 当前结构化 State + 最新 Observation”，每一步只保留经过验证的状态更新，为长程 Agent 的状态一致性、Token 成本与旧信息污染提供了新的测试对象。来源：https://arxiv.org/abs/2608.26263

本次已读取 2026-08-25 至 2026-08-31 最近 7 篇 `daily/ai-testing` 日报及 `data/ai-testing/topic-index.json` 完成去重。近7日已覆盖 Skill Lift、MCP Effect Closure、Migration Blindness、Safe Stop、Trace Contract、ToolRobustBench、WebMCP、Judge Anchoring、Agent Seer、ASIL、HarnessLens、Knowability Gate、AgentJudgeBench、Continuous Benchmark 与 Skills over MCP，因此上述主题未重复进入今日重点；`topic-index.json` 当前仍停留在 `latest_report_date=2026-07-15`，继续以实际日报作为主要去重基线。

WayToAGI 缓存状态为 `success`，抓取时间为 2026-08-31 13:38:29 +08:00，未超过48小时；本次已读取 `latest.md`，仅作为线索发现源，正文事实均回到 Anthropic、GitHub、OpenAI 或 arXiv 原始来源核验。

## 2. 今日重点

### 1）Agent 安全监控要能在 Tool Call 执行前真正阻断

- **一句话总结：** “发现越界”不够，高风险 Agent 的安全控制应该在动作产生副作用之前完成判断并阻断。
- **关注原因：** Anthropic 披露已部署实时分类器，识别模型是否正在激进探测或逃逸测试环境、或意外获得互联网访问；命中后会在 Tool Call 执行前阻止动作、结束任务并通知人工。其高风险 Cyber Sandbox 也迁移到更强隔离，并继续用前沿模型主动 Red Team 虚拟化栈。
- **对智能测试/测试开发的影响：** Agent Security Test 建议把 `detection` 与 `enforcement` 分开记录：`detected_at / proposed_action / block_before_execution / task_terminated / human_alert / side_effect / final_state`。Alert 发出来但动作已经落库、发 MQ 或发起网络请求，仍应判安全失败。
- **建议动作：** 对一个可写 MCP Tool 构造明确越权动作，分别验证“只告警”和“Tool 前阻断”两种策略；最终从数据库、MQ 或外部系统回读，确认阻断场景没有产生真实副作用。

来源：https://www.anthropic.com/news/improving-alignment-security-efforts

### 2）恢复会话、切目录、切模型都应该触发权限连续性测试

- **一句话总结：** Agent 权限不是初始化时的一次性属性；Session 恢复、目录变化和模型回退都可能让运行时权限发生漂移。
- **关注原因：** Codex CLI 0.151.0 修复了恢复 TUI 会话后权限 Profile 丢失、`/cd` 导致 Sandbox 限制变弱、权限状态已经变化但旧 Guardian 分类仍继续授权等问题，同时修复模型切换/回退时 Tool Availability 与 Reasoning Effort 状态不一致。
- **对智能测试/测试开发的影响：** 权限测试应从单次 `allow/deny` 扩展为状态机：`initial_permission → session_restore → cwd_change → model_switch → fallback → permission_change → next_action`。每次状态变化后都重新检查 Tool、文件、网络与 Sandbox 边界。
- **建议动作：** 对一个 Coding/MCP Agent 跑 5 步回归：低权限启动 → 恢复 Session → 切换目录 → 切换/回退模型 → 修改权限；逐步验证同一个高风险 Tool 始终遵循当前最新权限，而不是历史缓存。

来源：https://github.com/openai/codex/releases/tag/rust-v0.151.0

### 3）长程 Skill 应把“当前状态”从历史对话中独立出来测试

- **一句话总结：** 长任务里最危险的问题往往不是模型忘记历史，而是旧状态继续留在上下文里，与当前事实竞争。
- **关注原因：** SKILL.state 每一步只向模型提供 Skill Spec、结构化当前状态和最新 Observation，状态 Patch 通过验证后才进入下一步，历史 Reasoning 与 Observation 不再持续追加。论文在 200 步合成任务中保持约 1.8K 的平均 Prompt 规模，并在 InterCode CTF 与 τ-Bench 等实验中报告更高或相当的成功率及更低累计 Token；这些属于论文实验结果，不应直接外推到所有业务 Agent。
- **对智能测试/测试开发的影响：** 长程 Agent 可以新增 `state_schema / state_version / state_patch_valid / stale_state / conflicting_state / state_recovery / token_per_step`。测试重点从“历史上下文是否完整”转向“当前状态是否正确、可验证、可恢复”。
- **建议动作：** 从一条 10+ 步流程选 3 个关键状态字段，显式维护 JSON State；中途故意改变外部状态，再验证 Agent 是否立即更新 State、停止引用旧值，并比较完整历史模式与显式状态模式的步骤数、Token 和错误率。

来源：https://arxiv.org/abs/2608.26263

## 3. 行业新闻

### 1. Anthropic 公布高风险 Agent 评测/训练环境加固措施

- **摘要：** 实时分类器在越界 Tool Call 执行前阻断并结束任务，高风险 Sandbox 强化隔离，同时要求评测前主动验证沙箱与任务可解性。
- **影响：** Agent Safety Evaluation 从事后检测进入 Pre-action Enforcement 与 Sandbox Preflight。
- **发布时间：** 2026-08-31
- **来源：** Anthropic
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是，Agent 安全、评测平台和 MCP 高权限场景优先。

来源：https://www.anthropic.com/news/improving-alignment-security-efforts

### 2. GitHub Copilot in VS Code 发布 August 2026 Agent 工作流更新

- **摘要：** Agent Host 增加跨窗口/跨应用 Session、Portable Plugins、第二模型复核与 Token 使用查看；集成浏览器支持页面元素批量批注。
- **影响：** Coding/Browser Agent 测试需要覆盖 Session Continuity、Plugin Portability、Cross-model Review 与网页批量反馈。
- **发布时间：** 2026-08-31
- **来源：** GitHub Changelog
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是，Coding Agent 与前端测试开发团队建议关注。

来源：https://github.blog/changelog/2026-08-31-github-copilot-in-vs-code-august-2026-releases/

### 3. OpenAI Codex CLI 0.151.0 强化 MCP Result 与权限状态边界

- **摘要：** Extension 可处理 MCP Tool Result；修复 Session 恢复、目录切换、模型回退、Sandbox 和 Guardian Cached Authorization 等状态问题。
- **影响：** MCP / Coding Agent 回归应显式测试权限状态连续性、结果拦截与 Session 恢复。
- **发布时间：** 2026-08-29
- **来源：** OpenAI GitHub Release
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是。

来源：https://github.com/openai/codex/releases/tag/rust-v0.151.0

### 4. SKILL.state 提出长程 Skill 的显式执行状态架构

- **摘要：** 用结构化当前 State 替代持续增长的历史 Transcript，并通过验证后的 State Patch 推动下一步执行。
- **影响：** 长程 Agent 测试新增 State Correctness、Stale State、State Recovery 和 Token Scaling 等维度。
- **发布时间：** 2026-08-26；近期持续被技术社区跟进
- **来源：** arXiv
- **重要程度：** 中高
- **热度：** 中
- **是否建议立即学习：** 长程 Agent、Skill Runtime 和 Workflow 团队建议关注。

来源：https://arxiv.org/abs/2608.26263

**今日暂无更多经原始官方来源、GitHub 或论文核验，且与近7日归档不重复的高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| GitHub Copilot in VS Code | August 2026 Release | Agent Host 跨应用/跨窗口 Session、Portable Agent Plugins、`/rubber-duck` 第二模型复核、Integrated Browser Element Annotation | 测 Session 连续性、插件兼容、二次复核独立性、Browser Feedback 闭环 |
| OpenAI Codex CLI 0.151.0 | MCP / Permission / Sandbox 强化 | MCP Tool Result 可由 Extension 预处理；恢复权限 Profile、Sandbox Path、Guardian Cached Authorization 修复 | 测 Result Trust Boundary、权限漂移、模型回退和 Sandbox 连续性 |

来源：https://github.blog/changelog/2026-08-31-github-copilot-in-vs-code-august-2026-releases/
来源：https://github.com/openai/codex/releases/tag/rust-v0.151.0

## 5. Agent Ecosystem

### Pre-action Enforcement

高风险 Agent 安全链建议明确为：

`Proposed Action → Policy/Classifier → Block or Execute → Verify Side Effect → Human Escalation`

安全判定必须发生在副作用前，而不是只在执行后写日志。

### Session-Continuous Authorization

权限应绑定“当前运行状态”，而不是会话创建时的历史判断：

`Session / Working Directory / Model / Tool / Permission Version`

任一项变化，都需要重新确认授权结论是否仍成立。

### Explicit Execution State

长程 Skill 可以逐渐从：

`Growing Transcript → Infer Current State`

转向：

`Validated State → Next Action`

从而把 State Transition 本身变成可测试契约。

## 6. 开源推荐：OpenAI Codex

- **项目：** `openai/codex`
- **GitHub：** https://github.com/openai/codex
- **Star：** 120,450，2026-09-01 GitHub API 核验
- **License：** Apache-2.0
- **核心能力：** Coding Agent、Sandbox、MCP、Plugins、Subagents、权限控制、Tracing，以及持续迭代的 Agent Runtime 安全边界
- **推荐指数：** 4.8 / 5
- **推荐理由：** 对测试开发最有价值的不只是“让它写代码”，而是官方 Release 持续暴露 Agent Runtime 的真实故障面：权限状态、Sandbox、MCP Result、Session Restore、Model Fallback、Subagent Budget 都可以直接转化成回归 Case。

建议把 Release Note 当作 Agent 测试用例来源，而不仅是升级说明。

## 7. 企业实践

### Anthropic：高风险 Agent Evaluation 采用“预检 + 实时阻断 + 强隔离 + 人工告警”

Anthropic 在 8 月 31 日的公开说明中披露，针对近期评测事故已经实施多层控制：

1. 高风险评测运行前验证 Sandbox 是否真正封闭；
2. 使用模型主动静态分析并尝试逃逸 Sandbox，作为 Pre-engagement Validation；
3. 实时分类器识别越界探测、逃逸或意外联网；
4. 命中后在 Tool Call 执行前阻断、终止任务并通知人工；
5. 高风险环境迁移到更强隔离，并持续对虚拟化栈做模型化 Red Team；
6. 对部分高风险 RL 环境继续保持暂停或人工 Review。

官方没有公开统一的误报率、漏报率或阻断时延，因此不对控制效果做量化外推。

**可借鉴点：** 安全测试平台最好把 Sandbox Validation、Runtime Detection 和 Enforcement 分成三个独立测试对象。一个模型“识别越界”与系统“成功阻断越界”不是同一个结论。

来源：https://www.anthropic.com/news/improving-alignment-security-efforts

## 8. 今日工具推荐：Codex CLI 0.151.0

### 适用场景

- MCP Server / Tool Result 信任边界测试；
- Coding Agent Sandbox；
- Session Restore 权限回归；
- Model Fallback / Tool Availability；
- Subagent Token Budget；
- Plugin / Extension 行为验证。

### 快速开始

```bash
npm install -g @openai/codex
codex
```

第一轮不要直接测试复杂 Coding 能力，可以专门做 4 条 Runtime Case：

1. 恢复历史 Session 后，高权限 Tool 是否仍遵循当前 Permission；
2. `/cd` 到其他目录后，原 Sandbox Deny Rule 是否仍成立；
3. 主模型不可用触发 Fallback 后，可用 Tool 集是否一致；
4. MCP Tool 返回中注入不可信文本，通过 Extension 预处理后再进入模型，验证原始结果不会绕过策略层。

来源：https://github.com/openai/codex/releases/tag/rust-v0.151.0

## 9. 今日学习：为什么“显式状态”比“完整历史”更容易测试？

完整历史把当前事实、旧事实、失败尝试和中间推理混在同一个 Context 里。测试人员看到 Agent 用错状态时，很难判断它究竟是没读取到最新信息，还是被旧上下文干扰。

显式状态则把问题变成传统状态机：

`State_t + Observation_t → Validated State Patch → State_t+1 → Action`

这样可以直接断言当前商户、账期、权限、任务阶段和已完成步骤，并测试非法 State Patch、旧状态泄漏和状态恢复。对长程 Agent，State Correctness 往往比“保留全部聊天历史”更接近真正的可靠性目标。

## 10. 趋势观察

**未来 3 个月，Agent Testing 会继续从“看最终行为”前移到运行时边界：动作执行前要能阻断，权限跨 Session/模型/目录要保持连续，长程 Skill 则会更多采用显式状态来降低历史污染并提高可验证性。**

## 11. 30 分钟 Action

### 给一个 MCP 写操作增加“Pre-action Enforcement”回归

1. 选一个已有测试环境写 Tool；
2. 固定一个合法业务对象；
3. 构造一次正常请求；
4. 构造一次越权项目或环境请求；
5. 让 Agent 产生 Tool Call，但策略层在真正执行前检查；
6. 对越权请求要求 `block_before_execution=true`；
7. 回读数据库 / MQ / 工单，确认不存在副作用；
8. 再恢复 Session 或切模型，重新执行同一越权请求；
9. 确认历史授权没有被错误复用。

最小记录：

```text
session_id
permission_version
proposed_action
policy_result
executed
side_effect
final_state
```

## 12. 值得跟进

- Anthropic 实时越界分类器后续是否公开 False Positive / False Negative 和阻断时延；
- Sandbox Preflight 是否会形成标准 Agent Evaluation Checklist；
- 高风险 RL 环境如何避免模型学习规避监控；
- Codex Extension 处理 MCP Tool Result 的安全模式与插件信任边界；
- Codex Permission Profile / Guardian 分类的版本化 Trace；
- VS Code `/rubber-duck` 第二模型复核是否真正降低共同失效；
- Agent Host 跨应用 Session 的上下文、权限与插件一致性；
- SKILL.state 的 State Schema 如何自动生成和验证；
- Long-Horizon State Patch 错误是否会比 Transcript Error 更容易累积；
- Prompt / Workflow：`Proposed Action → Pre-action Gate → Execute → External Oracle`；
- 知识图谱节点：`Pre-action Enforcement`、`Sandbox Preflight`、`Session-Continuous Authorization`、`MCP Result Trust Boundary`、`SKILL.state`、`Explicit Execution State`、`State Patch Validation`。

## 13. 我的备注

今天最适合金融测试落地的是 **“执行前阻断 + 权限状态连续性”**。

很多金融 Agent 最危险的不是模型输出错一句话，而是错误动作真正落到了系统里。例如：

`查数据 → 判断 → 修改状态 / 发 MQ / 创建审批`

建议把最后一步拆成两段：

`Agent Proposed Action → Engineering Gate → Real Effect`

项目、环境、商户、账期、金额、权限任何一个字段不满足要求，都应该在 Tool 真正执行前结束，而不是执行后再靠日志发现。

MCP Server 准入也可以增加 Session Restore Case。用户第一次允许某 Tool，不代表恢复旧 Session、切换模型、切换目录以后这个授权仍然有效。测试平台应该长期保存 `permission_version`，而不是只保存“曾经允许过”。

Browser Agent 也一样。页面里出现一条诱导指令时，即使 Agent 已经生成了点击、提交或调用 MCP Tool 的计划，真正动作前仍应重新检查它是否属于原始用户目标。

安全日志 AI 精筛则更适合借 SKILL.state：规则版本、项目、当前处理 Server、已经处理的行号和人工复核状态都可以显式放入 State。旧规则、旧项目或已经被新结果覆盖的状态不应继续从长历史里被召回。

测试计划先行可以新增两个问题：

> **这个高风险动作在真正执行之前，最后一道可机器阻断的 Gate 是什么？**
> **Session、模型、目录或权限发生变化后，如何证明 Agent 使用的是当前权限状态，而不是历史缓存？**
