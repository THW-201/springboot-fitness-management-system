package com.fitness.dto;

import lombok.Data;

import java.util.List;

/**
 * 更新帖子DTO
 */
@Data
public class UpdatePostDTO {

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
     * 话题列表
     */
    private List<String> topics;
}