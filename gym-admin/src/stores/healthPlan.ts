import { defineStore } from 'pinia'
import { ref } from 'vue'
import type {
  HealthPlan,
  HealthPlanStatus,
  CreateHealthPlanRequest,
  UpdateHealthPlanProgressRequest,
  PageParams,
  PageResponse
} from '@/types'
import { http } from '@/api/request'

/**
 * 健康计划状态管理
 */
export const useHealthPlanStore = defineStore('healthPlan', () => {
  // ========== 状态 ==========
  const healthPlans = ref<HealthPlan[]>([])
  const currentHealthPlan = ref<HealthPlan | null>(null)
  const myHealthPlans = ref<HealthPlan[]>([])
  const loading = ref(false)
  const total = ref(0)

  // ========== 方法 ==========
  /**
   * 获取健康计划列表
   */
  const fetchHealthPlans = async (params?: PageParams) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<HealthPlan>>('/health-plans', { params })
      healthPlans.value = response.data.list
      total.value = response.data.total
      return response.data
    } catch (error) {
      console.error('获取健康计划列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取我的健康计划
   */
  const fetchMyHealthPlans = async () => {
    loading.value = true
    try {
      const response = await http.get<HealthPlan[]>('/health-plans/my')
      myHealthPlans.value = response.data
      return response.data
    } catch (error) {
      console.error('获取我的健康计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取健康计划详情
   */
  const fetchHealthPlanById = async (id: number) => {
    loading.value = true
    try {
      const response = await http.get<HealthPlan>(`/health-plans/${id}`)
      currentHealthPlan.value = response.data
      return response.data
    } catch (error) {
      console.error('获取健康计划详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建健康计划
   */
  const createHealthPlan = async (data: CreateHealthPlanRequest) => {
    loading.value = true
    try {
      const response = await http.post<HealthPlan>('/health-plans', data)
      myHealthPlans.value.unshift(response.data)
      return response.data
    } catch (error) {
      console.error('创建健康计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新健康计划
   */
  const updateHealthPlan = async (id: number, data: Partial<CreateHealthPlanRequest>) => {
    loading.value = true
    try {
      const response = await http.put<HealthPlan>(`/health-plans/${id}`, data)
      // 更新列表中的计划
      const index = myHealthPlans.value.findIndex(p => p.id === id)
      if (index !== -1) {
        myHealthPlans.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('更新健康计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除健康计划
   */
  const deleteHealthPlan = async (id: number) => {
    loading.value = true
    try {
      await http.delete(`/health-plans/${id}`)
      // 从列表中移除计划
      myHealthPlans.value = myHealthPlans.value.filter(p => p.id !== id)
      return true
    } catch (error) {
      console.error('删除健康计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新健康计划进度
   */
  const updateProgress = async (id: number, data: UpdateHealthPlanProgressRequest) => {
    loading.value = true
    try {
      const response = await http.put<HealthPlan>(`/health-plans/${id}/progress`, data)
      // 更新列表中的计划
      const index = myHealthPlans.value.findIndex(p => p.id === id)
      if (index !== -1) {
        myHealthPlans.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('更新健康计划进度失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按状态筛选健康计划
   */
  const filterHealthPlansByStatus = async (status: HealthPlanStatus) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<HealthPlan>>('/health-plans', {
        params: { status }
      })
      healthPlans.value = response.data.list
      return response.data
    } catch (error) {
      console.error('筛选健康计划失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    healthPlans.value = []
    currentHealthPlan.value = null
    myHealthPlans.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    healthPlans,
    currentHealthPlan,
    myHealthPlans,
    loading,
    total,

    // 方法
    fetchHealthPlans,
    fetchMyHealthPlans,
    fetchHealthPlanById,
    createHealthPlan,
    updateHealthPlan,
    deleteHealthPlan,
    updateProgress,
    filterHealthPlansByStatus,
    $reset
  }
})
