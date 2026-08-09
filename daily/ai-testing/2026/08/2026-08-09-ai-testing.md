---
title: "AI Testing Daily Brief"
date: "2026-08-09"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - MCP Testing
  - MCP Apps
  - Host Conformance
  - RAG Evaluation
  - Test Automation
source: "ChatGPT"
status: "published"
summary: "今日新增聚焦MCP Apps Host侧一致性测试、MCP C# SDK v2.1.0的订阅流与协议回退可靠性，以及TREC RAG 2026 Agent-first评测轨道；核心判断是MCP准入需要从Server连通性扩展到Host渲染、权限、回退和端到端评测。"
---

# AI Testing Daily Brief - 2026-08-09

## 1. 今日摘要

今天没有发现最近24小时内、且与近7日日报不重复的高价值主流模型发布，因此不使用旧内容补足条数。

最值得关注的新进展来自MCP生态。Model Context Protocol官方社区日历在2026年8月7日单独安排了 **“MCP Apps - Host Conformance Testing”** 工作环节；与此同时，官方 `ext-apps` 仓库仍保留“建立官方MCP App validator”的开放议题，说明MCP Apps的质量问题已经从“Server能否返回UI资源”进一步扩展到“不同Host是否按照同一安全与行为约束执行”。来源：https://meet.modelcontextprotocol.io/2026 、https://github.com/modelcontextprotocol/ext-apps/issues/673

MCP官方C# SDK于2026年8月5日发布 **v2.1.0**，新增可选的 `subscriptions/listen` Server处理器，并强化HTTP传输回退：当 `server/discover` 在HTTP层失败时，可回退到旧版 `initialize` 流程；同时增加Application Insights遥测示例和二进制资源说明。对MCP Server准入而言，这类变化直接影响协议降级、订阅中断、错误码保持和Trace完整性。来源：https://github.com/modelcontextprotocol/csharp-sdk/releases/tag/v2.1.0

评测侧，TREC RAG 2026于2026年8月8日进入提交截止节点。官方将其称为TREC首个 **Agent-first** 轨道，并提供测试主题、ClimbMix-400b语料访问和RAGDoll端到端评测工具，用统一任务同时观察检索、证据和生成质量。来源：https://trec-rag.github.io/

本次已读取2026年8月2日至8月8日最近7篇 `daily/ai-testing` 日报及 `data/ai-testing/topic-index.json` 完成去重。近7日已经覆盖ClawTrack、Agent评测隔离、NOOA、ACM、OpenART、Trajectory Assurance、运行时故障监控、PIMiner、Codex Security、Benchmark QA、JudgeSkill、World Rehearsal等主题，本期未重复进入重点栏目。当前 `topic-index.json` 的 `latest_report_date` 仍停留在2026年7月15日，因此实际日报仍作为主要去重依据。WayToAGI缓存状态为 `success`，抓取时间为2026年8月8日10:23:13（UTC+8），距本次执行不足48小时，本期仅将其用于发现线索。

## 2. 今日重点

### 1）MCP准入需要从Server Conformance扩展到Host Conformance

- **一句话总结：** MCP Apps把HTML UI、表单、按钮和工具调用带进Host后，是否安全不再只取决于Server是否符合协议，还取决于Host是否正确实现sandbox、能力协商、工具可见性和App-to-Host通信。
- **关注原因：** 官方MCP日历已经将“Host Conformance Testing”单独列为工作主题；官方 `ext-apps` 仓库也明确指出，目前缺少统一的MCP App validator，不同Host可能执行不同的规则子集。
- **对智能测试/测试开发的影响：** MCP准入矩阵应至少拆成Server、Host、View三层。Server验证Schema和资源声明；Host验证能力协商、iframe隔离、CSP、权限与工具代理；View验证初始化顺序、消息协议和异常降级。只跑Server conformance不足以证明MCP App可以安全进入企业客户端。
- **建议动作：** 对同一个测试MCP App分别在两个Host或参考Host中运行，比较初始化、工具可见性、CSP、未声明域名访问、权限请求、App工具调用和不支持UI时的文本降级行为。

