-- 大学生健身管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fitness_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role ENUM('ADMIN', 'COACH', 'STUDENT') NOT NULL COMMENT '角色',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学生信息表
CREATE TABLE IF NOT EXISTS student_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生信息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    student_number VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    coach_id BIGINT COMMENT '负责教练ID',
    gender ENUM('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
    age INT COMMENT '年龄',
    height DECIMAL(5,2) COMMENT '身高(cm)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    late_cancellation_count INT DEFAULT 0 COMMENT '迟到取消次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (coach_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_coach_id (coach_id),
    INDEX idx_student_number (student_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

-- 教练信息表
CREATE TABLE IF NOT EXISTS coach_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教练信息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    specialization VARCHAR(100) COMMENT '专业领域',
    certification VARCHAR(255) COMMENT '资格证书',
    experience_years INT COMMENT '从业年限',
    bio TEXT COMMENT '个人简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练信息表';

-- 课程表
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    coach_id BIGINT NOT NULL COMMENT '授课教练ID',
    course_type VARCHAR(50) COMMENT '课程类型',
    capacity INT NOT NULL COMMENT '容量',
    current_enrollment INT DEFAULT 0 COMMENT '当前报名人数',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    location VARCHAR(100) COMMENT '上课地点',
    status ENUM('AVAILABLE', 'FULL', 'CANCELLED', 'COMPLETED') DEFAULT 'AVAILABLE' COMMENT '课程状态',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (coach_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_coach_id (coach_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    INDEX idx_course_type (course_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 器材表
CREATE TABLE IF NOT EXISTS equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '器材ID',
    name VARCHAR(100) NOT NULL COMMENT '器材名称',
    equipment_type VARCHAR(50) COMMENT '器材类型',
    description TEXT COMMENT '器材描述',
    location VARCHAR(100) COMMENT '存放位置',
    status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE', 'DAMAGED') DEFAULT 'AVAILABLE' COMMENT '器材状态',
    purchase_date DATE COMMENT '购买日期',
    last_maintenance_date DATE COMMENT '最后维护日期',
    image_url VARCHAR(255) COMMENT '器材图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_equipment_type (equipment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';

-- 预约表
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    reservation_type ENUM('COURSE', 'EQUIPMENT') NOT NULL COMMENT '预约类型',
    course_id BIGINT COMMENT '课程ID',
    equipment_id BIGINT COMMENT '器材ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'CONFIRMED' COMMENT '预约状态',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    INDEX idx_composite (student_id, start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 签到表
CREATE TABLE IF NOT EXISTS check_ins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '签到ID',
    reservation_id BIGINT NOT NULL COMMENT '预约ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    check_in_time TIMESTAMP NOT NULL COMMENT '签到时间',
    check_out_time TIMESTAMP NULL COMMENT '签退时间',
    duration_minutes INT COMMENT '活动时长(分钟)',
    location VARCHAR(100) COMMENT '签到位置',
    calories_burned DECIMAL(8,2) COMMENT '消耗卡路里',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_reservation_id (reservation_id),
    INDEX idx_student_id (student_id),
    INDEX idx_check_in_time (check_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';

-- 健康计划表
CREATE TABLE IF NOT EXISTS health_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '健康计划ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    description TEXT COMMENT '计划描述',
    target_weight DECIMAL(5,2) COMMENT '目标体重(kg)',
    target_duration_minutes INT COMMENT '目标运动时长(分钟/周)',
    current_weight DECIMAL(5,2) COMMENT '当前体重(kg)',
    current_duration_minutes INT DEFAULT 0 COMMENT '当前运动时长(分钟/周)',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    status ENUM('ACTIVE', 'COMPLETED', 'ABANDONED') DEFAULT 'ACTIVE' COMMENT '计划状态',
    completion_percentage DECIMAL(5,2) DEFAULT 0 COMMENT '完成百分比',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康计划表';

-- 健康计划打卡表
CREATE TABLE IF NOT EXISTS plan_check_ins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡ID',
    plan_id BIGINT NOT NULL COMMENT '健康计划ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    check_date DATE NOT NULL COMMENT '打卡日期',
    check_in_time TIMESTAMP NOT NULL COMMENT '开始打卡时间',
    check_out_time TIMESTAMP NULL COMMENT '结束打卡时间',
    duration_minutes INT COMMENT '打卡时长(分钟)',
    status VARCHAR(20) DEFAULT 'IN_PROGRESS' COMMENT '打卡状态(IN_PROGRESS, COMPLETED)',
    exercise_type VARCHAR(50) COMMENT '运动方式',
    calories_burned DECIMAL(8,2) COMMENT '消耗卡路里',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (plan_id) REFERENCES health_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_plan_id (plan_id),
    INDEX idx_student_id (student_id),
    INDEX idx_check_date (check_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康计划打卡表';

-- AI问答历史表
CREATE TABLE IF NOT EXISTS ai_chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问答历史ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question TEXT NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '回答',
    context TEXT COMMENT '上下文',
    response_time_ms INT COMMENT '响应时间(毫秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答历史表';

-- 健康建议表
CREATE TABLE IF NOT EXISTS health_advice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '健康建议ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    advice_type VARCHAR(50) COMMENT '建议类型',
    content TEXT NOT NULL COMMENT '建议内容',
    based_on_data JSON COMMENT '基于的数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_student_id (student_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康建议表';

-- 审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    details TEXT COMMENT '操作详情',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(255) COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';
