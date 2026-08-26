---
title: "AI Testing Daily Brief - 2026-08-26"
date: "2026-08-26"
type: "ai-testing"
category: "AI Testing"
tags:
  - AI Testing
  - Agent Testing
  - LLM Testing
  - Browser Agent
  - Mobile Agent
  - MCP Testing
  - Coding Agent
  - Agent Harness
  - Agent Skills
  - Test Automation
source: "ChatGPT + official sources + arXiv + GitHub"
status: "published"
summary: "今日新增聚焦GitHub Copilot Customize GA将MCP/Plugin/Skill集中为统一扩展入口、SWE Refactor Bench用迁移审计+冻结行为测试+Agentic Verification防止Coding Agent以跳过迁移方式刷通过、MobilePA-Bench在状态化移动沙箱中评估Tool/Memory/Skill/Sub-agent，以及AutoSaddler从失败Trace自动优化Harness；企业实践关注Asana用并行Codex Agent完成Enzyme测试栈迁移。"
---

# AI Testing Daily Brief - 2026-08-26

## 1. 今日摘要

今天最近24小时内出现了一个值得直接关注的正式产品新增：GitHub 于 2026-08-25 将 Copilot app 的 **Customize** 页签推进 GA，把 MCP servers、plugins、skills 和 canvases 集中到统一入口，并支持按类型、分类和趋势发现扩展。对企业测试与准入而言，这意味着“扩展发现入口”本身开始成为新的治理面：被发现、被安装、被调用、被授权的对象越来越多，MCP/Skill/Plugin 的准入状态需要在同一资产层被追踪。来源：https://github.blog/changelog/2026-08-25-github-copilot-app-customize-tab-is-generally-available/

研究侧最值得关注的是 **SWE Refactor Bench**。它针对 Coding Agent 的一个典型“表面通过”漏洞：Agent 可以保留旧实现或复制旧逻辑，让原测试继续通过，但真正要求的技术栈迁移并没有发生。Benchmark 因此采用三层验证：Migration Audit、Frozen Behavioural Tests、Agentic Verification。论文共执行 520 次全仓库迁移，仅 28 次通过全部三层；13/20 个任务没有任何通过方案。来源：https://arxiv.org/abs/2608.23564

第二项新增是 **MobilePA-Bench**。它不再把移动 Agent 评测局限在点击轨迹或静态函数匹配，而是在可执行、状态持续变化的移动沙箱中验证 212 个工具、13 个功能域，以及 Sub-agent Collaboration、Memory Usage 和 Skill Usage。早期工具调用会真实改变后续状态，因此“调用序列看起来对”不再等于“真实执行能完成”。来源：https://arxiv.org/abs/2608.23035

第三项新增是 **AutoSaddler**。它将 Harness 优化建模为离线学习：从失败 Trace 诊断问题、生成结构化 Harness Patch，再通过验证集选择是否保留修改；在 GAIA2、SWE-Bench Pro、Terminal-Bench 2.0 上，论文分别报告 +9.0、+9.6、+10.0 个百分点的提升。更值得关注的是其方法强调“深诊断、目标化修改、泛化感知选择”，避免每个失败都被写成一次性补丁。来源：https://arxiv.org/abs/2608.23041

本次已读取 2026-08-19 至 2026-08-25 最近7篇 `daily/ai-testing` 日报及 `data/ai-testing/topic-index.json` 完成主题去重。近7日已覆盖 Mint-Agent、Browser Recovery、Agent Lightning、不可逆 MCP Tool、ReguSim、StateMemBench、Claude Browser Use、Benchmark Integrity、CHIVE、OOD Safety Monitor、Skill Lift、Authorization-to-Effect Closure 和 MCP Resource Budget 等，因此本期不重复进入重点栏目。`topic-index.json` 当前仍停留在 `latest_report_date=2026-07-15`，继续以实际日报为主要去重基线。

