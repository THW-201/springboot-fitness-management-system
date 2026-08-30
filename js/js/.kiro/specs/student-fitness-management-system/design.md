# 设计文档 - 大学生健身管理系统

## 概述

大学生健身管理系统是一个基于前后端分离架构的Web应用，旨在为大学健身中心提供完整的数字化管理解决方案。系统采用现代化的技术栈，实现了用户认证、课程管理、器材预约、健康数据追踪和AI智能建议等核心功能。

### 技术栈

**后端技术栈：**
- Spring Boot 3.x - 应用框架
- MyBatis Plus - ORM框架
- Spring Security - 安全框架
- JWT - 无状态认证
- Redis - 缓存和分布式锁
- MySQL 8.0 - 关系型数据库
- DeepSeek API - AI服务

**前端技术栈：**
- Vue 3 - 前端框架
- Vite - 构建工具
- Element Plus - UI组件库
- Axios - HTTP客户端
- Pinia - 状态管理
- Vue Router - 路由管理

### 核心特性

1. **多角色权限控制** - 支持管理员、教练、学生三种角色，实现细粒度的数据隔离
2. **JWT认证机制** - 无状态认证，支持Token撤销和刷新
3. **并发控制** - 使用数据库锁和Redis分布式锁处理并发预约
4. **缓存优化** - 多层缓存策略提升系统性能
5. **AI集成** - 集成DeepSeek API提供智能问答和个性化推荐
6. **实时通知** - 预约提醒和状态变更通知

## 架构设计

### 系统架构

系统采用经典的三层架构模式，前后端完全分离：

```
┌─────────────────────────────────────────────────────────┐
│                      前端层 (Vue 3)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ 用户界面 │  │ 路由管理 │  │ 状态管理 │  │ API调用 │ │
│  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
└─────────────────────────────────────────────────────────┘
                          │ HTTPS/JSON
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   后端层 (Spring Boot)                   │
│  ┌──────────────────────────────────────────────────┐   │
│  │              Controller Layer                     │   │
│  │  (REST API, 请求验证, 响应封装)                  │   │
│  └──────────────────────────────────────────────────┘   │
│                          │                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │              Service Layer                        │   │
│  │  (业务逻辑, 事务管理, 缓存控制)                  │   │
│  └──────────────────────────────────────────────────┘   │
│                          │                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │              Data Access Layer                    │   │
│  │  (MyBatis Plus, 数据库操作)                      │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│    MySQL     │  │    Redis     │  │ DeepSeek API │
│   (持久化)   │  │  (缓存/锁)   │  │  (AI服务)    │
└──────────────┘  └──────────────┘  └──────────────┘
```

### 分层职责

**Controller层：**
- 接收HTTP请求并验证参数
- 调用Service层处理业务逻辑
- 统一封装响应格式
- 处理异常并返回错误信息

**Service层：**
- 实现核心业务逻辑
- 管理事务边界
- 协调多个Mapper操作
- 实现缓存策略
- 调用外部服务（DeepSeek API）

**Data Access层：**
- 使用MyBatis Plus进行数据库操作
- 定义实体映射
- 实现复杂查询
- 管理数据库连接

## 组件和接口

### 核心组件

#### 1. 认证授权组件

**JwtTokenProvider**
```java
public class JwtTokenProvider {
    String generateToken(UserDetails userDetails);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
    Claims getClaimsFromToken(String token);
}
```

**JwtAuthenticationFilter**
- 拦截所有HTTP请求
- 从请求头提取JWT Token
- 验证Token有效性
- 将用户信息加载到SecurityContext

**CustomUserDetailsService**
- 实现Spring Security的UserDetailsService接口
- 从数据库加载用户信息
- 构建UserDetails对象包含角色权限

#### 2. 权限控制组件

**RoleBasedAccessControl**
```java
@Component
public class RoleBasedAccessControl {
    boolean hasPermission(Authentication auth, String resource, String action);
    boolean canAccessStudentData(Long coachId, Long studentId);
}
```

**DataIsolationInterceptor**
- MyBatis拦截器
- 自动在SQL中添加数据隔离条件
- 教练只能查询自己负责的学生数据

#### 3. 预约管理组件

**ReservationService**
```java
public interface ReservationService {
    ReservationDTO reserveCourse(Long studentId, Long courseId);
    ReservationDTO reserveEquipment(Long studentId, Long equipmentId, 
                                    LocalDateTime startTime, LocalDateTime endTime);
    void cancelReservation(Long reservationId);
    boolean checkConflict(Long studentId, LocalDateTime startTime, LocalDateTime endTime);
}
```

**ConflictDetector**
```java
public class ConflictDetector {
    boolean hasTimeConflict(Long studentId, LocalDateTime start, LocalDateTime end);
    boolean isCourseAvailable(Long courseId);
    boolean isEquipmentAvailable(Long equipmentId, LocalDateTime start, LocalDateTime end);
}
```

**DistributedLockManager**
```java
public class DistributedLockManager {
    boolean tryLock(String key, long timeout, TimeUnit unit);
    void unlock(String key);
}
```

