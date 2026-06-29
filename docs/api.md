# API 文档

所有接口返回统一格式：

```json
{ "code": 0, "message": "success", "data": {} }
```

## Dashboard

- `GET /api/dashboard/summary`

## 知识库

- `GET /api/knowledge/list?keyword=&category=&status=`
- `GET /api/knowledge/{id}`
- `POST /api/knowledge/create`
- `PUT /api/knowledge/update`
- `DELETE /api/knowledge/{id}`

## 问题库

- `GET /api/issues/list?keyword=&problemType=&status=`
- `GET /api/issues/{id}`
- `POST /api/issues/create`
- `PUT /api/issues/update`
- `PUT /api/issues/{id}/solve`
- `DELETE /api/issues/{id}`

## 项目中心

- `GET /api/projects/list?keyword=&status=`
- `GET /api/projects/{id}`
- `POST /api/projects/create`
- `PUT /api/projects/update`
- `DELETE /api/projects/{id}`

## Prompt

- `GET /api/prompts/list?keyword=`
- `GET /api/prompts/{id}`
- `POST /api/prompts/create`
- `PUT /api/prompts/update`
- `DELETE /api/prompts/{id}`

## 图谱

- `GET /api/graph/all`
- `POST /api/graph/relation/create`
- `DELETE /api/graph/relation/{id}`

## 时间轴

- `GET /api/timeline/list`
- `POST /api/timeline/create`
- `PUT /api/timeline/update`
- `DELETE /api/timeline/{id}`

## 搜索

- `GET /api/search?keyword=xxx`
