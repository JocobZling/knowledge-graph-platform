---
title: "AI Testing Daily Brief - 2026-09-11"
date: "2026-09-11"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - Browser Agent
  - MCP Testing
  - Agent Security
  - Agent Harness
  - EvalHub
  - Financial Services
  - Test Automation
source: "ChatGPT + official sources + GitHub + WayToAGI clues"
status: "published"
summary: "今日新增聚焦OpenAI Agents API把Codex Harness、长会话上下文压缩、动态Tool Search与多Agent编排作为托管运行时开放，Oracle Integration MCP Gateway把身份、Tool Filter、业务规则、凭据和审计统一到MCP控制平面，以及Red Hat AI 3.5将EvalHub正式GA并把Agent/Tool评测、安全扫描与合规证据纳入企业评测基础设施；企业实践关注ChatGPT for Financial Services对来源追溯、读写权限、信息隔离和审计日志的金融治理设计。"
---

# AI Testing Daily Brief - 2026-09-11

## 1. 今日摘要

最近24小时最值得关注的是OpenAI于9月10日发布 **Agents API Public Beta**。它把Codex背后的Agent Harness以托管API形式开放，原生提供长会话自动Context Compaction、按需Tool Search、Programmatic Tool Calling、多Agent并行，以及OpenAI托管或自有基础设施Sandbox。对测试而言，真正新增的对象不再只是模型，而是 `model + harness + environment + tools` 的组合版本。来源：https://openai.com/index/introducing-the-agents-api/

Oracle同日公布 **Oracle Integration MCP Gateway**，计划随Oracle Integration 26.10提供。它把MCP Client到企业系统之间的身份认证、Tool Filter、业务/安全Policy、Credential Resolution、第三方MCP路由和审计统一到一个Gateway；其中Business Policy明确用于拦截malformed、incomplete、hallucinated或不符合业务规则的请求。来源：https://blogs.oracle.com/integration/introducing-oracle-integration-mcp-gateway-governed-access-for-enterprise-ai-agents

Red Hat AI 3.5于9月9日GA，其中 **EvalHub正式GA**，评测对象覆盖模型、AI应用、Agent、Tool和AI漏洞扫描，并支持SDK/CLI、可审计结果、Garak对抗扫描、工具调用模型验证与MLflow Agent Trace。它说明企业Agent评测正在从项目脚本变成持续运行的受控平台服务。来源：https://www.redhat.com/en/about/press-releases/red-hat-puts-safety-and-observability-core-enterprise-ai-red-hat-ai-35 ，https://docs.redhat.com/en/documentation/red_hat_openshift_ai_self-managed/3.5/html/release_notes/new-features-and-enhancements_relnotes

本次已读取2026-09-04至2026-09-10最近7篇 `daily/ai-testing` 日报以及 `data/ai-testing/topic-index.json` 完成主题去重。近7日已覆盖Deployment Reliability、Agent Monitorability、Decision Replay、Shared-Knowledge Contagion、Judge Instrument Validation、MCP Scan-Test-Protect、Silent Failure、Skill Routing Collision、Independent Permission Authority、Credential Surrogation、Environment Reality、Abortability、Policy Precedence和Proportional Compliance，因此本期仅保留有实质新增机制的Harness Versioning、动态Tool Discovery准入、MCP Business Policy和Evaluation Control Plane。

WayToAGI缓存状态为 `success`，抓取时间为2026-09-10 12:45:32 +08:00，未超过48小时；本次已读取 `latest.md`，仅用于发现线索，正文事实均回到OpenAI、Oracle、Red Hat、GitHub或官方文档核验。`topic-index.json` 当前仍停留在 `latest_report_date=2026-07-15`，继续以实际日报作为主要去重基线。

## 2. 今日重点

### 1）Agent准入需要把Harness本身纳入版本与回归矩阵

