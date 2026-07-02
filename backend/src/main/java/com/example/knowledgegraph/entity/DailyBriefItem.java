package com.example.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.knowledgegraph.config.JsonbTypeHandler;

import java.time.LocalDateTime;

@TableName(value = "daily_brief_item", autoResultMap = true)
public class DailyBriefItem {
    private Long id;
    private Long briefId;
    private String title;
    private String sourceName;
    private String sourceUrl;
    private String category;
    private Integer importance;
    private String summary;
    private String analysis;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String tags;
    private LocalDateTime createdTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBriefId() { return briefId; }
    public void setBriefId(Long briefId) { this.briefId = briefId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getImportance() { return importance; }
    public void setImportance(Integer importance) { this.importance = importance; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
