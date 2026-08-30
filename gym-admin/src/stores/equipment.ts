import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Equipment, EquipmentStatus, PageParams, PageResponse } from '@/types'
import { http } from '@/api/request'

/**
 * 器材状态管理
 */
export const useEquipmentStore = defineStore('equipment', () => {
  // ========== 状态 ==========
  const equipmentList = ref<Equipment[]>([])
  const currentEquipment = ref<Equipment | null>(null)
  const availableEquipment = ref<Equipment[]>([])
  const loading = ref(false)
  const total = ref(0)

  // ========== 方法 ==========
  /**
   * 获取器材列表
   */
  const fetchEquipmentList = async (params?: PageParams) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Equipment>>('/equipment', { params })
      equipmentList.value = response.data.list
      total.value = response.data.total
      return response.data
    } catch (error) {
      console.error('获取器材列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取器材详情
   */
  const fetchEquipmentById = async (id: number) => {
    loading.value = true
    try {
      const response = await http.get<Equipment>(`/equipment/${id}`)
      currentEquipment.value = response.data
      return response.data
    } catch (error) {
      console.error('获取器材详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建器材
   */
  const createEquipment = async (data: Partial<Equipment>) => {
    loading.value = true
    try {
      const response = await http.post<Equipment>('/equipment', data)
      equipmentList.value.unshift(response.data)
      return response.data
    } catch (error) {
      console.error('创建器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新器材
   */
  const updateEquipment = async (id: number, data: Partial<Equipment>) => {
    loading.value = true
    try {
      const response = await http.put<Equipment>(`/equipment/${id}`, data)
      // 更新列表中的器材
      const index = equipmentList.value.findIndex(e => e.id === id)
      if (index !== -1) {
        equipmentList.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('更新器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除器材
   */
  const deleteEquipment = async (id: number) => {
    loading.value = true
    try {
      await http.delete(`/equipment/${id}`)
      // 从列表中移除器材
      equipmentList.value = equipmentList.value.filter(e => e.id !== id)
      return true
    } catch (error) {
      console.error('删除器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取可用器材
   */
  const fetchAvailableEquipment = async () => {
    loading.value = true
    try {
      const response = await http.get<Equipment[]>('/equipment/available')
      availableEquipment.value = response.data
      return response.data
    } catch (error) {
      console.error('获取可用器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 搜索器材
   */
  const searchEquipment = async (keyword: string) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Equipment>>('/equipment', {
        params: { keyword }
      })
      equipmentList.value = response.data.list
      return response.data
    } catch (error) {
      console.error('搜索器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按类型筛选器材
   */
  const filterEquipmentByType = async (type: string) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Equipment>>('/equipment', {
        params: { equipmentType: type }
      })
      equipmentList.value = response.data.list
      return response.data
    } catch (error) {
      console.error('筛选器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按状态筛选器材
   */
  const filterEquipmentByStatus = async (status: EquipmentStatus) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Equipment>>('/equipment', {
        params: { status }
      })
      equipmentList.value = response.data.list
      return response.data
    } catch (error) {
      console.error('筛选器材失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    equipmentList.value = []
    currentEquipment.value = null
    availableEquipment.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    equipmentList,
    currentEquipment,
    availableEquipment,
    loading,
    total,

    // 方法
    fetchEquipmentList,
    fetchEquipmentById,
    createEquipment,
    updateEquipment,
    deleteEquipment,
    fetchAvailableEquipment,
    searchEquipment,
    filterEquipmentByType,
    filterEquipmentByStatus,
    $reset
  }
})
