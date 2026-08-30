import request from './request'

// 公告相关API
const announcementApi = {
  // 创建公告
  createAnnouncement: (data: any) => {
    return request({
      url: '/api/v1/announcements',
      method: 'post',
      data
    })
  },

  // 更新公告
  updateAnnouncement: (id: number, data: any) => {
    return request({
      url: `/api/v1/announcements/${id}`,
      method: 'put',
      data
    })
  },

  // 获取公告详情
  getAnnouncementById: (id: number) => {
    return request({
      url: `/api/v1/announcements/${id}`,
      method: 'get'
    })
  },

  // 获取所有公告（管理员）
  getAllAnnouncements: () => {
    return request({
      url: '/api/v1/announcements',
      method: 'get'
    })
  },

  // 获取用户可见的公告
  getUserAnnouncements: () => {
    return request({
      url: '/api/v1/announcements/user',
      method: 'get'
    })
  },

  // 删除公告
  deleteAnnouncement: (id: number) => {
    return request({
      url: `/api/v1/announcements/${id}`,
      method: 'delete'
    })
  }
}

export default announcementApi
