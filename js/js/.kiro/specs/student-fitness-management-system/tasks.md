# 实现计划：大学生健身管理系统（后端）

## 概述

本实现计划专注于大学生健身管理系统的后端开发，基于 Spring Boot 3.x 技术栈。系统采用三层架构（Controller-Service-DAO），实现用户认证、权限控制、课程管理、器材预约、健康数据追踪和 AI 智能服务等核心功能。

## 任务列表

- [x] 1. 搭建项目基础架构
  - 创建 Spring Boot 项目，配置 Maven/Gradle 依赖
  - 配置 application.yml（数据库、Redis、JWT 等）
  - 创建项目目录结构（controller、service、mapper、entity、dto、config、util）
  - 配置 MyBatis Plus 和数据源
  - 配置 Redis 连接池
  - _需求: 17.1, 17.2, 16.1, 16.4_

- [x] 2. 实现数据库层
  - [x] 2.1 创建数据库表结构
    - 执行 SQL 脚本创建所有数据表（users、student_profiles、coach_profiles、courses、equipment、reservations、check_ins、health_plans、ai_chat_history、health_advice、audit_logs）
    - 创建必要的索引以优化查询性能
    - _需求: 17.1, 17.3_
  
  - [x] 2.2 创建实体类（Entity）
    - 创建 User、StudentProfile、CoachProfile、Course、Equipment、Reservation、CheckIn、HealthPlan、AiChatHistory、HealthAdvice、AuditLog 实体类
    - 使用 MyBatis Plus 注解配置表映射
    - 定义枚举类型（UserRole、ReservationType、ReservationStatus 等）
    - _需求: 17.2_
  
  - [x] 2.3 创建 Mapper 接口
    - 创建所有实体对应的 Mapper 接口，继承 BaseMapper
    - 定义复杂查询方法（如按教练查询学生、按时间范围查询预约等）
    - _需求: 17.2_

- [x] 3. 实现统一响应和异常处理
  - [x] 3.1 创建统一响应封装类
    - 创建 Result 类封装响应数据（code、message、data、timestamp）
    - 创建 ResultCode 枚举定义状态码
    - _需求: 所有 API 相关需求_
  
  - [x] 3.2 实现全局异常处理器
    - 使用 @ControllerAdvice 创建全局异常处理器
    - 处理业务异常、参数验证异常、认证授权异常
    - 统一返回错误响应格式
    - _需求: 所有 API 相关需求_

- [x] 4. 实现 JWT 认证机制
  - [x] 4.1 实现 JWT 工具类
    - 创建 JwtTokenProvider 类
    - 实现 Token 生成、验证、解析功能
    - 配置 Token 过期时间和签名密钥
    - _需求: 1.1, 1.3, 1.5_
  
  - [x] 4.2 实现 UserDetailsService
    - 创建 CustomUserDetailsService 实现 Spring Security 的 UserDetailsService
    - 从数据库加载用户信息并构建 UserDetails 对象
    - _需求: 1.1_
  
  - [x] 4.3 实现 JWT 认证过滤器
    - 创建 JwtAuthenticationFilter 拦截请求
    - 从请求头提取并验证 JWT Token
    - 将用户信息加载到 SecurityContext
    - _需求: 1.3, 1.4_
  
  - [x] 4.4 配置 Spring Security
    - 创建 SecurityConfig 配置类
    - 配置认证和授权规则
    - 配置密码加密器（BCryptPasswordEncoder）
    - 禁用 CSRF（前后端分离）
    - _需求: 1.1, 1.3, 3.3_
  
  - [x] 4.5 实现 Token 存储到 Redis
    - 在 Redis 中存储有效 Token
    - 实现 Token 撤销功能（登出时删除 Redis 中的 Token）
    - _需求: 1.6_

- [x] 5. 实现用户认证服务
  - [x] 5.1 创建认证相关 DTO
    - 创建 RegisterRequest、LoginRequest、LoginResponse、UserDTO 等 DTO 类
    - 添加参数验证注解（@NotBlank、@Email 等）
    - _需求: 1.1, 3.1_
  
  - [x] 5.2 实现 AuthService
    - 实现用户注册功能（验证用户名/邮箱唯一性、密码加密）
    - 实现用户登录功能（验证凭据、生成 JWT Token）
    - 实现用户登出功能（撤销 Token）
    - 实现 Token 刷新功能
    - _需求: 1.1, 1.2, 3.1, 3.2, 3.3_
  
  - [x] 5.3 实现 AuthController
    - 创建 /api/v1/auth 路由
    - 实现注册、登录、登出、刷新 Token、获取当前用户信息接口
    - _需求: 1.1, 1.2_

