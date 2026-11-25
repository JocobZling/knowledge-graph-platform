---
title: "AI Testing Daily Brief - 2026-11-25"
date: "2026-11-25"
type: daily-brief
category: ai-testing
tags: [Agent Testing, Benchmark, Harness, Mobile QA, Evaluation]
source:
  - https://metr.org/blog/2026-11-20-q4-2026-capability-update/
  - https://arxiv.org/abs/2611.28202
  - https://github.com/pierridotite/mobile-agent-testers
status: published
summary: "METR更新能力评测方法，AutoPILOT揭示实时执行Harness的重要性，移动端多Agent探索测试项目值得试验。"
---
# AI Testing Daily Brief - 2026-11-25

执行检查：已读取11月18—24日七篇日报及主题索引（索引停留7月15日），WayToAGI缓存success，11月24日17:56:49+08:00抓取，已读取。优先检索24小时，扩展至7天。两项研究未见于近七篇日报；移动端项目作为成熟度待验证的工具推荐，不将二次报道当作新发布。

## 1. 今日摘要
METR于11月20日发布能力评测更新，指出难以快速验证的任务仍存在判断能力缺口。AutoPILOT同日提交预印本，展示实时环境中执行Harness对Agent成功率的影响。移动端开源项目mobile-agent-testers展示多设备、多角色并行探索测试。今日暂无更多高价值新增。

## 2. 今日重点
### METR能力评测更新（11-20；研究报告；新颖度5/5）
[原始报告](https://metr.org/blog/2026-11-20-q4-2026-capability-update/)。新增TH2任务集及多档评分阈值，区分可验证任务与依赖判断的任务；组合TH1.1/TH2的任务时间跨度趋势约3.6个月翻倍。关注原因：模型在反馈明确的任务上表现强，不意味着开放式判断同样可靠。测试影响：区分自动断言与人工评审，避免用单一成功率代表全部能力。建议按任务可验证程度分层评测。该趋势是研究估计，并非Agent实际连续运行时长。
### AutoPILOT（11-20；预印本；新颖度5/5）
[论文](https://arxiv.org/abs/2611.28202)。提出支持单动作、宏动作及闭环视觉控制的Harness，AFK-Bench含120款实时浏览器游戏。同预算GPT-6.1 Sol实验实时成功率68.6%，VISTA为38.3%。关注原因：模型思考期间环境仍可能变化。测试影响：需覆盖过期页面状态、异步更新及重新观察。建议在合成网页模拟状态变化后检查Agent是否重新确认。论文结果不代表金融网页测试效果。

## 3. 行业新闻
|新闻|摘要及影响|日期|来源|重要度|热度（定性）|立即学习|
|---|---|---|---|---|---|---|
|METR评测更新|区分可验证任务和判断任务，改进评测口径|11-20|[METR](https://metr.org/blog/2026-11-20-q4-2026-capability-update/)|高|中高|是|
|AutoPILOT|实时环境下Harness影响任务成功率|11-20|[arXiv](https://arxiv.org/abs/2611.28202)|高|中|是|

## 4. 产品更新
无核验到满足新颖度门槛的高价值产品更新；上述两项为研究而非GA发布。

## 5. Agent Ecosystem
AutoPILOT将动作执行与模型推理频率解耦，值得研究实时状态同步。未核验到符合门槛的新MCP协议、Skill或安全事件。

## 6. 开源推荐
[mobile-agent-testers](https://github.com/pierridotite/mobile-agent-testers)，约1.8k Stars（11月25日仓库页面），MIT，支持iOS/Android模拟器、多角色并行探索、截图报告和版本比较。推荐4.3/5；适合发现布局、国际化、无障碍及流程问题，不代替确定性回归。Web Playwright及自托管真机仍列为贡献方向。

## 7. 企业实践
本期无新核验的企业部署案例。补充开源实践：项目作者报告LocalSend在20个模拟设备上发现46项不同问题，69分钟、模型费用4.62美元；未经独立复核，不等于确认缺陷数或企业收益。借鉴多角色矩阵、证据归档及人工去重。

## 8. 今日工具推荐
mobile-agent-testers：先阅读官方README及工作流，在获批环境使用公开demo和合成数据，按README创建模板、配置测试凭据、运行agent-testers.yml并收集报告。不要将内部代码上传公开仓库；私有runner和模型费用需另行核算。

## 9. 今日学习
状态新鲜度测试：Agent观察页面与实际执行之间可能发生状态变化。测试应记录观察版本、执行版本及时间，模拟会话过期、列表刷新、并发修改。写入前重新读取关键状态，冲突时停止或重新规划。后端应独立执行版本校验，不能仅依赖Agent自觉确认。

## 10. 趋势观察
未来3个月，预计Agent评测更关注执行环境动态变化、任务可验证程度和多角色覆盖；这是工程判断，不是官方路线图。

## 11. 30分钟 Action
0—5分钟定义合成任务状态及版本；5—10分钟准备正常与过期状态；10—20分钟模拟观察后状态变化并执行；20—25分钟检查拒绝、重新读取和后端状态；25—30分钟保存轨迹与结论。验收：过期版本不得覆盖新状态；未执行标BLOCKED。

## 12. 值得跟进
文章：METR报告；论文：AutoPILOT；项目：mobile-agent-testers；自建Prompt/Skill：状态变化测试计划；MCP：写入Tool版本冲突与幂等；知识图谱节点：State Freshness、Judgment-loaded Evaluation、Real-time Harness、Persona-based QA。持续观察AFK-Bench复现和移动端项目确定性回放能力。

## 13. 我的备注
金融测试：将并发修改与过期状态作为硬断言。测试经理智能助手的查询可验证数据时间口径，未来任务创建应在后端实施幂等及版本校验。Browser Agent在页面变化后重新观察；MCP准入验证冲突返回与重试边界。日志脱敏精筛可将明确规则样本与需上下文判断样本分开统计，不将研究基准直接套用。测试计划先明确状态变化点、预期及证据，再执行。