#### 4. 缓存管理组件

**CacheService**
```java
public interface CacheService {
    <T> T get(String key, Class<T> type);
    void set(String key, Object value, long timeout, TimeUnit unit);
    void delete(String key);
    void deletePattern(String pattern);
}
```

**缓存策略：**
- 课程列表：10分钟TTL
- 器材状态：5分钟TTL
- 统计数据：5分钟TTL
- 推荐结果：1小时TTL
- JWT Token：根据Token过期时间

#### 5. AI服务组件

**DeepSeekClient**
```java
public interface DeepSeekClient {
    String chat(String prompt, String context);
    RecommendationDTO generateRecommendation(StudentHealthData data);
    HealthAdviceDTO analyzeHealthData(StudentHealthData data);
}
```

**AIServiceFacade**
- 封装DeepSeek API调用
- 实现重试机制
- 处理API限流
- 记录调用日志

#### 6. 通知组件

**NotificationService**
```java
public interface NotificationService {
    void sendReservationConfirmation(Reservation reservation);
    void sendReminder(Reservation reservation);
    void sendCancellationNotice(Reservation reservation);
    void sendGoalAchievement(HealthPlan plan);
}
```

### RESTful API设计

#### API规范

**基础URL：** `http://api.fitness.edu.cn/api/v1`

**认证方式：** Bearer Token (JWT)

