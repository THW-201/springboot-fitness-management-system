import { http } from './request'
import type {
  Course,
  CreateCourseRequest,
  UpdateCourseRequest,
  PageParams,
  PageResponse
} from '@/types'

/**
 * 课程管理 API
 */
export const courseApi = {
  /**
   * 获取课程列表
   */
  getCourses(params?: PageParams & { keyword?: string; courseType?: string; status?: string }) {
    return http.get<PageResponse<Course>>('/courses', { params })
  },

  /**
   * 获取课程详情
   */
  getCourseById(id: number) {
    return http.get<Course>(`/courses/${id}`)
  },

  /**
   * 创建课程（管理员/教练）
   */
  createCourse(data: CreateCourseRequest) {
    return http.post<Course>('/courses', data)
  },

  /**
   * 更新课程（管理员/教练）
   */
  updateCourse(id: number, data: UpdateCourseRequest) {
    return http.put<Course>(`/courses/${id}`, data)
  },

  /**
   * 删除课程（管理员/教练）
   */
  deleteCourse(id: number) {
    return http.delete(`/courses/${id}`)
  },

  /**
   * 搜索课程
   */
  searchCourses(params?: { keyword?: string; courseType?: string; status?: string; current?: number; size?: number }) {
    return http.get<PageResponse<Course>>('/courses/search', { params })
  },

  /**
   * 获取课程学员列表（教练/管理员）
   */
  getCourseStudents(courseId: number, params?: PageParams) {
    return http.get(`/reservations/course/${courseId}/students`, { params })
  },

  /**
   * 获取教练的课程列表（教练）
   */
  getCoachCourses(params?: PageParams & { coachId?: number }) {
    return http.get<PageResponse<Course>>('/courses', { params })
  }
}
