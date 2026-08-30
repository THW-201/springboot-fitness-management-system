# 大学生健身管理系统

## 项目简介

大学生健身管理系统是一个基于Spring Boot 3.x的后端服务，为大学健身中心提供完整的数字化管理解决方案。系统支持用户认证、课程管理、器材预约、健康数据追踪和AI智能服务等核心功能。

## 技术栈

- **Spring Boot 3.2.0** - 应用框架
- **MyBatis Plus 3.5.5** - ORM框架
- **Spring Security** - 安全框架
- **JWT** - 无状态认证
- **Redis** - 缓存和分布式锁
- **MySQL 8.0** - 关系型数据库
- **SpringDoc OpenAPI** - API文档（Swagger）
- **Maven** - 构建工具
- **Java 17** - 开发语言

## 项目结构

```
src/
├── main/
│   ├── java/com/fitness/
│   │   ├── FitnessApplication.java    # 主应用类
│   │   ├── config/                     # 配置类
│   │   │   ├── RedisConfig.java       # Redis配置
│   │   │   ├── MyBatisPlusConfig.java # MyBatis Plus配置
│   │   │   └── OpenApiConfig.java     # OpenAPI配置
│   │   ├── controller/                 # 控制器层
│   │   ├── service/                    # 服务层
│   │   │   └── impl/                  # 服务实现
│   │   ├── mapper/                     # 数据访问层
│   │   ├── entity/                     # 实体类
│   │   │   └── enums/                 # 枚举类
│   │   ├── dto/                        # 数据传输对象
│   │   └── util/                       # 工具类
│   └── resources/
│       ├── application.yml             # 应用配置
│       └── mapper/                     # MyBatis XML映射文件
└── test/                               # 测试目录
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 配置数据库

1. 创建数据库：
```sql
CREATE DATABASE fitness_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fitness_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 配置Redis

修改 `src/main/resources/application.yml` 中的Redis配置：
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_password  # 如果没有密码则留空
```

### 运行项目

1. 安装依赖：
```bash
mvn clean install
```

2. 运行应用：
```bash
mvn spring-boot:run
```

3. 访问API文档：
```
http://localhost:8080/api/v1/swagger-ui.html
```

## 核心功能

### 用户认证与授权
- JWT Token认证
- 基于角色的访问控制（管理员、教练、学生）
- 数据隔离（教练只能访问自己负责的学生数据）

### 课程管理
- 课程发布、更新、删除
- 课程搜索和筛选
- 课程容量管理

### 器材管理
- 器材录入、更新、删除
- 器材状态管理
- 器材搜索和筛选

### 预约管理
- 课程预约
- 器材预约
- 预约冲突检测
- 并发控制（数据库锁 + Redis分布式锁）

### 签到打卡
- 签到签退
- 活动时长统计
- 卡路里消耗计算

### 健康计划
- 个人健康计划创建
- 进度跟踪
- 目标达成通知

### 数据统计
- 课程统计（预约人数、签到率）
- 器材统计（使用频率、使用时长）
- 学生统计（活跃度、健身频率）
- 个人健康数据统计

### AI智能服务
- AI问答
- 个性化推荐
- 实时健康建议

## API文档

启动项目后访问 Swagger UI：
```
http://localhost:8080/api/v1/swagger-ui.html
```

## 开发规范

### 代码规范
- 使用Lombok简化代码
- 所有实体类和方法需要完整的注解
- 使用统一的响应格式
- 使用全局异常处理

### 命名规范
- 类名：大驼峰命名法（PascalCase）
- 方法名：小驼峰命名法（camelCase）
- 常量：全大写下划线分隔（UPPER_SNAKE_CASE）
- 包名：全小写

### 注释规范
- 所有公共类和方法必须有JavaDoc注释
- 复杂业务逻辑需要添加行内注释
- API接口需要添加OpenAPI注解

## 许可证

Apache License 2.0
