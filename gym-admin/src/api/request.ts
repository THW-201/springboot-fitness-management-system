import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError, InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

// API 响应格式
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
  errors?: string[]
}

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 120000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从 Pinia store 获取 token
    const userStore = useUserStore()
    const token = userStore.token

    // 自动添加 JWT Token 到请求头
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加请求时间戳，防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }

    return config
  },
  (error: AxiosError) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data

    // 判断业务状态码
    if (res.code !== 200) {
      // 显示错误消息
      const errorMessage = res.message || '请求失败'

      // 显示详细错误信息
      if (res.errors && res.errors.length > 0) {
        ElMessage.error({
          message: `${errorMessage}: ${res.errors.join(', ')}`,
          duration: 5000,
          showClose: true
        })
      } else {
        ElMessage.error({
          message: errorMessage,
          duration: 3000,
          showClose: true
        })
      }

      // 401: 未授权，清除 token 并跳转登录页
      if (res.code === 401) {
        ElMessage.warning('登录已过期，请重新登录')
        // const userStore = useUserStore()
        router.push('/login')
        // userStore.logout()
      }

      // 403: 权限不足
      if (res.code === 403) {
        const userStore = useUserStore()
        const hasToken = !!userStore.token
        
        if (hasToken) {
          // 有token但403，说明token可能失效或权限变更，需要重新登录
          ElMessage.warning('登录状态已失效，请重新登录')
          userStore.logout()
          router.push('/login')
        } else {
          // 没有token的403，纯粹是权限不足
          ElMessageBox.alert(
            res.message || '您没有权限访问该功能，请联系管理员',
            '权限不足',
            {
              confirmButtonText: '我知道了',
              type: 'error',
              showClose: false
            }
          )
        }
      }

      // // 400: 请求参数错误
      // if (res.code === 400) {
      //   console.error('请求参数错误:', res.errors)
      // }

      // // 404: 资源不存在
      // if (res.code === 404) {
      //   ElMessage.error('请求的资源不存在')
      // }

      // // 409: 资源冲突（如用户名已存在）
      // if (res.code === 409) {
      //   ElMessage.warning(errorMessage)
      // }

      // // 500: 服务器错误
      // if (res.code === 500) {
      //   ElMessage.error('服务器内部错误，请稍后重试')
      // }

      return Promise.reject(new Error(errorMessage))
    }

    return response
  },
  (error: AxiosError<ApiResponse>) => {
    console.error('响应拦截器错误:', error)

    // 网络错误处理
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 400:
          // 请求参数错误
          if (data?.errors && data.errors.length > 0) {
            ElMessage.error({
              message: `参数错误: ${data.errors.join(', ')}`,
              duration: 5000,
              showClose: true
            })
          } else {
            ElMessage.error(data?.message || '请求参数错误，请检查输入')
          }
          break

        case 401:
          // 未授权
          ElMessage.warning({
            message: data?.message || '登录已过期，请重新登录',
            duration: 3000
          })
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break

        case 403:
          // 权限不足
          {
            const userStore = useUserStore()
            const hasToken = !!userStore.token
            
            if (hasToken) {
              // 有token但403，说明token可能失效或权限变更，需要重新登录
              ElMessage.warning('登录状态已失效，请重新登录')
              userStore.logout()
              router.push('/login')
            } else {
              // 没有token的403，纯粹是权限不足
              ElMessageBox.alert(
                data?.message || '您没有权限执行此操作，请联系管理员获取相应权限',
                '权限不足',
                {
                  confirmButtonText: '我知道了',
                  type: 'error',
                  showClose: false
                }
              )
            }
          }
          break

        case 404:
          // 资源不存在
          ElMessage.error({
            message: data?.message || '请求的资源不存在',
            duration: 3000
          })
          break

        case 409:
          // 资源冲突
          ElMessage.warning({
            message: data?.message || '资源冲突，请检查数据是否重复',
            duration: 4000
          })
          break

        case 422:
          // 数据验证失败
          if (data?.errors && data.errors.length > 0) {
            ElMessage.error({
              message: `验证失败: ${data.errors.join(', ')}`,
              duration: 5000,
              showClose: true
            })
          } else {
            ElMessage.error(data?.message || '数据验证失败')
          }
          break

        case 429:
          // 请求过于频繁
          ElMessage.warning({
            message: '请求过于频繁，请稍后再试',
            duration: 4000
          })
          break

        case 500:
          // 服务器内部错误
          ElMessage.error({
            message: data?.message || '服务器内部错误，请稍后重试或联系管理员',
            duration: 4000,
            showClose: true
          })
          break

        case 502:
        case 503:
        case 504:
          // 服务不可用
          ElMessage.error({
            message: '服务暂时不可用，请稍后再试',
            duration: 4000
          })
          break

        default:
          ElMessage.error({
            message: data?.message || `请求失败 (HTTP ${status})`,
            duration: 3000
          })
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error({
        message: '网络连接失败，请检查网络设置',
        duration: 4000,
        showClose: true
      })
    } else if (error.code === 'ECONNABORTED') {
      // 请求超时
      ElMessage.error({
        message: '请求超时，请稍后重试',
        duration: 3000
      })
    } else {
      // 请求配置错误
      ElMessage.error({
        message: error.message || '请求配置错误',
        duration: 3000
      })
    }

    return Promise.reject(error)
  }
)

// 导出请求方法
export const http = {
  get<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.get(url, config).then(res => res.data)
  },

  post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.post(url, data, config).then(res => res.data)
  },

  put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.put(url, data, config).then(res => res.data)
  },

  delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.delete(url, config).then(res => res.data)
  },

  patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
    return request.patch(url, data, config).then(res => res.data)
  }
}

export default request
