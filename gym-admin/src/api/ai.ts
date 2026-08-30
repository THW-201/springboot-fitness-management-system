import { http } from './request'
import type {
  AIChatHistory,
  AIChatRequest,
  AIRecommendationsResponse
} from '@/types'

/**
 * AI 服务 API
 */
export const aiApi = {
  /**
   * AI 问答
   */
  chat(data: AIChatRequest) {
    return http.post<string>('/ai/chat', data)
  },

  /**
   * 获取个性化推荐
   */
  getRecommendations() {
    return http.get<AIRecommendationsResponse>('/ai/recommendations')
  },

  /**
   * 获取健康建议
   */
  getHealthAdvice() {
    return http.get('/ai/health-advice')
  },

  /**
   * 获取问答历史
   */
  getChatHistory() {
    return http.get<AIChatHistory[]>('/ai/chat-history')
  }
}
