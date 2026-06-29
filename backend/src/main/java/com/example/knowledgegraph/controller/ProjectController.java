package com.example.knowledgegraph.controller;

        import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
        import com.example.knowledgegraph.common.ApiResponse;
        import com.example.knowledgegraph.entity.ProjectRecord;
        import com.example.knowledgegraph.service.ProjectRecordService;
        import org.springframework.web.bind.annotation.*;

        import java.time.LocalDateTime;
        import java.util.List;

        @RestController
        @RequestMapping("/api/projects")
        public class ProjectController {
            private final ProjectRecordService service;
            public ProjectController(ProjectRecordService service) { this.service = service; }

            @GetMapping("/list")
            public ApiResponse<List<ProjectRecord>> list(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) String status) {
                LambdaQueryWrapper<ProjectRecord> wrapper = new LambdaQueryWrapper<ProjectRecord>()
                        .like(keyword != null && !keyword.isBlank(), ProjectRecord::getName, keyword)
                        .eq(status != null && !status.isBlank(), ProjectRecord::getStatus, status)
                        .orderByDesc(ProjectRecord::getUpdatedTime);
                return ApiResponse.success(service.list(wrapper));
            }

            @GetMapping("/{id}")
            public ApiResponse<ProjectRecord> detail(@PathVariable Long id) { return ApiResponse.success(service.getById(id)); }

            @PostMapping("/create")
            public ApiResponse<ProjectRecord> create(@RequestBody ProjectRecord item) {
                item.setCreatedTime(LocalDateTime.now());
                item.setUpdatedTime(LocalDateTime.now());
                if (item.getStatus() == null) item.setStatus("idea");
if (item.getPriority() == null) item.setPriority(3);
if (item.getTags() == null) item.setTags("[]");
                service.save(item);
                return ApiResponse.success(item);
            }

            @PutMapping("/update")
            public ApiResponse<Boolean> update(@RequestBody ProjectRecord item) {
                item.setUpdatedTime(LocalDateTime.now());
                return ApiResponse.success(service.updateById(item));
            }

            @DeleteMapping("/{id}")
            public ApiResponse<Boolean> delete(@PathVariable Long id) { return ApiResponse.success(service.removeById(id)); }
        }
