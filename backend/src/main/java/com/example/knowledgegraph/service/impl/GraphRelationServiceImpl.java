package com.example.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.knowledgegraph.entity.GraphRelation;
import com.example.knowledgegraph.mapper.GraphRelationMapper;
import com.example.knowledgegraph.service.GraphRelationService;
import org.springframework.stereotype.Service;

@Service
public class GraphRelationServiceImpl extends ServiceImpl<GraphRelationMapper, GraphRelation> implements GraphRelationService {
}
