---
title: "Recent Conversation Knowledge Update"
date: "2026-07-08"
type: "knowledge-update"
category: "Codex Context"
status: "active"
author: "ChatGPT"
version: "1.0"
---

# Recent Conversation Knowledge Update

> 本文档用于同步最近一周 ChatGPT 对话内容到 Codex。
>
> 更新目标：
>
> - Problem Library（问题库）
> - Knowledge Base（知识库）
> - Knowledge Graph（知识图谱）
> - Architecture（架构）
> - Patent（专利方向）
> - AI Testing（智能测试）

---

# 1. 当前长期项目（Current Projects）

目前主要维护以下几个长期项目。

## 1.1 金融测试平台（Financial Test Platform）

定位：

> 一个面向金融测试开发人员的一站式工作平台。

主要目标：

- 自动造数
- 自动校验
- 自动查询数据库
- 自动计算金额
- 自动生成测试报告
- AI 辅助分析

未来计划：

- Browser Agent
- MCP Server
- AI Workflow
- Agent Testing

---

## 1.2 AI Tech Daily

每日生成 AI 科技日报。

新增关注方向：

- AI 模型
- GitHub
- Browser Agent
- MCP
- Tool Calling
- Agent Workflow
- Agent Security
- Agent Governance

同步方式：GitHub

目录：

```text
daily/ai-tech/YYYY/MM/
```

---

## 1.3 AI Testing Daily

每日生成智能测试日报。

关注内容：

- AI 测试
- 自动化测试
- Browser Agent
- MCP
- 测试工具
- 金融测试
- 企业实践

---

# 2. 最近新增需求

## 2.1 新版金融测试平台流程改造

用户输入：

```text
商户号 + 清算日期
```

流程：

```text
输入参数
↓
查询汇总表
↓
判断是否存在记录
↓
获取汇总流水号
↓
查询清分流水
↓
重新汇总金额
↓
计算多个金额字段
↓
与汇总表字段比较
↓
展示是否一致
```

说明：

数据库 SQL 由开发人员自行补充。

Codex 负责：

- Controller
- Service
- DTO
- VO
- 页面
- 计算框架
- 比对逻辑
- 展示结构

Codex 不负责：

- 真实数据库连接配置
- 真实 SQL 编写
- 生产库表结构推断

要求：

在数据库查询位置保留空方法、TODO 或接口占位，方便后续人工补充。

---

# 3. 本周新增 Problem Library

## 3.1 PostgreSQL Sequence 问题

问题：

导入历史数据后，表主键已经增加到较大值，但新增接口仍然从 1 开始生成 ID，导致主键冲突。

原因：

PostgreSQL 的 Sequence 没有随着导入数据自动同步。

解决：

```sql
SELECT setval(
    pg_get_serial_sequence('table_name','id'),
    (SELECT MAX(id) FROM table_name)
);
```

标签：

```text
PostgreSQL
Sequence
Primary Key
Data Import
```

---

## 3.2 GitHub 连接失败

错误：

```text
Failed to connect to github.com port 443
```

重点排查：

- VPN
- Proxy
- SSL
- DNS
- Hosts
- Git Proxy
- 公司网络限制

常用命令：

```bash
git config --global --unset http.proxy
git config --global --unset https.proxy
```

标签：

```text
GitHub
Network
Proxy
SSL
```

---

## 3.3 Postman Pre-request Script

新增知识：

执行生命周期：

```text
Collection
↓
Folder
↓
Request
```

常用 API：

```javascript
pm.environment.set("key", "value");
pm.environment.get("key");
pm.variables.get("key");
pm.request.headers.add({ key: "token", value: "xxx" });
```

常见用途：

- Token 自动刷新
- 签名
- 时间戳
- Mock
- 加密
- 请求头补充

标签：

```text
Postman
Pre-request Script
API Testing
```

---

## 3.4 动态二维码支付测试

现状：

测试支付流程时，需要不断生成动态二维码并扫码支付，测试效率低。

