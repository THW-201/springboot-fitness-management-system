package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialTopic;
import com.fitness.dto.TopicDTO;

import java.util.List;

/**
 * 社交话题服务
 */
public interface SocialTopicService extends IService<SocialTopic> {

    /**
     * 获取热门话题列表
     */
    List<TopicDTO> getHotTopics();

    /**
     * 创建或获取话题
     */
    SocialTopic createOrGetTopic(String title);

    /**
     * 创建话题（管理员）
     */
    void createTopic(String title);

    /**
     * 更新话题（管理员）
     */
    void updateTopic(Long id, String title);

    /**
     * 删除话题（管理员）
     */
    void deleteTopic(Long id);
}