---
title: "AI Testing Daily Brief - 2026-09-18"
date: "2026-09-18"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - Browser Agent
  - MCP Testing
  - Agent Security
  - Human Approval
  - Agent Skill Security
  - Test Automation
source: "ChatGPT + official sources + GitHub + WayToAGI clues"
status: "published"
summary: "今日新增聚焦iProov发布HAPS实验规范，把高风险Agent动作的人类批准从界面确认升级为可验证且绑定具体动作的证据；Snyk披露Evo在企业生产中的Agent Skill预注册扫描、持续行为检查与确定性独立验证实践。核心建议是把approval与permission分离、为高风险Tool建立action-bound approval，并把Skill/MCP供应链准入从上线前扫描扩展为持续验证。"
---

# AI Testing Daily Brief - 2026-09-18

## 1. 今日摘要

最近24小时最值得智能测试关注的是iProov于9月17日发布Human Approval and Presence Specification（HAPS）。它针对一个此前常被混在一起的问题给出明确拆分：Agent“有权限执行”不等于“人类批准了这一次具体执行”。HAPS要求高风险动作暂停、向人展示准确动作、取得真实人在场与批准证据，并把批准与具体动作安全绑定后再执行。规范以Apache-2.0开放，并提供部分Rust参考实现和测试向量。来源：https://www.iproov.com/press/haps-specification-human-approval-ai-agent-actions

第二个高价值新增来自Snyk 9月17日披露的Evo生产实践。Snyk称其平台当前每月处理240万次Agent供应链扫描、每天进行420万次Agent行为检查；其中一家美国大型银行正在集中管理约1,000个内部Agent Skill，供约50,000名开发者使用Claude Code，并在Skill进入Registry前执行风险评估、上线后持续扫描。上述规模均为Snyk官方披露，未独立审计。来源：https://snyk.io/news/agentic-ai-security-moves-into-production/

本次已读取2026-09-11至2026-09-17最近7篇日报及`data/ai-testing/topic-index.json`完成去重。近7日已覆盖Harness Versioning、MCP Business Policy、Cross-session Risk、Action Trace、Tenant Isolation、Project-level Agent State、Model Alias Drift、CI-as-Agent-Tool、Router Policy、Financial MCP Entitlement、Continuous Adversarial Testing、Out-of-model Enforcement、Misalignment Incident与Agent Circuit Breaker，因此今天只保留新的`Action-bound Human Approval`和`Agent Skill Registry Continuous Assurance`。`topic-index.json`仍停留在2026-07-15，继续以实际日报作为主要去重基线。

WayToAGI缓存状态为success，抓取时间2026-09-17 12:55:42 +08:00，未超过48小时；本期已读取`latest.md`，仅用于线索发现，正文事实均回到官方来源或GitHub核验。

## 2. 今日重点

### 1）高风险Agent动作要区分Permission与Approval

- **一句话总结：** 用户给Agent某个Tool权限，只证明它“可以调用”，不能证明用户批准了当前这一次具体副作用。
- **关注原因：** HAPS把高风险动作设计成`pause → disclose exact action → prove human presence/approval → bind approval to action → verify → execute`，并明确不建议所有动作都人工确认，以避免Approval Fatigue。
- **对智能测试/测试开发的影响：** MCP/Agent准入建议新增`permission_scope / approval_required / approval_subject / approved_action_digest / approval_time / approval_expiry / execution_action_digest`，重点验证批准内容与最终执行内容完全一致。
- **建议动作：** 对一个测试写Tool做TOCTOU Case：用户批准“转账100元给A”后，在批准与执行之间把参数改成“1000元给B”。即使Agent仍有转账权限，执行层也必须因Action Digest不一致而拒绝。

来源：https://www.iproov.com/press/haps-specification-human-approval-ai-agent-actions

### 2）Agent Skill Registry需要“准入前扫描 + 上线后持续验证”

- **一句话总结：** Skill一旦成为组织级共享资产，安全测试对象就从一个文件升级为供应链生命周期。
- **关注原因：** Snyk披露一家美国大型银行正在集中约1,000个内部Agent Skill供约50,000名开发者使用，并在Registry前执行风险评估、之后持续扫描；Snyk同时强调Coding Agent会动态拉取第三方MCP Server与Skill，传统只扫描代码Artifact的方式覆盖不了运行时Toolchain。
- **对智能测试/测试开发的影响：** Skill准入建议记录`skill_id / source / version / owner / tool_scope / risk_score / pre_registry_result / dependency_digest / continuous_scan_result / revoked_at`，任何Skill、依赖、Tool描述或权限变化都应触发重新评估。
- **建议动作：** 先选5个内部Prompt/Skill做最小Registry，加入版本Hash、Owner、允许Tool、禁止数据域和准入结果；修改其中一个Skill的Tool Scope，验证平台能否自动使旧准入结果失效。

