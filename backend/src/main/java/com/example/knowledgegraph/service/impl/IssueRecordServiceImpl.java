package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.IssueRecord;
import com.example.knowledgegraph.mapper.IssueRecordMapper;
import com.example.knowledgegraph.service.IssueRecordService;
import org.springframework.stereotype.Service;

@Service
public class IssueRecordServiceImpl extends ServiceImpl<IssueRecordMapper, IssueRecord> implements IssueRecordService {
}
