/**
 * 用户角色枚举
 */
export enum UserRole {
  ADMIN = 'ADMIN',
  COACH = 'COACH',
  STUDENT = 'STUDENT'
}

/**
 * 预约类型枚举
 */
export enum ReservationType {
  COURSE = 'COURSE',
  EQUIPMENT = 'EQUIPMENT'
}

/**
 * 预约状态枚举
 */
export enum ReservationStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

/**
 * 课程状态枚举
 */
export enum CourseStatus {
  AVAILABLE = 'AVAILABLE',
  FULL = 'FULL',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED'
}

/**
 * 器材状态枚举
 */
export enum EquipmentStatus {
  AVAILABLE = 'AVAILABLE',
  IN_USE = 'IN_USE',
  MAINTENANCE = 'MAINTENANCE',
  DAMAGED = 'DAMAGED'
}

/**
 * 健康计划状态枚举
 */
export enum HealthPlanStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  ABANDONED = 'ABANDONED'
}

/**
 * 运动类型枚举
 */


/**
 * 用户实体 (UserDTO)
 */
export interface User {
  id: number
  username: string
  password?: string
  email: string
  phone?: string
  realName: string
  role: UserRole | string
  avatarUrl?: string
  status: number
  createdAt: string
  updatedAt: string
  studentProfile?: StudentProfile
  coachProfile?: CoachProfile
}

/**
 * 学生档案 (StudentProfileDTO)
 */
export interface StudentProfile {
  id: number
  userId: number
  studentNumber: string
  coachId?: number
  coachName?: string
  gender?: 'MALE' | 'FEMALE' | 'OTHER'
  age?: number
  height?: number
  weight?: number
}

/**
 * 教练档案 (CoachProfileDTO)
 */
export interface CoachProfile {
  id: number
  userId: number
  specialization?: string
  certification?: string
  experienceYears?: number
  bio?: string
}

/**
 * 课程实体
 */
export interface Course {
  id: number
  name: string
  description: string
  coachId: number
  coachName?: string
  courseType: string
  capacity: number
  currentEnrollment: number
  startTime: string
  endTime: string
  location: string
  status: CourseStatus
  createdBy?: number
  createdAt: string
  updatedAt: string
  classSchedule?: ScheduleItem[]
}

/**
 * 器材实体
 */
export interface Equipment {
  id: number
  name: string
  equipmentType: string
  description: string
  location: string
  status: EquipmentStatus
  purchaseDate?: string
  lastMaintenanceDate?: string
  imageUrl?: string
  coachId?: number
  createdAt: string
  updatedAt: string
}

/**
 * 预约实体
 */
export interface ScheduleItem {
  dayOfWeek: string
  startTime: string
  endTime: string
}

export interface Reservation {
  id: number
  studentId: number
  studentName?: string
  reservationType: ReservationType
  courseId?: number
  courseName?: string
  equipmentId?: number
  equipmentName?: string
  startTime: string
  endTime: string
  status: ReservationStatus
  cancelReason?: string
  cancelledAt?: string
  createdAt: string
  updatedAt: string
  courseSchedule?: ScheduleItem[]
}

/**
 * 签到记录
 */
export interface CheckIn {
  id: number
  reservationId: number
  studentId: number
  checkInTime: string
  checkOutTime?: string
  durationMinutes?: number
  location?: string
  caloriesBurned?: number
  createdAt: string
  updatedAt: string
}

/**
 * 健康计划打卡记录
 */
export interface PlanCheckIn {
  id: number
  planId: number
  studentId: number
  checkDate: string
  checkInTime: string
  checkOutTime?: string
  durationMinutes?: number
  status: string
  exerciseType?: string
  caloriesBurned?: number
  createdAt: string
  updatedAt: string
}

/**
 * 健康计划
 */
export interface HealthPlan {
  id: number
  studentId: number
  studentName?: string
  planName: string
  description: string
  targetWeight?: number
  targetDurationMinutes: number
  currentWeight?: number
  currentDurationMinutes: number
  startDate: string
  endDate: string
  status: HealthPlanStatus
  completionPercentage: number
  createdAt: string
  updatedAt: string
}

/**
 * AI 问答历史
 */
export interface AIChatHistory {
  id: number
  userId: number
  question: string
  answer: string
  context?: string
  responseTimeMs?: number
  createdAt: string
}

/**
 * 健康建议
 */
export interface HealthAdvice {
  id: number
  studentId: number
  adviceType?: string
  content: string
  basedOnData?: any
  createdAt: string
}

/**
 * 审计日志
 */
export interface AuditLog {
  id: number
  userId?: number
  action: string
  resourceType?: string
  resourceId?: number
  details?: string
  ipAddress?: string
  userAgent?: string
  createdAt: string
}

/**
 * 课程统计数据项
 */
export interface CourseStatisticsDTO {
  courseId: number
  courseName: string
  totalReservations: number
  cancelledReservations: number
  actualReservations: number
  totalCheckIns: number
  checkInRate: number
  capacity: number
  utilizationRate: number
  createdAt: string
  updatedAt: string
}

/**
 * 课程统计（返回数组）
 */
export interface CourseStatistics {
  totalCourses: number
  availableCourses: number
  fullCourses: number
  completedCourses: number
  totalEnrollments: number
  attendanceRate: number
  courseTypeDistribution: Array<{ type: string; count: number }>
  monthlyTrend: Array<{ month: string; count: number }>
}

/**
 * 器材统计数据项
 */
