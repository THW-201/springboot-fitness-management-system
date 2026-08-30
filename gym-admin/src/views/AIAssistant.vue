<template>
  <div class="ai-assistant">
    <div class="container">
      <div class="row">
        <!-- 左侧聊天区域 -->
        <div class="col-8">
          <div class="chat-container-main">
            <!-- 聊天头部 -->
            <div class="chat-header">
              <div class="header-left">
                <el-avatar :size="40" class="ai-avatar">
                  <el-icon :size="24">
                    <Service />
                  </el-icon>
                </el-avatar>
                <div class="ai-info">
                  <div class="ai-name">健身助手</div>
                  <div class="ai-status">
                    <span class="status-dot"></span>
                    <span>在线</span>
                  </div>
                </div>
              </div>
              <div class="header-right">
                <el-button text @click="showHistory = !showHistory" class="history-btn">
                  <el-icon>
                    <Timer />
                  </el-icon>
                  历史记录
                </el-button>
                <el-button text @click="handleClearHistory" class="clear-btn">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  清空对话
                </el-button>
              </div>
            </div>

            <!-- 历史记录面板 -->
            <div v-if="showHistory" class="history-panel">
              <div class="history-header">
                <h3>历史对话</h3>
                <el-button text @click="clearAllHistory" size="small">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  清空全部
                </el-button>
              </div>
              <div class="history-list">
                <div v-if="chatHistory.length === 0" class="empty-history">
                  <el-empty description="暂无历史记录" :image-size="60" />
                </div>
                <div v-else v-for="chat in chatHistory" :key="chat.id" class="history-item">
                  <div class="history-item-content" @click="loadChatFromHistory(chat)">
                    <h4 class="history-title">{{ chat.title }}</h4>
                    <p class="history-time">{{ formatTime(chat.timestamp) }}</p>
                    <p class="history-preview">{{ chat.messages[0]?.content || '' }}</p>
                  </div>
                  <el-button text @click.stop="deleteHistory(chat.id)" class="delete-btn">
                    <el-icon>
                      <Delete />
                    </el-icon>
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 聊天消息区域 -->
            <div class="chat-container" ref="chatContainer">
              <!-- 欢迎消息 -->
              <div v-if="messages.length === 0" class="welcome-message">
                <el-icon :size="64">
                  <ChatDotRound />
                </el-icon>
                <h3>你好！我是 AI 健身助手</h3>
                <p>我可以为您提供以下帮助：</p>
                <div class="feature-list">
                  <div class="feature-item" @click="handleFeatureClick('制定个性化健身计划')">
                    <el-icon>
                      <Trophy />
                    </el-icon>
                    <span>制定个性化健身计划</span>
                  </div>
                  <div class="feature-item" @click="handleFeatureClick('解答训练相关问题')">
                    <el-icon>
                      <Guide />
                    </el-icon>
                    <span>解答训练相关问题</span>
                  </div>
                  <div class="feature-item" @click="handleFeatureClick('提供饮食营养建议')">
                    <el-icon>
                      <Food />
                    </el-icon>
                    <span>提供饮食营养建议</span>
                  </div>
                  <div class="feature-item" @click="handleFeatureClick('运动损伤预防指导')">
                    <el-icon>
                      <FirstAidKit />
                    </el-icon>
                    <span>运动损伤预防指导</span>
                  </div>
                </div>
              </div>

              <!-- 消息列表 -->
              <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
                <div class="message-content">
                  <div class="avatar">
                    <el-icon v-if="message.role === 'user'">
                      <User />
                    </el-icon>
                    <el-icon v-else>
                      <Service />
                    </el-icon>
                  </div>
                  <div class="text-wrapper">
                    <div class="text">{{ message.content }}</div>
                    <div class="message-time">{{ formatTime(message.timestamp) }}</div>
                  </div>
                </div>
              </div>

              <!-- 加载中 -->
              <div v-if="loading" class="message assistant">
                <div class="message-content">
                  <div class="avatar">
                    <el-icon>
                      <Service />
                    </el-icon>
                  </div>
                  <div class="text-wrapper">
                    <div class="loading-dots">
                      <span></span>
                      <span></span>
                      <span></span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 输入区域 -->
            <div class="input-area">
              <div class="input-wrapper">
                <el-input v-model="inputMessage" type="textarea" :rows="3" placeholder="请输入您的问题，例如：如何制定减脂计划？"
                  resize="none" @keydown.enter="handleSend" :disabled="loading" />
              </div>
              <div class="input-footer">
                <span class="hint">按 Enter 发送消息</span>
                <el-button type="primary" :loading="loading" :disabled="!inputMessage.trim()" @click="handleSend">
                  <el-icon>
                    <Position />
                  </el-icon>
                  发送
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：快捷问题和个性化建议 -->
        <div class="col-4">
          <div class="right-container">
            <!-- 个性化建议 -->
            <el-card shadow="hover" class="recommendations-card">
              <template #header>
                <div class="card-header-simple">
                  <span>个性化建议</span>
                  <el-button type="primary" size="small" :loading="recommendationsLoading" @click="getRecommendations">
                    <el-icon><Refresh /></el-icon>
                    获取建议
                  </el-button>
                </div>
              </template>
              <div v-if="hasRecommendations" class="recommendations-content">
                <!-- 评估说明 -->
                <div v-if="recommendations?.reasoning" class="recommendation-section">
                  <h4 class="section-title">
                    <el-icon><Document /></el-icon>
                    评估说明
                  </h4>
                  <div class="recommendation-item reasoning-content">
                    <div class="reasoning-text" v-html="formatReasoning(recommendations.reasoning)"></div>
                  </div>
                </div>

                <!-- 课程推荐 -->
                <div v-if="recommendations && recommendations.courseRecommendations && recommendations.courseRecommendations.length > 0" class="recommendation-section">
                  <h4 class="section-title">
                    <el-icon><Reading /></el-icon>
                    课程推荐
                  </h4>
                  <div v-for="course in recommendations.courseRecommendations" :key="course.courseId" class="recommendation-item">
                    <div class="item-header">
                      <span class="item-name">{{ course.courseName }}</span>
                    </div>
                    <div class="item-reason">
                      <el-icon><InfoFilled /></el-icon>
                      {{ course.reason }}
                    </div>
                  </div>
                </div>

                <!-- 器材推荐 -->
                <div v-if="recommendations && recommendations.equipmentRecommendations && recommendations.equipmentRecommendations.length > 0" class="recommendation-section">
                  <h4 class="section-title">
                    <el-icon><Goods /></el-icon>
                    器材推荐
                  </h4>
                  <div v-for="equipment in recommendations.equipmentRecommendations" :key="equipment.equipmentId" class="recommendation-item">
                    <div class="item-header">
                      <span class="item-name">{{ equipment.equipmentName }}</span>
                    </div>
                    <div class="item-reason">
                      <el-icon><InfoFilled /></el-icon>
                      {{ equipment.reason }}
                    </div>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无个性化建议" :image-size="80" />
            </el-card>

            <!-- 快捷问题 -->
            <el-card shadow="hover" class="quick-questions-card">
              <template #header>
                <span>快捷问题</span>
              </template>
              <div class="questions-list">
                <div v-for="(item, index) in quickQuestions" :key="index"
                  :class="['question-item', `question-item-${index}`]" @click="handleQuickQuestion(item.question)">
                  <el-icon>
                    <QuestionFilled />
                  </el-icon>
                  <span>{{ item.question }}</span>
                  <el-icon>
                    <ArrowRight />
                  </el-icon>
                </div>
              </div>
            </el-card>

            <!-- 功能说明 -->
            <el-card shadow="hover" class="features-card">
              <template #header>
                <span>功能特点</span>
              </template>
              <div class="feature-grid">
                <div class="feature-icon-item">
                  <el-icon :size="28" color="#667eea">
                    <ChatDotRound />
                  </el-icon>
                  <span>智能对话</span>
                </div>
                <div class="feature-icon-item">
                  <el-icon :size="28" color="#764ba2">
                    <Document />
                  </el-icon>
                  <span>个性化建议</span>
                </div>
                <div class="feature-icon-item">
                  <el-icon :size="28" color="#f093fb">
                    <Clock />
                  </el-icon>
                  <span>24小时在线</span>
                </div>
                <div class="feature-icon-item">
                  <el-icon :size="28" color="#4facfe">
                    <Star />
                  </el-icon>
                  <span>专业指导</span>
                </div>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElEmpty } from 'element-plus'
