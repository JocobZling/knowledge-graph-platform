package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.*;
import com.example.knowledgegraph.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final KnowledgeCardService knowledgeService;
    private final IssueRecordService issueService;
    private final ProjectRecordService projectService;
    private final PromptRecordService promptService;
    private final TimelineEventService timelineService;
    private final DailyBriefService dailyBriefService;

    public SearchController(KnowledgeCardService knowledgeService, IssueRecordService issueService,
                            ProjectRecordService projectService, PromptRecordService promptService,
                            TimelineEventService timelineService, DailyBriefService dailyBriefService) {
        this.knowledgeService = knowledgeService;
        this.issueService = issueService;
        this.projectService = projectService;
        this.promptService = promptService;
        this.timelineService = timelineService;
        this.dailyBriefService = dailyBriefService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> search(@RequestParam String keyword) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("knowledge", knowledgeService.list(new LambdaQueryWrapper<KnowledgeCard>().like(KnowledgeCard::getTitle, keyword).or().like(KnowledgeCard::getSummary, keyword)));
        data.put("issues", issueService.list(new LambdaQueryWrapper<IssueRecord>().like(IssueRecord::getTitle, keyword).or().like(IssueRecord::getSolution, keyword)));
        data.put("projects", projectService.list(new LambdaQueryWrapper<ProjectRecord>().like(ProjectRecord::getName, keyword).or().like(ProjectRecord::getSummary, keyword)));
        data.put("prompts", promptService.list(new LambdaQueryWrapper<PromptRecord>().like(PromptRecord::getTitle, keyword).or().like(PromptRecord::getContent, keyword)));
        data.put("timeline", timelineService.list(new LambdaQueryWrapper<TimelineEvent>().like(TimelineEvent::getTitle, keyword).or().like(TimelineEvent::getContent, keyword)));
        data.put("dailyBriefs", dailyBriefService.list(new LambdaQueryWrapper<DailyBrief>()
                .like(DailyBrief::getTitle, keyword)
                .or().like(DailyBrief::getSummary, keyword)
                .or().like(DailyBrief::getContent, keyword)
                .or().like(DailyBrief::getCategory, keyword)
                .or().like(DailyBrief::getType, keyword)
                .or().apply("tags::text like {0}", "%" + keyword + "%")));
        return ApiResponse.success(data);
    }
}
