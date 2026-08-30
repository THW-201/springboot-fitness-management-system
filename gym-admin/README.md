# 大学生健身管理系统

一个完整的大学生健身管理系统，包含前端（Vue3）和后端（SpringBoot），支持三种角色：管理员、教练、学生。

## 📋 项目概述

### 核心功能

- **用户管理**：注册登录、个人信息维护、角色权限管理
- **课程管理**：课程发布编辑、课程预约、签到归还
- **器材管理**：器材录入管理、器材预约、状态更新
- **预约管理**：预约取消、签到归还、冲突检测、自动提醒
- **健身计划**：计划创建管理、运动项目记录、数据追踪
- **数据统计**：参与率统计、使用率统计、可视化展示
- **AI 助手**：基于 DeepSeek 的智能问答、个性化推荐、实时答疑

### 技术栈

#### 前端
- **框架**：Vue 3.5 + TypeScript 5
- **构建工具**：Vite 5
- **UI 组件库**：Element Plus 2.13
- **状态管理**：Pinia 2.3
- **路由管理**：Vue Router 4.6
- **HTTP 客户端**：Axios 1.13

#### 后端
- **框架**：Spring Boot 3.2
- **ORM**：MyBatis Plus 3.5
- **安全**：Spring Security + JWT
- **缓存**：Redis 7
- **数据库**：MySQL 8.0
- **AI 集成**：DeepSeek API

---

## 📂 项目结构

```
fitness-system/
├── src/                          # 前端源码
│   ├── api/                      # API 请求封装
│   │   └── request.ts
│   ├── assets/                   # 静态资源
│   ├── components/               # 公共组件
│   ├── router/                   # 路由配置
│   │   └── index.ts
│   ├── stores/                   # 状态管理
│   │   └── user.ts
│   ├── types/                    # TypeScript 类型定义
│   │   └── index.ts
│   ├── views/                    # 页面组件
│   │   ├── admin/                # 管理员页面
│   │   │   ├── Courses.vue       # 课程管理
│   │   │   ├── Equipment.vue     # 器材管理
│   │   │   ├── Users.vue         # 用户管理
│   │   │   └── Statistics.vue    # 数据统计
│   │   ├── coach/                # 教练页面
│   │   │   ├── Students.vue      # 学生管理
│   │   │   └── Courses.vue       # 我的课程
│   │   ├── student/              # 学生页面
│   │   │   ├── Courses.vue       # 课程预约
│   │   │   ├── Equipment.vue     # 器材预约
│   │   │   ├── Plans.vue         # 健身计划
│   │   │   └── Profile.vue       # 个人中心
│   │   ├── AIAssistant.vue       # AI 助手
│   │   ├── Dashboard.vue         # 仪表盘
│   │   ├── Layout.vue            # 布局组件
│   │   ├── Login.vue             # 登录页
│   │   └── Register.vue          # 注册页
│   ├── App.vue                   # 根组件
│   └── main.ts                   # 入口文件
├── docs/                         # 后端设计文档
│   ├── database-design.md        # 数据库设计文档
│   ├── api-design.md             # API 接口设计文档
│   └── backend-architecture.md   # 后端架构设计文档
├── public/                       # 公共资源
├── index.html                    # HTML 模板
├── .coze                         # 项目配置
├── package.json                  # 依赖配置
├── tsconfig.json                 # TypeScript 配置
├── vite.config.ts                # Vite 配置
└── README.md                     # 项目说明
```

---

## 🚀 快速开始

### 前置要求

- Node.js 18+
- pnpm 8+
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7+

### 前端启动

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev

# 构建生产版本
pnpm build

# 预览生产版本
pnpm preview
```

前端服务运行在：http://localhost:5000

### 后端启动（参考文档）

后端代码需要根据设计文档进行开发。请查看以下文档：

1. **数据库设计**：`docs/database-design.md`
   - 包含完整的 MySQL 表结构设计
   - 包含初始化 SQL 脚本

2. **API 接口设计**：`docs/api-design.md`
   - 包含所有 RESTful API 接口定义
   - 包含请求参数和响应格式

3. **后端架构设计**：`docs/backend-architecture.md`
   - 包含 SpringBoot 项目结构
   - 包含核心配置和代码实现示例
   - 包含 Security、JWT、Redis 配置

---

## 📚 后端开发指南

### 1. 创建 SpringBoot 项目

```bash
# 使用 Spring Initializr 创建项目
# https://start.spring.io/