import { Timer, Service, User, Delete, ChatDotRound, Trophy, Guide, Food, FirstAidKit, Position, Refresh, Document, Reading, Goods, InfoFilled, QuestionFilled, ArrowRight, Clock, Star } from '@element-plus/icons-vue'
import { aiApi } from '@/api/ai'
import { useAIStore } from '@/stores/ai'
import type { AIChatRequest, AIRecommendationsResponse } from '@/types'

interface Message {
  role: 'user' | 'assistant'
  content: string
  timestamp: number
}

const chatContainer = ref<HTMLElement>()
const inputMessage = ref('')
const loading = ref(false)
const recommendationsLoading = ref(false)
const recommendations = ref<AIRecommendationsResponse | null>(null)
const messages = ref<Message[]>([])
const chatHistory = ref<Array<{id: string, title: string, messages: Message[], timestamp: number}>>([])
const showHistory = ref(false)

// 使用AI store
const aiStore = useAIStore()

// 计算是否有推荐内容
const hasRecommendations = computed(() => {
  if (!recommendations.value) return false
  return recommendations.value.courseRecommendations.length > 0 || 
         recommendations.value.equipmentRecommendations.length > 0 ||
         !!recommendations.value.reasoning
})

// 加载历史记录
const loadHistory = async () => {
  try {
    await aiStore.fetchChatHistory()
    // 转换后端历史记录为前端格式
    chatHistory.value = aiStore.chatHistory.map(chat => {
      const messages: Message[] = [
        {
          role: 'user',
          content: chat.question,
          timestamp: new Date(chat.createdAt).getTime()
        },
        {
          role: 'assistant',
          content: chat.answer,
          timestamp: new Date(chat.createdAt).getTime()
        }
      ]
      
      return {
        id: chat.id.toString(),
        title: chat.question.length > 20 ? chat.question.substring(0, 20) + '...' : chat.question,
        messages,
        timestamp: new Date(chat.createdAt).getTime()
      }
    })
  } catch (error) {
    console.error('加载历史记录失败:', error)
  }
}

