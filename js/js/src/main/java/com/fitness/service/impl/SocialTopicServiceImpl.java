package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialTopic;
import com.fitness.mapper.SocialTopicMapper;
import com.fitness.service.SocialTopicService;
import com.fitness.dto.TopicDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社交话题服务实现
 */
@Service
public class SocialTopicServiceImpl extends ServiceImpl<SocialTopicMapper, SocialTopic> implements SocialTopicService {

    @Override
    public List<TopicDTO> getHotTopics() {
        return baseMapper.getHotTopics();
    }

    @Override
    public SocialTopic createOrGetTopic(String title) {
        // 查找话题
        SocialTopic topic = this.getOne(new QueryWrapper<SocialTopic>().eq("title", title));
        if (topic == null) {
            // 创建话题
            topic = new SocialTopic()
                    .setTitle(title)
                    .setPosts(0);
            this.save(topic);
        }
        return topic;
    }

    @Override
    public void createTopic(String title) {
        // 检查话题是否已存在
        SocialTopic existingTopic = this.getOne(new QueryWrapper<SocialTopic>().eq("title", title));
        if (existingTopic != null) {
            throw new RuntimeException("话题已存在");
        }

        // 创建话题
        SocialTopic topic = new SocialTopic()
                .setTitle(title)
                .setPosts(0);
        this.save(topic);
    }

    @Override
    public void updateTopic(Long id, String title) {
        // 检查话题是否存在
        SocialTopic topic = this.getById(id);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }

        // 检查新标题是否已被其他话题使用
        SocialTopic existingTopic = this.getOne(new QueryWrapper<SocialTopic>().eq("title", title).ne("id", id));
        if (existingTopic != null) {
            throw new RuntimeException("话题标题已存在");
        }

        // 更新话题
        topic.setTitle(title);
        this.updateById(topic);
    }

    @Override
    public void deleteTopic(Long id) {
        // 检查话题是否存在
        SocialTopic topic = this.getById(id);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }

        // 删除话题
        this.removeById(id);
    }
}