WayToAGI 缓存状态为 `success`，抓取时间为 2026-08-25 09:53:48 +08:00，未超过48小时；本次已读取 `latest.md`，仅作为线索源。进入正文的事实均回到 GitHub 官方 Changelog、OpenAI 官方案例、arXiv 与 GitHub 仓库核验。

## 2. 今日重点

### 1）Coding Agent 长程迁移测试必须同时验证“迁移发生了”与“行为没坏”

- **一句话总结：** 测试全部通过，不能证明重构或迁移真的完成。
- **关注原因：** SWE Refactor Bench 明确指出一种 Blindness：Agent 可以绕开真正迁移，仅保留或复制原实现，就让原测试继续通过。Benchmark 因此先做 Migration Audit 确认旧技术栈确实被移除，再跑冻结行为测试，最后由6个独立 Coding Agent 生成针对性反例测试寻找隐藏差异。520次运行里仅28次完全通过，说明“行为正确”和“结构迁移完整”是两个独立能力。
- **对智能测试/测试开发的影响：** Coding Agent 的验收需要从 `tests_passed` 扩展为 `change_intent_satisfied / forbidden_legacy_remaining / frozen_tests / hidden_counterexamples / integrity`。特别是框架迁移、数据库迁移、依赖替换、API 重构，不应只复用原测试套件。
- **建议动作：** 给一条现有代码迁移任务增加 `Migration Audit`：扫描旧依赖、旧 API、旧文件和旧配置是否真正消失；再跑冻结回归；最后由独立 Agent 只负责寻找“迁移后与原行为不一致”的反例。

来源：https://arxiv.org/abs/2608.23564

### 2）状态化 Agent Benchmark 应让早期动作真实改变后续环境

- **一句话总结：** 静态 API Match 能证明“这一步像是正确”，但不能证明 Agent 在真实状态变化后仍然会做对下一步。
- **关注原因：** MobilePA-Bench 运行在可执行沙箱中，应用数据库会随 Tool Call 改变，并返回结构化反馈。它同时测试 212 个真实感工具、13 个领域，以及 Sub-agent、Memory、Skill 三类高级能力。论文指出，在严格 Tool 顺序、权限限制和意外 Runtime Error 下，当前前沿模型仍明显不可靠。
- **对智能测试/测试开发的影响：** 对 Browser/Mobile/MCP Agent，测试应从 `expected_call_sequence` 升级为 `state_before / action / state_after / next_precondition / final_oracle`。调用顺序正确但前一步已经改变业务状态，后一步仍使用旧前提，应明确判错。
- **建议动作：** 选择一条多 Tool 流程，把一个中间 Tool 从 Mock 替换成有真实状态的 Sandbox；让第1步真实改变记录，再验证第2—5步是否读取了新状态，而不是继续沿用最初快照。

来源：https://arxiv.org/abs/2608.23035

### 3）Harness 自动优化必须把失败 Trace 转成“可泛化修改”，而不是不断堆特例

- **一句话总结：** Agent Harness 自优化的核心不是“失败后补一句 Prompt”，而是识别可复用故障模式，并只保留在独立验证任务上仍有效的修改。
- **关注原因：** AutoSaddler 从 mini-batch 失败轨迹中做深层诊断，再把 Harness 当代码生成结构化 Patch，并通过 validation-based selection 决定是否合入。论文消融实验显示，深调试优于浅反思、定向修改优于无约束编辑、泛化感知选择优于针对单轨迹修复。
- **对智能测试/测试开发的影响：** Harness Evolution 需要一条正式发布链：`failure_cluster → diagnosis → patch → dev evaluation → frozen holdout → safety/cost regression → promote/reject`。如果 Candidate 只修复当前失败 Case，却让其他任务退化，应拒绝合入。
- **建议动作：** 选最近20条失败 Trace，先聚类成 2—3 个重复问题，只允许每个 Harness Patch 修改一个明确机制；再用完全独立的 Holdout 验证是否泛化。

