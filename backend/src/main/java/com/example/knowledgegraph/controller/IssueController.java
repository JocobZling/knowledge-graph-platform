package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.IssueRecord;
import com.example.knowledgegraph.service.IssueRecordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {
    private final IssueRecordService service;
    public IssueController(IssueRecordService service) { this.service = service; }

    @GetMapping("/list")
    public ApiResponse<List<IssueRecord>> list(@RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) String status,
                                               @RequestParam(required = false) String problemType) {
        LambdaQueryWrapper<IssueRecord> wrapper = new LambdaQueryWrapper<IssueRecord>()
                .like(keyword != null && !keyword.isBlank(), IssueRecord::getTitle, keyword)
                .or(keyword != null && !keyword.isBlank())
                .like(keyword != null && !keyword.isBlank(), IssueRecord::getSolution, keyword)
                .eq(status != null && !status.isBlank(), IssueRecord::getStatus, status)
                .eq(problemType != null && !problemType.isBlank(), IssueRecord::getProblemType, problemType)
                .orderByDesc(IssueRecord::getUpdatedTime);
        return ApiResponse.success(service.list(wrapper));
    }

    @GetMapping("/{id}")
    public ApiResponse<IssueRecord> detail(@PathVariable Long id) { return ApiResponse.success(service.getById(id)); }

    @PostMapping("/create")
    public ApiResponse<IssueRecord> create(@RequestBody IssueRecord item) {
        item.setCreatedTime(LocalDateTime.now());
        item.setUpdatedTime(LocalDateTime.now());
        if (item.getStatus() == null) item.setStatus("open");
        if (item.getDifficulty() == null) item.setDifficulty(1);
        if (item.getTags() == null) item.setTags("[]");
        service.save(item);
        return ApiResponse.success(item);
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody IssueRecord item) {
        item.setUpdatedTime(LocalDateTime.now());
        return ApiResponse.success(service.updateById(item));
    }

    @PutMapping("/{id}/solve")
    public ApiResponse<Boolean> solve(@PathVariable Long id) {
        IssueRecord item = new IssueRecord();
        item.setId(id);
        item.setStatus("solved");
        item.setUpdatedTime(LocalDateTime.now());
        return ApiResponse.success(service.updateById(item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(service.removeById(id)); }
}
