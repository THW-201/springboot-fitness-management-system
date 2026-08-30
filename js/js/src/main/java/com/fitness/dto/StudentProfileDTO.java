package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学生信息")
public class StudentProfileDTO {

    @Schema(description = "学生信息ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "学号", example = "2021001")
    private String studentNumber;

    @Schema(description = "负责教练ID", example = "2")
    private Long coachId;

    @Schema(description = "负责教练姓名", example = "李教练")
    private String coachName;

    @Schema(description = "性别", example = "MALE", allowableValues = {"MALE", "FEMALE", "OTHER"})
    private String gender;

    @Schema(description = "年龄", example = "20")
    private Integer age;

    @Schema(description = "身高(cm)", example = "175.5")
    private Double height;

    @Schema(description = "体重(kg)", example = "70.0")
    private Double weight;

    @Schema(description = "头像URL", example = "https://example.com/avatar/student1.jpg")
    private String avatarUrl;
}
