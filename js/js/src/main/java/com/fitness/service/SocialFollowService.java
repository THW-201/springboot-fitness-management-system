package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialFollow;
import com.fitness.dto.UserDTO;

import java.util.List;

/**
 * 社交关注服务
 */
public interface SocialFollowService extends IService<SocialFollow> {

    /**
     * 关注/取消关注
     */
    boolean toggleFollow(Long followingId, Long userId);

    /**
     * 检查用户是否已关注
     */
    boolean checkFollow(Long followerId, Long followingId);

    /**
     * 获取推荐用户列表
     */
    List<UserDTO> getRecommendedUsers(Long userId, int limit);

    /**
     * 获取关注用户列表
     */
    List<UserDTO> getFollowingUsers(Long userId, int limit);
}