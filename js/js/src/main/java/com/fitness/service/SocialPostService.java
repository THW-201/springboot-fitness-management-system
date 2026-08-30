package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialPost;
import com.fitness.dto.CreatePostDTO;
import com.fitness.dto.UpdatePostDTO;
import com.fitness.dto.PostListDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 社交帖子服务
 */
public interface SocialPostService extends IService<SocialPost> {

    /**
     * 创建帖子
     */
    PostListDTO createPost(CreatePostDTO createPostDTO, Long userId);

    /**
     * 更新帖子
     */
    PostListDTO updatePost(Long postId, UpdatePostDTO updatePostDTO, Long userId);

    /**
     * 删除帖子
     */
    void deletePost(Long postId, Long userId);

    /**
     * 获取帖子详情
     */
    PostListDTO getPostDetail(Long postId, Long userId);

    /**
     * 获取帖子列表
     */
    IPage<PostListDTO> getPostList(long current, long size, Long userId);

    /**
     * 获取用户帖子列表
     */
    IPage<PostListDTO> getMyPostList(long current, long size, Long userId);

    /**
     * 获取关注用户帖子列表
     */
    IPage<PostListDTO> getFollowingPostList(long current, long size, Long userId);

    /**
     * 获取管理员帖子列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<PostListDTO> getAdminPostList(long current, long size);
}