- [x] 6. 实现基于角色的访问控制
  - [x] 6.1 实现权限控制组件
    - 创建 RoleBasedAccessControl 组件
    - 实现角色权限验证方法
    - 实现教练数据隔离验证方法
    - _需求: 2.1, 2.2, 2.3, 2.4_
  
  - [x] 6.2 实现数据隔离拦截器
    - 创建 MyBatis 拦截器 DataIsolationInterceptor
    - 自动在 SQL 中添加教练数据隔离条件
    - _需求: 2.2, 2.5_
  
  - [x] 6.3 创建权限注解
    - 创建自定义注解 @RequireRole
    - 实现 AOP 切面验证用户角色
    - _需求: 2.1, 2.2, 2.3, 2.4_

- [x] 7. 实现用户管理服务
  - [x] 7.1 实现 UserService
    - 实现获取用户列表功能（管理员权限）
    - 实现获取用户详情功能
    - 实现更新用户信息功能
    - 实现删除用户功能（管理员权限）
    - 实现获取教练负责的学生列表功能
    - _需求: 2.1, 2.2, 2.3, 3.4_
  
  - [x] 7.2 实现 UserController
    - 创建 /api/v1/users 路由
    - 实现用户管理相关接口
    - 添加权限控制注解
    - _需求: 2.1, 2.2, 2.3, 3.4_

- [x] 8. 实现 Redis 缓存服务
  - [x] 8.1 创建缓存服务接口
    - 创建 CacheService 接口定义缓存操作
    - 实现 RedisTemplate 封装类
    - 实现 get、set、delete、deletePattern 方法
    - _需求: 16.1, 16.2, 16.3, 16.4_
  
  - [x] 8.2 实现缓存注解支持
    - 配置 Spring Cache 使用 Redis
    - 在 Service 层使用 @Cacheable、@CacheEvict 注解
    - _需求: 16.1, 16.2, 16.3_

- [x] 9. 实现课程管理服务
  - [x] 9.1 创建课程相关 DTO
    - 创建 CourseDTO、CreateCourseRequest、UpdateCourseRequest 等 DTO
    - 添加参数验证注解
    - _需求: 4.1, 4.2, 4.3_
  
  - [x] 9.2 实现 CourseService
    - 实现创建课程功能（记录创建者、验证权限）
    - 实现更新课程功能（验证权限）
    - 实现删除课程功能（检查未完成预约、验证权限）
    - 实现获取课程列表功能（支持分页、搜索、筛选）
    - 实现获取课程详情功能
    - 集成 Redis 缓存（10 分钟 TTL）
    - _需求: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 16.1, 16.3_
  
  - [x] 9.3 实现 CourseController
    - 创建 /api/v1/courses 路由
    - 实现课程管理相关接口
    - 添加权限控制注解
    - _需求: 4.1, 4.2, 4.3, 4.4, 4.5, 4.6, 4.7_

- [x] 10. 实现器材管理服务
  - [x] 10.1 创建器材相关 DTO
    - 创建 EquipmentDTO、CreateEquipmentRequest、UpdateEquipmentRequest 等 DTO
    - 添加参数验证注解
    - _需求: 5.1, 5.2, 5.3_
  
  - [x] 10.2 实现 EquipmentService
    - 实现添加器材功能（管理员权限）
    - 实现更新器材功能（管理员权限、记录状态变更时间戳）
    - 实现删除器材功能（管理员权限）
    - 实现获取器材列表功能（支持搜索、筛选）
    - 实现获取器材详情功能（返回实时可用状态）
    - 集成 Redis 缓存（5 分钟 TTL）
    - _需求: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 16.2, 16.3_
  
  - [x] 10.3 实现 EquipmentController
    - 创建 /api/v1/equipment 路由
    - 实现器材管理相关接口
    - 添加权限控制注解
    - _需求: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6_

- [x] 11. 实现分布式锁管理器
  - [x] 11.1 实现 Redis 分布式锁
    - 创建 DistributedLockManager 类
    - 实现基于 Redis 的 tryLock 和 unlock 方法
    - 使用 Redisson 或自实现 Lua 脚本保证原子性
    - _需求: 9.2, 9.5, 16.5_

