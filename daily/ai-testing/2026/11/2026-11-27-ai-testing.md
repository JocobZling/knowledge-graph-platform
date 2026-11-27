---
title: "AI Testing Daily Brief - 2026-11-27"
date: 2026-11-27
type: daily
category: ai-testing
tags: [Agent-Evaluation, ShellBench, LLM-Testing, Safety, DeepEval]
source:
  - https://arxiv.org/abs/2611.32946
  - https://research.google/blog/verifiable-social-reasoning-for-llm-assistants/
  - https://github.com/confident-ai/deepeval
status: published
summary: "ShellBench比较模型与Harness在真实工作任务中的表现；Google公开可验证社会推理评测。"
---
# AI Testing Daily Brief - 2026-11-27

## 1. 今日摘要
ShellBench于11月24日提交预印本，使用100项日常任务同时评估正确性和安全性。Google Research于11月25日公开社会推理评测框架及超过2万条生成样本。两项研究提示，Agent验收应覆盖真实任务、信息偏差和安全约束，而非仅检查最终答案。

## 2. 今日重点
### ShellBench（11-24，预印本，新颖度5/5）
[原始论文](https://arxiv.org/abs/2611.32946)。研究比较12个模型、5种Harness、3种推理级别，共53100次运行。GPT-6 Astra通过率62.5%，GPT-6.1 Sol为61.4%，后者估算单次Token成本约为前者六分之一。五种Harness的pass@1介于42.9%和45.2%。该实验中模型差异大于Harness差异，不代表所有业务环境均如此。建议固定任务、数据和预算，对模型与Harness进行交叉对照，并分别记录正确性、安全性和成本。
### Google可验证社会推理（11-25，研究发布，新颖度5/5）
[官方文章](https://research.google/blog/verifiable-social-reasoning-for-llm-assistants/)。通过多Agent模拟预设隐藏动机，测试助手能否从用户的片面叙述推断真实情况；评估12个LLM并公开超过2万条样本。研究发现主观叙述和先入为主的表达会降低判断质量，多轮对话不一定改善结果。建议将同一业务事实改写为中性、误导和信息不足三种输入，检查助手是否核验事实而非迎合前提。模拟动机不能等同于真实人的心理状态。

## 3. 行业新闻
|事件|摘要与影响|日期|来源|重要程度|热度（定性）|立即学习|
|---|---|---|---|---|---|---|
|ShellBench|真实工作空间任务同时考核安全与正确性|11-24|[arXiv](https://arxiv.org/abs/2611.32946)|高|中高|是|
|Google社会推理评测|通过可控隐藏事实研究用户叙述偏差|11-25|[Google Research](https://research.google/blog/verifiable-social-reasoning-for-llm-assistants/)|高|中|是|
今日暂无更多高价值新增。

## 4. 产品更新
未核验到符合全部门槛的商业产品更新。上述两项属于研究成果，不视为GA。

## 5. Agent Ecosystem
ShellBench提供模型、执行框架和推理预算的交叉评测思路。Google的模拟框架提供可控事实与用户叙述分离的设计。未选入新的MCP协议、独立Skill或Browser Agent产品更新。

## 6. 开源推荐
[DeepEval](https://github.com/confident-ai/deepeval)，约19.7k Stars，Apache-2.0（11月27日仓库页面核验）。Python LLM评测框架，适合建立回归指标及测试用例。推荐4.4/5；成熟工具而非今日发布。

## 7. 企业实践
Google Research使用模拟框架和数据集辅助自身模型开发，已公开研究结果，未披露生产缺陷下降率。可借鉴：将真实状态与用户叙述分离，建立可重复的偏差测试。

## 8. 今日工具推荐
DeepEval。在批准的隔离环境执行 pip install deepeval，使用 deepeval test run test_agent.py 运行自建测试文件；先使用合成数据及本地确定性断言，模型评分器需另行配置获准服务。

## 9. 今日学习
成对反事实测试：保持真实业务状态不变，仅修改用户表达或无关信息，观察Agent是否仍遵循同一业务规则。若真实事实变化，则应相应改变答案。测试时同时检查最终回答、工具调用和业务副作用，避免把语言一致性误当作正确性。

## 10. 趋势观察
未来三个月预计Agent评测将更多采用任务级正确性、安全性和成本联合指标；属于工程判断。

## 11. 30分钟 Action
0–5分钟：准备固定测试执行统计数据。5–10分钟：编写中性、错误前提、信息不足三种问句。10–20分钟：每种执行三次并记录调用。20–25分钟：核对统计、权限和成本。25–30分钟：记录失败及复测计划。验收：不得迎合错误统计或越权；未执行不标PASS。

## 12. 值得跟进
P0：[ShellBench](https://arxiv.org/abs/2611.32946)、[Google研究](https://research.google/blog/verifiable-social-reasoning-for-llm-assistants/)；P1：[DeepEval](https://github.com/confident-ai/deepeval)。建议自建成对反事实Prompt、测试计划审查Skill、MCP工具权限断言。知识图谱节点：Counterfactual Testing、User Framing Bias、Cost per Successful Task。持续观察公开任务集及独立复现。

## 13. 我的备注
测试经理助手可先建立错误前提问答测试；多Agent开发团队应按角色分别统计成本和验收质量，而不是只比较模型价格。Browser Agent和MCP写入能力应检查实际副作用；安全日志只留必要脱敏证据。测试计划应提前定义独立预期及安全门禁。

归档备注：已读取11月20—26日七篇日报及主题索引（索引停留7月15日，以实际日报为准）；WayToAGI缓存success，11月26日16:21:42更新，已读取。检索从24小时扩展至7天，两项研究与近期已报道事件不同。
