package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialPost;
import com.fitness.mapper.SocialPostMapper;
import com.fitness.service.SocialPostService;
import com.fitness.dto.CreatePostDTO;
import com.fitness.dto.UpdatePostDTO;
import com.fitness.dto.PostListDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 社交帖子服务实现
 */
@Service
public class SocialPostServiceImpl extends ServiceImpl<SocialPostMapper, SocialPost> implements SocialPostService {

    @Override
    @Transactional
    public PostListDTO createPost(CreatePostDTO createPostDTO, Long userId) {
        // 创建帖子
        SocialPost post = new SocialPost()
                .setUserId(userId)
                .setContent(createPostDTO.getContent())
                .setImages(createPostDTO.getImages())
                .setVideo(createPostDTO.getVideo())
                .setExerciseType(createPostDTO.getExerciseType())
                .setExerciseDuration(createPostDTO.getExerciseDuration())
                .setExerciseCalories(createPostDTO.getExerciseCalories())
                .setLikes(0)
                .setComments(0);

        this.save(post);

        // 返回帖子详情
        return this.getPostDetail(post.getId(), userId);
    }

    @Override
    @Transactional
    public PostListDTO updatePost(Long postId, UpdatePostDTO updatePostDTO, Long userId) {
        // 检查帖子是否存在且属于当前用户
        SocialPost post = this.getById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            throw new RuntimeException("帖子不存在或无权限操作");
        }

        // 更新帖子
        post.setContent(updatePostDTO.getContent())
                .setImages(updatePostDTO.getImages())
                .setVideo(updatePostDTO.getVideo())
                .setExerciseType(updatePostDTO.getExerciseType())
                .setExerciseDuration(updatePostDTO.getExerciseDuration())
                .setExerciseCalories(updatePostDTO.getExerciseCalories());

        this.updateById(post);

        // 返回帖子详情
        return this.getPostDetail(postId, userId);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        // 检查帖子是否存在
        SocialPost post = this.getById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        // 如果不是管理员操作，检查帖子是否属于当前用户
        if (userId != null && !post.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作");
        }

        // 删除帖子
        this.removeById(postId);
    }

    @Override
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<PostListDTO> getAdminPostList(long current, long size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PostListDTO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        long offset = (current - 1) * size;
        page.setRecords(baseMapper.getPostList(offset, size, null));
        page.setTotal(baseMapper.getPostCount());
        return page;
    }

    @Override
    public PostListDTO getPostDetail(Long postId, Long userId) {
        return baseMapper.getPostDetail(postId, userId);
    }

    @Override
    public IPage<PostListDTO> getPostList(long current, long size, Long userId) {
        IPage<PostListDTO> page = new Page<>(current, size);
        long offset = (current - 1) * size;
        page.setRecords(baseMapper.getPostList(offset, size, userId));
        page.setTotal(baseMapper.getPostCount());
        return page;
    }

    @Override
    public IPage<PostListDTO> getMyPostList(long current, long size, Long userId) {
        IPage<PostListDTO> page = new Page<>(current, size);
        long offset = (current - 1) * size;
        page.setRecords(baseMapper.getMyPostList(offset, size, userId));
        page.setTotal(baseMapper.getMyPostCount(userId));
        return page;
    }

    @Override
    public IPage<PostListDTO> getFollowingPostList(long current, long size, Long userId) {
        IPage<PostListDTO> page = new Page<>(current, size);
        long offset = (current - 1) * size;
        page.setRecords(baseMapper.getFollowingPostList(offset, size, userId));
        page.setTotal(baseMapper.getFollowingPostCount(userId));
        return page;
    }
}