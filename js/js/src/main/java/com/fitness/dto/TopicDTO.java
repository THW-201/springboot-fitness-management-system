package com.fitness.dto;

import lombok.Data;

/**
 * 话题DTO
 */
@Data
public class TopicDTO {

    /**
     * 话题ID
     */
    private Long id;

    /**
     * 话题标题
     */
    private String title;

    /**
     * 帖子数
     */
    private Integer posts;
}