建议方向：

建设支付模拟平台。

```text
自动生成二维码
↓
模拟支付
↓
自动回调
↓
自动查询结果
↓
自动校验
```

目标：

降低人工扫码成本，提高金融支付链路回归效率。

标签：

```text
金融测试
支付测试
二维码
自动化测试
```

---

# 4. Knowledge Base 更新

## 4.1 Browser Agent

未来重点投入方向之一：Browser Agent。

应用场景：

- 自动登录
- 自动填表
- 自动点击
- 自动测试后台页面
- 自动验证页面状态
- 自动生成操作日志

适合金融测试的场景：

- 管理后台回归测试
- 商户配置检查
- 参数配置验证
- 多系统页面联动检查
- 流程型业务自动化

相关工具方向：

- OpenAI Operator
- Browser Use
- Playwright Agent
- Stagehand
- Browser Agent Workflow

---

## 4.2 MCP Server

理解方式：

```text
LLM
↓
MCP
↓
Tool
↓
Database / GitHub / Browser / Jenkins / Jira / 内部平台
```

MCP 的核心价值：

- 标准化工具接入
- 降低 Agent 调用外部系统的成本
- 统一权限、协议、上下文
- 方便企业治理 AI 工具生态

金融测试应用：

- 连接数据库查询测试数据
- 连接 GitHub 获取代码变更
- 连接 Jenkins 获取构建状态
- 连接测试平台创建任务
- 连接日志系统查询异常

---

## 4.3 AI Agent 生命周期治理

形成的核心流程：

```text
申请
↓
审批
↓
准入
↓
权限授予
↓
运行监控
↓
持续评估
↓
风险处置
↓
退出 / 回收权限
```

重点：

- 动态准入
- 最小权限
- 风险分级
- 行为审计
- 权限回收
- 持续治理

---

## 4.4 Agent Workflow

未来趋势：

```text
Workflow + Tool Calling + Agent
```

与传统自动化脚本的区别：

| 对比项 | 传统自动化 | Agent Workflow |
|---|---|---|
| 执行方式 | 固定脚本 | 动态决策 |
| 输入 | 固定参数 | 自然语言 + 上下文 |
| 异常处理 | 预设分支 | 自动分析后调整 |
| 工具调用 | 固定接口 | 按任务选择工具 |
| 适用场景 | 稳定流程 | 半结构化复杂流程 |

---

# 5. AI 日报更新

新增栏目：

```text
Agent Ecosystem
```

包含：

- Browser Agent
- MCP
- Workflow
- Skill
- Tool Calling
- Security
- Governance
- Agent 编排框架

要求：

每日保留 2～3 条重点内容，不需要图片。

GitHub 目录：

```text
daily/ai-tech/YYYY/MM/
```

---

# 6. GitHub Research

## 6.1 GitHub Skill 研究

近期关注：

- Prompt
- Skill
- Workflow
- MCP
- Agent
- Design Book
- Frontend Skill

关注指标：

```text
Stars 增长速度
项目活跃度
README 完整度
示例质量
是否易于接入 Codex
```

上涨快的项目常见特点：

- AI Native
- Browser
- Agent
- Prompt Library
- Workflow
- 一键安装
- 示例清晰
- 解决真实开发痛点

---

# 7. Codex 开发规范

推荐工作流：

```text
ChatGPT
↓
需求分析
↓
架构设计
↓
Prompt
↓
Codex 生成代码
↓
ChatGPT Review
↓
再次优化
```

职责划分：

## ChatGPT 负责

- 需求分析
- 架构设计
- Prompt 设计
- 代码评审思路
- 专利构思
- 文档沉淀
- 知识图谱梳理

## Codex 负责

- CRUD
- Demo
- 前端页面
- 后端接口
- 重构
- 单元测试
- 文件落库
- 项目结构调整

---

# 8. PostgreSQL 知识

新增内容：

- Sequence 同步
- pg_description
- 字段注释查询
- Trigger
- 动态 SQL

