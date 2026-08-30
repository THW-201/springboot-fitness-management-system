package com.fitness.dto;

import lombok.Data;

/**
 * 创建评论DTO
 */
@Data
public class CreateCommentDTO {

    /**
     * 评论内容
     */
    private String content;
}