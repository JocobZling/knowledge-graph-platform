---
title: "AI Testing Daily Brief"
date: "2026-08-11"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - Agent Skills
  - Skill Evaluation
  - Agent Harness
  - MCP Testing
  - Test Automation
source: "ChatGPT"
status: "published"
summary: "今日新增聚焦Agent Skill真实可用性评测：Skill-Use将能力拆为Trigger、Compliance、Boundary，并显示模型排名会随Harness变化；同时关注SKT通过可验证合成轨迹训练Skill使用能力。最近24小时高价值产品更新有限，未使用旧内容凑数。"
---

# AI Testing Daily Brief - 2026-08-11

## 1. 今日摘要

今天最近24小时内没有发现与近7日归档不重复、且足够高价值的OpenAI、GitHub、Anthropic、Google、Playwright、Cursor、Codex或MCP正式产品更新，因此本期减少条数，不使用旧公告补版面。

今天最值得关注的新研究是 **Skill-Use: Can LLMs Actually Use Skills in Agentic Harnesses?**。论文不再只问“Skill是否提高最终成功率”，而是拆开测试Agent能否主动触发正确Skill、是否遵循规定流程、以及是否守住禁止操作边界。Benchmark包含79个真实Skills、177个可执行任务、9个领域，并在隔离Docker环境中按轨迹评分；8个模型、2种Harness的实验中，最强配置SU分数也只有0.613。来源：https://arxiv.org/abs/2608.04828

更关键的发现是：Skill使用能力并不是一个只属于模型的固定属性。论文显示，Trigger、Compliance以及模型排序会随Agent Harness变化。这意味着企业不能只说“某模型支持Skill”，而应测试“模型 × Harness × Skill版本”的具体组合。

作为补充线索，8月3日提交的 **SKT** 用2,000个公开Skills构造4,000个任务包和27,164条经过验证的执行轨迹，强调Skill训练数据必须通过规则与Agent双重验证，并确保每个要求的Skill都被真实使用。来源：https://arxiv.org/abs/2608.02287

本次已读取2026年8月4日至8月10日最近7篇 `daily/ai-testing` 日报及 `data/ai-testing/topic-index.json` 完成去重。近7日已经覆盖NOOA、ACM、OpenART、Trajectory Assurance、运行时故障监控、PIMiner、Codex Security、Benchmark QA、JudgeSkill、World Rehearsal、MCP Host Conformance、协议回退和硬件Keystore，因此今天不重复进入重点栏目。当前 `topic-index.json` 的 `latest_report_date` 仍停留在2026-07-15，实际日报继续作为主要去重基线。

WayToAGI缓存状态为 `success`，抓取时间为2026-08-10 10:36:05（UTC+8），距本次执行不足48小时；本次已读取缓存，仅用于发现线索，进入正文的事实均回到论文、GitHub或官方文档核验。

## 2. 今日重点

### 1）Agent Skill测试要拆成 Trigger、Compliance、Boundary

- **一句话总结：** “任务最终做对了”不足以证明Agent真的会用Skill；需要分别验证是否主动触发、是否按流程执行、是否避免越界操作。
- **关注原因：** Skill-Use采用progressive disclosure：Agent最初只看到Skill名称和简短描述，需要先判断相关性，再主动获取完整流程。它把Skill能力拆成Trigger、Compliance、Boundary三部分，并只在Skill真实触发后计算执行贡献。
- **对智能测试/测试开发的影响：** Skill测试不能只做最终Pass/Fail。需要记录Skill候选集、被选择Skill、加载时间、关键步骤完成情况、禁止动作、工具轨迹和最终业务Oracle。否则Agent可能碰巧完成任务，却根本没遵守SOP。
- **建议动作：** 选择一个现有测试Skill，为它增加三组用例：应触发、相似但不应触发、触发后存在禁止操作。分别统计Trigger Recall、流程Compliance和Boundary Violation Rate。

