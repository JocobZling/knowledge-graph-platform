package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.PromptRecord;
import com.example.knowledgegraph.mapper.PromptRecordMapper;
import com.example.knowledgegraph.service.PromptRecordService;
import org.springframework.stereotype.Service;

@Service
public class PromptRecordServiceImpl extends ServiceImpl<PromptRecordMapper, PromptRecord> implements PromptRecordService {
}
