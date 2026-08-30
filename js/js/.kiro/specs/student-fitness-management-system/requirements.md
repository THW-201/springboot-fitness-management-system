# 需求文档 - 大学生健身管理系统

## 简介

大学生健身管理系统是一个前后端分离的Web应用，用于管理大学生的健身活动、课程预约、器材使用和健康数据。系统支持三种角色（管理员、教练、学生），提供课程管理、器材预约、数据统计和AI智能建议等功能。

## 术语表

- **System**: 大学生健身管理系统
- **User**: 系统使用者，包括管理员、教练和学生
- **Admin**: 管理员角色，拥有所有系统权限
- **Coach**: 教练角色，只能管理自己负责的学生
- **Student**: 学生角色，只能管理自己的信息
- **Course**: 健身课程，由教练授课
- **Equipment**: 健身器材
- **Reservation**: 预约记录，包括课程预约和器材预约
- **Health_Plan**: 学生的健康计划
- **JWT_Token**: JSON Web Token，用于身份认证
- **DeepSeek_API**: AI服务接口，提供智能建议功能
- **Check_In**: 签到打卡记录
- **Conflict**: 预约时间冲突

## 需求

### 需求 1: 用户认证与授权

**用户故事:** 作为系统用户，我希望通过安全的方式登录系统，以便访问我有权限的功能。

#### 验收标准

1. WHEN User提交有效的用户名和密码，THE System SHALL验证凭据并返回JWT_Token
2. WHEN User提交无效的凭据，THE System SHALL返回认证失败错误信息
3. WHEN User携带有效的JWT_Token访问受保护资源，THE System SHALL验证Token并允许访问
4. WHEN User携带过期或无效的JWT_Token，THE System SHALL拒绝访问并返回401错误
5. THE System SHALL在JWT_Token中包含用户角色信息
6. THE System SHALL将JWT_Token存储在Redis中以支持Token撤销

### 需求 2: 基于角色的访问控制

**用户故事:** 作为系统管理员，我希望不同角色的用户只能访问其权限范围内的功能，以确保数据安全。

#### 验收标准

1. WHEN Admin访问任何系统功能，THE System SHALL允许访问
2. WHEN Coach访问学生数据，THE System SHALL只返回该Coach负责的Student数据
3. WHEN Student访问个人数据，THE System SHALL只返回该Student自己的数据
4. WHEN User尝试访问超出其角色权限的功能，THE System SHALL返回403禁止访问错误
5. THE System SHALL在数据库查询层面实现Coach的数据隔离

### 需求 3: 用户注册与个人信息管理

**用户故事:** 作为新用户，我希望能够注册账号并管理个人信息，以便使用系统功能。

#### 验收标准

1. WHEN User提交注册信息，THE System SHALL验证信息完整性并创建新账号
2. WHEN User提交已存在的用户名或邮箱，THE System SHALL返回重复错误信息
3. THE System SHALL加密存储用户密码
4. WHEN User更新个人信息，THE System SHALL验证并保存更新
5. WHEN Student注册时选择Coach，THE System SHALL建立Student与Coach的关联关系

### 需求 4: 课程管理

**用户故事:** 作为管理员或教练，我希望能够发布和管理健身课程，以便学生预约参加。

#### 验收标准

1. WHEN Admin或Coach创建Course，THE System SHALL保存课程信息并设置课程状态为可预约
2. THE System SHALL记录Course的创建者信息
3. WHEN Admin或Coach更新Course信息，THE System SHALL验证权限并保存更新
4. WHEN Admin或Coach删除Course，THE System SHALL检查是否存在未完成的Reservation
5. IF Course存在未完成的Reservation，THEN THE System SHALL拒绝删除并返回错误信息
6. THE System SHALL支持按课程名称、教练、时间进行模糊搜索
7. THE System SHALL支持按课程类型进行分类筛选

### 需求 5: 器材管理

**用户故事:** 作为管理员，我希望能够录入和管理健身器材信息，以便学生查询和预约使用。

#### 验收标准

1. WHEN Admin录入Equipment信息，THE System SHALL保存器材信息并设置初始状态
2. THE System SHALL记录Equipment的可用状态（可用、使用中、维护中）
3. WHEN Admin更新Equipment状态，THE System SHALL保存状态变更并记录时间戳
4. THE System SHALL支持按器材名称、类型进行模糊搜索
5. THE System SHALL支持按器材状态进行筛选
6. WHEN User查询Equipment，THE System SHALL返回器材的实时可用状态