- **一句话总结：** 同一个模型换了Harness，已经可以变成一个行为明显不同的Agent，因此上线标识不能再只有 `model_version`。
- **关注原因：** OpenAI Agents API把Codex Harness作为托管能力开放，并明确Harness会随模型持续演进；长任务可自动压缩早期Context，Tool Search按需加载Tool定义，Programmatic Tool Calling可以并行、串联并在代码中筛选结果，多Agent模式又会拆分独立Subagent Context。这些机制都会改变Agent真实执行轨迹。
- **对智能测试/测试开发的影响：** 建议Agent版本至少固定 `model_version / harness_version / context_policy / tool_registry_version / sandbox_image / skill_version`。模型不变但Harness变化时，仍需要跑核心回归；否则很难判断一次行为变化究竟来自模型、上下文压缩、动态工具发现还是并发编排。
- **建议动作：** 选10条已有稳定结果的长程Agent Case，保持模型不变，只切换旧/新Harness配置。比较 `task_success / tool_selected / tool_call_count / compact_count / subagent_count / final_oracle`，建立第一版Harness Regression。

来源：https://openai.com/index/introducing-the-agents-api/

### 2）动态Tool Search让MCP准入新增“可发现性”这一层

- **一句话总结：** Tool没有常驻Context并不代表风险消失；按需发现后，需要测试“什么任务会让哪个Tool进入可用集合”。
- **关注原因：** Agents API的Tool Search会按需加载相关Tool定义，以降低Token和缓存成本。相比一次性把所有MCP Tool暴露给模型，这种方式更节省Context，但也引入新的路由状态：一个高权限Tool可能在某类语义任务下被动态发现，而测试人员如果只看初始Tool List，会遗漏真实可执行面。
- **对智能测试/测试开发的影响：** MCP Server准入建议新增 `discoverable_by_query / discovery_reason / effective_tool_set / tool_scope_after_discovery / permission_after_discovery`。尤其需要测试相似Tool描述、模糊业务目标和恶意外部内容是否会让不该出现的Tool被召回。
- **建议动作：** 准备一个只读Tool和一个高风险写Tool，默认都不常驻Context。设计正常查询、模糊指令、Prompt Injection三组任务，记录实际被加载的Tool集合；高风险Tool被发现后仍必须通过独立Policy Gate，不能把“检索命中”当成“执行授权”。

来源：https://openai.com/index/introducing-the-agents-api/

### 3）MCP Gateway开始把“业务规则正确性”放进Tool执行前控制面

- **一句话总结：** 企业MCP网关不应只做Auth和网络代理，还需要能判断Agent生成的业务请求本身是否完整、合理、合规。
- **关注原因：** Oracle Integration MCP Gateway统一认证授权、Tool过滤、Credential Resolution与审计，并允许在Gateway、MCP Server或单Tool层应用Policy。官方示例中的Business Policy用于防止 malformed、incomplete、hallucinated 或违反业务规则的请求进入下游系统；PII Policy则作用于请求/响应路径。
- **对智能测试/测试开发的影响：** MCP测试可以正式拆成 `protocol_valid / auth_valid / business_valid / data_policy_valid / executed / final_effect`。过去“Schema合法、HTTP成功”的Case可能仍然应该被Business Policy拒绝。
- **建议动作：** 对一个测试写Tool构造四条请求：字段合法且业务合法、字段合法但对象不存在、字段合法但缺少业务前置条件、字段合法但违反限额。要求前三层分别给出可解释结果，只有业务合法请求真正落库。

来源：https://blogs.oracle.com/integration/introducing-oracle-integration-mcp-gateway-governed-access-for-enterprise-ai-agents

## 3. 行业新闻

### 1. OpenAI发布Agents API Public Beta

- **摘要：** 将Codex Harness、长会话、Sandbox、MCP、Tool Search、Programmatic Tool Calling和多Agent能力以托管API开放。
- **影响：** Agent测试对象从Model扩大为Model + Harness + Environment + Tool Registry组合。
- **发布时间：** 2026-09-10
- **来源：** OpenAI
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是，Agent平台、Coding Agent和MCP团队优先。

