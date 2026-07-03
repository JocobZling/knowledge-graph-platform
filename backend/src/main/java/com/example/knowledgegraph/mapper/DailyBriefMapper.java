package com.example.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.knowledgegraph.entity.DailyBrief;
import org.apache.ibatis.annotations.Select;

public interface DailyBriefMapper extends BaseMapper<DailyBrief> {
    @Select("SELECT setval(pg_get_serial_sequence('daily_brief', 'id'), COALESCE((SELECT MAX(id) FROM daily_brief), 0) + 1, false)")
    Long syncIdSequence();
}
