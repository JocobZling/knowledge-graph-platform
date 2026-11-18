---
title: "AI Testing Daily Brief - 2026-11-18"
date: "2026-11-18"
type: "daily-brief"
category: "ai-testing"
tags: ["Agent Testing", "MCP", "Evals", "Copilot", "Security", "Audit"]
source:
  - "https://github.blog/changelog/2026-11-17-github-copilot-evals-is-now-in-public-preview/"
  - "https://github.blog/changelog/2026-11-17-secret-scanning-on-demand-for-developers-and-agents/"
  - "https://github.blog/changelog/2026-11-17-monitor-github-copilot-activity-through-microsoft-agent-365/"
  - "https://github.blog/changelog/2026-11-17-track-copilot-code-review-outcomes-in-repository-insights/"
  - "https://github.blog/changelog/2026-11-17-copilot-code-review-adaptive-effort-secret-detection-and-more/"
  - "https://github.com/promptfoo/promptfoo"
  - "https://microsoft.github.io/vally/"
status: "published"
summary: "Copilot Evals公开预览，Agent按需凭据扫描、Agent 365审计、代码审查结果指标和自适应审查同步更新。"
---
# AI Testing Daily Brief - 2026-11-18

去重：已读取11月11—17日七篇日报和主题索引（索引仍停在7月15日）；WayToAGI缓存成功，11月17日15:57:12北京时间抓取，已读取。优先采用11月17日官方新增，区别于昨日模型比较及此前日志扫描。

## 1. 今日摘要
Copilot Evals进入公开预览，可对Skill、自定义Agent和MCP工具进行可重复行为评测。[S1] 按需凭据扫描将规则与模型检测引入开发过程。[S2] Microsoft Agent 365接入Copilot活动审计。[S3] 仓库级审查结果指标与自适应审查提供新的质量治理入口。[S4][S5]

## 2. 今日重点
### Copilot Evals（11-17，新颖度5/5）
通过/eval创建、运行和比较评测，每次运行需批准，底层采用Vally。与昨日模型比较不同，它面向具体Agent、Skill和MCP行为。建议固定业务任务、预期工具调用及独立结果证据，修改后复跑。[S1]
### Agent按需凭据扫描（11-17，新颖度5/5）
公开预览，启用后将500多种凭据类型检测及专用模型加入安全审查，覆盖CLI、app、SDK及PR。默认关闭，仅检测变更而非历史，消耗AI credits。建议分别测规则命中、模型识别和漏扫。[S2]
### Agent 365审计（11-17，新颖度5/5）
支持将多个Copilot客户端的会话、提示、回答及Tool调用活动汇入Agent 365；适用于Enterprise Cloud且使用Enterprise Managed Users的客户。建议验证审计完整性及脱敏、权限和留存。[S3]

## 3. 行业新闻
| 标题 | 摘要 | 影响 | 日期 | 来源 | 重要度 | 热度（定性） | 立即学习 |
|---|---|---|---|---|---|---|---|
| Copilot Evals | 可重复评测Agent/Skill/MCP | 准入回归 | 11-17 | [S1] | 高 | 高 | 是 |
| 按需凭据扫描 | 开发过程中扫描变更 | 安全左移 | 11-17 | [S2] | 高 | 中 | 是 |
| Agent 365审计 | 集中查看多客户端活动 | 治理 | 11-17 | [S3] | 高 | 中 | 是 |
| Repository insights | 审查意见处理率及覆盖指标 | 质量度量 | 11-17 | [S4] | 高 | 中 | 是 |
| Adaptive review | 按风险选择审查深度 | 风险分级 | 11-17 | [S5] | 中高 | 中 | 是 |

## 4. 产品更新
| 产品 | 更新 | 实质新增 | 测试价值 |
|---|---|---|---|
| Copilot Evals | 公开预览 | 行为评测与前后比较 | 回归 |
| Secret scanning | 按需扫描预览 | 开发阶段检测变更 | 漏报与覆盖 |
| Agent 365 | Copilot审计接入 | 多客户端活动 | 审计 |
| Repository insights | 审查结果指标 | 分子分母及不可评价排除 | 指标口径 |
| Copilot review | Adaptive预览 | Lite/Balanced/Max自动选择，不超过策略上限 | 风险分级 |

## 5. Agent Ecosystem
Vally是Copilot Evals的本地扩展评测引擎；MCP工具行为纳入可重复评测。Agent 365集中治理Tool轨迹。无新增Browser Agent专项发布入选，不将Research Skill归类为测试工具。[S1][S3][S7]

## 6. 开源推荐
[Promptfoo](https://github.com/promptfoo/promptfoo)，约27.4k Stars（11月18日仓库页面），MIT，支持声明式LLM/Agent评测、红队与CI，推荐4.5/5。成熟工具，非今日新发布。[S6]

## 7. 企业实践
GitHub将行为评测、变更安全扫描及审查反馈指标嵌入开发流程。审查指标包含意见处理、高严重度意见处理、无后续人工变更请求及未经过Copilot审查的合并PR。尚无独立缺陷逃逸率改善数据；意见处理率不等于缺陷识别准确率。[S4]

## 8. 今日工具推荐
Copilot Evals：VS Code安装官方扩展或CLI安装官方插件，执行/eval定义合成任务、批准运行、修改后复跑；预览功能，内网先核对数据流。[S1]

## 9. 今日学习
轨迹评测与结果评测：最终答案正确不代表工具调用正确。分别验证工具选择、参数、权限、实际副作用和最终答案；重复运行记录通过率，缺证据判INCONCLUSIVE。评分器应独立于被测Agent。

## 10. 趋势观察
未来3个月，预计Agent评测将逐步从离线模型对比进入Skill/MCP变更回归与CI门禁；这是判断而非既成标准。

## 11. 30分钟 Action
0—5分钟定义负责人查询任务；5—12分钟准备正常、歧义、越权三类合成样本；12—20分钟记录工具选择、参数与API响应；20—27分钟修改提示后复跑；27—30分钟比较独立断言。验收：越权请求不返回数据，正确答案与API一致。

## 12. 值得跟进
文章：[S1]—[S5]；项目：[Promptfoo][S6]、[Vally文档][S7]；自建Skill评测模板；MCP准入轨迹断言；知识图谱节点Agent Trajectory Evaluation、Review Coverage、Audit Streaming；持续跟踪Evals GA、12月14日Copilot模型退役。无充分核验的新论文入选。

## 13. 我的备注
测试经理助手可将Dify工作流视为被测系统，通过获批业务API做黑盒评测，不必为评测迁移架构。金融测试将权限拒绝设为硬断言；Browser Agent需独立核验后台副作用；MCP准入检查工具参数及权限；日志脱敏平台可借鉴规则与AI双通道但应明确各自覆盖；测试计划先定义证据和阈值，再执行。

## 来源
[S1]: https://github.blog/changelog/2026-11-17-github-copilot-evals-is-now-in-public-preview/
[S2]: https://github.blog/changelog/2026-11-17-secret-scanning-on-demand-for-developers-and-agents/
[S3]: https://github.blog/changelog/2026-11-17-monitor-github-copilot-activity-through-microsoft-agent-365/
[S4]: https://github.blog/changelog/2026-11-17-track-copilot-code-review-outcomes-in-repository-insights/
[S5]: https://github.blog/changelog/2026-11-17-copilot-code-review-adaptive-effort-secret-detection-and-more/
[S6]: https://github.com/promptfoo/promptfoo
[S7]: https://microsoft.github.io/vally/