来源：https://openai.com/index/introducing-the-agents-api/

### 2. Oracle Integration公布MCP Gateway

- **摘要：** 单一Gateway统一多MCP Server身份、Tool过滤、Policy、Credential与审计，并支持业务规则前置校验。
- **影响：** 企业MCP治理从Server级准入继续进入跨Server统一控制面与Business Policy Gate。
- **发布时间：** 2026-09-10
- **来源：** Oracle
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是，企业集成、金融MCP和Agent治理团队建议关注；功能计划随Oracle Integration 26.10提供。

来源：https://blogs.oracle.com/integration/introducing-oracle-integration-mcp-gateway-governed-access-for-enterprise-ai-agents

### 3. Red Hat AI 3.5正式GA，EvalHub进入生产级评测基础设施

- **摘要：** EvalHub GA覆盖模型、应用、Agent、Tool和漏洞扫描，提供SDK/CLI、可审计评测、Garak安全分和MLflow Agent Trace。
- **影响：** Agent Eval开始具备统一调度、版本化接口、多租户和合规证据能力，不再只是项目内脚本。
- **发布时间：** 2026-09-09
- **来源：** Red Hat
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是，智能测试平台和AI治理平台团队优先。

来源：https://www.redhat.com/en/about/press-releases/red-hat-puts-safety-and-observability-core-enterprise-ai-red-hat-ai-35
来源：https://docs.redhat.com/en/documentation/red_hat_openshift_ai_self-managed/3.5/html/release_notes/new-features-and-enhancements_relnotes

### 4. OpenAI发布ChatGPT for Financial Services

- **摘要：** 面向金融机构整合GPT-6 Astra、金融数据、50+连接器、细粒度引用、角色化Skill/App权限、信息隔离和合规日志导出。
- **影响：** 金融AI验收开始更明确地要求Source Traceability、Entitlement、Read/Write Scope和Information Barrier可验证。
- **发布时间：** 2026-09-10
- **来源：** OpenAI
- **重要程度：** 中高
- **热度：** 高
- **是否建议立即学习：** 金融测试、数据治理和AI平台团队建议关注。

来源：https://openai.com/index/introducing-chatgpt-financial-services/

**今日暂无更多经官方、GitHub或权威来源核验，且与近7日归档不重复的高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| OpenAI Agents API | Public Beta | 托管Codex Harness、自动Context Compaction、Tool Search、Programmatic Tool Calling、多Agent、可选Sandbox | Harness版本回归、Context压缩完整性、动态Tool发现、并发Subagent、环境一致性 |
| Oracle Integration MCP Gateway | 计划随26.10提供 | 多MCP Server统一身份、Tool Filter、Security/Business/PII Policy、Credential Resolution、Audit | 跨Server准入、Business Policy、敏感数据、最小权限、统一审计 |
| Red Hat AI 3.5 / EvalHub | GA | Agent/Tool Eval、Garak安全扫描、SDK/CLI、MLflow Trace、合规报告、Tool-calling模型验证 | 企业评测控制面、可重复准入、证据留存、CI/CD集成 |
| ChatGPT for Financial Services | 面向符合条件金融机构可用 | 原生金融数据、来源引用、角色化Skill/App读写权限、信息隔离、合规日志 | 金融数据来源、Entitlement、信息墙、读写动作和审计测试 |

## 5. Agent Ecosystem

### Harness-aware Qualification

`Model + Harness + Context Policy + Tool Registry + Sandbox → Agent Behavior`

模型版本不再足够唯一标识一个Agent运行实例。

### Dynamic Tool Exposure

`Task Intent → Tool Search → Effective Tool Set → Permission Gate → Execution`

动态发现解决Context成本，但需要新增Tool可发现性和高权限Tool误召回测试。

### Evaluation Control Plane

`Eval Definition → Controlled Execution → Trace/Artifact → Score → Qualification → Audit Evidence`