- [x] 12. 实现预约冲突检测器
  - [x] 12.1 实现 ConflictDetector
    - 实现学生时间冲突检测（查询学生在指定时间段的预约）
    - 实现课程容量检测（查询课程当前报名人数）
    - 实现器材可用性检测（查询器材在指定时间段的预约）
    - _需求: 6.2, 6.3, 7.2, 7.3, 9.1, 9.3_

- [x] 13. 实现课程预约服务
  - [x] 13.1 创建预约相关 DTO
    - 创建 ReservationDTO、ReserveCourseRequest、ReserveEquipmentRequest 等 DTO
    - 添加参数验证注解
    - _需求: 6.1, 7.1_
  
  - [x] 13.2 实现课程预约功能
    - 使用数据库事务保证原子性
    - 检查课程容量限制
    - 检查学生时间冲突
    - 创建预约记录并更新课程报名人数
    - 发送预约确认通知
    - _需求: 6.1, 6.2, 6.3, 6.4, 6.5, 9.1_
  
  - [x] 13.3 实现课程预约取消功能
    - 更新预约状态为已取消
    - 释放课程名额
    - 检查取消时间并记录迟到取消次数
    - _需求: 6.7, 6.8_

- [ ] 14. 实现器材预约服务
  - [x] 14.1 实现器材预约功能
    - 使用 Redis 分布式锁防止并发超额预约
    - 检查器材在指定时间段的可用性
    - 检查学生时间冲突
    - 创建预约记录并更新器材状态
    - 发送预约确认通知
    - _需求: 7.1, 7.2, 7.3, 7.4, 7.5, 9.2, 9.3_
  
  - [x] 14.2 实现器材预约取消功能
    - 更新预约状态为已取消
    - 释放器材
    - _需求: 7.7_
  
  - [x] 14.3 实现 ReservationController
    - 创建 /api/v1/reservations 路由
    - 实现预约管理相关接口（获取列表、详情、预约课程、预约器材、取消预约、获取我的预约）
    - _需求: 6.1, 6.2, 6.3, 6.4, 6.5, 6.7, 6.8, 7.1, 7.2, 7.3, 7.4, 7.5, 7.7_

- [x] 15. 实现签到打卡服务
  - [x] 15.1 创建签到相关 DTO
    - 创建 CheckInDTO、CheckInRequest、CheckOutRequest 等 DTO
    - 添加参数验证注解
    - _需求: 8.1, 8.4_
  
  - [x] 15.2 实现 CheckInService
    - 实现签到功能（验证预约时间范围、记录时间戳和位置）
    - 实现签退功能（更新签到记录、计算活动时长）
    - 实现获取签到记录功能
    - 将签到数据关联到健康数据统计
    - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_
  
  - [x] 15.3 实现 CheckInController
    - 创建 /api/v1/checkins 路由
    - 实现签到、签退、获取签到记录接口
    - _需求: 8.1, 8.2, 8.3, 8.4_

- [x] 16. 实现健康计划服务
  - [x] 16.1 创建健康计划相关 DTO
    - 创建 HealthPlanDTO、CreateHealthPlanRequest、UpdateHealthPlanRequest 等 DTO
    - 添加参数验证注解
    - _需求: 10.1, 10.3_
  
  - [x] 16.2 实现 HealthPlanService
    - 实现创建健康计划功能
    - 实现更新健康计划功能
    - 实现删除健康计划功能
    - 实现更新进度功能（计算完成百分比）
    - 实现根据签到数据自动更新运动时长统计
    - 实现目标达成检测并发送祝贺通知
    - _需求: 10.1, 10.2, 10.3, 10.4, 10.5_
  
  - [x] 16.3 实现 HealthPlanController
    - 创建 /api/v1/health-plans 路由
    - 实现健康计划管理相关接口
    - _需求: 10.1, 10.2, 10.3, 10.4_

- [x] 17. 实现数据统计服务
  - [x] 17.1 实现 StatisticsService
    - 实现课程统计（预约人数、签到率）
    - 实现器材统计（使用频率、使用时长）
    - 实现学生统计（活跃度、健身频率）
    - 支持按日、周、月的时间维度统计
    - 实现个人健康数据统计（总运动时长、运动次数、消耗卡路里、体重变化趋势）
    - 使用 Redis 缓存统计结果（5 分钟 TTL）
    - _需求: 11.1, 11.2, 11.3, 11.4, 11.6, 12.1, 12.2, 12.3, 12.4, 16.6_
  
  - [x] 17.2 实现 StatisticsController
    - 创建 /api/v1/statistics 路由
    - 实现统计相关接口（课程统计、器材统计、学生统计、个人健康数据统计）
    - 添加权限控制注解
    - _需求: 11.1, 11.2, 11.3, 11.4, 11.5, 12.1, 12.2, 12.3, 12.4, 12.5_

