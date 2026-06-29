package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.PromptRecord;
import com.example.knowledgegraph.service.PromptRecordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {
    private final PromptRecordService service;
    public PromptController(PromptRecordService service) { this.service = service; }

    @GetMapping("/list")
    public ApiResponse<List<PromptRecord>> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String scenario) {
        LambdaQueryWrapper<PromptRecord> wrapper = new LambdaQueryWrapper<PromptRecord>()
                .like(keyword != null && !keyword.isBlank(), PromptRecord::getTitle, keyword)
                .or(keyword != null && !keyword.isBlank())
                .like(keyword != null && !keyword.isBlank(), PromptRecord::getContent, keyword)
                .eq(scenario != null && !scenario.isBlank(), PromptRecord::getScenario, scenario)
                .orderByDesc(PromptRecord::getUpdatedTime);
        return ApiResponse.success(service.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromptRecord> detail(@PathVariable Long id) { return ApiResponse.success(service.getById(id)); }

    @PostMapping("/create")
    public ApiResponse<PromptRecord> create(@RequestBody PromptRecord item) {
        item.setCreatedTime(LocalDateTime.now());
        item.setUpdatedTime(LocalDateTime.now());
        if (item.getTags() == null) item.setTags("[]");
        service.save(item);
        return ApiResponse.success(item);
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody PromptRecord item) {
        item.setUpdatedTime(LocalDateTime.now());
        return ApiResponse.success(service.updateById(item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(service.removeById(id)); }
}
