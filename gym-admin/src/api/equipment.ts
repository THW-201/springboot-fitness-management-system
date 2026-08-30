import { http } from './request'
import type {
  Equipment,
  PageResponse,
  CreateEquipmentRequest,
  UpdateEquipmentRequest
} from '@/types'

/**
 * 器材管理 API
 */
export const equipmentApi = {
  /**
   * 获取器材列表
   * @param params 分页和筛选参数
   */
  getEquipmentList(params?: {
    current?: number
    size?: number
    name?: string
    equipmentType?: string
    status?: string
  }) {
    return http.get<PageResponse<Equipment>>('/equipment', { params })
  },

  /**
   * 获取器材详情
   */
  getEquipmentById(id: number) {
    return http.get<Equipment>(`/equipment/${id}`)
  },

  /**
   * 添加器材（管理员）
   */
  createEquipment(data: CreateEquipmentRequest) {
    return http.post<Equipment>('/equipment', data)
  },

  /**
   * 更新器材（管理员）
   */
  updateEquipment(id: number, data: UpdateEquipmentRequest) {
    return http.put<Equipment>(`/equipment/${id}`, data)
  },

  /**
   * 删除器材（管理员）
   */
  deleteEquipment(id: number) {
    return http.delete<string>(`/equipment/${id}`)
  }
}