评测平台开始像CI/CD一样成为长期企业基础设施。

## 6. 开源推荐：EvalHub

- **项目：** `eval-hub/eval-hub`
- **GitHub：** https://github.com/eval-hub/eval-hub
- **Star：** 24，2026-09-11 GitHub API核验
- **License：** Apache-2.0
- **核心能力：** 统一编排不同AI评测框架，提交/调度Evaluation Job、Provider与Benchmark发现、结果结构化管理，可结合MLflow与Kubernetes/OpenShift构建可审计评测流水线。
- **推荐指数：** 4.7 / 5

推荐理由：今天Red Hat把EvalHub正式推进到GA后，它更适合当作“评测基础设施设计样本”来研究。它的价值不是替代所有Judge，而是把不同评测框架放进统一控制面，让版本、任务、结果和运行环境更容易追踪。

## 7. 企业实践

### OpenAI金融服务：把“结论可追溯”与“数据/动作权限”同时做成产品能力

ChatGPT for Financial Services并不是单纯给金融行业换一套Prompt。OpenAI在产品层提供金融数据来源追溯，支持把数字和结论定位到具体表格和段落；同时通过SAML SSO、SCIM、RBAC管理人员权限，管理员可以按角色控制Skill和App，并分别开启或关闭支持的读/写动作。多个Workspace还可用于实现信息隔离，合规团队可以把支持的Workspace日志导入现有审计与调查流程。

这套结构可以抽象成：

`Entitlement → Retrieval → Citation/Evidence → Analysis → Read/Write Policy → Audit`

对金融测试最值得借鉴的是：模型“回答正确”只是其中一项验收，数据是否有权访问、引用是否能回到原始证据、写动作是否符合角色权限、跨信息墙是否隔离，同样应该成为Case。

来源：https://openai.com/index/introducing-chatgpt-financial-services/

## 8. 今日工具推荐：EvalHub

### 适用场景

模型/Agent统一准入、Prompt与Harness回归、对抗安全扫描、评测CI/CD、不同Judge/Benchmark统一调度，以及生成可审计的长期评测记录。

### 快速开始

本地可通过EvalHub SDK的Server模式搭建实验环境：

```bash
pip install "eval-hub-sdk[server]"
```

企业环境可以再接入Kubernetes/OpenShift、MLflow和不同Provider。第一轮不必追求全量平台化，先统一3类资产：

```text
eval_definition
runtime_snapshot
result_artifact
```

每次模型、Harness或Prompt变化时都生成新的Evaluation Run，避免只覆盖一张Excel结果表。

## 9. 今日学习：什么是Harness Regression？

传统LLM回归经常固定Prompt，然后比较模型A/B。但Agent的行为还由Harness决定：上下文如何压缩、Tool何时加载、调用是否并行、失败如何重试、Subagent如何拆分、Sandbox里有什么文件和权限。

因此即使 `model_version` 完全相同，只要Harness升级，真实行为也可能变化。

更完整的测试单元是：

`Model × Harness × Environment × Tool Registry`

Harness Regression就是冻结一组任务和Oracle，在Harness变化时重新检查Tool轨迹、状态恢复、Context完整性、副作用与最终结果，而不是只确认API还能调用。

## 10. 趋势观察

**未来3个月，Agent Testing会越来越明显地进入“运行时工程化”：模型只是版本矩阵的一维，Harness、动态Tool Discovery、Sandbox、Gateway Policy和Evaluation Control Plane都会成为可独立升级、也必须独立回归的一等组件。**

## 11. 30分钟 Action

### 给现有Agent补第一版Runtime Snapshot

1. 选择一条已有稳定结果的Browser/MCP Agent流程。
2. 固定10条Frozen Case。
3. 每次运行记录 `model_version`。
4. 增加 `harness_version`。
5. 增加 `tool_registry_version`。
6. 增加 `sandbox/environment_version`。
7. 记录实际动态加载的Tool集合。
8. 记录是否发生Context Compaction或Session恢复。
9. 保存Final Oracle与Side Effect。
10. 后续任何一个Runtime组件变化时，自动重跑这10条Case。

