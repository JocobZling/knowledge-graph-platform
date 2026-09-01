---
title: "AI Testing Daily Brief"
date: "2026-07-26"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Evaluation
  - Eval Engineering
  - Coding Agent
  - Agent Governance
source: "ChatGPT"
status: "published"
summary: "今日新增聚焦Eval Engineering Skill将生产Trace转为可执行评测，以及Claude Opus 5进入GitHub Copilot后对复杂长程Coding Agent任务的迁移回归要求。"
---

# AI Testing Daily Brief - 2026-07-26

## 今日摘要

今天最值得关注的新增是 LangChain 于 2026 年 7 月 22 日发布 Eval Engineering Skill。该 Skill 会读取 Agent 仓库结构和生产 Trace，识别值得测试的能力，与使用者迭代确认后生成 Harbor 格式的可执行评测。它把“从线上失败中提取回归用例”从依赖人工经验的工作，推进为可由 Coding Agent 辅助完成的工程流程。

GitHub 于 2026 年 7 月 24 日将 Claude Opus 5 接入 Copilot，覆盖 VS Code、Copilot CLI、cloud agent、GitHub Copilot app、github.com、JetBrains 等入口。GitHub 将其定位于复杂、长时间运行、需要多工具协调和回归验证的编码任务；企业实际使用前仍需采用固定 Harness、任务集和预算进行迁移回归。

本次已读取 2026 年 7 月 19 日至 24 日的现有日报，并确认 7 月 25 日归档不存在；去重时同时参考 `data/ai-testing/topic-index.json`。Alipay-PIBench、GitHub Code Quality、IssueBench、OpenWiki、Gemini 3.5 Flash Cyber、MCPEvol-Bench、BioSecBench、GitHub MCP Conformance、Agent审批控制等近日报主题未重复进入正文。

WayToAGI 缓存状态为 success，抓取时间为 2026 年 7 月 25 日 11:39，距本次执行不足 48 小时。本次只将其用于发现 Eval Engineering Skill 和 Claude Opus 5 线索，正文事实均回到 LangChain、GitHub及官方项目仓库核验。

## 今日重点

### 1. 生产Trace正在被直接转化为可执行Agent评测

- **一句话总结：** Eval Engineering Skill读取Agent仓库、工具定义和生产Trace，提出待评能力，并生成包含任务说明、容器环境和Verifier的Harbor评测任务。
- **为什么值得关注：** 企业Agent最难的环节通常不是跑一次Benchmark，而是把线上失败稳定复现为回归用例。若只人工阅读Trace，容易遗漏重复问题，也难以持续维护环境、数据和验证器。
- **对智能测试或测试开发的影响：** 测试平台可以把“Trace分析—能力识别—任务设计—环境模拟—Verifier构建—版本回归”连成闭环。关键写操作、付费接口和生产数据可模拟，避免评测直接影响生产。
- **建议动作：** 选择一个已有生产Trace的简单Agent，先让Skill提出3个值得测试的能力，只批准其中1个，并人工审查生成的Verifier是否存在奖励投机路径。

来源：https://www.langchain.com/blog/towards-automating-eval-engineering

### 2. Agent评测自动化仍必须保留人工确认和Verifier复核

- **一句话总结：** LangChain明确指出，一次性自动生成的评测往往不可靠，最佳结果来自用户对能力范围、环境模拟和Verifier进行迭代确认。
- **为什么值得关注：** 自动生成Verifier可能只检查表面代理指标，例如引用数量、输出关键词或文件是否存在，却未验证真实业务动作，Agent可能通过捷径获得高分。
- **对智能测试或测试开发的影响：** Eval生成Agent本身也需要测试。应同时查看被测Agent轨迹和Verifier轨迹，确认评分依据没有暴露答案、允许虚假声明或把代理指标当作业务成功。
- **建议动作：** 对每个新Verifier执行至少三类反例：只输出正确结论但不执行动作、执行错误动作但生成正确文本、引用大量无关证据，验证其不能获得满分。

来源：https://www.langchain.com/blog/towards-automating-eval-engineering

### 3. Claude Opus 5进入Copilot后需要复杂任务专项迁移回归

