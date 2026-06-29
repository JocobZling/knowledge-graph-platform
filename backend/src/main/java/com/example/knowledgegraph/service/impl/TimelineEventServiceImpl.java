package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.TimelineEvent;
import com.example.knowledgegraph.mapper.TimelineEventMapper;
import com.example.knowledgegraph.service.TimelineEventService;
import org.springframework.stereotype.Service;

@Service
public class TimelineEventServiceImpl extends ServiceImpl<TimelineEventMapper, TimelineEvent> implements TimelineEventService {
}
