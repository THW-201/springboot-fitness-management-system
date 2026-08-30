import { http } from './request'

/**
 * 社交相关API
 */
export const socialApi = {
  // 帖子相关
  createPost: (data: any) => {
    return http.post('/social/posts', data)
  },
  
  updatePost: (id: number, data: any) => {
    return http.put(`/social/posts/${id}`, data)
  },
  
  deletePost: (id: number) => {
    return http.delete(`/social/posts/${id}`)
  },
  
  getPostDetail: (id: number) => {
    return http.get(`/social/posts/${id}`)
  },
  
  getPosts: (params: { current: number; size: number }) => {
    return http.get('/social/posts', { params })
  },
  
  getMyPosts: (params: { current: number; size: number }) => {
    return http.get('/social/posts/my', { params })
  },
  
  // 评论相关
  createComment: (postId: number, data: any) => {
    return http.post(`/social/posts/${postId}/comments`, data)
  },
  
  deleteComment: (id: number) => {
    return http.delete(`/social/comments/${id}`)
  },
  
  getComments: (postId: number) => {
    return http.get(`/social/posts/${postId}/comments`)
  },
  
  // 点赞相关
  toggleLike: (postId: number) => {
    return http.post(`/social/posts/${postId}/like`)
  },
  
  checkLike: (postId: number) => {
    return http.get(`/social/posts/${postId}/like/check`)
  },
  
  // 话题相关
  getHotTopics: () => {
    return http.get('/social/topics/hot')
  },
  
  // 关注相关
  toggleFollow: (userId: number) => {
    return http.post(`/social/users/${userId}/follow`)
  },
  
  checkFollow: (userId: number) => {
    return http.get(`/social/users/${userId}/follow/check`)
  },
  
  getRecommendedUsers: () => {
    return http.get('/social/users/recommended')
  },
  
  getFollowingUsers: () => {
    return http.get('/social/users/following')
  },
  
  getFollowingPosts: (params: { current: number; size: number }) => {
    return http.get('/social/posts/following', { params })
  },
  
  // 举报相关
  reportPost: (postId: number, data: any) => {
    return http.post(`/social/posts/${postId}/report`, data)
  },
  
  // 管理员相关
  getAdminPosts: (params: { current: number; size: number }) => {
    return http.get('/social/admin/posts', { params })
  },
  
  deleteAdminPost: (id: number) => {
    return http.delete(`/social/admin/posts/${id}`)
  },
  
  getAdminComments: (params: { current: number; size: number }) => {
    return http.get('/social/admin/comments', { params })
  },
  
  deleteAdminComment: (id: number) => {
    return http.delete(`/social/admin/comments/${id}`)
  },
  
  getAdminTopics: () => {
    return http.get('/social/admin/topics')
  },
  
  createTopic: (data: { title: string }) => {
    return http.post('/social/admin/topics', data)
  },
  
  updateTopic: (id: number, data: { title: string }) => {
    return http.put(`/social/admin/topics/${id}`, data)
  },
  
  deleteTopic: (id: number) => {
    return http.delete(`/social/admin/topics/${id}`)
  },
  
  getAdminReports: (params: { current: number; size: number }) => {
    return http.get('/social/admin/reports', { params })
  },
  
  processReport: (id: number, data: { status: string }) => {
    return http.put(`/social/admin/reports/${id}/process`, data)
  }
}
