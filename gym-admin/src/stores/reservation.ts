import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {
  Reservation,
  ReservationType,
  ReservationStatus,
  ReserveCourseRequest,
  ReserveEquipmentRequest,
  PageParams,
  PageResponse
} from '@/types'
import { http } from '@/api/request'

/**
 * 预约状态管理
 */
export const useReservationStore = defineStore('reservation', () => {
  // ========== 状态 ==========
  const reservations = ref<Reservation[]>([])
  const currentReservation = ref<Reservation | null>(null)
  const myReservations = ref<Reservation[]>([])
  const loading = ref(false)
  const total = ref(0)

  // ========== 方法 ==========
  /**
   * 获取预约列表
   */
  const fetchReservations = async (params?: PageParams) => {
    loading.value = true
    try {
      // 构建后端接口支持的参数
      const backendParams: any = {}
      if (params?.type) {
        backendParams.reservationType = params.type
      }
      if (params?.status) {
        backendParams.status = params.status
      }
      
      const response = await http.get<any[]>('/reservations', { params: backendParams })
      // 转换为前端期望的格式
      const reservationList = response.data.map(item => ({
        id: item.id,
        studentId: item.studentId,
        studentName: item.studentName,
        reservationType: item.reservationType,
        courseId: item.courseId,
        courseName: item.courseName,
        equipmentId: item.equipmentId,
        equipmentName: item.equipmentName,
        startTime: item.startTime,
        endTime: item.endTime,
        status: item.status,
        cancelReason: item.cancelReason,
        cancelledAt: item.cancelledAt,
        createdAt: item.createdAt,
        updatedAt: item.updatedAt
      }))
      
      // 本地处理分页
      const startIndex = ((params?.page || 1) - 1) * (params?.pageSize || 10)
      const endIndex = startIndex + (params?.pageSize || 10)
      reservations.value = reservationList.slice(startIndex, endIndex)
      total.value = reservationList.length
      
      return {
        records: reservations.value,
        total: total.value,
        page: params?.page || 1,
        pageSize: params?.pageSize || 10,
        totalPages: Math.ceil(total.value / (params?.pageSize || 10))
      }
    } catch (error) {
      console.error('获取预约列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取我的预约
   */
  const fetchMyReservations = async () => {
    loading.value = true
    try {
      const response = await http.get<Reservation[]>('/reservations/my')
      myReservations.value = response.data
      return response.data
    } catch (error) {
      console.error('获取我的预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取预约详情
   */
  const fetchReservationById = async (id: number) => {
    loading.value = true
    try {
      const response = await http.get<Reservation>(`/reservations/${id}`)
      currentReservation.value = response.data
      return response.data
    } catch (error) {
      console.error('获取预约详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 预约课程
   */
  const reserveCourse = async (data: ReserveCourseRequest) => {
    loading.value = true
    try {
      const response = await http.post<Reservation>('/reservations/course', data)
      myReservations.value.unshift(response.data)
      return response.data
    } catch (error) {
      console.error('预约课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 预约器材
   */
  const reserveEquipment = async (data: ReserveEquipmentRequest) => {
    loading.value = true
    try {
      const response = await http.post<Reservation>('/reservations/equipment', data)
      myReservations.value.unshift(response.data)
      return response.data
    } catch (error) {
      console.error('预约器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 取消预约
   */
  const cancelReservation = async (id: number, reason?: string) => {
    loading.value = true
    try {
      await http.delete(`/reservations/${id}`, {
        data: { cancelReason: reason }
      })
      // 从列表中移除预约
      myReservations.value = myReservations.value.filter(r => r.id !== id)
      return true
    } catch (error) {
      console.error('取消预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按类型筛选预约
   */
  const filterReservationsByType = async (type: ReservationType) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Reservation>>('/reservations', {
        params: { type }
      })
      reservations.value = response.data.records
      return response.data
    } catch (error) {
      console.error('筛选预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按状态筛选预约
   */
  const filterReservationsByStatus = async (status: ReservationStatus) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Reservation>>('/reservations', {
        params: { status }
      })
      reservations.value = response.data.records
      return response.data
    } catch (error) {
      console.error('筛选预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 确认预约
   */
  const confirmReservation = async (id: number) => {
    loading.value = true
    try {
      const response = await http.put<Reservation>(`/reservations/${id}/confirm`)
      // 更新本地列表
      const index = reservations.value.findIndex(r => r.id === id)
      if (index !== -1) {
        reservations.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('确认预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 拒绝预约
   */
  const rejectReservation = async (id: number, reason?: string) => {
    loading.value = true
    try {
      const response = await http.put<Reservation>(`/reservations/${id}/reject`, {
        rejectReason: reason
      })
      // 更新本地列表
      const index = reservations.value.findIndex(r => r.id === id)
      if (index !== -1) {
        reservations.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('拒绝预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 完成预约
   */
  const completeReservation = async (id: number) => {
    loading.value = true
    try {
      const response = await http.put<Reservation>(`/reservations/${id}/complete`)
      // 更新本地列表
      const index = reservations.value.findIndex(r => r.id === id)
      if (index !== -1) {
        reservations.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('完成预约失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    reservations.value = []
    currentReservation.value = null
    myReservations.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    reservations,
    currentReservation,
    myReservations,
    loading,
    total,

    // 方法
    fetchReservations,
    fetchMyReservations,
    fetchReservationById,
    reserveCourse,
    reserveEquipment,
    cancelReservation,
    filterReservationsByType,
    filterReservationsByStatus,
    confirmReservation,
    rejectReservation,
    completeReservation,
    $reset
  }
})
