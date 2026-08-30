package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialFollow;
import com.fitness.mapper.SocialFollowMapper;
import com.fitness.service.SocialFollowService;
import com.fitness.dto.UserDTO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 社交关注服务实现
 */
@Service
public class SocialFollowServiceImpl extends ServiceImpl<SocialFollowMapper, SocialFollow> implements SocialFollowService {

    @Override
    @Transactional
    public boolean toggleFollow(Long followingId, Long userId) {
        // 检查是否已关注
        boolean followed = this.checkFollow(userId, followingId);

        if (followed) {
            // 取消关注
            this.removeByMap(Map.of("follower_id", userId, "following_id", followingId));
        } else {
            // 关注
            SocialFollow follow = new SocialFollow()
                    .setFollowerId(userId)
                    .setFollowingId(followingId);
            this.save(follow);
        }

        return !followed; // 返回操作后的状态
    }

    @Override
    public boolean checkFollow(Long followerId, Long followingId) {
        return baseMapper.checkFollow(followerId, followingId) > 0;
    }

    @Override
    public List<UserDTO> getRecommendedUsers(Long userId, int limit) {
        return baseMapper.getRecommendedUsers(userId, limit);
    }

    @Override
    public List<UserDTO> getFollowingUsers(Long userId, int limit) {
        return baseMapper.getFollowingUsers(userId, limit);
    }
}