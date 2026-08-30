import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { User, StudentProfile, CoachProfile, UserRole } from '@/types'
import { authApi } from '@/api/auth'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // ========== 状态 ==========
  const token = ref<string>(localStorage.getItem('token') || '')
  const refreshToken = ref<string>(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<User | null>(
    localStorage.getItem('userInfo')
      ? JSON.parse(localStorage.getItem('userInfo')!)
      : null
  )
  const studentProfile = ref<StudentProfile | null>(null)
  const coachProfile = ref<CoachProfile | null>(null)

  // ========== 计算属性 ==========
  const isLoggedIn = computed(() => !!token.value)
  const userId = computed(() => userInfo.value?.id ?? null)
  const username = computed(() => userInfo.value?.username ?? '')
  const userRole = computed(() => userInfo.value?.role ?? null)
  const userAvatar = computed(() => userInfo.value?.avatarUrl ?? '')
  const userName = computed(() => userInfo.value?.realName ?? '')
  const userEmail = computed(() => userInfo.value?.email ?? '')

  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN' || userInfo.value?.role === UserRole.ADMIN)
  const isCoach = computed(() => userInfo.value?.role === 'COACH' || userInfo.value?.role === UserRole.COACH)
  const isStudent = computed(() => userInfo.value?.role === 'STUDENT' || userInfo.value?.role === UserRole.STUDENT)

  // ========== 方法 ==========
  /**
   * 设置 Token
   */
  const setToken = (newToken: string, newRefreshToken?: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)

    if (newRefreshToken) {
      refreshToken.value = newRefreshToken
      localStorage.setItem('refreshToken', newRefreshToken)
    }
  }

  /**
   * 处理登录响应
   */
  const handleLoginResponse = (response: { accessToken: string; tokenType: string; expiresIn: number; user: User }) => {
    // 保存 accessToken
    setToken(response.accessToken)
    
    // 保存用户信息(包含 studentProfile 和 coachProfile)
    setUserInfo(response.user)
    
    // 如果有学生档案,单独保存
    if (response.user.studentProfile) {
      setStudentProfile(response.user.studentProfile)
    }
    
    // 如果有教练档案,单独保存
    if (response.user.coachProfile) {
      setCoachProfile(response.user.coachProfile)
    }
    
    // 保存 token 过期时间
    const expiresAt = Date.now() + response.expiresIn * 1000
    localStorage.setItem('tokenExpiresAt', expiresAt.toString())
  }

  /**
   * 设置用户信息
   */
  const setUserInfo = (info: User) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  /**
   * 设置学生档案
   */
  const setStudentProfile = (profile: StudentProfile) => {
    studentProfile.value = profile
  }

  /**
   * 设置教练档案
   */
  const setCoachProfile = (profile: CoachProfile) => {
    coachProfile.value = profile
  }

  /**
   * 更新用户信息
   */
  const updateUserInfo = (updates: Partial<User>) => {
    if (userInfo.value) {
      userInfo.value = { ...userInfo.value, ...updates }
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }
  }

  /**
   * 检查权限
   */
  const hasPermission = (requiredRoles: UserRole[]) => {
    if (!userInfo.value) return false
    const role = userInfo.value.role
    return requiredRoles.some(r => r === role || r.valueOf() === role)
  }

  /**
   * 登出
   */
  const logout = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    studentProfile.value = null
    coachProfile.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  /**
   * 获取当前用户信息
   */
  const fetchCurrentUser = async () => {
    if (!token.value) {
      return null
    }
    
    try {
      const res = await authApi.getCurrentUser()
      setUserInfo(res.data)
      
      if (res.data.studentProfile) {
        setStudentProfile(res.data.studentProfile)
      }
      
      if (res.data.coachProfile) {
        setCoachProfile(res.data.coachProfile)
      }
      
      return res.data
    } catch (error) {
      console.error('获取当前用户信息失败:', error)
      return null
    }
  }

  /**
   * 刷新 Token
   */
  const refreshTokenIfNeeded = async () => {
    // TODO: 实现刷新 Token 逻辑
    return token.value
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    studentProfile.value = null
    coachProfile.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    studentProfile,
    coachProfile,

    // 计算属性
    isLoggedIn,
    userId,
    username,
    userRole,
    userAvatar,
    userName,
    userEmail,
    isAdmin,
    isCoach,
    isStudent,

    // 方法
    setToken,
    setUserInfo,
    setStudentProfile,
    setCoachProfile,
    handleLoginResponse,
    updateUserInfo,
    hasPermission,
    fetchCurrentUser,
    logout,
    refreshTokenIfNeeded,
    $reset
  }
})
