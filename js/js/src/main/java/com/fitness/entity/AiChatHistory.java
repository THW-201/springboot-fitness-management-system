package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI问答历史实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_chat_history")
@Schema(description = "AI问答历史实体")
public class AiChatHistory {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "问答历史ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("question")
    @Schema(description = "问题")
    private String question;

    @TableField("answer")
    @Schema(description = "回答")
    private String answer;

    @TableField("context")
    @Schema(description = "上下文")
    private String context;

    @TableField("response_time_ms")
    @Schema(description = "响应时间(毫秒)", example = "1500")
    private Integer responseTimeMs;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
