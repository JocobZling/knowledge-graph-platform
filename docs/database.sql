DROP TABLE IF EXISTS graph_relation;
DROP TABLE IF EXISTS timeline_event;
DROP TABLE IF EXISTS prompt_record;
DROP TABLE IF EXISTS issue_record;
DROP TABLE IF EXISTS knowledge_card;
DROP TABLE IF EXISTS project_record;

CREATE TABLE project_record (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    summary TEXT,
    description TEXT,
    status VARCHAR(50) DEFAULT 'idea',
    priority INT DEFAULT 3,
    start_date DATE,
    end_date DATE,
    tags JSONB DEFAULT '[]'::jsonb,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE knowledge_card (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    content TEXT,
    category VARCHAR(100),
    tags JSONB DEFAULT '[]'::jsonb,
    level INT DEFAULT 1,
    status VARCHAR(50) DEFAULT 'active',
    source VARCHAR(100),
    related_project_id BIGINT REFERENCES project_record(id),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE issue_record (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    problem_type VARCHAR(100),
    error_message TEXT,
    background TEXT,
    reason TEXT,
    solution TEXT,
    code_snippet TEXT,
    status VARCHAR(50) DEFAULT 'open',
    difficulty INT DEFAULT 1,
    tags JSONB DEFAULT '[]'::jsonb,
    related_project_id BIGINT REFERENCES project_record(id),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE prompt_record (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    scenario VARCHAR(100),
    content TEXT,
    model VARCHAR(100),
    tags JSONB DEFAULT '[]'::jsonb,
    related_project_id BIGINT REFERENCES project_record(id),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE graph_relation (
    id BIGSERIAL PRIMARY KEY,
    source_type VARCHAR(50) NOT NULL,
    source_id BIGINT NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    relation_type VARCHAR(50) NOT NULL,
    description TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE timeline_event (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    event_type VARCHAR(50),
    event_date DATE,
    related_project_id BIGINT REFERENCES project_record(id),
    related_knowledge_id BIGINT REFERENCES knowledge_card(id),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO project_record (id, name, summary, description, status, priority, tags) VALUES
(1, '个人知识图谱网站', '沉淀个人知识、问题、项目与 Prompt。', '前后端分离的个人知识管理系统。', 'developing', 1, '["知识管理","Vue","SpringBoot"]'),
(2, '智能测试平台', '沉淀测试开发实践。', '面向自动化测试、数据准备、报告分析的长期项目。', 'planning', 2, '["测试开发","AI Testing"]');

INSERT INTO knowledge_card (id, title, summary, content, category, tags, level, source, related_project_id) VALUES
(1, 'SpringBoot 读取 application.yml', '排查配置文件未生效的常见路径。', '检查 resources 目录、active profile、打包配置和配置键拼写。', 'SpringBoot', '["SpringBoot","配置"]', 3, '项目实践', 1),
(2, 'PostgreSQL JSONB', '适合存储扩展标签和半结构化字段。', 'JSONB 支持索引、包含查询和灵活扩展。', 'PostgreSQL', '["PostgreSQL","JSONB"]', 3, '手动整理', 1),
(3, 'Codex Prompt 工程', '把目标、约束、验收标准写清楚。', '优先提供上下文、技术栈、输出目录和验收标准。', 'AI', '["Codex","Prompt"]', 4, 'ChatGPT', 1);

INSERT INTO issue_record (id, title, problem_type, error_message, background, reason, solution, status, difficulty, tags, related_project_id) VALUES
(1, 'Failed to configure a DataSource', 'SpringBoot', 'Failed to determine a suitable driver class', '启动 SpringBoot 项目时报错。', '缺少数据库驱动或 datasource 配置。', '补充 PostgreSQL driver，并检查 spring.datasource.url。', 'solved', 2, '["SpringBoot","DataSource"]', 1),
(2, 'MyBatis Invalid bound statement', 'MyBatis', 'Invalid bound statement not found', '调用 Mapper 方法时报错。', 'Mapper XML 未扫描或 namespace 不匹配。', '检查 mapper-locations、namespace、方法名。', 'open', 3, '["MyBatis"]', 2);

INSERT INTO prompt_record (id, title, scenario, content, model, tags, related_project_id) VALUES
(1, 'Codex 生成 Demo', 'Codex 生成 Demo', '请基于需求文档生成前后端分离 Demo，包含 README、SQL 和 API 文档。', 'gpt-5', '["Codex","Demo"]', 1);

INSERT INTO graph_relation (source_type, source_id, target_type, target_id, relation_type, description) VALUES
('issue', 1, 'knowledge', 1, 'SOLVES', 'DataSource 报错可通过配置检查解决'),
('knowledge', 3, 'prompt', 1, 'RELATED_TO', 'Prompt 工程沉淀为可复用模板'),
('project', 1, 'knowledge', 2, 'RELATED_TO', '项目使用 JSONB 存储标签');

INSERT INTO timeline_event (title, content, event_type, event_date, related_project_id, related_knowledge_id) VALUES
('个人知识图谱网站启动', '完成需求拆解和 MVP 模块规划。', 'milestone', CURRENT_DATE, 1, 3);
