package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.KnowledgeCard;
import com.example.knowledgegraph.service.KnowledgeCardService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeCardService service;
    public KnowledgeController(KnowledgeCardService service) { this.service = service; }

    @GetMapping("/list")
    public ApiResponse<List<KnowledgeCard>> list(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String status) {
        LambdaQueryWrapper<KnowledgeCard> wrapper = new LambdaQueryWrapper<KnowledgeCard>()
                .like(keyword != null && !keyword.isBlank(), KnowledgeCard::getTitle, keyword)
                .or(keyword != null && !keyword.isBlank())
                .like(keyword != null && !keyword.isBlank(), KnowledgeCard::getSummary, keyword)
                .eq(category != null && !category.isBlank(), KnowledgeCard::getCategory, category)
                .eq(status != null && !status.isBlank(), KnowledgeCard::getStatus, status)
                .orderByDesc(KnowledgeCard::getUpdatedTime);
        return ApiResponse.success(service.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeCard> detail(@PathVariable Long id) { return ApiResponse.success(service.getById(id)); }

    @PostMapping("/create")
    public ApiResponse<KnowledgeCard> create(@RequestBody KnowledgeCard item) {
        item.setCreatedTime(LocalDateTime.now());
        item.setUpdatedTime(LocalDateTime.now());
        if (item.getStatus() == null) item.setStatus("active");
        if (item.getLevel() == null) item.setLevel(1);
        if (item.getTags() == null) item.setTags("[]");
        service.save(item);
        return ApiResponse.success(item);
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody KnowledgeCard item) {
        item.setUpdatedTime(LocalDateTime.now());
        return ApiResponse.success(service.updateById(item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(service.removeById(id)); }
}
