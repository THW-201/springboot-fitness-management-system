import { http } from './request'
import type {
  User,
  LoginRequest,
  RegisterRequest,
  LoginResponse
} from '@/types'

/**
 * 认证相关 API
 */
export const authApi = {
  /**
   * 用户登录
   * 使用用户名和密码登录系统，成功后返回 JWT Token 和用户信息
   */
  login(data: LoginRequest) {
    return http.post<LoginResponse>('/auth/login', data)
  },

  /**
   * 用户注册
   * 注册新用户账号，支持管理员、教练、学生三种角色
   * 学生角色必须提供学号，教练角色可选填专业信息
   */
  register(data: RegisterRequest) {
    return http.post<User>('/auth/register', data)
  },

  /**
   * 用户登出
   * 登出当前用户，撤销 Token。需要在请求头中携带有效的 JWT Token
   */
  logout() {
    return http.post<string>('/auth/logout')
  },

  /**
   * 获取当前用户信息
   * 获取当前登录用户的详细信息，包括角色对应的 Profile 信息
   * 需要在请求头中携带有效的 JWT Token
   */
  getCurrentUser() {
    return http.get<User>('/auth/me')
  }
}