// 保存当前对话为历史记录
const saveCurrentChatToHistory = () => {
  // 历史记录由后端保存，这里不需要本地保存
  // 当发送消息时，后端会自动保存到数据库
}

// 加载历史对话
const loadChatFromHistory = (chat: {id: string, title: string, messages: Message[], timestamp: number}) => {
  messages.value = [...chat.messages]
  showHistory.value = false
  scrollToBottom()
}

// 删除历史记录
const deleteHistory = async (id: string) => {
  try {
    // 调用后端API删除历史记录
    // 这里需要添加删除历史记录的API调用
    chatHistory.value = chatHistory.value.filter(chat => chat.id !== id)
  } catch (error) {
    console.error('删除历史记录失败:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

// 清空所有历史记录
const clearAllHistory = () => {
  ElMessageBox.confirm('确定要清空所有历史记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 调用后端API清空历史记录
      // 这里需要添加清空历史记录的API调用
      chatHistory.value = []
    } catch (error) {
      console.error('清空历史记录失败:', error)
      ElMessage.error('清空失败，请稍后重试')
    }
  }).catch(() => {})
}

// 快捷问题列表
const quickQuestions = ref([
  { question: '如何制定减脂计划？', icon: 'TrendCharts' },
  { question: '新手应该从哪些训练开始？', icon: 'Flag' },
  { question: '力量训练和有氧训练如何搭配？', icon: 'Timer' },
  { question: '增肌期间应该怎么吃？', icon: 'Food' }
])

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const handleSend = async () => {
  const content = inputMessage.value.trim()
  if (!content) return
  
  // 添加用户消息
  const userMessage: Message = {
    role: 'user',
    content,
    timestamp: Date.now()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  scrollToBottom()
  
  // 显示加载状态
  loading.value = true
  
  try {
    const response = await aiStore.sendChatMessage(content)
    
    // 添加助手消息
    const assistantMessage: Message = {
      role: 'assistant',
      content: response.answer,
      timestamp: Date.now()
    }
    messages.value.push(assistantMessage)
    
    // 重新加载历史记录
    await loadHistory()
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
    console.error('Chat error:', error)
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const handleQuickQuestion = (question: string) => {
  inputMessage.value = question
  handleSend()
}

const handleFeatureClick = (feature: string) => {
  let question = ''
  switch (feature) {
    case '制定个性化健身计划':
      question = '请帮我制定一个个性化的健身计划'
      break
    case '解答训练相关问题':
      question = '我想了解一些训练相关的问题'
      break
    case '提供饮食营养建议':
      question = '请给我一些饮食营养方面的建议'
      break
    case '运动损伤预防指导':
      question = '如何预防运动损伤'
      break
  }
  if (question) {
    inputMessage.value = question
    handleSend()
  }
}

const handleClearHistory = () => {
  ElMessageBox.confirm('确定要清空聊天记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 保存当前对话到历史记录
    saveCurrentChatToHistory()
    // 清空当前消息
    messages.value = []
  }).catch(() => {})
}

const getRecommendations = async () => {
  if (recommendationsLoading.value) return
  
  recommendationsLoading.value = true
  try {
    const response = await aiApi.getRecommendations()
    recommendations.value = response.data
  } catch (error) {
    ElMessage.error('获取推荐失败，请稍后重试')
    console.error('Get recommendations error:', error)
  } finally {
    recommendationsLoading.value = false
  }
}

const formatTime = (timestamp: number) => {
  const date = new Date(timestamp)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatReasoning = (reasoning: string) => {
  // 简单的文本格式化，实际项目中可能需要更复杂的处理
  return reasoning.replace(/\n/g, '<br>')
}

// 初始化加载历史记录
onMounted(async () => {
  await loadHistory()
})
</script>

<style scoped>
.ai-assistant {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  box-sizing: border-box;
  overflow: hidden;
}

.container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.row {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 聊天容器 */
.chat-container-main {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 0;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.right-container {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
  box-sizing: border-box;
}

/* 网格布局 */
.col-8 {
  flex: 0 0 66.666667%;
  max-width: 66.666667%;
}

.col-4 {
  flex: 0 0 33.333333%;
  max-width: 33.333333%;
}

/* 聊天头部 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #ffffff;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transition: all 0.3s ease;
}

.ai-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ai-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #909399;
}

.status-dot {
  width: 6px;
  height: 6px;
  background: #67c23a;
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.2); }
}

.clear-btn {
  font-size: 14px;
  color: #909399;
  transition: all 0.3s ease;
}

.clear-btn:hover {
  color: #667eea;
}

.history-btn {
  font-size: 14px;
  color: #909399;
  margin-right: 10px;
  transition: all 0.3s ease;
}

.history-btn:hover {
  color: #667eea;
}

/* 历史记录面板 */
.history-panel {
  max-height: 300px;
  overflow-y: auto;
  border-bottom: 1px solid #f0f0f0;
  background: #f9fafb;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: white;
}

.history-header h3 {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.history-list {
  padding: 10px;
}

.empty-history {
  padding: 40px 20px;
  text-align: center;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  margin-bottom: 8px;
  background: white;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.history-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.history-item-content {
  flex: 1;
  min-width: 0;
}

.history-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-time {
  font-size: 12px;
  color: #909399;
  margin: 0 0 4px 0;
}

.history-preview {
  font-size: 12px;
  color: #606266;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.delete-btn {
  color: #909399;
  transition: color 0.3s ease;
}

.delete-btn:hover {
  color: #f56c6c;
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f9fafb;
  position: relative;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 40px 20px;
}

.welcome-message > .el-icon {
  color: #667eea;
  margin-bottom: 20px;
}

.welcome-message h3 {
  font-size: 22px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.welcome-message > p {
  font-size: 14px;
  margin-bottom: 32px;
  color: #606266;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-width: 420px;
  width: 100%;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  font-size: 13px;
  text-align: left;
}

.feature-item:hover {
  background: #f0f5ff;
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.feature-item .el-icon {
  font-size: 18px;
  color: #667eea;
  flex-shrink: 0;
}

.feature-item span {
  color: #606266;
  font-weight: 400;
  line-height: 1.4;
}

/* 消息样式 */
.message {
  margin-bottom: 16px;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.user {
  display: flex;
  justify-content: flex-end;
}

.message.assistant {
  display: flex;
  justify-content: flex-start;
}

.message-content {
  display: flex;
  gap: 10px;
  max-width: 70%;
}

.message.user .message-content {
  flex-direction: row-reverse;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 16px;
}

.message.user .avatar {
  background: #e6f7ff;
  color: #1890ff;
}

.message.assistant .avatar {
  background: #f0f0f0;
  color: #666;
}

.text-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.text {
  padding: 12px 16px;
  border-radius: 12px;
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.5;
  font-size: 14px;
  position: relative;
}

.message.user .text {
  background: #1890ff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.assistant .text {
  background: white;
  color: #303133;
  border: 1px solid #f0f0f0;
  border-bottom-left-radius: 4px;
}

.message-time {
  font-size: 11px;
  color: #909399;
  font-weight: 400;
}

.message.user .message-time {
  text-align: right;
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  border-bottom-left-radius: 4px;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  background: #1890ff;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots span:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0.6);
  }
  40% {
    transform: scale(1);
  }
}

/* 输入区域 */
.input-area {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  background: white;
}

.input-wrapper {
  margin-bottom: 12px;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
  min-height: 80px;
  font-size: 14px;
  line-height: 1.5;
  resize: none;
}

.input-wrapper :deep(.el-textarea__inner:focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.input-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
}

.input-footer .el-button {
  border-radius: 20px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  background: #1890ff;
  border: none;
}

.input-footer .el-button:hover {
  background: #40a9ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
}

.input-footer .el-button:disabled {
  background: #f5f5f5;
  color: #999;
  box-shadow: none;
}

.input-footer .el-button:disabled:hover {
  background: #f5f5f5;
}

/* 右侧卡片通用样式 */
.recommendations-card,
.quick-questions-card,
.features-card {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #f0f0f0;
  background: white;
}

.card-header-simple {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  border-bottom: 1px solid #f0f0f0;
}

.card-header-simple span {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.card-header-simple .el-button {
  border-radius: 16px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 400;
  background: #1890ff;
  border: none;
}

.card-header-simple .el-button:hover {
  background: #40a9ff;
}

/* 个性化建议卡片 */
.recommendations-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 16px;
}

.recommendation-section {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #f0f0f0;
}

.recommendation-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
  padding-bottom: 6px;
  border-bottom: 1px solid #e8e8e8;
}

.section-title .el-icon {
  color: #1890ff;
  font-size: 16px;
}

.recommendation-item {
  background: #f9fafb;
  border-radius: 6px;
  padding: 12px 14px;
  margin-bottom: 8px;
  transition: all 0.2s ease;
  border-left: 3px solid #e8e8e8;
}

.recommendation-item:hover {
  background: #f0f5ff;
  border-left-color: #1890ff;
}

.recommendation-item:last-child {
  margin-bottom: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.item-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.item-reason {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.item-reason .el-icon {
  color: #1890ff;
  font-size: 14px;
  margin-top: 1px;
  flex-shrink: 0;
}

/* 评估说明样式 */
.reasoning-content {
  background: #f0f5ff;
  border-left: 3px solid #1890ff;
}

.reasoning-text {
  line-height: 1.6;
  font-size: 13px;
  color: #303133;
}

.reasoning-text strong {
  color: #1890ff;
  font-weight: 600;
}

.reasoning-heading {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin: 12px 0 8px 0;
  padding-bottom: 6px;
  border-bottom: 1px solid #e8e8e8;
}

.reasoning-subheading {
  font-size: 13px;
  font-weight: 600;
  color: #1890ff;
  margin: 8px 0 6px 0;
}

.reasoning-list {
  margin: 6px 0 12px 0;
  padding-left: 16px;
}

.reasoning-list li {
  margin-bottom: 4px;
  position: relative;
  font-size: 12px;
}

.reasoning-list li::before {
  content: "•";
  color: #1890ff;
  font-weight: bold;
  position: absolute;
  left: -12px;
}

.reasoning-divider {
  margin: 12px 0;
  border: 0;
  border-top: 1px dashed #e8e8e8;
}

/* 快捷问题卡片 */
.questions-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
}

.question-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 14px;
  background: #f9fafb;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #f0f0f0;
}

.question-item:hover {
  background: #f0f5ff;
  border-color: #1890ff;
}

.question-item .el-icon {
  font-size: 14px;
  color: #1890ff;
}

.question-item span {
  flex: 1;
  font-size: 13px;
  color: #606266;
}

.question-item .el-icon:last-child {
  opacity: 0.6;
  font-size: 12px;
}

/* 功能特点卡片 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}

.feature-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 12px;
  background: #f9fafb;
  border-radius: 6px;
  transition: all 0.2s ease;
  border: 1px solid #f0f0f0;
}

.feature-icon-item:hover {
  background: #f0f5ff;
  border-color: #1890ff;
}

.feature-icon-item span {
  font-size: 12px;
  color: #606266;
  font-weight: 400;
  text-align: center;
}

.feature-icon-item .el-icon {
  font-size: 20px;
  color: #1890ff;
}

/* 右侧容器滚动条样式 */
.right-container::-webkit-scrollbar {
  width: 4px;
}

.right-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.right-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.right-container::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

/* 响应式 */
@media (max-width: 768px) {
  .chat-container-main {
    height: calc(100vh - 60px);
  }
  
  .right-container {
    display: none;
  }
  
  .chat-container {
    padding: 16px;
  }
  
  .welcome-content {
    padding: 40px 16px;
  }
  
  .feature-list {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .feature-item {
    padding: 10px 12px;
    font-size: 12px;
  }
  
  .message-content {
    max-width: 85%;
    gap: 8px;
  }
  
  .avatar {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .text {
    padding: 10px 14px;
    font-size: 13px;
  }
  
  .input-area {
    padding: 12px 16px;
  }
  
  .input-wrapper :deep(.el-textarea__inner) {
    min-height: 60px;
    font-size: 13px;
  }
  
  .input-footer .el-button {
    padding: 6px 16px;
    font-size: 13px;
  }
  
  .hint {
    font-size: 11px;
  }
}
</style>