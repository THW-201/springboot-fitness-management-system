import { http } from './request'
import type {
  User,
  StudentProfile,
  CoachProfile
} from '@/types'

/**
 * 更新用户请求
 */
export interface UpdateUserRequest {
  realName?: string
  email?: string
  phone?: string
  avatarUrl?: string
  newPassword?: string
}

/**
 * 获取用户列表参数
 */
export interface GetUsersParams {
  current?: number
  size?: number
  role?: 'ADMIN' | 'COACH' | 'STUDENT'
  keyword?: string
}

/**
 * 获取教练学生列表参数
 */
export interface GetCoachStudentsParams {
  current?: number
  size?: number
  realName?: string
}

/**
 * 分页响应（MyBatis Plus格式）
 */
export interface MybatisPlusPageResponse<T> {
  records: T[]
  total: number
  size: number
  current: number
}

/**
 * 用户管理 API
 */
export const userApi = {
  /**
   * 获取用户列表（管理员）- 支持分页和筛选
   */
  getUsers(params?: GetUsersParams) {
    return http.get<MybatisPlusPageResponse<User>>('/users', { params })
  },

  /**
   * 获取用户详情
   */
  getUserById(id: number) {
    return http.get<User>(`/users/${id}`)
  },

  /**
   * 更新用户信息
   */
  updateUser(id: number, data: Partial<User>) {
    return http.put<User>(`/users/${id}`, data)
  },

  /**
   * 删除用户（管理员）
   */
  deleteUser(id: number) {
    return http.delete(`/users/${id}`)
  },

  /**
   * 获取教练负责的学生列表（教练）
   * @param params - 可选参数，包含分页参数和 keyword 搜索关键词
   */
  getCoachStudents(params?: GetCoachStudentsParams) {
    return http.get<MybatisPlusPageResponse<User>>('/users/coach/students', { params })
  },

  /**
   * 为学生分配教练（管理员）
   */
  assignCoach(studentId: number, coachId: number) {
    return http.put<void>(`/users/${studentId}/coach/${coachId}`)
  },

  /**
   * 获取学生档案
   */
  getStudentProfile(userId: number) {
    return http.get<StudentProfile>(`/users/${userId}/profile`)
  },

  /**
   * 更新学生档案
   */
  updateStudentProfile(userId: number, data: Partial<StudentProfile>) {
    return http.put<StudentProfile>(`/users/${userId}/profile`, data)
  },

  /**
   * 获取教练档案
   */
  getCoachProfile(userId: number) {
    return http.get<CoachProfile>(`/users/${userId}/profile`)
  },

  /**
   * 更新教练档案
   */
  updateCoachProfile(userId: number, data: Partial<CoachProfile>) {
    return http.put<CoachProfile>(`/users/${userId}/profile`, data)
  }
}
