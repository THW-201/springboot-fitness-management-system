package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialLike;
import com.fitness.mapper.SocialLikeMapper;
import com.fitness.service.SocialLikeService;
import com.fitness.service.SocialPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

/**
 * 社交点赞服务实现
 */
@Service
public class SocialLikeServiceImpl extends ServiceImpl<SocialLikeMapper, SocialLike> implements SocialLikeService {

    @Autowired
    private SocialPostService socialPostService;

    @Override
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        // 检查帖子是否存在
        if (socialPostService.getById(postId) == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 检查是否已点赞
        boolean liked = this.checkLike(postId, userId);

        if (liked) {
            // 取消点赞
            this.removeByMap(Map.of("post_id", postId, "user_id", userId));
        } else {
            // 点赞
            SocialLike like = new SocialLike()
                    .setPostId(postId)
                    .setUserId(userId);
            this.save(like);
        }

        // 更新帖子点赞数
        int likeCount = this.getLikeCountByPostId(postId);
        socialPostService.getById(postId).setLikes(likeCount);
        socialPostService.updateById(socialPostService.getById(postId));

        return !liked; // 返回操作后的状态
    }

    @Override
    public boolean checkLike(Long postId, Long userId) {
        return baseMapper.checkLike(postId, userId) > 0;
    }

    @Override
    public int getLikeCountByPostId(Long postId) {
        return baseMapper.getLikeCountByPostId(postId);
    }
}