来源：https://snyk.io/news/agentic-ai-security-moves-into-production/

## 3. 行业新闻

### iProov发布HAPS实验规范
- **摘要：** HAPS为高风险Agent动作定义可验证的人类批准流程，把批准证据与具体动作绑定，并提供Apache-2.0规范、部分Rust实现和测试向量。
- **影响：** Agent治理可从“弹确认框”升级为可验证、可审计、可测试的Approval Protocol。
- **发布时间：** 2026-09-17
- **来源：** iProov
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是，金融MCP、Agent写操作与安全治理优先。

### Snyk披露Evo Agent安全生产规模与银行Skill Registry实践
- **摘要：** Snyk称Evo已在生产中执行大规模Agent供应链扫描与行为检查，并披露大型银行约1,000个内部Skill的预注册评估与持续扫描实践。
- **影响：** Agent Skill安全开始从静态审查进入Registry治理和Continuous Assurance。
- **发布时间：** 2026-09-17
- **来源：** Snyk
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是，企业Agent平台/测试平台建议关注。

### GitHub Copilot Impact Dashboard增加Feature Engagement
- **摘要：** 企业管理员现在可以查看关键Copilot功能的28天活跃使用情况，组织/企业报告API同步提供该拆分。
- **影响：** 对企业AI工程治理有价值，但更偏采用度度量，不属于核心Agent测试能力更新。
- **发布时间：** 2026-09-17
- **来源：** GitHub
- **重要程度：** 中
- **热度：** 中
- **是否建议立即学习：** 否，可持续观察。

来源：https://github.blog/changelog/2026-09-17-copilot-impact-dashboard-now-shows-feature-engagement/

今日暂无更多与近7日归档不重复的高价值新增。

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| iProov HAPS | Experimental Specification | 可验证Human Presence/Approval并与具体Agent Action绑定 | Approval Replay、参数篡改、过期批准、TOCTOU |
| Snyk Evo | 企业生产实践扩展 | Skill预注册评估、持续供应链扫描、Agent行为检查 | Skill Registry准入、运行时Toolchain治理、独立验证 |
| GitHub Copilot Impact Dashboard | Feature Engagement | 28天功能使用拆分及API输出 | 采用度/治理指标，直接测试价值有限 |

今日未发现与近7日不重复、且测试价值足够高的OpenAI、Anthropic、Playwright、Cursor或Codex正式产品更新，不以旧Release补数。

## 5. Agent Ecosystem

1. **Action-bound Human Approval**：`Agent Proposal → Risk Gate → Human Approval Evidence → Action Binding → Server Verification → Execute`。
2. **Agent Skill Registry Continuous Assurance**：`Skill Submit → Pre-registry Scan → Approve → Runtime Use → Continuous Scan → Revoke/Re-evaluate`。

今天没有第三条足够高新颖度的Agent Ecosystem新增，不补旧内容。

## 6. 开源推荐：MCPMark

- **项目：** `eval-sys/mcpmark`
- **GitHub：** https://github.com/eval-sys/mcpmark
- **Star：** 461（2026-09-18 GitHub API核验）
- **License：** Apache-2.0
- **核心能力：** 在Notion、GitHub、Filesystem、Postgres、Playwright等真实MCP环境中做Agent Tool-use压力评测；提供隔离Sandbox、版本固定环境、自动恢复、统一指标和Verifier。
- **推荐指数：** 4.5/5
- **推荐理由：** 它不是今日新项目，但很适合补齐“真实MCP任务 + 可复现环境 + 独立Verifier”这一准入层。当前Verified任务集明确固定环境版本并稳定Verifier，旧任务成绩不再直接可比，这一点本身就是企业MCP Benchmark治理值得借鉴的设计。

## 7. 企业实践：大型银行的Agent Skill Registry