来源：https://arxiv.org/abs/2608.04828

### 2）Skill能力必须按“模型 × Harness × Skill版本”回归

- **一句话总结：** Skill-Use实验显示，同一模型换Harness后Skill使用表现和模型排名都可能变化，因此不能把Skill能力只归因于基础模型。
- **关注原因：** Harness决定Skill如何被发现、何时加载、上下文如何注入、工具如何暴露以及执行轨迹如何组织。相同的SKILL.md，在不同Coding Agent、CLI或企业Agent平台中可能产生不同触发与遵循行为。
- **对智能测试/测试开发的影响：** 版本矩阵至少应保存 `model_version / harness_version / skill_version / tool_schema_version`。任何一项变化，都需要跑Skill专项回归，而不是只抽查最终答案。
- **建议动作：** 对同一个Skill分别在两个Agent Harness运行同一批10条任务，对比Skill触发率、漏步骤率、越权率、Token和最终成功率。如果最终成功率接近但过程差异显著，应优先看过程指标。

来源：https://arxiv.org/abs/2608.04828

### 3）Skill训练数据也需要“真实使用”验证

- **一句话总结：** SKT显示，仅把Skill文本塞进训练上下文不够；训练轨迹需要验证任务确实依赖Skill，而且Agent实际使用了全部要求的Skill。
- **关注原因：** SKT从2,000个公开Skills构造4,000个任务包和27,164条验证轨迹，通过规则检查、Agent验证和反馈修复过滤无效样本，并构建独立SkillEval测试集。
- **对智能测试/测试开发的影响：** 企业如果未来根据人工复核轨迹训练内部Agent，应避免把“结果正确但未按Skill执行”的轨迹当优质样本。训练集需要同时保留最终结果、Skill调用和流程证据。
- **建议动作：** 给历史成功轨迹增加一个 `skill_evidence` 字段：记录Skill ID、版本、触发步骤、关键步骤证据和最终Verifier。缺少证据的成功轨迹不要直接进入训练集。

来源：https://arxiv.org/abs/2608.02287

## 3. 行业新闻

### 1. Skill-Use发布Agent Skill可执行评测框架

- **摘要：** Benchmark包含79个真实Skills、177个可执行任务和9个领域，分别评估Skill触发、流程遵循和边界控制；最强实验配置SU仅0.613。
- **影响：** Agent Skill评测从“有没有Skill/最终是否成功”进入过程级能力测试，并暴露Harness对结果的显著影响。
- **发布时间：** 2026-08-05
- **来源：** arXiv
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是

来源：https://arxiv.org/abs/2608.04828

### 2. SKT提出可验证Skill训练数据生成方法

- **摘要：** 使用2,000个公开Skills生成4,000个任务包与27,164条验证轨迹，并通过独立SkillEval检查跨Harness的Skill使用能力。
- **影响：** Skill生态开始从手工提示资产走向可规模化训练、验证与回归的数据工程。
- **发布时间：** 2026-08-03
- **来源：** arXiv
- **重要程度：** 中高
- **热度：** 中
- **是否建议立即学习：** 从事Agent Skill、内部SOP Agent或训练数据建设的团队建议关注

来源：https://arxiv.org/abs/2608.02287

**今日暂无更多经官方、GitHub或论文原始来源核验，且与近7日归档不重复的高价值行业新增。**

## 4. 产品更新

最近24小时内未发现OpenAI、GitHub、Anthropic、Google、Playwright、Cursor、Codex、MCP等主流产品中，与近7日归档不重复且足以进入正文的正式产品更新。

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| — | 今日暂无高价值正式产品更新 | 不使用旧公告或已报道功能凑数 | 保持日报的新颖度和可检索性 |

## 5. Agent Ecosystem

### Skill Trigger Testing

Skill目录越来越大以后，首先要测的不是Skill内容本身，而是Agent能否在正确任务上发现它，并在无关任务上不误触发。

