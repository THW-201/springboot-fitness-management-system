package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.SocialComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SocialCommentMapper extends BaseMapper<SocialComment> {

    /**
     * 获取帖子评论列表
     */
    List<SocialComment> getCommentsByPostId(@Param("postId") Long postId);

    /**
     * 获取帖子评论数
     */
    int getCommentCountByPostId(@Param("postId") Long postId);

    /**
     * 根据评论ID查询评论，同时关联出发布者的用户名和头像
     */
    SocialComment selectCommentWithUserById(@Param("id") Long id);

    /**
     * 获取评论列表（管理员），同时关联出发布者的用户名和头像
     */
    List<SocialComment> getAdminComments(@Param("current") long current, @Param("size") long size);

    /**
     * 获取评论总数（管理员）
     */
    long getAdminCommentCount();
}