Snyk披露的银行案例与金融测试场景高度相关：约1,000个内部Skill集中管理，面向约50,000名Claude Code开发者，Skill在进入Registry前接受风险评估，上线后继续扫描。由于客户身份未公开，本期不推测具体银行。

真正值得借鉴的是治理链：`Skill Owner → Version → Pre-registry Assessment → Approved Scope → Runtime Distribution → Continuous Scan → Revocation`。这比“大家各自在本地放一个SKILL.md”更接近企业可审计的软件供应链。

来源：https://snyk.io/news/agentic-ai-security-moves-into-production/

## 8. 今日工具推荐：MCPMark

**适用场景：** MCP Server能力回归、Agent Tool Calling能力比较、Playwright/Postgres等真实Tool环境评测，以及验证模型/Harness升级是否导致MCP任务退化。

快速开始建议先不要跑全量Benchmark，只选一个环境和5—10条任务，固定`model + harness + MCP server version + environment image + verifier version`，保存成功率、Tool Trace和最终Verifier结果。项目：https://github.com/eval-sys/mcpmark

## 9. 今日学习：Action-bound Approval

传统确认框通常只记录`approved=true`，但高风险Agent真正需要证明的是“人批准了什么”。Action-bound Approval会先把目标、金额、收款方、Tool和关键参数规范化后生成Action Digest，再让批准证据绑定该Digest。执行前重新计算实际动作Digest；只要参数被Agent、Prompt Injection或中间层修改，就必须重新批准。这样可以防止“批准A、实际执行B”，也能避免把一次批准变成长期通行证。

## 10. 趋势观察

未来3个月，Agent治理会继续从“身份有权限”细化到“本次动作有授权”：高风险Tool的批准证据、动作绑定、有效期与防重放会逐渐成为金融和企业Agent准入的重要测试维度；与此同时Skill/MCP会越来越像软件包一样进入Registry与持续供应链治理。

## 11. 30分钟Action

给现有MCP准入模板增加一个最小`approval_contract`：`action_type / target / critical_args / action_digest / approver / approved_at / expires_at / consumed`。准备一条正常Case和三条反例：批准后修改金额、修改目标、重复使用同一批准。预期只有完全匹配且未过期、未消费的动作能够执行。

## 12. 值得跟进

继续观察HAPS GitHub参考实现和测试向量是否形成跨厂商实现；关注Action Digest的Canonicalization、防重放和Approval Expiry；跟进Snyk银行Skill Registry案例是否披露更具体的准入规则；关注Skill更新、Tool Scope变化和依赖变化如何触发重新准入；持续观察MCPMark Verified的环境固定与Verifier治理方式。

建议新增知识图谱节点：`Action-bound Human Approval / Approval Evidence / Agent Skill Registry / Continuous Skill Assurance / Approval Replay / MCPMark Verified`。

## 13. 我的备注

今天对金融测试最直接的启发，是把“权限”和“本次授权”彻底拆开。一个用户拥有转账权限、Agent也被允许调用转账Tool，并不代表Agent可以在任意时间、以任意参数替用户发起交易。金融MCP准入可以在既有`source entitlement`之上增加`transaction approval`层：权限回答“你能不能做”，Approval回答“这一次到底批准做什么”。

这也适用于Browser Agent。用户在页面点击一次“确认”后，如果Agent在后续步骤改变了收款人、金额、文件、发布范围或其他关键参数，旧确认不能继续沿用；最终Business Oracle还要对账真实后端状态，而不是只看页面成功提示。

对智能测试平台，Snyk的Skill Registry案例更值得长期吸收。以后如果内部逐渐积累测试计划Skill、日志精筛Skill、规则生成Skill或Browser测试Skill，最好从一开始就保存Owner、Version、Tool Scope、数据权限和准入状态。Skill内容或权限发生变化时，旧测试结论自动失效，而不是“文件还叫同一个名字就继续可信”。

安全日志审查也可以复用这个模型：AI可以提出整改或自动处置建议，但涉及白名单修改、规则发布、扫描范围扩大、报告删除等高副作用操作时，应要求新的Action-bound Approval；普通只读分析则不必制造Approval Fatigue。

测试计划先行建议新增三个问题：

> 当前Agent只是拥有Tool权限，还是能够证明用户批准了本次具体动作？

> 批准后关键参数被修改、批准过期或重复使用时，执行层是否Fail-closed？

> 内部Skill/MCP进入共享Registry后，版本、Owner、Tool Scope变化是否会自动触发重新准入？