建议核心指标：`Trigger Precision / Trigger Recall / Wrong-Skill Rate`。

### Procedural Compliance Testing

Skill本质上是SOP。测试需要把Skill中的关键步骤转成可执行检查项，区分“完成任务”和“按规定完成任务”。

### Skill Boundary Testing

Skill的 `allowed-tools`、禁止动作、数据范围和环境约束都应作为负向用例。Agent最终结果正确但越过Skill边界，仍应判定失败。

## 6. 开源推荐：Agent Skills Specification

- **项目：** agentskills/agentskills
- **GitHub：** https://github.com/agentskills/agentskills
- **Star：** 24,101，2026-08-11通过GitHub API核验
- **License：** Apache-2.0
- **核心能力：** Agent Skills开放规范与文档，定义 `SKILL.md`、metadata、scripts、references、assets、compatibility及实验性的 `allowed-tools` 等结构
- **推荐指数：** 4.7/5
- **推荐理由：** 今天Skill-Use研究说明真正的问题已经不只是“如何写Skill”，而是如何稳定触发、执行和限制Skill。规范仓库适合用作企业Skill静态校验与版本治理的基础，但它不是完整的动态测试框架，仍需自行补充Trigger、Compliance与Boundary运行时测试。

来源：https://github.com/agentskills/agentskills
规范：https://agentskills.io

## 7. 企业实践

### GitHub：把Skill当成供应链资产，而不是普通Prompt文件

GitHub的 `gh skill` 已支持Skill搜索、预览、安装、更新与发布，并使用Git tag、commit SHA、tree SHA和frontmatter provenance跟踪Skill来源。GitHub官方同时明确警告：Skill并不会由GitHub验证，可能包含Prompt Injection、隐藏指令或恶意脚本，安装前应先使用 `gh skill preview` 检查内容。

这项能力本身并非今日发布，因此不计入今日产品更新；但它与今天Skill-Use研究形成了很好的工程补充：**静态供应链可信 + 运行时Skill行为测试**缺一不可。

可借鉴点：

1. Skill必须记录仓库、版本或commit SHA；
2. 发布版本尽量不可变；
3. 更新Skill先生成diff，再跑专项回归；
4. 安装前静态扫描脚本、外链和工具声明；
5. 安装后必须跑Trigger、Compliance、Boundary测试。

来源：https://github.blog/changelog/2026-04-16-manage-agent-skills-with-github-cli/

## 8. 今日工具推荐：gh skill preview

### 适用场景

- 第三方Agent Skill准入；
- Skill更新前review；
- Claude Code、Codex、Cursor、Copilot等跨Harness Skill治理；
- Skill供应链安全检查；
- 企业Skill白名单维护。

### 快速开始

```bash
gh skill search mcp
gh skill preview OWNER/REPOSITORY SKILL
gh skill install OWNER/REPOSITORY SKILL --pin <tag-or-commit>
```

第一轮检查建议至少看：

- `SKILL.md` 是否包含超范围指令；
- `scripts/` 是否执行网络、文件删除或Shell高风险操作；
- `references/` 是否引入不可信外部内容；
- `allowed-tools` 与实际脚本权限是否一致；
- 来源仓库、commit SHA和版本是否可以追溯。

安装后再进入动态Skill-Use测试。GitHub明确说明Skill未经过平台验证，因此 `preview` 只能降低风险，不能替代沙箱和运行时测试。

来源：https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/customize-cloud-agent/add-skills

## 9. 今日学习：为什么“Agent加载了Skill”仍不等于“会用Skill”？

Skill使用至少包含三个不同能力。

**Trigger**：看到任务后，Agent能否判断应该加载哪个Skill。

**Compliance**：加载后是否真正按照规定顺序、工具和检查步骤执行。

**Boundary**：遇到捷径、冲突指令或异常环境时，是否仍然遵守禁止动作和权限范围。