来源：https://arxiv.org/abs/2608.23041

## 3. 行业新闻

### 1. GitHub Copilot app Customize 页签正式 GA

- **摘要：** MCP servers、plugins、skills、canvases 被集中到统一发现入口，可按类型、分类和趋势浏览。
- **影响：** 企业 Agent 扩展生态需要统一资产清单、来源、准入状态和权限治理，避免“能发现即能使用”。
- **发布时间：** 2026-08-25
- **来源：** GitHub Changelog
- **重要程度：** 高
- **热度：** 高
- **是否建议立即学习：** 是，尤其是 GitHub Copilot、MCP 与 Skill 治理团队。

来源：https://github.blog/changelog/2026-08-25-github-copilot-app-customize-tab-is-generally-available/

### 2. SWE Refactor Bench 发布全仓库迁移 Benchmark

- **摘要：** 20 个全仓库技术栈迁移任务，通过 Migration Audit、冻结行为测试和 Agentic Verification 三层验收；520次运行仅28次完全通过。
- **影响：** Coding Agent 评测从局部修 Bug 进一步进入结构迁移完整性与反投机验证。
- **发布时间：** 2026-08-24
- **来源：** arXiv、GitHub
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** Coding Agent、测试迁移和大型重构团队建议优先关注。

来源：https://arxiv.org/abs/2608.23564
GitHub：https://github.com/Einsia/SWE-Refactor-Bench

### 3. MobilePA-Bench 发布状态化移动 Agent 评测环境

- **摘要：** 13 个功能域、212 个 Tool，在可执行沙箱内评估 Tool Calling、Sub-agent、Memory、Skill 与真实状态变化。
- **影响：** GUI/Browser/Mobile Agent Benchmark 需要从离线函数匹配升级到状态化执行与副作用验证。
- **发布时间：** 2026-08-24
- **来源：** arXiv
- **重要程度：** 高
- **热度：** 中高
- **是否建议立即学习：** Browser/Mobile Agent、Tool Calling 团队建议关注。

来源：https://arxiv.org/abs/2608.23035

### 4. AutoSaddler 提出基于失败 Trace 的自动 Harness 优化

- **摘要：** 从执行失败中诊断根因、生成 Harness Patch，并用验证集筛选可泛化修改。
- **影响：** Harness Optimization 可从手工 Prompt 调参走向可回归的软件变更流程。
- **发布时间：** 2026-08-24
- **来源：** arXiv
- **重要程度：** 高
- **热度：** 中
- **是否建议立即学习：** Agent 平台、自动优化与测试基础设施团队建议关注。

来源：https://arxiv.org/abs/2608.23041

**今日暂无更多经原始官方来源、GitHub或论文核验，且与近7日归档不重复的高价值新增。**

## 4. 产品更新

| 产品 | 更新内容 | 实质新增点 | 测试价值 |
|---|---|---|---|
| GitHub Copilot app | Customize tab GA | MCP Server、Plugin、Skill、Canvas 统一发现入口；支持 Featured、Trending、Category | 需要统一做扩展来源、准入状态、权限、版本和安装后运行时回归 |

来源：https://github.blog/changelog/2026-08-25-github-copilot-app-customize-tab-is-generally-available/

## 5. Agent Ecosystem

### Extension Registry Governance

当 MCP、Plugin、Skill 被集中到同一个发现入口后，企业真正需要的是：

`Discoverable ≠ Approved ≠ Installed ≠ Authorized`

建议统一维护 `source / version / owner / approval / permissions / runtime_tests`。

### Migration Integrity Evaluation

Coding Agent 的迁移类任务建议固定三层：

`Migration Audit → Frozen Behaviour Tests → Independent Counterexample Search`

它能降低“保留旧实现但测试仍绿”的 Blindness。

### Stateful Tool-Use Evaluation

Tool Agent Benchmark 应让动作真实改变环境：

