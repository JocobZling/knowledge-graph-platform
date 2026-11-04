---
title: "AI Testing Daily Brief - 2026-11-04"
date: "2026-11-04"
type: "daily-brief"
category: "ai-testing"
tags: ["AI Testing", "Agent Testing", "LLM Testing", "MCP Server", "Agent Skill", "Supply Chain", "CodeQL", "Agent Security"]
source:
  - "https://github.blog/changelog/2026-11-03-dependabot-version-updates-support-agent-package-manager/"
  - "https://github.blog/changelog/2026-11-03-codeql-2-27-3-adds-python-3-15-features-and-json-data-extensions/"
  - "https://github.blog/changelog/2026-11-03-github-copilot-is-now-available-in-jetbrains-air/"
  - "https://microsoft.github.io/apm/"
  - "https://github.com/microsoft/apm"
status: "published"
summary: "GitHub Dependabot支持APM的Agent Skill/Prompt依赖版本更新；CodeQL 2.27.3增加可选AI数据威胁模型和JSON数据扩展；Copilot进入JetBrains Air，明确云任务隔离及治理限制。"
---

# AI Testing Daily Brief - 2026-11-04

**日期：** 2026-11-04（北京时间）  
**检索窗口：** 11月3日至4日，必要时回溯7天。  
**核心判断：** Agent资产正在成为需要锁版本、审核更新、建模非可信数据流和执行准入的正式软件依赖。

## 1. 今日摘要

11月3日，GitHub宣布Dependabot版本更新支持Agent Package Manager（APM），可以为apm.yml声明的受支持Git依赖提出更新PR，并在存在锁文件时重新生成apm.lock.yaml。该能力只覆盖版本更新，不覆盖安全漏洞更新。[S1]

同日，CodeQL 2.27.3新增可选的ai威胁模型和JSON格式数据扩展；使用者需要自行定义LLM生成内容的数据来源，并非自动覆盖所有提示注入场景。[S2]

GitHub Copilot进入JetBrains Air，使用Agent Client Protocol（ACP），支持本地和隔离云任务，但云任务仍存在订阅检查、使用量可见性及成本管理边界。[S3]

本期优先使用最近24小时的三项官方新增；暂无更多达到新颖度门槛的高价值更新。

**去重：** 已读取2026-10-28至2026-11-03七篇实际日报及topic-index.json；索引latest_report_date仍为2026-07-15，以实际日报为准。WayToAGI缓存success，fetched_at=2026-11-03T14:50:28+08:00，48小时内；已读取latest.md，仅作线索。排除近7日已报道的Agentic Workflows GA、Actions锁文件GA、Copilot Memory、AgentProofBench、Req2Test-Bench等旧主题。

## 2. 今日重点

### 重点一：Dependabot开始管理Agent资产依赖（11-03；新颖度5/5）

**一句话：** Skill、Prompt、指令等Agent上下文依赖开始拥有类似软件包的版本更新PR流程。[S1]

**关注原因：** Dependabot支持apm.yml中的受支持Git依赖，并在存在apm.lock.yaml时更新锁文件；github.com全计划GA，GHES预计3.24支持。官方明确此功能只提供版本更新，不提供安全漏洞更新。

**测试影响：** 需要审查Skill更新的行为变化、依赖版本、内容哈希、MCP权限及回归用例；不能因为Dependabot提出PR就自动批准合并。

**建议动作：** 建立“锁定版本→更新PR→Diff/权限审查→固定Agent回归→人工合并”的准入卡，优先使用合成Skill和实验仓库。

### 重点二：CodeQL 2.27.3增加AI数据流威胁建模（11-03；新颖度5/5）

**一句话：** 静态分析可以显式将LLM生成数据建模为非可信来源，但仍需配置具体Source。[S2]

**关注原因：** CodeQL CLI数据扩展支持JSON，新增opt-in的ai threat model；其本身没有内置Source模型。Python XXE/XML bomb/NoSQL注入查询会尊重已配置威胁模型。Java SSRF和URL重定向查询优化固定Host白名单识别以减少误报；自定义Actions CFG查询可能需要迁移。

