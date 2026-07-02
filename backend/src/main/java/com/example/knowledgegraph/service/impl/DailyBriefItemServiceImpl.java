package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.DailyBriefItem;
import com.example.knowledgegraph.mapper.DailyBriefItemMapper;
import com.example.knowledgegraph.service.DailyBriefItemService;
import org.springframework.stereotype.Service;

@Service
public class DailyBriefItemServiceImpl extends ServiceImpl<DailyBriefItemMapper, DailyBriefItem> implements DailyBriefItemService {
}