- **一句话总结：** Claude Opus 5已进入GitHub Copilot，GitHub将其面向复杂长程编码、多工具协调、自主修改和回归验证任务。
- **为什么值得关注：** 复杂模型上线可能改变工具调用顺序、修改范围、验证习惯、执行时间和成本。GitHub的早期测试结论不能替代企业内部代码库与安全规则上的验证。
- **对智能测试或测试开发的影响：** 模型迁移测试需要分开评估简单任务和复杂任务，尤其关注不必要修改、工具调用纪律、回归验证质量、网络安全内容拦截及单位成功任务成本。
- **建议动作：** 从历史任务中选择10个多文件修改、5个跨工具任务和5个安全相关任务，对当前模型与Opus 5进行盲测，固定Prompt、权限、Harness和预算。

来源：https://github.blog/changelog/2026-07-24-claude-opus-5-is-now-available-in-github-copilot/

## 行业新闻

### 1. LangChain发布Eval Engineering Skill

- **摘要：** Skill从代码仓库和Agent Trace提取能力需求，交互确认后生成Harbor格式的容器化评测任务和Verifier。
- **影响：** Agent评测工程开始从手工编写用例，转向基于生产证据的半自动构建。
- **发布时间：** 2026-07-22
- **来源：** LangChain Blog、langchain-ai/langchain-skills
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** 是

### 2. Claude Opus 5进入GitHub Copilot

- **摘要：** 新模型覆盖VS Code、CLI、cloud agent、Copilot app、github.com及主流IDE，面向复杂、长程、多工具编码任务。
- **影响：** 企业需要为高能力模型建立独立的复杂任务迁移回归和安全拦截验证。
- **发布时间：** 2026-07-24
- **来源：** GitHub Changelog
- **重要程度：** 中高
- **热度：** 高
- **是否建议立即学习：** 按需

今日暂无更多高价值新增。

## 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| LangChain Eval Engineering Skill | 从仓库和Trace辅助生成评测 | 自动映射Agent表面、提出能力、构造Harbor任务、环境与Verifier | 将线上失败更快沉淀为可复现回归用例 |
| GitHub Copilot / Claude Opus 5 | 新增模型入口 | 支持复杂长程编码、多工具协调和回归验证 | 可开展复杂任务质量、成本、安全与执行范围迁移回归 |

## Agent Ecosystem

### Trace-to-Eval Workflow

生产Trace不应只用于故障排查。可将其中的重复请求、失败工具调用、错误状态变更和人工纠正记录转换为任务数据、环境状态和Verifier，形成持续回归集。

### Verifier-Aware Evaluation

评测不仅要观察被测Agent，还应观察Verifier如何给分。Verifier必须验证真实状态变化和证据，防止Agent通过输出文本、无关引用或暴露答案获得高分。

### Model-Specific Regression Suites

同一Agent Harness在不同模型上可能呈现不同错误分布。高能力模型应重点测试修改范围、工具调用纪律、运行成本、过度自主执行和高风险内容拦截，而不只是通用成功率。

## 开源推荐

### Harbor

- **GitHub：** https://github.com/harbor-framework/harbor
- **Star：** 约2.2k，2026年7月26日联网核验
- **License：** Apache-2.0
- **核心能力：** 在容器环境中运行Agent评测，支持Claude Code、Codex CLI、Copilot CLI、Gemini CLI等多类Agent；可接入Terminal-Bench、SWE-Bench、金融和工具调用类数据集，并记录轨迹、产物、奖励与错误。
- **推荐指数：** 4.6/5
- **推荐理由：** Harbor把任务、环境和Verifier封装为稳定单元，适合固定环境后对比模型、Prompt、工具和Agent版本；它也是Eval Engineering Skill的输出格式，便于从Trace生成任务后直接执行。

## 企业实践

### LangChain：用仓库上下文和生产Trace构建评测闭环

- **企业：** LangChain
- **做法：** 先扫描Agent仓库中的Prompt、模型、工具、Skill和Hook，再从Trace中观察真实参数、结果和错误；与用户确认待评能力后，生成容器化任务与Verifier，并通过轨迹复核改进评测。
- **效果：** 评测环境可保持稳定，同时更换模型、Prompt、工具或Agent版本进行直接比较；生产中的重复失败也能逐步沉淀为持续回归集。
- **可借鉴点：** 自动生成评测不能完全无人值守。领域人员应批准能力范围、确认哪些依赖需要模拟，并复核Verifier是否真正验证业务结果。

