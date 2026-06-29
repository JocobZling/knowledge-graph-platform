package com.example.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.knowledgegraph.config.JsonbTypeHandler;

import java.time.LocalDateTime;

@TableName(value = "prompt_record", autoResultMap = true)
public class PromptRecord {
    private Long id;
    private String title;
    private String scenario;
    private String content;
    private String model;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String tags;
    private Long relatedProjectId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Long getRelatedProjectId() { return relatedProjectId; }
    public void setRelatedProjectId(Long relatedProjectId) { this.relatedProjectId = relatedProjectId; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
