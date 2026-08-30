package com.fitness.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一状态码枚举
 * 定义系统中所有可能的响应状态码
 */
@Schema(description = "响应状态码")
public enum ResultCode {
    
    // 成功状态码 (2xx)
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),
    
    // 客户端错误 (4xx)
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问该资源"),
    NOT_FOUND(404, "请求的资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "资源冲突"),
    
    // 业务错误 (4xx)
    USERNAME_EXISTS(4001, "用户名已存在"),
    EMAIL_EXISTS(4002, "邮箱已存在"),
    INVALID_CREDENTIALS(4003, "用户名或密码错误"),
    TOKEN_EXPIRED(4004, "Token已过期"),
    TOKEN_INVALID(4005, "Token无效"),
    COURSE_FULL(4006, "课程已满员"),
    TIME_CONFLICT(4007, "时间冲突"),
    EQUIPMENT_OCCUPIED(4008, "器材已被占用"),
    RESERVATION_NOT_FOUND(4009, "预约不存在"),
    LATE_CANCELLATION(4010, "取消时间过晚"),
    CHECKIN_TIME_INVALID(4011, "签到时间无效"),
    COURSE_HAS_RESERVATIONS(4012, "课程存在未完成的预约，无法删除"),
    VALIDATION_ERROR(4013, "参数验证失败"),
    NOT_IMPLEMENTED(4014, "功能尚未实现"),
    
    // 服务器错误 (5xx)
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用"),
    DEEPSEEK_API_ERROR(5001, "AI服务调用失败"),
    DATABASE_ERROR(5002, "数据库操作失败"),
    REDIS_ERROR(5003, "缓存服务错误");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
