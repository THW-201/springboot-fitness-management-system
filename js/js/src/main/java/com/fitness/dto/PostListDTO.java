package com.fitness.dto;

import com.fitness.entity.SocialComment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子列表DTO
 */
@Data
public class PostListDTO {

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 图片URL列表
     */
    private List<String> images;

    /**
     * 视频URL
     */
    private String video;

    /**
     * 运动类型
     */
    private String exerciseType;

    /**
     * 运动时长（分钟）
     */
    private Integer exerciseDuration;

    /**
     * 消耗卡路里
     */
    private Integer exerciseCalories;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 是否已点赞
     */
    private Boolean liked;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 评论列表
     */
    private List<CommentDTO> comments;
}

/**
 * 评论DTO
 */
@Data
class CommentDTO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}