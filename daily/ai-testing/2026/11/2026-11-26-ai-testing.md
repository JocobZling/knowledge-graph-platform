---
title: "AI Testing Daily Brief - 2026-11-26"
date: 2026-11-26
type: daily
category: ai-testing
tags: [skill-evaluation, agent-testing, adaptive-benchmark, governance]
source: [Uber Engineering, arXiv, Promptfoo]
status: published
summary: "Uber公开规模化Skill评测实践；CUA-SEE提出任务与验证器共同演进的评测环境。"
---
# AI Testing Daily Brief - 2026-11-26

## 1. 今日摘要
Uber于11月23日公开企业级Skill评测实践，覆盖超过5000个Skill及单月百万次调用。CUA-SEE于11月23日提交预印本，提出根据执行证据共同演进任务与验证器。今日重点是Skill生命周期治理和动态评测资产建设。最近24小时检索未发现更多满足条件的事件，扩展至7天。

## 2. 今日重点
### Uber Skill评测（11-23，企业实践，新颖度5/5）
[官方文章](https://www.uber.com/us/en/blog/optimizing-ubers-agent-skills/)
将技能作为有责任人、版本、评测及使用指标的工程资产。建议将Skill准入分为规范检查、独立环境运行、轨迹与结果评测、上线观测。官方建议样本构成为5%固定金标、80%真实任务轨迹、15%作者判断；不是通用强制比例。对测试开发而言，需验证技能是否真正改善结果而非仅增加调用量。建议做有无Skill的配对实验。

### CUA-SEE（11-23，预印本，新颖度5/5）
[原始论文](https://arxiv.org/abs/2611.30962)
根据Agent运行证据迭代任务难度、能力边界探针及可执行验证器。关注原因：固定测试集可能逐渐失去区分度。测试影响：应同时维护任务与判定器版本，保留历史基线。建议将真实失败转为新回归样本，并由独立人员审核验证器。论文尚不能证明金融业务适用性。

## 3. 行业新闻
| 事件 | 摘要及影响 | 发布时间 | 来源 | 重要程度 | 热度（编辑判断） | 立即学习 |
|---|---|---|---|---|---|---|
| Uber Skill评测 | 企业规模化技能质量治理 | 11-23 | Uber官方 | 高 | 中高 | 是 |
| CUA-SEE | 自适应计算机操作Agent评测 | 11-23 | arXiv | 高 | 中 | 是 |

今日暂无更多高价值新增。

## 4. 产品更新
未核验到满足去重和时效要求的产品更新；研究及企业实践不作为GA发布。

## 5. Agent Ecosystem
Uber的Skill Workshop体现规范、评测、发布、观测和退役的生命周期；CUA-SEE体现动态任务与验证器的共同演进。无新增独立MCP协议或安全事件入选。

## 6. 开源推荐
[Promptfoo](https://github.com/promptfoo/promptfoo)，27.7k Stars（11月26日仓库页面），MIT，推荐4.5/5。支持LLM应用评测、红队、模型对比和CI集成。成熟工具而非今日发布。

## 7. 企业实践
Uber使用Git管理技能资产，记录版本和使用情况；其仓库查询Skill消融实验中，无Skill组工具调用多13%、工具错误为6倍、耗时长31%，有Skill答案赢得87%的比较。为企业自报单场景结果，不可泛化。借鉴：技能上线后仍应量化质量和成本。

## 8. 今日工具推荐
Promptfoo：在获准的隔离环境安装 npm install -g promptfoo，执行 promptfoo init --example getting-started，进入示例目录配置获准模型或模拟接口，再执行 promptfoo eval、promptfoo view。禁止将内部数据发送外部模型；检查遥测及网络配置。

## 9. 今日学习
Skill消融测试：保持模型、Harness、任务、数据和预算一致，比较启用与禁用Skill的多次运行。分别记录任务成功、越权、工具调用、耗时与成本；盲评结果，保留随机波动。若无显著收益，考虑精简或退役。高风险安全断言不能因平均质量提高而放宽。

## 10. 趋势观察
未来三个月预计企业将逐步从Skill数量建设转向版本化质量治理与持续评测；为工程判断。

## 11. 30分钟 Action
0—5分钟选择测试计划Skill；5—10分钟准备5条合成需求及独立验收规则；10—20分钟启用/禁用各执行一次；20—25分钟比较遗漏、错误及耗时；25—30分钟保存版本、结果和待复测项。仅为初筛，正式结论需要重复实验。

## 12. 值得跟进
- [Uber官方文章](https://www.uber.com/us/en/blog/optimizing-ubers-agent-skills/) P0
- [CUA-SEE论文](https://arxiv.org/abs/2611.30962) P0
- [Promptfoo](https://github.com/promptfoo/promptfoo) P1
- 自建Prompt/Skill：测试计划消融实验模板；MCP：工具轨迹与权限断言。
- 知识图谱节点：Skill Ablation、Skill Lifecycle、Adaptive Benchmark、Verifier Evolution。
- 持续观察：动态评测的可比性与技能退役机制。

## 13. 我的备注
金融测试可先对测试计划生成Skill建立责任人、版本、适用范围、禁止动作、验收规则和执行记录。智能测试平台进一步关联任务、Skill版本、实际产出、人工复核与未完成事项，支持跨项目恢复工作。Browser Agent保留任务和验证器版本；MCP准入记录实际工具调用和副作用；安全日志审查保留脱敏证据，不记录不必要的原始内容。先做小范围验证，再决定平台化。

归档备注：已读取11月19—25日七篇日报并核对标题与摘要，主题索引读取但更新停留7月15日；WayToAGI缓存success，2026-11-25 16:37:55+08:00，已读取并用于发现Uber线索，事实回溯官方。历史主题未重复作为新增。