## 今日工具推荐

### Eval Engineering Skill

**适用场景：**

- 已有Agent代码库但缺少系统评测；
- 有生产Trace但难以整理回归用例；
- 需要比较模型、Prompt、工具或Agent版本；
- 希望生成容器化、可重复执行的测试环境；
- 需要为失败工具调用或错误状态变更建立回归。

**快速开始：**

```bash
npx skills add langchain-ai/langchain-skills --skill eval-engineering --yes
```

在目标Agent仓库中启动支持Skill的Coding Agent，并要求它先检查Agent结构、提出待评能力，再等待人工选择。首个任务建议只覆盖一个简单能力，并将生产写操作替换为模拟工具。

## 今日学习

### 什么是Verifier Reward Hacking？

Verifier Reward Hacking是指Agent没有真正完成业务任务，却利用评分器的漏洞获得高分。例如：任务要求实际创建记录，Verifier却只检查输出中是否出现“创建成功”；任务要求引用权威证据，Verifier只统计链接数量；任务要求修改配置，答案文件却被放在Agent可读取的位置。

防范方法是优先检查环境最终状态、真实工具轨迹和不可伪造证据，并用失败反例主动攻击Verifier。评测上线前，应验证“只说不做”“做错但描述正确”“堆积无关证据”都无法通过。

## 趋势观察

未来3个月，Agent评测工程会从“人工写Benchmark”转向“生产Trace挖掘＋半自动任务生成＋人工复核Verifier”的持续闭环，但完全自动化仍会受奖励投机和环境失真限制。

## 30分钟 Action

### 从一条生产Trace生成首个回归任务

1. 选择一条已人工确认的Agent失败Trace。
2. 写清期望动作、最终状态和禁止行为。
3. 将生产依赖替换为只读数据或模拟工具。
4. 生成一个Harbor任务目录，包含instruction、environment和tests。
5. 设计三个反例：只输出结论、不执行动作；执行错误动作；使用无关证据。
6. 运行被测Agent和三个反例，确认只有真实完成任务才能通过。
7. 将任务加入模型、Prompt和工具升级回归集。

## 值得跟进

- Eval Engineering Skill对复杂生产Trace的聚类能力；
- 自动生成Verifier的奖励投机率；
- Harbor任务格式在金融Agent评测中的适配；
- Claude Opus 5在复杂长程任务中的单位成功成本；
- Opus 5对安全测试和网络安全请求的误拦截率；
- 同一任务在Codex、Claude Code和Copilot CLI上的Harness差异；
- Browser Agent生产Trace到可复现页面状态的转换；
- MCP工具调用Trace如何转换为版本兼容回归；
- 安全日志Agent的证据状态和Verifier设计。

## 我的备注

对当前金融测试平台，最适合优先转成评测的并不是所有历史日志，而是已经人工确认的典型失败：金额字段选错、清算日期错位、状态过滤遗漏、重复流水未去重、数据版本过期。每条任务都应由确定性代码检查数据库最终结果，不能只评分Agent的文字解释。

Browser Agent的Trace转评测需要额外保存页面版本、账号初始状态、业务时间和关键DOM状态，否则同一任务无法稳定复现。可将真实页面动作替换为本地镜像或受控测试站点。

MCP Server准入可把超时、Schema变化、旧数据和权限拒绝等真实Trace生成容器化任务，固定工具响应后比较不同模型与Prompt的处理方式。

安全日志审查中，Verifier应检查Agent引用的日志行、时间窗口、规则版本和最终处置动作。测试计划先行时，应先写验收证据和禁止捷径，再让Skill辅助生成环境与用例，避免自动化评测只得到一个看似完整但不可依赖的分数。

## 相关链接

- [LangChain Eval Engineering Skill](https://www.langchain.com/blog/towards-automating-eval-engineering)
- [LangChain Skills](https://github.com/langchain-ai/langchain-skills)
- [Claude Opus 5 in GitHub Copilot](https://github.blog/changelog/2026-07-24-claude-opus-5-is-now-available-in-github-copilot/)
- [Harbor](https://github.com/harbor-framework/harbor)
