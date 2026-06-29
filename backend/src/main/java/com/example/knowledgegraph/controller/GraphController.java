package com.example.knowledgegraph.controller;

import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.dto.GraphData;
import com.example.knowledgegraph.entity.*;
import com.example.knowledgegraph.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/graph")
public class GraphController {
    private final KnowledgeCardService knowledgeService;
    private final IssueRecordService issueService;
    private final ProjectRecordService projectService;
    private final PromptRecordService promptService;
    private final GraphRelationService relationService;

    public GraphController(KnowledgeCardService knowledgeService, IssueRecordService issueService,
                           ProjectRecordService projectService, PromptRecordService promptService,
                           GraphRelationService relationService) {
        this.knowledgeService = knowledgeService;
        this.issueService = issueService;
        this.projectService = projectService;
        this.promptService = promptService;
        this.relationService = relationService;
    }

    @GetMapping("/all")
    public ApiResponse<GraphData> all() {
        GraphData graph = new GraphData();
        for (KnowledgeCard item : knowledgeService.list()) graph.getNodes().add(new GraphData.GraphNode("knowledge-" + item.getId(), item.getTitle(), "knowledge", item.getCategory()));
        for (IssueRecord item : issueService.list()) graph.getNodes().add(new GraphData.GraphNode("issue-" + item.getId(), item.getTitle(), "issue", item.getStatus()));
        for (ProjectRecord item : projectService.list()) graph.getNodes().add(new GraphData.GraphNode("project-" + item.getId(), item.getName(), "project", item.getStatus()));
        for (PromptRecord item : promptService.list()) graph.getNodes().add(new GraphData.GraphNode("prompt-" + item.getId(), item.getTitle(), "prompt", item.getScenario()));
        for (GraphRelation item : relationService.list()) graph.getLinks().add(new GraphData.GraphLink(item.getSourceType() + "-" + item.getSourceId(), item.getTargetType() + "-" + item.getTargetId(), item.getRelationType()));
        return ApiResponse.success(graph);
    }

    @PostMapping("/relation/create")
    public ApiResponse<GraphRelation> create(@RequestBody GraphRelation item) {
        relationService.save(item);
        return ApiResponse.success(item);
    }

    @DeleteMapping("/relation/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(relationService.removeById(id)); }
}
