import { http } from './request'
import type { StatisticsParams } from '@/types'

/**
 * 统计相关API
 */
export const statisticsApi = {
  /**
   * 获取课程统计数据
   */
  getCourseStatistics: (params: StatisticsParams) => {
    return http.get('/statistics/courses', { params })
  },

  /**
   * 获取器材统计数据
   */
  getEquipmentStatistics: (params: StatisticsParams) => {
    return http.get('/statistics/equipment', { params })
  },

  /**
   * 获取学生统计数据
   */
  getStudentStatistics: (params: StatisticsParams, page: number = 1, size: number = 10) => {
    return http.get('/statistics/students', {
      params: {
        ...params,
        page,
        size
      }
    })
  },

  /**
   * 获取个人健康数据
   */
  getPersonalHealthData: () => {
    return http.get('/statistics/my')
  }
}
