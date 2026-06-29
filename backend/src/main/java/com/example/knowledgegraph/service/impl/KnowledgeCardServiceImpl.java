package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.KnowledgeCard;
import com.example.knowledgegraph.mapper.KnowledgeCardMapper;
import com.example.knowledgegraph.service.KnowledgeCardService;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeCardServiceImpl extends ServiceImpl<KnowledgeCardMapper, KnowledgeCard> implements KnowledgeCardService {
}
