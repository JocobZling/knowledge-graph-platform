# AI Testing 日报去重规则

## 目的与运行方式

`data/ai-testing/topic-index.json` 是 `daily/ai-testing/**/*.md` 的主题级索引。生成新日报前查询该索引，可避免仅修改标题后重复报道同一事件。

```bash
python scripts/build_ai_testing_topic_index.py
```

脚本只使用 Python 标准库，读取 UTF-8 Markdown，并以 UTF-8、两空格缩进输出 JSON。

## 解析范围

- `今日摘要`：新闻没有独立摘要时，作为 `core_fact` 的后备内容。
- `重点新闻`：每个三级标题是一条新闻，读取 `来源`、`时间`、`关键词` 和 `摘要`。
- `相关链接`：根据标题、主体、来源和关键词与重点新闻匹配，得到 `source_url`。

日报日期优先取 Front Matter 的 `date`，缺失时从文件名提取。

## topic_id 规则

`topic_id` 按“主体 + 事件类型 + 核心功能”生成，统一小写并用短横线连接，例如：

```text
playwright-test-agents-news-planner-generator-healer
```

主体优先识别产品、组织、论文或项目名；事件类型归一为 `release`、`update`、`research`、`security-event`、`case-study`、`governance`、`tool` 或 `news`；核心功能从标题、摘要和关键词中提取。标题措辞变化不会改变由相同来源 URL 确认的主题身份。

## 合并优先级

1. 先规范化 `source_url`：协议和主机名转小写，移除 fragment、常见跟踪参数和末尾 `/`。
2. arXiv 的 `/abs/<id>`、`/html/<id>vN`、`/pdf/<id>.pdf` 统一成 `https://arxiv.org/abs/<id>`。
3. 相同非空 `source_url` 无条件合并，因此一个来源链接只保留一条主题记录。
4. URL 不同或缺失时，再按 `topic_id` 合并。
5. 合并时保留最早的 `first_reported`，更新最晚的 `last_reported`，累计 `appeared_count`，并在 `appeared_dates` 中记录实际出现日期。

同一来源页面包含多个更新时，本索引仍按“一 URL 一主题”处理。如需拆分聚合页中的不同事件，应在日报中使用各事件的独立永久链接。

## 最近 7 天判断

为保证历史重建可重复，最近 7 天以扫描到的 `latest_report_date` 为锚点，包含该日及之前 6 天：

```text
recent_7_days_start <= last_reported <= latest_report_date
```

满足条件的主题标记为 `reported_in_last_7_days: true`。生成下一期日报时，应先排除这些主题；只有出现新版本、新论文、新案例或重大安全事件时才作为新主题报道。

## 日报生成前检查

1. 运行索引脚本。
2. 规范化候选新闻的来源 URL，检查是否已有相同 `source_url`。
3. 比较主体、事件类型和核心功能，检查是否命中已有 `topic_id`。
4. 若命中且 `reported_in_last_7_days` 为 `true`，默认移到“持续观察”。
5. 确认是独立新事件时，使用直接、稳定的事件链接，避免只引用首页或聚合页。