最小字段：

```text
model_version
harness_version
tool_registry_version
environment_version
effective_tools
compact_count
subagent_count
side_effect
final_oracle
```

## 12. 值得跟进

- OpenAI Agents API后续GA前的Harness版本策略与兼容承诺；
- 自动Context Compaction是否可能丢失安全约束、审批状态和未完成任务；
- Tool Search对高权限Tool误召回率与描述碰撞的影响；
- 多Subagent并行下共享状态、预算和权限是否一致；
- OpenAI Hosted Sandbox与自有/VPC Sandbox的回归差异；
- Oracle MCP Gateway 26.10正式可用后的Policy表达能力、延迟和Fail-closed行为；
- MCP Business Policy如何验证“合法Schema但错误业务对象”；
- EvalHub Environment Card、不可变评测Artifact与CI/CD门禁；
- Red Hat Tool-calling模型“validated”标签的具体判定集；
- ChatGPT for Financial Services信息隔离、Entitlement和读写App权限的实际测试模型；
- Prompt/Workflow：`Runtime Snapshot → Dynamic Tool Discovery → Policy Gate → Execute → External Oracle → Eval Record`；
- 知识图谱节点：`Harness Regression`、`Runtime Snapshot`、`Dynamic Tool Exposure`、`MCP Business Policy`、`Evaluation Control Plane`、`EvalHub`、`Financial AI Entitlement`。

## 13. 我的备注

今天这期对金融测试和智能测试平台的关联比较直接：以后做Agent准入，最好不要再只写“使用GPT-6 Astra / 某某模型”。真正影响结果的运行单元已经变成：

```text
模型
+ Harness
+ Skill
+ MCP Tool集合
+ Sandbox
+ 权限/Policy
```

尤其是Harness这一层很容易被忽略。比如自动Context Compaction升级以后，模型本身没换，但如果早期“只允许测试环境”“金额超过阈值必须人工确认”这类约束被压缩丢失，后面的Agent行为已经可能完全不同。所以关键安全条件最好既进入Context，也由工程Policy独立保存。

OpenAI的Tool Search也很适合未来MCP Server准入：不要只记录“这个Agent配置了哪些Tool”，而应该记录**这次任务最终发现并暴露给模型的是哪些Tool**。一个高权限写Tool如果只在特定语义下动态出现，它仍然属于真实攻击面。

Oracle MCP Gateway今天最值得借的是Business Policy层。金融Tool经常出现这种情况：Schema全部合法，但业务仍然错——比如商户存在但不属于当前项目、账期合法但未开放、金额格式正确但超限。未来MCP准入最好把“协议合法”和“业务合法”分开。

Red Hat EvalHub则比较适合智能测试平台的中长期演进：评测Case、Judge、Prompt、Harness、运行环境和结果都不要散落在不同脚本里，逐渐沉淀成统一Evaluation Run与可审计Artifact。这样后面做模型升级、Prompt优化、Browser Agent和MCP准入时，才能真正比较“哪一层变化导致结果变化”。

ChatGPT for Financial Services的产品设计也给金融AI测试一个很清晰的检查清单：

```text
数据是否有权访问
来源是否可追溯
结论是否有Evidence
Skill/App是否按角色授权
读写动作是否分别受控
信息墙是否真的隔离
日志是否可进入合规审计
```

测试计划先行建议新增三个问题：

> **本次Agent运行由哪些Model、Harness、Tool Registry和Environment版本共同组成，是否能够完整重现？**

> **动态发现Tool后，实际暴露的Tool集合是否仍满足最小权限，发现高风险Tool是否会被误当成执行授权？**

> **一个MCP请求即使Schema合法，还需要经过哪些业务Policy才能真正产生副作用？**
