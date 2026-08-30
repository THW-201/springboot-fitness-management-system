package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.SocialLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 社交点赞Mapper
 */
@Mapper
public interface SocialLikeMapper extends BaseMapper<SocialLike> {

    /**
     * 检查用户是否已点赞
     */
    Integer checkLike(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 获取帖子点赞数
     */
    int getLikeCountByPostId(@Param("postId") Long postId);
}