**测试影响：** 应分别验证静态数据流告警、后端鉴权与实际副作用。日志、模型回答、Tool结果进入SQL/URL/文件操作之前需要清晰的信任边界。

**建议动作：** 在合成仓库建模一条“模型输出→URL请求”路径，分别测试有无数据扩展时的告警变化；不要宣称新版本自动发现所有Agent漏洞。

### 重点三：Copilot通过ACP进入JetBrains Air（11-03；新颖度4/5）

**一句话：** IDE中的多Agent任务可以经ACP交给Copilot本地或云端执行。[S3]

**关注原因：** Air支持并行会话和云端隔离任务；云任务在新VM的Docker容器中运行、流量经代理、任务间不保留状态。Air管理端可禁用Copilot，但不支持在Air内直接设置Copilot花费上限，治理分析不包含Copilot使用量。

**测试影响：** 需要验证云/本地执行位置、凭据边界、网络出口、任务隔离、成本归属和结果回传；不能把ACP接入等同于企业内网可用。

**建议动作：** 仅在获批实验环境测试无敏感数据的构建任务，核对隔离和账单证据。

## 3. 行业新闻

| 标题 | 摘要（100字以内） | 影响 | 发布时间 | 来源 | 重要程度 | 热度 | 立即学习 |
|---|---|---|---|---|---|---|---|
| Dependabot支持APM | Agent Skill/Prompt等受支持Git依赖可自动提版本更新PR，但不提供安全更新。 | Agent供应链 | 11-03 | [S1] | 高 | 中 | 是 |
| CodeQL 2.27.3 | 新增可选AI数据流威胁模型、JSON扩展与多语言分析改进。 | LLM数据流安全 | 11-03 | [S2] | 高 | 中 | 是 |
| Copilot进入JetBrains Air | ACP接入本地/云端Agent任务，云任务隔离及成本治理边界明确。 | Coding Agent治理 | 11-03 | [S3] | 中高 | 中 | 视环境 |

热度为编辑定性判断，不是实时传播统计。**今日暂无更多高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| GitHub Dependabot | APM版本更新GA | apm.yml依赖PR与锁文件再生成 | Skill/MCP依赖变更回归 |
| CodeQL 2.27.3 | AI威胁模型与JSON扩展 | 可自定义LLM生成数据Source | 非可信数据流分析 |
| GitHub Copilot + JetBrains Air | ACP集成 | 本地/云端Agent会话 | 隔离、网络和权限验收 |

## 5. Agent Ecosystem

**APM依赖治理：** apm.yml声明Skill、Prompt、Plugin、MCP依赖，锁文件固定版本和内容哈希，安装时执行策略检查。注意安装策略不替代运行时Tool鉴权。[S4]

**ACP跨工具执行：** Copilot与Air通过ACP连接，需区分协议互通、实际云端执行和企业策略覆盖范围。[S3]

本期未核验到新的MCP核心协议正式版本或Browser Agent专项发布。Research Skill不视为核心测试工具。

## 6. 开源推荐：microsoft/apm