建议归档目录：

```text
knowledge/database/
```

建议新增文档：

```text
knowledge/database/postgresql-sequence.md
knowledge/database/pg-description.md
knowledge/database/trigger.md
```

---

# 9. 专利方向

目前形成三个方向。

## 9.1 金融企业 AI Agent 动态准入方法

核心：

金融企业内部存在大量 AI Agent、MCP Server、Tool、Workflow，需要一套动态准入机制。

关键点：

- 工具注册
- 权限审批
- 风险评估
- 使用审计
- 动态下线

---

## 9.2 交易生命周期感知的 AI Agent 动态治理方法

核心：

将金融交易生命周期与 AI Agent 工具调用权限绑定。

生命周期包括：

```text
交易发起
↓
授权
↓
清算
↓
对账
↓
差错处理
↓
归档
```

不同阶段，对 Agent 开放不同工具权限和数据范围。

---

## 9.3 AI Agent 持续风险治理平台

核心：

对企业内部 Agent 进行持续治理。

包括：

- 权限
- 日志
- 风险
- 生命周期
- 审计
- 安全策略

---

# 10. Knowledge Graph 更新建议

新增节点：

```text
AI Agent
├── Browser Agent
├── MCP
├── Workflow
├── Governance
├── Tool Calling
└── Testing

金融测试
├── 自动造数
├── 清算验证
├── 动态二维码
├── Browser Agent
└── MCP

Codex
├── Prompt
├── Workflow
├── Review
├── GitHub
└── Design Book

PostgreSQL
├── Sequence
├── Trigger
├── JSONB
├── pg_description
└── Query

专利
├── 生命周期
├── AI 治理
├── 风险控制
└── 金融 AI
```

---

# 11. 建议新增知识库文档

```text
knowledge/
├── ai/
│   ├── browser-agent.md
│   ├── mcp.md
│   ├── agent-governance.md
│   └── workflow.md
│
├── testing/
│   ├── payment-simulator.md
│   ├── settlement-platform.md
│   ├── browser-agent-testing.md
│   └── ai-testing.md
│
├── database/
│   ├── postgresql-sequence.md
│   ├── pg-description.md
│   └── trigger.md
│
├── github/
│   ├── github-skills.md
│   └── awesome-agent.md
│
├── codex/
│   ├── workflow.md
│   ├── prompt-guide.md
│   └── best-practice.md
│
└── patent/
    ├── agent-lifecycle.md
    ├── financial-agent.md
    └── governance.md
```

---

# 12. 下一阶段建议

建议知识库正式升级为四层结构：

```text
knowledge/
├── problem-library/
│   └── 存放所有踩坑记录
│
├── knowledge-base/
│   └── 存放学习资料
│
├── architecture/
│   └── 存放系统设计
│
├── graph/
│   └── 存放知识图谱节点
│
├── prompts/
│   └── Codex Prompt
│
└── patents/
    └── 专利构思
```

最终目标：

建立一个能够持续积累、持续演进、可供 Codex 理解的个人 AI 知识体系。

分工：

```text
ChatGPT：知识沉淀 + 架构设计 + Prompt + Review
Codex：代码实现 + 文件调整 + Demo 搭建 + 自动化生成
```

---

# 13. Codex 使用建议

Codex 读取本文档后，应重点理解：

1. 用户正在建设一个长期演进的知识图谱与金融测试平台。
2. 用户希望把 ChatGPT 对话沉淀为结构化知识。
3. 代码生成时需要优先保持可扩展结构，而不是只做一次性脚本。
4. 涉及数据库查询的位置可以留空，用户会自行补充 SQL。
5. 前端应避免默认模板感，尽量结合已有 Design Book 和科技感风格规范。
6. 后端应保持 Controller、Service、DTO、VO、Mapper 的清晰分层。
7. 所有新增知识应尽量沉淀为 Markdown，便于后续进入知识库和知识图谱。
