package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialComment;
import com.fitness.dto.CreateCommentDTO;

import java.util.List;

/**
 * 社交评论服务
 */
public interface SocialCommentService extends IService<SocialComment> {

    /**
     * 创建评论
     */
    SocialComment createComment(Long postId, CreateCommentDTO createCommentDTO, Long userId);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取帖子评论列表
     */
    List<SocialComment> getCommentsByPostId(Long postId);

    /**
     * 获取帖子评论数
     */
    int getCommentCountByPostId(Long postId);

    /**
     * 获取管理员评论列表
     */
    com.baomidou.mybatisplus.extension.plugins.pagination.Page<?> getAdminCommentList(long current, long size);
}