来源：https://meet.modelcontextprotocol.io/2026
参考实现：https://github.com/modelcontextprotocol/ext-apps
官方validator议题：https://github.com/modelcontextprotocol/ext-apps/issues/673

### 2）协议回退本身必须成为MCP回归用例

- **一句话总结：** C# SDK v2.1.0修复 `server/discover` 在HTTP层失败后的回退路径，并改善AutoDetect传输可靠性，说明“新客户端连接旧Server”仍是实际故障面。
- **关注原因：** 2026-07-28协议引入discovery-first和无状态HTTP，但企业环境仍会长期存在旧协议Server、反向代理、网关和混合SDK版本。接口成功并不代表协商路径正确。
- **对智能测试/测试开发的影响：** MCP兼容回归应记录“期望协议”和“实际协商协议”，并覆盖 `server/discover` 404/500/超时、SSE临时失败、旧 `initialize` 回退、HTTP状态码保持以及回退后能力降级。否则平台可能表面可用，但悄悄进入不同的权限、订阅或缓存语义。
- **建议动作：** 建立新Client×新Server、新Client×旧Server、代理返回404、代理超时四组用例，保存实际协商协议、Transport、错误码、启用扩展和最终工具列表。

来源：https://github.com/modelcontextprotocol/csharp-sdk/releases/tag/v2.1.0

### 3）RAG评测开始进入Agent-first、端到端证据评测

- **一句话总结：** TREC RAG 2026不仅比较生成答案，而是通过统一语料、测试主题和评测工具，把检索、证据支撑与答案质量放到同一条Agent式执行链中。
- **关注原因：** 企业RAG Agent往往同时包含查询改写、检索、过滤、重排、证据选择和生成。只测最终回答很难判断问题究竟发生在哪一层。
- **对智能测试/测试开发的影响：** RAG Agent回归应保留查询、检索结果、证据片段、最终引用和答案，分别评估检索覆盖、证据一致性、引用支撑和生成正确性，而不是只用一个LLM Judge给总分。
- **建议动作：** 从内部知识库随机选择10个问题，把每次执行的Query、Top-K、引用证据和答案保存下来；当答案失败时，强制标记失败来源是“未检索到、检索到了但没采用、采用了错误证据、证据正确但生成错误”。

来源：https://trec-rag.github.io/

## 3. 行业新闻

### 1. MCP社区将Host Conformance Testing单列为MCP Apps工作主题

- **摘要：** MCP官方社区日历在8月7日列出“MCP Apps - Host Conformance Testing”，官方ext-apps仓库同时仍在讨论统一validator的范围和形态。
- **影响：** MCP App测试将从Server Schema验证扩展到Host渲染、安全沙箱、能力协商和行为一致性。
- **发布时间/事件时间：** 2026-08-07
- **来源：** Model Context Protocol官方社区日历、官方ext-apps GitHub
- **重要程度：** 高
- **热度：** 中
- **是否建议立即学习：** MCP平台和准入团队建议立即关注

来源：https://meet.modelcontextprotocol.io/2026

### 2. MCP C# SDK v2.1.0增强订阅与协议回退

- **摘要：** v2.1.0增加 `subscriptions/listen` Server处理器，修复 `server/discover` HTTP失败时的旧协议回退，并改善AutoDetect/SSE失败处理，同时新增Application Insights遥测示例。
- **影响：** MCP兼容性测试要覆盖协议协商、回退、订阅生命周期、HTTP状态码和Trace。
- **发布时间：** 2026-08-05
- **来源：** Model Context Protocol官方C# SDK
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是

来源：https://github.com/modelcontextprotocol/csharp-sdk/releases/tag/v2.1.0

### 3. TREC RAG 2026进入提交截止节点

- **摘要：** TREC RAG 2026于8月8日截止系统提交，官方将其定位为TREC首个Agent-first轨道，并提供2026测试主题和RAGDoll端到端评测工具。
- **影响：** RAG Agent评测进一步强调可重复数据、统一任务和端到端证据链，而非单一主观Judge。
- **时间：** 2026-08-08
- **来源：** TREC RAG官方站点
- **重要程度：** 中高
- **热度：** 中
- **是否建议立即学习：** RAG/知识库测试团队建议关注

来源：https://trec-rag.github.io/

