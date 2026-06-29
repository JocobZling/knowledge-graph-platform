package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.IssueRecord;
import com.example.knowledgegraph.entity.ProjectRecord;
import com.example.knowledgegraph.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final KnowledgeCardService knowledgeService;
    private final IssueRecordService issueService;
    private final ProjectRecordService projectService;
    private final PromptRecordService promptService;

    public DashboardController(KnowledgeCardService knowledgeService, IssueRecordService issueService,
                               ProjectRecordService projectService, PromptRecordService promptService) {
        this.knowledgeService = knowledgeService;
        this.issueService = issueService;
        this.projectService = projectService;
        this.promptService = promptService;
    }

    @GetMapping("/summary")
    public ApiResponse<Map<String, Object>> summary() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("knowledgeCount", knowledgeService.count());
        data.put("issueCount", issueService.count());
        data.put("projectCount", projectService.count());
        data.put("promptCount", promptService.count());
        data.put("recentIssues", issueService.list(new LambdaQueryWrapper<IssueRecord>().orderByDesc(IssueRecord::getUpdatedTime).last("limit 5")));
        data.put("activeProjects", projectService.list(new LambdaQueryWrapper<ProjectRecord>().in(ProjectRecord::getStatus, "planning", "developing").last("limit 5")));
        return ApiResponse.success(data);
    }
}
