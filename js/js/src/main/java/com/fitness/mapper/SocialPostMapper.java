package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.SocialPost;
import com.fitness.dto.PostListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社交帖子Mapper
 */
@Mapper
public interface SocialPostMapper extends BaseMapper<SocialPost> {

    /**
     * 获取帖子列表
     */
    List<PostListDTO> getPostList(
            @Param("current") long current,
            @Param("size") long size,
            @Param("userId") Long userId
    );

    /**
     * 获取用户帖子列表
     */
    List<PostListDTO> getMyPostList(
            @Param("current") long current,
            @Param("size") long size,
            @Param("userId") Long userId
    );

    /**
     * 获取帖子详情
     */
    PostListDTO getPostDetail(
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    /**
     * 获取帖子总数
     */
    long getPostCount();

    /**
     * 获取用户帖子总数
     */
    long getMyPostCount(@Param("userId") Long userId);

    /**
     * 获取关注用户帖子列表
     */
    List<PostListDTO> getFollowingPostList(
            @Param("current") long current,
            @Param("size") long size,
            @Param("userId") Long userId
    );

    /**
     * 获取关注用户帖子总数
     */
    long getFollowingPostCount(@Param("userId") Long userId);
}