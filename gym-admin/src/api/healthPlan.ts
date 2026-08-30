import { http } from './request'
import type {
  HealthPlan,
  CreateHealthPlanRequest,
  UpdateHealthPlanProgressRequest,
  HealthPlanStatus
} from '@/types'

/**
 * 更新健康计划请求
 */
export interface UpdateHealthPlanRequest {
  planName?: string
  description?: string
  targetWeight?: number
  targetDurationMinutes?: number
  currentWeight?: number
  startDate?: string
  endDate?: string
  status?: HealthPlanStatus
}

/**
 * 获取健康计划列表参数
 */
export interface GetHealthPlansParams {
  studentId?: number
  current?: number
  size?: number
}

/**
 * 健康计划 API
 */
export const healthPlanApi = {
  /**
   * 获取健康计划列表
   * 获取当前登录用户的所有健康计划，或指定学生的健康计划
   * @param params - 可选参数，包含 studentId
   */
  getHealthPlans(params?: GetHealthPlansParams) {
    return http.get('/health-plans', { params })
  },

  /**
   * 获取所有健康计划（管理员）
   * 获取系统中所有的健康计划，支持分页和筛选
   */
  getAllHealthPlans(params?: any) {
    return http.get('/health-plans', { params })
  },

  /**
   * 获取健康计划详情
   * 根据健康计划ID获取详细信息
   */
  getHealthPlanById(id: number) {
    return http.get<HealthPlan>(`/health-plans/${id}`)
  },

  /**
   * 获取健康计划详情（管理员）
   */
  getHealthPlanDetail(id: number) {
    return http.get<HealthPlan>(`/health-plans/${id}`)
  },

  /**
   * 创建健康计划
   * 创建新的健康计划，设置目标体重和运动时长等
   */
  createHealthPlan(data: any) {
    return http.post<HealthPlan>('/health-plans', data)
  },

  /**
   * 更新健康计划
   * 更新健康计划信息，包括目标、状态等
   */
  updateHealthPlan(data: any) {
    return http.put<HealthPlan>('/health-plans', data)
  },

  /**
   * 删除健康计划
   */
  deleteHealthPlan(id: number) {
    return http.delete(`/health-plans/${id}`)
  },

  /**
   * 更新健康计划进度
   */
  updateHealthPlanProgress(id: number, data: UpdateHealthPlanProgressRequest) {
    return http.put<HealthPlan>(`/health-plans/${id}/progress`, data)
  }
}