### 需求 6: 课程预约

**用户故事:** 作为学生，我希望能够预约健身课程，以便参加感兴趣的课程。

#### 验收标准

1. WHEN Student提交Course预约请求，THE System SHALL检查课程容量限制
2. WHEN Student提交Course预约请求，THE System SHALL检查时间冲突
3. IF 存在时间Conflict，THEN THE System SHALL拒绝预约并返回冲突信息
4. IF 课程已满员，THEN THE System SHALL拒绝预约并返回已满信息
5. WHEN 预约成功，THE System SHALL创建Reservation记录并发送确认通知
6. THE System SHALL在课程开始前24小时发送提醒通知
7. WHEN Student取消Reservation，THE System SHALL更新预约状态并释放名额
8. IF 取消时间距离课程开始少于2小时，THEN THE System SHALL记录迟到取消次数

### 需求 7: 器材预约

**用户故事:** 作为学生，我希望能够预约健身器材，以便在指定时间使用器材。

#### 验收标准

1. WHEN Student提交Equipment预约请求，THE System SHALL检查器材在指定时间段的可用性
2. WHEN Student提交Equipment预约请求，THE System SHALL检查Student的时间冲突
3. IF Equipment在指定时间已被预约，THEN THE System SHALL拒绝预约并返回已占用信息
4. IF 存在时间Conflict，THEN THE System SHALL拒绝预约并返回冲突信息
5. WHEN 预约成功，THE System SHALL创建Reservation记录并更新Equipment状态
6. THE System SHALL在预约开始前30分钟发送提醒通知
7. WHEN Student取消Equipment预约，THE System SHALL更新预约状态并释放器材

### 需求 8: 签到打卡

**用户故事:** 作为学生，我希望能够在参加课程或使用器材时签到打卡，以便记录我的健身活动。

#### 验收标准

1. WHEN Student在预约时间范围内提交签到请求，THE System SHALL创建Check_In记录
2. WHEN Student在预约时间之外提交签到请求，THE System SHALL拒绝签到并返回时间错误
3. THE System SHALL记录Check_In的时间戳和位置信息
4. WHEN Student完成活动后提交签退请求，THE System SHALL更新Check_In记录并计算活动时长
5. THE System SHALL将Check_In数据关联到Student的健康数据统计

### 需求 9: 并发预约冲突检测

**用户故事:** 作为系统，我需要处理并发预约请求，以防止超额预约和数据不一致。

#### 验收标准

1. WHEN 多个Student同时预约同一Course，THE System SHALL使用数据库锁机制确保原子性
2. WHEN 多个Student同时预约同一Equipment，THE System SHALL使用Redis分布式锁防止超额预约
3. THE System SHALL在预约事务提交前验证最终的容量和可用性
4. IF 并发导致超额，THEN THE System SHALL回滚后提交的预约并返回已满信息
5. THE System SHALL在200毫秒内完成冲突检测和预约处理

### 需求 10: 健康计划管理

**用户故事:** 作为学生，我希望能够创建和管理个人健康计划，以便跟踪我的健身目标。

#### 验收标准

1. WHEN Student创建Health_Plan，THE System SHALL保存计划信息并设置初始状态
2. THE System SHALL支持Health_Plan包含目标体重、目标运动时长等指标
3. WHEN Student更新Health_Plan进度，THE System SHALL保存进度数据并计算完成百分比
4. THE System SHALL根据Check_In数据自动更新Health_Plan的运动时长统计
5. WHEN Health_Plan达成目标，THE System SHALL发送祝贺通知

### 需求 11: 数据统计与可视化

**用户故事:** 作为管理员，我希望查看系统的使用统计数据，以便了解系统运营状况。

#### 验收标准

1. THE System SHALL统计Course的预约人数和签到率
2. THE System SHALL统计Equipment的使用频率和使用时长
3. THE System SHALL统计Student的活跃度和健身频率
4. THE System SHALL提供按日、周、月的时间维度统计
5. THE System SHALL将统计数据以图表形式展示（折线图、柱状图、饼图）
6. THE System SHALL使用Redis缓存统计结果，缓存有效期为5分钟

### 需求 12: 个人健康数据监控

**用户故事:** 作为学生，我希望查看我的健康数据统计，以便了解我的健身进展。

#### 验收标准

