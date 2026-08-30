import { http } from './request'
import type { CheckIn, PageParams, PageResponse } from '@/types'

/**
 * 签到打卡 API
 */
export const checkInApi = {
  /**
   * 签到
   */
  checkIn(reservationId: number, location?: string) {
    return http.post<CheckIn>('/checkins', {
      reservationId,
      location
    })
  },

  /**
   * 签退
   */
  checkOut(checkInId: number, caloriesBurned?: number) {
    return http.put<CheckIn>(`/checkins/${checkInId}/checkout`, {
      caloriesBurned
    })
  },

  /**
   * 获取我的签到记录
   */
  getMyCheckIns(params?: PageParams) {
    return http.get<PageResponse<CheckIn>>('/checkins/my', { params })
  },

  /**
   * 获取所有签到记录（管理员/教练）
   */
  getAllCheckIns(params?: {
    page?: number
    pageSize?: number
    studentId?: number
    status?: string
    startDate?: string
    endDate?: string
  }) {
    return http.get<any>('/checkins', { params })
  },

  /**
   * 获取签到统计数据（管理员/教练）
   */
  getCheckInStatistics() {
    return http.get<{
      totalCheckIns: number
      completedCheckIns: number
      pendingCheckIns: number
    }>('/checkins/statistics')
  },

  /**
   * 获取签到详情
   */
  getCheckInDetail(checkInId: number) {
    return http.get<CheckIn>(`/checkins/${checkInId}`)
  },

  /**
   * 更新签到状态（管理员）
   */
  updateCheckInStatus(checkInId: number, data: {
    status: string
    checkOutTime?: string
    calories?: number
  }) {
    return http.put<CheckIn>(`/checkins/${checkInId}`, data)
  },

  /**
   * 获取未签到的预约记录
   */
  getUncheckedInReservations(params?: {
    studentId?: number
    startDate?: string
    endDate?: string
  }) {
    return http.get<any[]>('/checkins/unchecked', { params })
  }
}