export interface EquipmentStatisticsDTO {
  equipmentId: number
  equipmentName: string
  totalReservations: number
  totalUsageMinutes: number
  averageUsageMinutes: number | string
  usageFrequency: number
  status: string
  createdAt: string
  updatedAt: string
}

/**
 * 器材统计（返回数组）
 */
export interface EquipmentStatistics {
  totalEquipment: number
  availableEquipment: number
  inUseEquipment: number
  maintenanceEquipment: number
  damagedEquipment: number
  utilizationRate: number
  typeDistribution: Array<{ type: string; count: number }>
  topUsedEquipment: Array<{ name: string; usageCount: number }>
}

/**
 * 学生统计数据项
 */
export interface StudentStatisticsDTO {
  studentId: number
  studentName: string
  totalReservations: number
  totalCheckIns: number
  totalExerciseMinutes: number
  totalCaloriesBurned: number | string
  activityScore: number
}

/**
 * 学生统计（返回数组）
 */
export interface StudentStatistics {
  totalStudents: number
  activeStudents: number
  inactiveStudents: number
  totalCheckIns: number
  totalWorkoutDuration: number
  totalCaloriesBurned: number
  weeklyActivity: Array<{ week: string; count: number }>
  coachDistribution: Array<{ coachName: string; studentCount: number }>
}

/**
 * 体重记录
 */
export interface WeightRecord {
  date: string
  weight: number | string
}

/**
 * 课程参与记录
 */
export interface CourseParticipation {
  courseId: number
  courseName: string
  date: string
  durationMinutes: number
}

/**
 * 器材使用记录
 */
export interface EquipmentUsage {
  equipmentId: number
  equipmentName: string
  date: string
  durationMinutes: number
}

/**
 * 个人健康数据统计
 */
export interface PersonalHealthStatistics {
  studentId: number
  totalExerciseMinutes: number
  totalExerciseCount: number
  totalCaloriesBurned: number | string
  weightHistory: WeightRecord[]
  courseHistory: CourseParticipation[]
  equipmentHistory: EquipmentUsage[]
}

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 注册请求 (RegisterRequest)
 */
export interface RegisterRequest {
  username: string
  password: string
  email: string
  phone?: string
  realName?: string
  role: UserRole
  studentNumber?: string
  coachId?: number
  specialization?: string
  certification?: string
  experienceYears?: number
}

/**
 * 登录响应 (LoginResponse)
 */
export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: User
}

/**
 * 创建课程请求
 */
export interface CreateCourseRequest {
  name: string
  description: string
  coachId: number
  courseType: string
  capacity: number
  startTime: string
  endTime: string
  location: string
  classSchedule?: ScheduleItem[]
  imageUrl?: string
  status?: CourseStatus
}

/**
 * 更新课程请求
 */
export interface UpdateCourseRequest {
  name?: string
  description?: string
  courseType?: string
  capacity?: number
  startTime?: string
  endTime?: string
  location?: string
  classSchedule?: ScheduleItem[]
  imageUrl?: string
  status?: CourseStatus
}

/**
 * 预约课程请求
 */
export interface ReserveCourseRequest {
  courseId: number
}

/**
 * 预约器材请求
 */
export interface ReserveEquipmentRequest {
  equipmentId: number
  startTime: string
  endTime: string
}

/**
 * 创建健康计划请求
 */
export interface CreateHealthPlanRequest {
  studentId: number
  planName: string
  description?: string
  targetWeight?: number
  targetDurationMinutes?: number
  currentWeight?: number
  startDate: string
  endDate: string
}

/**
 * 更新健康计划进度请求
 */
export interface UpdateHealthPlanProgressRequest {
  currentWeight?: number
  currentDurationMinutes?: number
}

/**
 * 创建器材请求
 */
export interface CreateEquipmentRequest {
  name: string
  equipmentType: string
  description?: string
  location: string
  purchaseDate?: string
  lastMaintenanceDate?: string
  imageUrl?: string
  coachId?: number
}

/**
 * 更新器材请求
 */
export interface UpdateEquipmentRequest {
  name?: string
  equipmentType?: string
  description?: string
  location?: string
  status?: EquipmentStatus
  purchaseDate?: string
  lastMaintenanceDate?: string
  imageUrl?: string
  coachId?: number
}

/**
 * AI 聊天请求
 */
export interface AIChatRequest {
  question: string
  context?: string
  data?: any
}

/**
 * AI 聊天响应
 */
export interface AIChatResponse {
  answer: string
  context?: string
  responseTimeMs: number
}

/**
 * 课程推荐项
 */
export interface CourseRecommendation {
  courseId: number
  courseName: string
  reason: string
}

/**
 * 器材推荐项
 */
export interface EquipmentRecommendation {
  equipmentId: number
  equipmentName: string
  reason: string
}

/**
 * AI 个性化推荐响应
 */
export interface AIRecommendationsResponse {
  courseRecommendations: CourseRecommendation[]
  equipmentRecommendations: EquipmentRecommendation[]
  reasoning?: string
}

/**
 * API 统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
  errors?: string[]
}

/**
 * 分页参数
 */
export interface PageParams {
  page: number
  pageSize: number
  keyword?: string
  courseType?: string
  equipmentType?: string
  type?: string
  status?: string
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

/**
 * 时间范围参数
 */
export interface DateRangeParams {
  startDate: string
  endDate: string
}

/**
 * 统计查询参数
 */
export interface StatisticsParams extends DateRangeParams {
  groupBy?: 'day' | 'week' | 'month'
}