**项目：** [microsoft/apm](https://github.com/microsoft/apm)  
**Star：** 4,185（2026-11-04查询GitHub REST API，动态值）  
**License：** MIT  
**核心能力：** Agent资产清单、依赖锁定、内容哈希、MCP安装策略及跨Harness部署。  
**推荐指数：** 4.5/5（研究价值）。  
**推荐理由：** 适合构建Skill/MCP准入实验，形成“来源—版本—权限—测试—批准”闭环；不应未经审查直接接入内网。

## 7. 企业实践：GitHub的Agent依赖更新PR治理

GitHub将Dependabot版本更新能力扩展至APM资产，沿用依赖声明、锁文件、变更PR的可审查模式。官方未披露该机制降低Agent安全事件的量化效果；企业可借鉴自动提出变更、人工审查、固定回归和合并审批的流程。[S1]

## 8. 今日工具推荐：Agent Package Manager（APM）

**适用场景：** 合成Skill/Prompt依赖管理和MCP准入验证。  
**快速开始：** 阅读[官方Quickstart](https://microsoft.github.io/apm/)，在隔离实验目录建立apm.yml，使用获批安装方式安装APM，然后运行`apm install`。检查生成的锁文件、内容哈希、目标Harness配置和策略拒绝结果。避免直接执行未经审核的远程安装脚本。

## 9. 今日学习：Agent资产的可复现性（300字以内）

传统依赖锁文件解决“今天安装与下周安装得到不同代码”的问题。Agent依赖还包括Skill、Prompt、指令与MCP Server：即使模型版本不变，资产内容变化也可能改变工具选择和安全行为。准入应同时保存依赖来源、提交SHA、内容哈希、Tool权限和回归结果。版本更新PR只能证明有新版本，不证明安全；运行时权限仍须由后端独立执行。

## 10. 趋势观察

未来3个月，Agent资产预计会更广泛纳入依赖管理和安全策略体系；从“安装一个Skill”走向“可锁定、可审查、可回滚的Agent供应链”。

## 11. 30分钟 Action

**目标：** 做一张Skill/MCP更新准入卡。  
0—5分钟：列出一个合成Skill的来源、版本和哈希。  
5—12分钟：记录允许的Tool与MCP Server。  
12—20分钟：设计正常调用、恶意Tool返回、越权调用三条用例。  
20—26分钟：定义后端授权、审计与业务状态Oracle。  
26—30分钟：形成PASS/FAIL/INCONCLUSIVE结论。  
**验收：** 未授权Tool不执行；依赖变更可追踪；证据不足不判PASS。

## 12. 值得跟进

| 类型 | 对象 | 观察点 |
|---|---|---|
| 官方公告 | [Dependabot APM][S1] | 版本更新范围及GHES 3.24 |
| 工具/开源 | [Microsoft APM][S4] | 锁文件、MCP策略、跨Harness兼容 |
| 安全工具 | [CodeQL 2.27.3][S2] | AI Source建模及误报 |
| Agent协议 | [Copilot JetBrains Air][S3] | ACP隔离和治理 |
| Prompt/Skill | 合成Skill回归模板 | 更新前后行为差异 |
| 知识图谱 | Agent Dependency Lock / AI Data Source / ACP Isolation | 来源、风险、证据节点 |
| 持续观察 | MCP核心协议、Browser Agent、LLM测试论文 | 仅新增时再报 |

## 13. 我的备注

**金融测试：** 受限内网优先研究APM清单和锁文件理念，不直接引入未获批准的公网Skill或MCP。  
**测试经理智能助手：** 一期继续保持Dify编排、Java鉴权、PostgreSQL实时查询；未来增加Skill时需记录版本与工具权限，不必为新工具重构现有工程。  
**Browser Agent：** 今日没有新的专门基准，继续使用页面操作、后端状态、权限审计三层Oracle。  
**MCP Server准入：** 将来源、版本哈希、依赖变更、Tool白名单、越权副作用纳入准入。  
**安全日志审查：** 日志文本和模型输出均视为非可信数据；CodeQL的AI威胁模型是可研究的静态分析补充，不替代AI精筛的真实风险召回测试。  
**测试计划先行：** 先定义变更风险和验收证据，再安装、升级或执行Agent资产。

## 来源

[S1]: https://github.blog/changelog/2026-11-03-dependabot-version-updates-support-agent-package-manager/
[S2]: https://github.blog/changelog/2026-11-03-codeql-2-27-3-adds-python-3-15-features-and-json-data-extensions/
[S3]: https://github.blog/changelog/2026-11-03-github-copilot-is-now-available-in-jetbrains-air/
[S4]: https://microsoft.github.io/apm/
