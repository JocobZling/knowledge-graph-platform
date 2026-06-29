package com.example.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.knowledgegraph.config.JsonbTypeHandler;

import java.time.LocalDateTime;

@TableName(value = "knowledge_card", autoResultMap = true)
public class KnowledgeCard {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String category;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String tags;
    private Integer level;
    private String status;
    private String source;
    private Long relatedProjectId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getRelatedProjectId() { return relatedProjectId; }
    public void setRelatedProjectId(Long relatedProjectId) { this.relatedProjectId = relatedProjectId; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