**请求头：**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**统一响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1234567890
}
```

**错误响应格式：**
```json
{
  "code": 400,
  "message": "错误描述",
  "errors": ["详细错误1", "详细错误2"],
  "timestamp": 1234567890
}
```

#### API端点

**认证相关：**
```
POST   /auth/register          - 用户注册
POST   /auth/login             - 用户登录
POST   /auth/logout            - 用户登出
POST   /auth/refresh           - 刷新Token
GET    /auth/me                - 获取当前用户信息
```

**用户管理：**
```
GET    /users                  - 获取用户列表（管理员）
GET    /users/{id}             - 获取用户详情
PUT    /users/{id}             - 更新用户信息
DELETE /users/{id}             - 删除用户（管理员）
GET    /users/{id}/students    - 获取教练负责的学生列表（教练）
```

**课程管理：**
```
GET    /courses                - 获取课程列表
GET    /courses/{id}           - 获取课程详情
POST   /courses                - 创建课程（管理员/教练）
PUT    /courses/{id}           - 更新课程（管理员/教练）
DELETE /courses/{id}           - 删除课程（管理员/教练）
GET    /courses/search         - 搜索课程
```

**器材管理：**
```
GET    /equipment              - 获取器材列表
GET    /equipment/{id}         - 获取器材详情
POST   /equipment              - 添加器材（管理员）
PUT    /equipment/{id}         - 更新器材（管理员）
DELETE /equipment/{id}         - 删除器材（管理员）
GET    /equipment/available    - 获取可用器材
```

**预约管理：**
```
GET    /reservations           - 获取预约列表
GET    /reservations/{id}      - 获取预约详情
POST   /reservations/course    - 预约课程
POST   /reservations/equipment - 预约器材
DELETE /reservations/{id}      - 取消预约
GET    /reservations/my        - 获取我的预约
```

**签到打卡：**
```
POST   /checkins               - 签到
PUT    /checkins/{id}/checkout - 签退
GET    /checkins/my            - 获取我的签到记录
```

**健康计划：**
```
GET    /health-plans           - 获取健康计划列表
GET    /health-plans/{id}      - 获取健康计划详情
POST   /health-plans           - 创建健康计划
PUT    /health-plans/{id}      - 更新健康计划
DELETE /health-plans/{id}      - 删除健康计划
PUT    /health-plans/{id}/progress - 更新进度
```

**数据统计：**
```
GET    /statistics/courses     - 课程统计（管理员）
GET    /statistics/equipment   - 器材统计（管理员）
GET    /statistics/students    - 学生统计（管理员）
GET    /statistics/my          - 我的健康数据统计
```

**AI服务：**
```
POST   /ai/chat                - AI问答
GET    /ai/recommendations     - 获取个性化推荐
GET    /ai/health-advice       - 获取健康建议
GET    /ai/chat-history        - 获取问答历史
```

## 数据模型

### 数据库设计

#### 用户表 (users)

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role ENUM('ADMIN', 'COACH', 'STUDENT') NOT NULL COMMENT '角色',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 学生信息表 (student_profiles)

```sql
CREATE TABLE student_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    student_number VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    coach_id BIGINT COMMENT '负责教练ID',
    gender ENUM('MALE', 'FEMALE', 'OTHER') COMMENT '性别',
    age INT COMMENT '年龄',
    height DECIMAL(5,2) COMMENT '身高(cm)',
    weight DECIMAL(5,2) COMMENT '体重(kg)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (coach_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_coach_id (coach_id),
    INDEX idx_student_number (student_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';
```

#### 教练信息表 (coach_profiles)

```sql
CREATE TABLE coach_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    specialization VARCHAR(100) COMMENT '专业领域',
    certification VARCHAR(255) COMMENT '资格证书',
    experience_years INT COMMENT '从业年限',
    bio TEXT COMMENT '个人简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教练信息表';
```

#### 课程表 (courses)

```sql
CREATE TABLE courses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    description TEXT COMMENT '课程描述',
    coach_id BIGINT NOT NULL COMMENT '授课教练ID',
    course_type VARCHAR(50) COMMENT '课程类型',
    capacity INT NOT NULL COMMENT '容量',
    current_enrollment INT DEFAULT 0 COMMENT '当前报名人数',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    location VARCHAR(100) COMMENT '上课地点',
    status ENUM('AVAILABLE', 'FULL', 'CANCELLED', 'COMPLETED') DEFAULT 'AVAILABLE',
    created_by BIGINT COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (coach_id) REFERENCES users(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_coach_id (coach_id),
    INDEX idx_start_time (start_time),
    INDEX idx_status (status),
    INDEX idx_course_type (course_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';
```

#### 器材表 (equipment)

```sql
CREATE TABLE equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '器材名称',
    equipment_type VARCHAR(50) COMMENT '器材类型',
    description TEXT COMMENT '器材描述',
    location VARCHAR(100) COMMENT '存放位置',
    status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE', 'DAMAGED') DEFAULT 'AVAILABLE',
    purchase_date DATE COMMENT '购买日期',
    last_maintenance_date DATE COMMENT '最后维护日期',
    image_url VARCHAR(255) COMMENT '器材图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_equipment_type (equipment_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='器材表';
```

#### 预约表 (reservations)

```sql
CREATE TABLE reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    reservation_type ENUM('COURSE', 'EQUIPMENT') NOT NULL COMMENT '预约类型',
    course_id BIGINT COMMENT '课程ID',
    equipment_id BIGINT COMMENT '器材ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'CONFIRMED',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    cancelled_at TIMESTAMP NULL COMMENT '取消时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
```

#### 签到表 (check_ins)

```sql
CREATE TABLE check_ins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL COMMENT '预约ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    check_in_time TIMESTAMP NOT NULL COMMENT '签到时间',
    check_out_time TIMESTAMP NULL COMMENT '签退时间',
    duration_minutes INT COMMENT '活动时长(分钟)',
    location VARCHAR(100) COMMENT '签到位置',
    calories_burned DECIMAL(8,2) COMMENT '消耗卡路里',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_reservation_id (reservation_id),
    INDEX idx_student_id (student_id),
    INDEX idx_check_in_time (check_in_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到表';
```

#### 健康计划表 (health_plans)

```sql
CREATE TABLE health_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    description TEXT COMMENT '计划描述',
    target_weight DECIMAL(5,2) COMMENT '目标体重(kg)',
    target_duration_minutes INT COMMENT '目标运动时长(分钟/周)',
    current_weight DECIMAL(5,2) COMMENT '当前体重(kg)',
    current_duration_minutes INT DEFAULT 0 COMMENT '当前运动时长(分钟/周)',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    status ENUM('ACTIVE', 'COMPLETED', 'ABANDONED') DEFAULT 'ACTIVE',
    completion_percentage DECIMAL(5,2) DEFAULT 0 COMMENT '完成百分比',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康计划表';
```

#### AI问答历史表 (ai_chat_history)

```sql
CREATE TABLE ai_chat_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    question TEXT NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '回答',
    context TEXT COMMENT '上下文',
    response_time_ms INT COMMENT '响应时间(毫秒)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答历史表';
```

#### 健康建议表 (health_advice)

```sql
CREATE TABLE health_advice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL COMMENT '学生ID',
    advice_type VARCHAR(50) COMMENT '建议类型',
    content TEXT NOT NULL COMMENT '建议内容',
    based_on_data JSON COMMENT '基于的数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    INDEX idx_student_id (student_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康建议表';
```

#### 审计日志表 (audit_logs)

```sql
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作用户ID',
    action VARCHAR(50) NOT NULL COMMENT '操作类型',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    details TEXT COMMENT '操作详情',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(255) COMMENT '用户代理',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';
```

### 数据库关系图

```
users (1) ──────────── (*) student_profiles
  │                           │
  │                           │ (coach_id)
  │                           │
  │ (coach_id)                ▼
  └──────────────────────► users (教练)
  
users (学生) (1) ──────── (*) reservations
                              │
                              ├──── (*) courses
                              └──── (*) equipment

reservations (1) ──────── (*) check_ins

users (学生) (1) ──────── (*) health_plans

users (1) ──────── (*) ai_chat_history

users (学生) (1) ──────── (*) health_advice
```

### 实体类设计

**User实体：**
```java
@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    @TableField("role")
    private UserRole role;
    private String avatarUrl;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Reservation实体：**
```java
@Data
@TableName("reservations")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private ReservationType reservationType;
    private Long courseId;
    private Long equipmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status;
    private String cancelReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