# 依赖选择：
# - Spring Web
# - Spring Security
# - Spring Data Redis
# - MySQL Driver
# - Validation
# - Lombok
```

### 2. 配置数据库

按照 `docs/database-design.md` 中的 SQL 脚本创建数据库和表：

```sql
CREATE DATABASE fitness_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行 docs/database-design.md 中的建表语句
-- 执行初始化数据插入语句
```

### 3. 配置 application.yml

参考 `docs/backend-architecture.md` 中的配置示例，修改数据库连接信息、Redis 配置、JWT 密钥等。

### 4. 实现核心模块

按照 `docs/backend-architecture.md` 中的项目结构和代码示例，逐步实现：

1. 实体类（Entity）
2. Mapper 接口
3. Service 接口和实现
4. Controller 控制器
5. Security 配置
6. JWT 工具类

### 5. 测试接口

使用 Postman 或其他工具测试 API 接口，参考 `docs/api-design.md` 中的接口定义。

---

## 🔧 配置说明

### 环境变量

前端环境变量配置（`.env` 文件）：

```env
# API 地址
VITE_API_BASE_URL=http://localhost:8080/api
```

后端环境变量配置（`application.yml`）：

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fitness_system
    username: root
    password: your_password

# Redis 配置
spring:
  data:
    redis:
      host: localhost
      port: 6379

# JWT 配置
jwt:
  secret: your-secret-key-at-least-256-bits
  expiration: 86400000  # 24小时
```

---

## 👥 角色权限

### 管理员
- ✅ 用户管理（增删改查）
- ✅ 课程管理（发布、编辑、删除）
- ✅ 器材管理（录入、更新、状态管理）
- ✅ 预约管理（查看所有预约）
- ✅ 健身计划管理
- ✅ 数据统计分析
- ✅ AI 助手

### 教练
- ✅ 学生管理（仅查看自己负责的学生）
- ✅ 课程管理（查看自己的课程、查看学员）
- ✅ 健身计划（为学生创建计划）
- ✅ 数据统计（查看自己的统计数据）
- ✅ AI 助手

### 学生
- ✅ 课程预约
- ✅ 器材预约
- ✅ 健身计划管理（创建、查看、跟踪）
- ✅ 健身数据记录
- ✅ 个人信息维护
- ✅ AI 助手

---

## 📖 API 接口

详细的 API 接口文档请查看：`docs/api-design.md`

### 主要接口

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/courses` - 获取课程列表
- `POST /api/bookings` - 创建预约
- `POST /api/fitness-plans` - 创建健身计划
- `POST /api/ai/chat` - AI 问答

---

## 🤖 AI 助手集成

### DeepSeek API 配置

后端配置文件中添加：

```yaml
deepseek:
  api-key: your-deepseek-api-key
  api-url: https://api.deepseek.com/v1/chat/completions
```

### 使用方式

前端调用示例：

```typescript
import request from '@/api/request'

export const chatWithAI = (message: string) => {
  return request.post('/ai/chat', { message })
}
```

---

## 📱 页面预览

### 登录页
- 支持用户名/邮箱登录
- 密码加密传输
- JWT Token 认证

### 仪表盘
- 数据统计卡片
- 图表展示
- 最近预约列表

### 管理员功能
- 课程管理：发布、编辑、删除课程
- 器材管理：录入、更新器材信息
- 用户管理：查看、管理用户
- 数据统计：各类数据分析

### 教练功能
- 学生管理：查看负责的学生信息
- 课程管理：管理自己的课程

### 学生功能
- 课程预约：浏览和预约课程
- 器材预约：浏览和预约器材
- 健身计划：创建和管理健身计划
- 个人中心：查看个人信息和健身数据

### AI 助手
- 智能问答
- 健身建议
- 计划推荐

---

## 🐛 常见问题

### 1. 前端启动失败

**问题**：`pnpm install` 失败

**解决**：
```bash
# 清除缓存重新安装
rm -rf node_modules pnpm-lock.yaml
pnpm install
```

### 2. 后端连接数据库失败

**问题**：`com.mysql.cj.jdbc.exceptions.CommunicationsException`

**解决**：
- 检查 MySQL 是否启动
- 检查数据库连接配置是否正确
- 检查防火墙设置

### 3. Token 过期

**问题**：401 Unauthorized

**解决**：
- 前端会自动跳转到登录页
- 重新登录获取新 Token

---

## 📝 开发进度

### ✅ 已完成

- [x] 前端项目初始化
- [x] 路由配置和状态管理
- [x] 登录注册页面
- [x] 布局组件（侧边栏、导航栏）
- [x] 管理员页面（课程、器材、用户、统计）
- [x] 教练页面（学生、课程）
- [x] 学生页面（课程预约、器材预约、健身计划、个人中心）
- [x] AI 助手页面
- [x] 数据库设计文档
- [x] API 接口设计文档
- [x] 后端架构设计文档

### 🚧 待完成（后端开发）

根据 `docs/backend-architecture.md` 文档进行开发：

- [ ] SpringBoot 项目初始化
- [ ] 数据库表创建
- [ ] 实体类开发
- [ ] Mapper 接口开发
- [ ] Service 层开发
- [ ] Controller 层开发
- [ ] Security 配置
- [ ] JWT 认证实现
- [ ] Redis 缓存集成
- [ ] DeepSeek AI 集成

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证。

---

## 📞 联系方式

如有问题，请联系项目维护者。

---

## 🙏 致谢

感谢以下开源项目：

- Vue.js
- Element Plus
- Spring Boot
- MyBatis Plus
- DeepSeek

---

**开发完成时间**：2024年3月2日
**版本**：v1.0.0