因此，一个Agent可能“加载对了Skill但漏步骤”，也可能“最终答案正确却完全没加载Skill”，甚至“严格执行流程但调用了禁止工具”。把三类问题混成一个最终成功率，会让Skill优化失去方向。

更适合企业回归的指标是：

`Task Success + Trigger + Compliance + Boundary`。

## 10. 趋势观察

**未来3个月，Agent Skill会从“Prompt资产管理”快速进入“可测试的程序性资产”阶段：Skill触发、流程遵循、边界控制、版本固定与跨Harness兼容性会逐渐成为企业Agent回归的标准维度。**

## 11. 30分钟 Action

### 给一个现有Skill建立第一套Skill专项测试

1. 选一个已经在使用的Skill；
2. 提取它的名称、description、关键步骤、允许工具和禁止动作；
3. 准备3条明确应触发任务；
4. 准备3条语义相似但不应触发任务；
5. 准备2条会诱导跳过关键步骤的任务；
6. 准备2条诱导调用禁止工具或超范围数据的任务；
7. 记录 `triggered_skill / steps_completed / tools_called / boundary_violation / final_result`；
8. 输出四个指标：Task Success、Trigger Recall、Compliance Rate、Boundary Violation Rate。

这10条用例就可以作为Skill版本升级后的第一套回归基线。

## 12. 值得跟进

- Skill-Use是否开放完整任务集和Harness实现；
- 79个真实Skills的来源分布与风险类型；
- Trigger与Compliance失败在不同模型上的相关性；
- 同一Skill在Claude Code、Codex、Cursor、Copilot等Harness上的排名差异；
- SKT的27,164条验证轨迹如何定义“substantially use every required skill”；
- Skill版本变化后的测试集自动生成；
- `allowed-tools` 能否从声明约束升级为Host强制执行；
- Skill中的脚本、引用文件和外链供应链风险；
- MCP Tool与Skill共同存在时的双层权限边界；
- Browser Agent Skill中的页面状态、下载和表单提交边界；
- 知识图谱节点：`Skill Trigger`、`Skill Compliance`、`Skill Boundary`、`Skill-Use`、`SKT`、`Skill Provenance`、`Harness-conditioned Capability`。

## 13. 我的备注

今天这个方向对智能测试平台比较值得落地，因为Skill很可能会成为未来测试Agent里承载SOP最自然的方式，但**不能把“写了一个Skill”当成能力已经建设完成**。

金融测试里可以先挑一个明确流程，例如“清算结果核对Skill”。它可以规定：先查汇总表，再根据汇总流水号反查清分明细，再计算金额，再核对最终状态。测试时除了看最终金额是否正确，还要检查Agent有没有跳过汇总表直接猜流水、有没有跨清算日期查数据、有没有使用未批准的查询工具。

智能测试平台可以把Skill注册表和测试资产直接绑定：

`Skill ID → Skill Version → Trigger Cases → Compliance Cases → Boundary Cases → Business Oracle`

这样Skill每次调整description、步骤或工具权限，都能自动触发对应回归。

Browser Agent场景尤其需要Trigger与Boundary测试。一个“网页回归测试Skill”不应该因为页面里出现类似描述就误触发其他高权限Skill；页面内容属于不可信数据，不能改变Skill选择和工具边界。

MCP Server准入可以把Skill作为调用上层一起测：MCP Tool本身通过准入，不代表任何Skill都可以调用它。建议绑定 `Skill → allowed MCP servers → allowed tools → business scope`，由Host或网关强制执行，而不是只写在Prompt里。

安全日志审查也适合建立Skill：规则命中后的上下文读取、证据提取、误报判断、无法判断升级人工，都可以写进SOP。重点是必须测“模型会不会真的按照这些步骤做”，而不是只看最终输出True/False。

测试计划先行可以进一步变成：**先定义Skill验收契约，再允许Agent使用Skill。** 这样后续更换模型或Harness时，可以清楚知道变化到底来自模型、Skill还是运行框架。
