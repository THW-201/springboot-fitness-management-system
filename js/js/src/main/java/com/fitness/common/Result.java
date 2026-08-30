package com.fitness.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一响应封装类
 * 用于封装所有API的响应数据
 * 
 * @param <T> 响应数据的类型
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "统一响应对象")
public class Result<T> {
    
    @Schema(description = "响应状态码", example = "200")
    private int code;
    
    @Schema(description = "响应消息", example = "操作成功")
    private String message;
    
    @Schema(description = "响应数据")
    private T data;
    
    @Schema(description = "错误详情列表")
    private List<String> errors;
    
    @Schema(description = "响应时间戳", example = "2024-01-01T12:00:00")
    private LocalDateTime timestamp;
    
    /**
     * 私有构造函数，防止直接实例化
     */
    private Result() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 私有构造函数，用于内部创建实例
     */
    private Result(int code, String message, T data, List<String> errors) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null, null);
    }
    
    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data, null);
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data, null);
    }
    
    /**
     * 创建成功响应（HTTP 201）
     */
    public static <T> Result<T> created(T data) {
        return new Result<>(ResultCode.CREATED.getCode(), ResultCode.CREATED.getMessage(), data, null);
    }
    
    /**
     * 失败响应（使用ResultCode）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, null);
    }
    
    /**
     * 失败响应（使用ResultCode和自定义消息）
     */
    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return new Result<>(resultCode.getCode(), message, null, null);
    }
    
    /**
     * 失败响应（使用ResultCode和错误详情列表）
     */
    public static <T> Result<T> error(ResultCode resultCode, List<String> errors) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, errors);
    }
    
    /**
     * 失败响应（自定义状态码和消息）
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, null);
    }
    
    /**
     * 失败响应（自定义状态码、消息和错误详情）
     */
    public static <T> Result<T> error(int code, String message, List<String> errors) {
        return new Result<>(code, message, null, errors);
    }
}