**今日暂无更多经官方、GitHub或论文原始来源核验，且与近7日归档不重复的高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| MCP C# SDK v2.1.0 | 订阅流、HTTP回退与遥测示例增强 | `subscriptions/listen`公开处理器；`server/discover` HTTP失败回退旧初始化；AutoDetect可靠性与状态码保持 | 构建新旧协议兼容、订阅断连、传输回退和Trace回归矩阵 |

最近24小时内未发现OpenAI、GitHub、Anthropic、Google、Playwright、Cursor、Codex等主流产品中，与近7日归档不重复且足以进入正文的高价值正式产品更新。

## 5. Agent Ecosystem

### Host-side Conformance

MCP生态中的“合规”不再只是Server能否按Schema返回结果。带UI的MCP Apps要求Host正确处理能力协商、sandbox、CSP、App Bridge、工具可见性和权限请求。企业准入应明确区分Server Conformance与Host Conformance。

### Negotiated Runtime

协议版本不是静态配置，而是运行时协商结果。测试报告应记录：Client SDK、Server SDK、请求协议、最终协商协议、启用扩展、回退原因和Transport。

### Agent-first RAG Evaluation

RAG Agent的测试对象不是单个Prompt，而是一条执行链：查询构造 → 检索 → 证据选择 → 生成 → 引用。失败定位应保留每一阶段的可验证产物。

## 6. 开源推荐：Model Context Protocol C# SDK

- **项目：** MCP C# SDK
- **GitHub：** https://github.com/modelcontextprotocol/csharp-sdk
- **Star：** 约4.4k，2026-08-09联网核验
- **License：** Apache-2.0
- **核心能力：** 官方.NET MCP Client/Server SDK，支持HTTP与stdio、2026-07-28协议、Tasks、MCP Apps、OAuth、缓存提示、版本协商、订阅流和遥测集成
- **推荐指数：** 4.7/5
- **推荐理由：** 官方SDK同时提供协议实现、样例、测试和版本迁移说明，适合用来建立企业MCP兼容矩阵。v2.1.0中的回退修复尤其适合作为故障注入用例来源。

来源：https://github.com/modelcontextprotocol/csharp-sdk
发布说明：https://github.com/modelcontextprotocol/csharp-sdk/releases/tag/v2.1.0

## 7. 企业实践

### Workato：把MCP App用于会话内表单与审批

**企业：** Workato

**做法**

Workato在8月7日的产品实践活动中展示MCP Apps如何直接在Claude和ChatGPT会话中呈现表单和审批按钮，并用于申请、审批、确认等企业流程。其官方文档还允许配置MCP App的Content Security Policy，以及摄像头、麦克风、位置、剪贴板等权限。

**效果**

这种模式减少从聊天界面跳转到独立业务系统的上下文切换，但官方没有公开量化的错误率、审批效率或风险降低数据，因此不作效果外推。

**可借鉴点**

企业MCP App测试不能只检查UI是否显示，还应验证：

- 当前用户身份是否与工具调用主体一致；
- 审批按钮是否绑定唯一业务对象；
- 重复点击是否幂等；
- CSP是否阻止未声明外联；
- App请求的摄像头、位置、剪贴板权限是否按需最小化；
- Host不支持MCP Apps时是否能安全降级到文本流程。

来源：https://mktg.workato.com/JPN-WBN-2026-08-07-Workato-MCP-Apps
文档：https://docs.workato.com/en/mcp/mcp-apps

## 8. 今日工具推荐：MCP Apps basic-host

官方 `modelcontextprotocol/ext-apps` 仓库提供一个基础Host参考实现，适合在没有正式企业Host测试环境时先验证MCP App初始化、UI资源、App Bridge和工具交互。

### 适用场景

- MCP Apps开发联调；
- Host兼容性测试；
- iframe/CSP与权限实验；
- 工具可见性测试；
- App到Server双向调用验证；
- Host不支持特性时的降级测试。

### 快速开始

```bash
git clone https://github.com/modelcontextprotocol/ext-apps.git
cd ext-apps
npm install
npm start
```

然后打开：

```text
http://localhost:8080/
```

