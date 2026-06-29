package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.ProjectRecord;
import com.example.knowledgegraph.mapper.ProjectRecordMapper;
import com.example.knowledgegraph.service.ProjectRecordService;
import org.springframework.stereotype.Service;

@Service
public class ProjectRecordServiceImpl extends ServiceImpl<ProjectRecordMapper, ProjectRecord> implements ProjectRecordService {
}
