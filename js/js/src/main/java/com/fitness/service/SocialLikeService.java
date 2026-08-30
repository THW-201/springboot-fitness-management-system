package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialLike;

/**
 * 社交点赞服务
 */
public interface SocialLikeService extends IService<SocialLike> {

    /**
     * 点赞/取消点赞
     */
    boolean toggleLike(Long postId, Long userId);

    /**
     * 检查用户是否已点赞
     */
    boolean checkLike(Long postId, Long userId);

    /**
     * 获取帖子点赞数
     */
    int getLikeCountByPostId(Long postId);
}