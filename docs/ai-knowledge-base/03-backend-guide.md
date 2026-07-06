# Backend Guide

## 技术栈

- Java 17
- Spring Boot 3.3.5
- MyBatis Plus 3.5.9
- PostgreSQL
- Spring Validation

## 命名和分层模式

新增业务模块时，保持现有结构：

```text
entity/Xxx.java
mapper/XxxMapper.java
service/XxxService.java
service/impl/XxxServiceImpl.java
controller/XxxController.java
```

Controller 返回统一响应，不直接泄漏异常细节。Service 负责业务判断，Mapper 只做数据访问。

## 接口约定

接口统一放在 `/api` 下。已有模块示例：

```text
GET    /api/knowledge/list
GET    /api/knowledge/{id}
POST   /api/knowledge/create
PUT    /api/knowledge/update
DELETE /api/knowledge/{id}
```

Daily Brief 相关：

```text
GET    /api/daily-brief/list
GET    /api/daily-brief/{id}
POST   /api/daily-brief/create
PUT    /api/daily-brief/update
DELETE /api/daily-brief/{id}
POST   /api/daily-brief/sync
POST   /api/daily-brief/relation/create
```

## 数据库约定

- 主键使用自增 ID。
- Java 字段使用 camelCase，数据库字段使用 snake_case。
- MyBatis Plus 已启用 `map-underscore-to-camel-case`。
- JSONB 字段使用现有 `JsonbTypeHandler` 模式。
- 变更表结构时同步更新 `docs/database.sql` 和相关知识库文档。

## 配置和安全

当前 `application.yml` 中仍有本地数据库连接信息。后续修改后端配置时，应迁移为环境变量，例如：

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```

不要把真实密码写入 Git；只在 `.env.example` 中保留变量名和无害示例。

