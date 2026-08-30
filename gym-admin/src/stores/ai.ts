import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { AIChatHistory, AIChatRequest, AIChatResponse } from '@/types'
import { http } from '@/api/request'

/**
 * AI 服务状态管理
 */
export const useAIStore = defineStore('ai', () => {
  // ========== 状态 ==========
  const chatHistory = ref<AIChatHistory[]>([])
  const loading = ref(false)
  const errorMessage = ref('')

  // ========== 方法 ==========
  /**
   * 发送聊天消息
   */
  const sendChatMessage = async (question: string, context?: string) => {
    loading.value = true
    errorMessage.value = ''

    try {
      const request: AIChatRequest = { question, context }
      const response = await http.post<AIChatResponse>('/ai/chat', request)

      // 保存到历史记录
      const chatRecord: AIChatHistory = {
        id: Date.now(),
        userId: 0, // 会从后端返回
        question,
        answer: response.data.answer,
        context: response.data.context,
        responseTimeMs: response.data.responseTimeMs,
        createdAt: new Date().toISOString()
      }

      chatHistory.value.push(chatRecord)

      return response.data
    } catch (error) {
      console.error('发送聊天消息失败:', error)
      errorMessage.value = '获取 AI 回复失败，请稍后重试'
      throw error
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取聊天历史
   */
  const fetchChatHistory = async () => {
    try {
      const response = await http.get<AIChatHistory[]>('/ai/chat-history')
      chatHistory.value = response.data
      return response.data
    } catch (error) {
      console.error('获取聊天历史失败:', error)
      throw error
    }
  }

  /**
   * 获取个性化推荐
   */
  const getRecommendations = async () => {
    try {
      const response = await http.get('/ai/recommendations')
      return response.data
    } catch (error) {
      console.error('获取推荐失败:', error)
      throw error
    }
  }

  /**
   * 获取健康建议
   */
  const getHealthAdvice = async () => {
    try {
      const response = await http.get('/ai/health-advice')
      return response.data
    } catch (error) {
      console.error('获取健康建议失败:', error)
      throw error
    }
  }

  /**
   * 清空聊天历史
   */
  const clearChatHistory = () => {
    chatHistory.value = []
  }

  /**
   * 重置状态
   */
  const $reset = () => {
    chatHistory.value = []
    loading.value = false
    errorMessage.value = ''
  }

  return {
    // 状态
    chatHistory,
    loading,
    errorMessage,

    // 方法
    sendChatMessage,
    fetchChatHistory,
    getRecommendations,
    getHealthAdvice,
    clearChatHistory,
    $reset
  }
})