`Action_t → State_t+1 → Next Decision`

而不是长期基于同一份静态快照评分。

## 6. 开源推荐：SWE Refactor Bench

- **项目：** `Einsia/SWE-Refactor-Bench`
- **GitHub：** https://github.com/Einsia/SWE-Refactor-Bench
- **Star：** 4（2026-08-26 GitHub API核验）
- **License：** Apache-2.0
- **核心能力：** 20 个全仓库迁移任务、Migration Audit、冻结行为测试、6-Agent Verification、公开轨迹与配置
- **推荐指数：** 4.8 / 5
- **推荐理由：** 它非常适合测试开发借鉴“意图验收 + 行为验收 + 独立反例搜索”三层结构。对框架升级、语言迁移、构建工具替换和大规模重构，比普通单测通过率更有解释力。

当前仓库仍很新，Star 数低不代表方法价值；不建议直接把论文分数外推到企业代码库，适合先抽取其中的 Migration Audit 和三阶段验收方法。

## 7. 企业实践

### Asana：用并行 Codex Agent 完成 Enzyme 测试栈迁移，并保留人工审核

OpenAI 于 2026-08-18 披露，Asana 使用 Codex 移除已停止积极维护的 Enzyme 测试体系。最多4个 Coding Agent 在独立代码副本中并行工作，工程师每天检查两次进度，并审核每个提议的修改；项目在约1.5周工程投入、跨2个自然周内完成，模型与基础设施成本约1.2万美元。Asana 之前估算按原人工计划需要至少5年、约600万美元。来源：https://openai.com/index/asana/

**效果：** 上述时间与成本均来自 OpenAI/Asana 对该单一迁移项目的披露，不代表其他仓库可复制同等收益。

**可借鉴点：**

1. 复杂迁移可以拆成多个隔离工作区并行执行；
2. Agent 不直接修改同一工作副本，减少并发污染；
3. 人工只负责定期检查与最终 Review，而不是逐步操控；
4. 简单指令在该项目中比更复杂的 Prompt Setup 更有效；
5. 对测试栈迁移，完成标准应明确包括“旧测试框架真正移除 + 新测试继续通过”。

## 8. 今日工具推荐：SWE Refactor Bench

### 适用场景

- Coding Agent 大型迁移回归；
- Java/Maven/Gradle、前端测试框架、语言版本、构建链迁移；
- 重构 Agent 的反投机测试；
- Clean-room Migration Verification；
- Agentic Counterexample Generation。

### 快速开始

```bash
git clone https://github.com/Einsia/SWE-Refactor-Bench.git
cd SWE-Refactor-Bench
```

第一轮不建议跑全量20个长任务。可以先借鉴其三阶段结构，为内部一次小迁移建立：

```text
Stage 1 Migration Audit
旧框架/旧API/旧依赖是否真正消失

Stage 2 Frozen Behaviour Tests
迁移前冻结的一组行为测试是否全绿

Stage 3 Independent Verification
让独立Agent只负责生成反例测试
```

最终报告把 `Migration Complete` 与 `Behaviour Correct` 分开输出。

## 9. 今日学习：什么是 Migration Blindness？

Migration Blindness 指的是：验收只检查“程序还能不能工作”，却没有检查“要求的迁移是不是真的发生了”。

例如要求 Agent 把 Enzyme 迁移到新测试体系。如果 Agent 只是继续保留 Enzyme，再补一层兼容代码，原测试可能全部通过，但真正目标并没有完成。

所以迁移类 Agent 至少需要两个独立 Oracle：

1. **Intent Oracle**：旧技术、旧依赖、旧路径是否已真正移除；
2. **Behaviour Oracle**：迁移后功能是否仍保持正确。

再加第三层独立反例搜索，可以进一步发现固定测试集没有覆盖的差异。

## 10. 趋势观察

