package com.example.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.knowledgegraph.entity.DailyBrief;

public interface DailyBriefService extends IService<DailyBrief> {
    int syncFromMarkdown();
}
