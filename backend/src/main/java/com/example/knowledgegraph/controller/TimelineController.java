package com.example.knowledgegraph.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.knowledgegraph.common.ApiResponse;
import com.example.knowledgegraph.entity.TimelineEvent;
import com.example.knowledgegraph.service.TimelineEventService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {
    private final TimelineEventService service;
    public TimelineController(TimelineEventService service) { this.service = service; }

    @GetMapping("/list")
    public ApiResponse<List<TimelineEvent>> list(@RequestParam(required = false) String eventType) {
        return ApiResponse.success(service.list(new LambdaQueryWrapper<TimelineEvent>()
                .eq(eventType != null && !eventType.isBlank(), TimelineEvent::getEventType, eventType)
                .orderByDesc(TimelineEvent::getEventDate)));
    }

    @PostMapping("/create")
    public ApiResponse<TimelineEvent> create(@RequestBody TimelineEvent item) {
        item.setCreatedTime(LocalDateTime.now());
        service.save(item);
        return ApiResponse.success(item);
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody TimelineEvent item) { return ApiResponse.success(service.updateById(item)); }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(service.removeById(id)); }
}
