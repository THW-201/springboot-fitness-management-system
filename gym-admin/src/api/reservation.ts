import { http } from './request'
import type {
  Reservation,
  ReserveCourseRequest,
  ReserveEquipmentRequest,
  PageParams,
  PageResponse
} from '@/types'

/**
 * 预约相关API
 */
export const reservationApi = {
  /**
   * 获取预约列表
   */
  getReservations: (params?: { studentId?: number; reservationType?: string; status?: string; keyword?: string }) => {
    return http.get<any>('/reservations', { params })
  },

  /**
   * 获取我的预约
   */
  getMyReservations: (params?: { reservationType?: string }) => {
    return http.get<Reservation[]>('/reservations/my', { params })
  },

  /**
   * 获取预约详情
   */
  getReservationById: (id: number) => {
    return http.get<Reservation>(`/reservations/${id}`)
  },

  /**
   * 预约课程
   */
  reserveCourse: (data: ReserveCourseRequest) => {
    return http.post<Reservation>('/reservations/course', data)
  },

  /**
   * 预约器材
   */
  reserveEquipment: (data: ReserveEquipmentRequest) => {
    return http.post<Reservation>('/reservations/equipment', data)
  },

  /**
   * 取消预约
   */
  cancelReservation: (id: number, reason?: string) => {
    return http.delete(`/reservations/${id}`, {
      data: { cancelReason: reason }
    })
  },

  /**
   * 确认预约
   */
  confirmReservation: (id: number) => {
    return http.put<Reservation>(`/reservations/${id}/confirm`)
  },

  /**
   * 拒绝预约
   */
  rejectReservation: (id: number, reason?: string) => {
    return http.put<Reservation>(`/reservations/${id}/reject`, {
      rejectReason: reason
    })
  },

  /**
   * 完成预约
   */
  completeReservation: (id: number) => {
    return http.put<Reservation>(`/reservations/${id}/complete`)
  },

  /**
 * 管理员获取所有预约
 */
getAdminReservations: (params?: PageParams & { type?: string; status?: string; keyword?: string }) => {
  return http.get<PageResponse<Reservation>>('/reservations', { params })
},

/**
 * 学生获取自己的预约列表
 */
getStudentReservations: (params?: PageParams & { type?: string; status?: string }) => {
  return http.get<PageResponse<Reservation>>('/reservations/student', { params })
}
}