1. THE System SHALL统计Student的总运动时长、运动次数、消耗卡路里
2. THE System SHALL展示Student的体重变化趋势
3. THE System SHALL展示Student的课程参与历史
4. THE System SHALL展示Student的器材使用历史
5. THE System SHALL提供健康数据的可视化图表

### 需求 13: AI智能问答

**用户故事:** 作为用户，我希望能够向AI助手提问健身相关问题，以便获得专业建议。

#### 验收标准

1. WHEN User提交健身相关问题，THE System SHALL调用DeepSeek_API获取回答
2. THE System SHALL在3秒内返回AI回答
3. IF DeepSeek_API调用失败，THEN THE System SHALL返回友好的错误提示
4. THE System SHALL记录问答历史以供User查看
5. THE System SHALL过滤不适当的问题内容

### 需求 14: 个性化推荐

**用户故事:** 作为学生，我希望系统能够根据我的健身数据推荐适合的课程和器材，以便更好地达成健身目标。

#### 验收标准

1. WHEN Student访问推荐页面，THE System SHALL基于Student的Health_Plan调用DeepSeek_API生成推荐
2. THE System SHALL基于Student的历史预约记录生成推荐
3. THE System SHALL基于Student的健康数据生成推荐
4. THE System SHALL返回至少3个Course推荐和3个Equipment推荐
5. THE System SHALL使用Redis缓存推荐结果，缓存有效期为1小时

### 需求 15: 实时健康建议

**用户故事:** 作为学生，我希望在查看健康数据时获得AI提供的实时建议，以便改进我的健身计划。

#### 验收标准

1. WHEN Student查看健康数据统计页面，THE System SHALL调用DeepSeek_API分析健康数据
2. THE System SHALL基于运动频率、运动时长、体重变化生成健康建议
3. THE System SHALL在5秒内返回健康建议
4. IF Student的运动频率低于每周3次，THEN THE System SHALL在建议中提示增加运动频率
5. THE System SHALL将健康建议保存到数据库供后续查看

### 需求 16: 缓存优化

**用户故事:** 作为系统，我需要使用缓存提升性能，以便为用户提供快速响应。

#### 验收标准

1. THE System SHALL使用Redis缓存Course列表数据，缓存有效期为10分钟
2. THE System SHALL使用Redis缓存Equipment状态数据，缓存有效期为5分钟
3. WHEN Course或Equipment数据更新，THE System SHALL清除相关缓存
4. THE System SHALL使用Redis缓存用户会话信息
5. THE System SHALL使用Redis实现分布式锁以处理并发预约

### 需求 17: 数据持久化

**用户故事:** 作为系统，我需要可靠地存储所有业务数据，以确保数据不丢失。

#### 验收标准

1. THE System SHALL使用MySQL存储所有用户、课程、器材、预约、健康数据
2. THE System SHALL使用MyBatis Plus进行数据库操作
3. THE System SHALL为所有数据表设置合适的索引以优化查询性能
4. THE System SHALL实现数据库事务以确保数据一致性
5. THE System SHALL记录所有关键操作的审计日志

### 需求 18: 前端路由与状态管理

**用户故事:** 作为前端开发者，我需要清晰的路由结构和状态管理，以便构建可维护的前端应用。

#### 验收标准

1. THE System SHALL使用Vue Router管理页面路由
2. THE System SHALL使用Pinia管理全局状态（用户信息、认证状态）
3. THE System SHALL实现路由守卫以保护需要认证的页面
4. WHEN User未登录访问受保护页面，THE System SHALL重定向到登录页面
5. THE System SHALL在Pinia中缓存用户权限信息

### 需求 19: API通信

**用户故事:** 作为前端应用，我需要与后端API安全通信，以便获取和提交数据。

#### 验收标准

1. THE System SHALL使用Axios进行HTTP请求
2. THE System SHALL在所有API请求中自动添加JWT_Token到Authorization头
3. WHEN API返回401错误，THE System SHALL清除本地Token并重定向到登录页面
4. THE System SHALL实现请求拦截器和响应拦截器统一处理错误
5. THE System SHALL在API请求失败时显示友好的错误提示

### 需求 20: 用户界面

**用户故事:** 作为用户，我希望系统界面美观易用，以便快速完成操作。

#### 验收标准

1. THE System SHALL使用Element Plus组件库构建用户界面
2. THE System SHALL实现响应式布局以支持不同屏幕尺寸
3. THE System SHALL在数据加载时显示加载动画
4. THE System SHALL在表单提交时禁用提交按钮防止重复提交
5. THE System SHALL使用消息提示组件反馈操作结果
