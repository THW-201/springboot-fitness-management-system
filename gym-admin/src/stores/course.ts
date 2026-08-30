import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Course, CourseStatus, CreateCourseRequest, UpdateCourseRequest, PageParams, PageResponse } from '@/types'
import { http } from '@/api/request'

/**
 * 课程状态管理
 */
export const useCourseStore = defineStore('course', () => {
  // ========== 状态 ==========
  const courses = ref<Course[]>([])
  const currentCourse = ref<Course | null>(null)
  const loading = ref(false)
  const total = ref(0)

  // ========== 方法 ==========
  /**
   * 获取课程列表
   */
  const fetchCourses = async (params?: PageParams) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Course>>('/courses', { params })
      courses.value = response.data.list
      total.value = response.data.total
      return response.data
    } catch (error) {
      console.error('获取课程列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取课程详情
   */
  const fetchCourseById = async (id: number) => {
    loading.value = true
    try {
      const response = await http.get<Course>(`/courses/${id}`)
      currentCourse.value = response.data
      return response.data
    } catch (error) {
      console.error('获取课程详情失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 创建课程
   */
  const createCourse = async (data: CreateCourseRequest) => {
    loading.value = true
    try {
      const response = await http.post<Course>('/courses', data)
      return response.data
    } catch (error) {
      console.error('创建课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 更新课程
   */
  const updateCourse = async (id: number, data: UpdateCourseRequest) => {
    loading.value = true
    try {
      const response = await http.put<Course>(`/courses/${id}`, data)
      // 更新列表中的课程
      const index = courses.value.findIndex(c => c.id === id)
      if (index !== -1) {
        courses.value[index] = response.data
      }
      return response.data
    } catch (error) {
      console.error('更新课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 删除课程
   */
  const deleteCourse = async (id: number) => {
    loading.value = true
    try {
      await http.delete(`/courses/${id}`)
      // 从列表中移除课程
      courses.value = courses.value.filter(c => c.id !== id)
      return true
    } catch (error) {
      console.error('删除课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 搜索课程
   */
  const searchCourses = async (keyword: string) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Course>>('/courses/search', {
        params: { keyword }
      })
      courses.value = response.data.list
      return response.data
    } catch (error) {
      console.error('搜索课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 按状态筛选课程
   */
  const filterCoursesByStatus = async (status: CourseStatus) => {
    loading.value = true
    try {
      const response = await http.get<PageResponse<Course>>('/courses', {
        params: { status }
      })
      courses.value = response.data.list
      return response.data
    } catch (error) {
      console.error('筛选课程失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    courses.value = []
    currentCourse.value = null
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    courses,
    currentCourse,
    loading,
    total,

    // 方法
    fetchCourses,
    fetchCourseById,
    createCourse,
    updateCourse,
    deleteCourse,
    searchCourses,
    filterCoursesByStatus,
    $reset
  }
})