建议第一轮不要测试复杂业务，先准备一个只读工具和一个App-only工具，验证模型是否看不到App-only工具、View能否调用它、未声明网络域名是否被限制，以及Host Context变化是否能正确通知View。

来源：https://github.com/modelcontextprotocol/ext-apps

## 9. 今日学习：什么是Host Conformance？

MCP Server Conformance回答的是“Server是否按协议提供工具、资源和消息”；Host Conformance回答的是“承载Agent与MCP App的客户端是否正确、安全地执行这些协议”。

当MCP只有文本工具时，两者差异不明显；加入MCP Apps后，Host还要负责iframe隔离、CSP、权限、App Bridge、工具代理和能力协商。Server完全正确，如果Host错误地放宽sandbox、把App-only工具暴露给模型、忽略能力协商或错误处理postMessage，系统仍然是不安全的。

因此企业MCP准入需要至少三层：Server协议测试、Host行为测试、Server×Host组合兼容测试。

## 10. 趋势观察

**未来3个月，MCP测试会明显从“Server能不能连通”升级到“Server、Host、App扩展和协议降级能否共同满足同一安全与行为契约”；Host Conformance很可能成为企业MCP准入的新测试层。**

## 11. 30分钟 Action

### 给现有MCP准入表增加Host测试列

1. 选一个已经通过Server准入的只读MCP Server。
2. 增加一个最小MCP App或使用官方示例App。
3. 记录Host是否声明支持 `io.modelcontextprotocol/ui`。
4. 检查UI资源是否在sandbox iframe中运行。
5. 注入一个未声明外部域名，验证CSP是否阻断。
6. 设置一个App-only工具，确认模型侧工具列表不可见，但View可以通过Host调用。
7. 模拟Host不支持Apps，确认工具仍能以文本结果安全降级。
8. 将结果加入准入矩阵：`Server Conformance / Host Conformance / 组合通过`。

## 12. 值得跟进

- MCP Apps Host Conformance Testing后续是否形成正式测试套件或规范文档；
- `ext-apps` 官方validator议题 #673 是否从静态Schema校验扩展到运行时行为检查；
- MCP C# SDK v2.1.0的 `subscriptions/listen` 与其他语言SDK的语义一致性；
- `server/discover` HTTP失败后回退是否会造成能力或安全策略降级；
- Application Insights / OpenTelemetry中MCP Trace字段的统一方式；
- TREC RAG 2026结果与Judge/人工评审的一致性；
- RAGDoll是否适合内部知识库回归；
- MCP Apps的CSP、权限请求和App-only工具测试；
- Browser Agent与MCP App组合时，UI状态和工具状态如何关联；
- 知识图谱节点：Host Conformance、MCP Apps、Protocol Fallback、Subscription Stream、Agent-first RAG Evaluation。

## 13. 我的备注

今天对金融测试比较值得借鉴的，不是新增一个MCP功能，而是**MCP准入对象需要重新定义**。

以前做MCP Server准入，很容易把注意力放在工具Schema、权限和Server是否可用。如果后续把审批、报表、查询结果通过MCP Apps直接嵌入Agent客户端，Host本身也变成安全边界。比如一个“确认清算结果”的按钮，如果Host没有把用户身份、业务流水和调用参数绑定起来，界面看上去正确，也可能执行错误主体的操作。

智能测试平台可以把MCP准入拆成三层：

> Server协议与权限 → Host渲染与代理 → 端到端业务结果

其中金额、账期、商户、流水状态仍由确定性Oracle验证，不能因为MCP App显示“成功”就直接判定通过。

Browser Agent测试也可以借用Host Conformance思路：不仅检查页面是否可操作，还要检查承载Agent的浏览器环境有没有正确实施权限、下载、剪贴板、跨域和Session隔离。

安全日志审查中，如果未来把风险确认、误报标记或整改审批做成MCP App按钮，建议把每一次UI动作同时记录：用户、Host、App版本、MCP Server、Tool、业务主键和最终数据库状态。这样UI层和工具层才能形成同一条证据链。

测试计划先行时，可以新增一项“运行载体假设”：任务到底运行在哪个Host、支持哪些扩展、什么能力会降级，以及降级后验收标准是否仍成立。
