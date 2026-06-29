package com.example.knowledgegraph.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.knowledgegraph.config.JsonbTypeHandler;

import java.time.LocalDateTime;

@TableName(value = "issue_record", autoResultMap = true)
public class IssueRecord {
    private Long id;
    private String title;
    private String problemType;
    private String errorMessage;
    private String background;
    private String reason;
    private String solution;
    private String codeSnippet;
    private String status;
    private Integer difficulty;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String tags;
    private Long relatedProjectId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getProblemType() { return problemType; }
    public void setProblemType(String problemType) { this.problemType = problemType; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getDifficulty() { return difficulty; }
    public void setDifficulty(Integer difficulty) { this.difficulty = difficulty; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Long getRelatedProjectId() { return relatedProjectId; }
    public void setRelatedProjectId(Long relatedProjectId) { this.relatedProjectId = relatedProjectId; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
