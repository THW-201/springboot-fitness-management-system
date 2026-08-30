import { http } from './request'
import type { PlanCheckIn } from '@/types'

/**
 * 健康计划打卡 API
 */
export const planCheckInApi = {
  /**
   * 开始打卡
   */
  startCheckIn(planId: number, studentId: number, exerciseType: string) {
    return http.post<PlanCheckIn>('/plan-check-ins/start', null, {
      params: { planId, studentId, exerciseType }
    })
  },

  /**
   * 结束打卡
   */
  endCheckIn(id: number) {
    return http.post<PlanCheckIn>(`/plan-check-ins/end/${id}`)
  },

  /**
   * 获取计划的打卡记录
   */
  getCheckInsByPlanId(planId: number) {
    return http.get<PlanCheckIn[]>(`/plan-check-ins/plan/${planId}`)
  },

  /**
   * 获取计划在指定日期范围内的打卡记录
   */
  getCheckInsByDateRange(planId: number, startDate: string, endDate: string) {
    return http.get<PlanCheckIn[]>(`/plan-check-ins/plan/${planId}/date-range`, {
      params: { startDate, endDate }
    })
  },

  /**
   * 获取当天的打卡记录
   */
  getTodayCheckIn(planId: number, studentId: number) {
    return http.get<PlanCheckIn>('/plan-check-ins/today', {
      params: { planId, studentId }
    })
  },

  /**
   * 计算计划的完成度
   */
  calculateCompletionPercentage(planId: number) {
    return http.get<number>(`/plan-check-ins/completion/${planId}`)
  }
}
