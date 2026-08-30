package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.SocialTopic;
import com.fitness.dto.TopicDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 社交话题Mapper
 */
@Mapper
public interface SocialTopicMapper extends BaseMapper<SocialTopic> {

    /**
     * 获取热门话题列表
     */
    List<TopicDTO> getHotTopics();
}