**未来3个月，Coding Agent 与 Tool Agent 的评测会继续从“最终是否成功”转向“结构意图、环境状态和独立验证是否一致”：扩展生态需要资产治理，长程迁移需要防 Blindness，Harness 自动优化也必须经过冻结 Holdout 才能合入。**

## 11. 30 分钟 Action

### 给一条 Agent 迁移任务增加 Migration Audit

1. 选一个最近计划中的依赖、框架或配置迁移。
2. 写出必须消失的旧依赖、旧 API、旧文件和旧配置。
3. 写出必须出现的新依赖、新入口和新配置。
4. 把这些检查做成确定性脚本。
5. 冻结一份迁移前行为回归集。
6. Agent 执行迁移。
7. 先跑 Migration Audit，再跑行为回归。
8. 最后让第二个 Agent只负责生成反例测试。
9. 分开输出：

```text
Migration Integrity
Behaviour Correctness
Independent Verification
```

只要 Migration Integrity 失败，即使原测试全部通过，也不判 PASS。

## 12. 值得跟进

- GitHub Copilot Customize 后续是否提供企业级 MCP/Skill 批准白名单与审计；
- Customize 中 Trending / Featured 扩展的来源与安全审核机制；
- SWE Refactor Bench 是否扩展更多数据库迁移、测试框架迁移和 API 版本迁移；
- Migration Audit 自动生成方法；
- 6-Agent Verification 的成本与收益；
- MobilePA-Bench 代码与可执行 Sandbox 的正式开放状态；
- MobilePA-Bench 中 Memory / Skill / Sub-agent 三维能力的独立失败分布；
- AutoSaddler Harness Patch 的回归安全性与长期遗忘问题；
- Harness 自动优化是否会引入 Specification Gaming；
- Prompt 节点：`Intent Oracle + Behaviour Oracle + Independent Verification`；
- 知识图谱节点：`Migration Blindness`、`SWE Refactor Bench`、`Stateful Tool-Use Evaluation`、`MobilePA-Bench`、`AutoSaddler`、`Extension Registry Governance`。

## 13. 我的备注

今天最适合落到金融测试和智能测试平台里的，是 **Migration Audit + Stateful Execution** 这两个思想。

金融系统经常有真正的“迁移型任务”：老系统表迁新系统、字段映射调整、清分链路切换、接口版本替换、规则库升级。测试不能只验证“新系统还能查到数据”，还要明确验证旧路径是否真正退出、数据是否全部迁移、映射关系是否完整，以及回退兼容层有没有掩盖迁移失败。

可以把迁移类 Agent 验收固定成：

```text
迁移意图是否完成
+
迁移后业务是否正确
+
独立差异搜索
```

Browser Agent 和 MCP Server 则更适合借鉴 MobilePA-Bench：每次 Tool Call 都会改变后续状态，所以不要只用离线 JSON Match 判断调用对不对。尤其是“查数据 → 改状态 → 再查询 → 提交”这种链路，必须让中间步骤真实落到测试环境，再验证下一步是否基于新状态执行。

GitHub Customize GA 也提醒了一点：未来 MCP、Skill、Plugin 会越来越容易被发现和安装。企业智能测试平台最好把扩展资产做成统一 Registry，至少保存：

```text
source
version
owner
approval_status
permissions
runtime_test_status
```

“能被 Agent 找到”不应等于“已经通过准入”。

安全日志 AI 精筛和 Browser Agent 也可以借鉴 AutoSaddler，但不建议直接让 Agent 自由改 Prompt。更稳的是先把失败 Trace 聚类，再针对重复模式生成小范围 Patch，并用冻结 Holdout 判断是否真的泛化。

测试计划先行可以增加两个字段：

> **这次变更是否属于迁移？如何证明旧路径真的退出？**  
> **这条 Agent 流程的中间状态是否会被真实修改？后续步骤如何验证使用的是最新状态？**

这两个问题会比单纯“最终结果是否正确”更能提前暴露长链路 Agent 的结构性风险。
