package com.example.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("timeline_event")
public class TimelineEvent {
    private Long id;
    private String title;
    private String content;
    private String eventType;
    private LocalDate eventDate;
    private Long relatedProjectId;
    private Long relatedKnowledgeId;
    private LocalDateTime createdTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public Long getRelatedProjectId() { return relatedProjectId; }
    public void setRelatedProjectId(Long relatedProjectId) { this.relatedProjectId = relatedProjectId; }
    public Long getRelatedKnowledgeId() { return relatedKnowledgeId; }
    public void setRelatedKnowledgeId(Long relatedKnowledgeId) { this.relatedKnowledgeId = relatedKnowledgeId; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}
