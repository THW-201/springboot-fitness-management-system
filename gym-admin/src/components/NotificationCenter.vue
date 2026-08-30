<template>
  <div class="notification-center">
    <el-dropdown trigger="click" @command="handleCommand">
      <div class="notification-trigger">
        <el-badge :value="unreadCount" :hidden="unreadCount === 0">
          <el-icon class="notification-icon"><Bell /></el-icon>
        </el-badge>
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <div class="notification-header">
            <span>通知中心</span>
            <el-button type="text" size="small" @click="markAllAsRead">
              全部已读
            </el-button>
          </div>
          <div class="notification-list" v-if="notifications.length > 0">
            <el-dropdown-item
              v-for="notification in notifications"
              :key="notification.id"
              :command="notification"
              class="notification-item"
              :class="{ 'unread': !notification.read }"
            >
              <div class="notification-content">
                <div class="notification-title">{{ notification.title }}</div>
                <div class="notification-meta">
                  <span>{{ notification.createdAt }}</span>
                  <el-tag :type="getTypeTagType(notification.type)" size="small">
                    {{ getTypeText(notification.type) }}
                  </el-tag>
                </div>
              </div>
            </el-dropdown-item>
          </div>
          <div v-else class="empty-notification">
            暂无通知
          </div>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="通知详情"
      width="600px"
      destroy-on-close
    >
      <div class="notification-detail">
        <h2 class="detail-title">{{ currentNotification?.title }}</h2>
        <div class="detail-meta">
          <span class="meta-item">
            <el-tag :type="getTypeTagType(currentNotification?.type)">
              {{ getTypeText(currentNotification?.type) }}
            </el-tag>
          </span>
          <span class="meta-item">
            发布人：{{ currentNotification?.createdByName }}
          </span>
          <span class="meta-item">
            发布时间：{{ currentNotification?.createdAt }}
          </span>
        </div>
        <div class="detail-content" v-html="currentNotification?.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { announcementApi } from '@/api'

// 状态
const notifications = ref<any[]>([])
const unreadCount = ref(0)
const detailDialogVisible = ref(false)
const currentNotification = ref<any>(null)

// 类型文本
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    SYSTEM: '系统公告',
    ROLE: '角色公告',
    PERSONAL: '个人通知'
  }
  return typeMap[type] || type
}

// 类型标签类型
const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    SYSTEM: 'primary',
    ROLE: 'success',
    PERSONAL: 'warning'
  }
  return typeMap[type] || 'info'
}

// 加载通知
const loadNotifications = async () => {
  try {
    const response = await announcementApi.getUserAnnouncements()
    console.log('getUserAnnouncements response:', response)
    notifications.value = response.data.data || []
    console.log('notifications:', notifications.value)
    // 计算未读数量（这里简化处理，实际应该有已读状态）
    unreadCount.value = notifications.value.length
  } catch (error) {
    console.error('获取通知失败', error)
  }
}

// 处理通知点击
const handleCommand = async (command: any) => {
  currentNotification.value = command
  detailDialogVisible.value = true
  // 标记为已读
  try {
    await announcementApi.getAnnouncementById(command.id)
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  } catch (error) {
    console.error('标记已读失败', error)
  }
}

// 标记所有为已读
const markAllAsRead = async () => {
  try {
    for (const notification of notifications.value) {
      await announcementApi.getAnnouncementById(notification.id)
    }
    unreadCount.value = 0
    ElMessage.success('已标记所有通知为已读')
  } catch (error) {
    ElMessage.error('标记已读失败')
  }
}

// 生命周期
onMounted(() => {
  loadNotifications()
  // 每30秒刷新一次通知
  setInterval(loadNotifications, 30000)
})
</script>

<style scoped>
.notification-center {
  position: relative;
}

.notification-trigger {
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.notification-trigger:hover {
  background-color: #f5f7fa;
}

.notification-icon {
  font-size: 20px;
  color: #606266;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-item {
  padding: 12px 16px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notification-item:hover {
  background-color: #f5f7fa;
}

.notification-item.unread {
  background-color: #f0f9ff;
}

.notification-content {
  width: 100%;
}

.notification-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  color: #303133;
}

.notification-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.empty-notification {
  padding: 20px;
  text-align: center;
  color: #909399;
}

.notification-detail {
  padding: 20px;
}

.detail-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
}

.detail-content {
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
}
</style>