- [x] 18. 实现 DeepSeek AI 服务
  - [x] 18.1 创建 AI 服务配置
    - 配置 DeepSeek API 密钥和端点
    - 创建 RestTemplate 或 WebClient 用于 HTTP 调用
    - _需求: 13.1, 14.1, 15.1_
  
  - [x] 18.2 实现 DeepSeekClient
    - 实现通用聊天接口（chat 方法）
    - 实现生成推荐接口（generateRecommendation 方法）
    - 实现分析健康数据接口（analyzeHealthData 方法）
    - 实现重试机制和超时控制
    - 处理 API 限流和错误响应
    - _需求: 13.1, 13.2, 13.3, 14.1, 14.2, 14.3, 15.1, 15.2, 15.3_
  
  - [x] 18.3 实现 AIService
    - 实现 AI 问答功能（调用 DeepSeek API、记录问答历史、过滤不适当内容）
    - 实现个性化推荐功能（基于健康计划、历史预约、健康数据生成推荐、使用 Redis 缓存 1 小时）
    - 实现实时健康建议功能（分析健康数据、生成建议、保存到数据库）
    - _需求: 13.1, 13.2, 13.3, 13.4, 13.5, 14.1, 14.2, 14.3, 14.4, 14.5, 15.1, 15.2, 15.3, 15.4, 15.5, 16.5_
  
  - [x] 18.4 实现 AIController
    - 创建 /api/v1/ai 路由
    - 实现 AI 问答、获取推荐、获取健康建议、查看问答历史接口
    - _需求: 13.1, 13.2, 13.3, 13.4, 14.1, 14.2, 14.3, 14.4, 15.1, 15.2, 15.3, 15.4, 15.5_

- [x] 19. 实现通知服务
  - [x] 19.1 实现 NotificationService
    - 实现预约确认通知
    - 实现预约提醒通知（课程前 24 小时、器材前 30 分钟）
    - 实现取消通知
    - 实现目标达成祝贺通知
    - 可使用邮件、短信或站内消息实现
    - _需求: 6.5, 6.6, 7.6, 10.5_
  
  - [x] 19.2 实现定时任务
    - 使用 Spring @Scheduled 创建定时任务
    - 定时扫描预约记录并发送提醒通知
    - _需求: 6.6, 7.6_

- [x] 20. 实现审计日志服务
  - [x] 20.1 实现 AuditLogService
    - 实现记录操作日志功能（用户 ID、操作类型、资源类型、资源 ID、详情、IP 地址、User Agent）
    - _需求: 17.5_
  
  - [x] 20.2 实现 AOP 切面自动记录日志
    - 创建 AuditLogAspect 切面
    - 拦截关键操作并自动记录审计日志
    - _需求: 17.5_

- [x] 21. 检查点 - 确保所有测试通过
  - 运行所有单元测试和集成测试
  - 验证所有 API 端点正常工作
  - 检查数据库事务和缓存是否正确配置
  - 确认权限控制和数据隔离正常工作
  - 如有问题请向用户询问

- [x] 22. 配置跨域和生产环境设置
  - [x] 22.1 配置 CORS
    - 配置允许的前端域名
    - 配置允许的 HTTP 方法和请求头
    - _需求: 19.1_
  
  - [x] 22.2 配置生产环境参数
    - 配置数据库连接池参数
    - 配置 Redis 连接池参数
    - 配置日志级别和输出
    - 配置 JWT 密钥和过期时间
    - _需求: 17.1, 16.4_

## 注意事项

- 所有标记 `*` 的子任务为可选任务，可根据项目进度决定是否实现
- 每个任务都引用了对应的需求编号，确保需求覆盖完整
- 实现过程中应遵循 RESTful API 设计规范
- 所有敏感信息（密码、API 密钥）应通过环境变量或配置文件管理
- 代码应包含适当的注释和文档
- 关键业务逻辑应使用数据库事务保证数据一致性
- 使用 Redis 缓存时应注意缓存失效和更新策略
