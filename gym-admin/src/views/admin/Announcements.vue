<template>
  <div class="announcements-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            发布公告
          </el-button>
        </div>
      </template>

      <!-- 公告列表 -->
      <el-table v-loading="loading" :data="announcements" style="width: 100%">
        <el-table-column prop="title" label="公告标题" min-width="200">
          <template #default="scope">
            <el-link type="primary" @click="handleView(scope.row)">
              {{ scope.row.title }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="100">
          <template #default="scope">
            <el-tag :type="getPriorityTagType(scope.row.priority)">
              {{ getPriorityText(scope.row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ scope.row.status === 'ACTIVE' ? '活跃' : '失效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="查看次数" width="100" />
        <el-table-column prop="createdByName" label="发布人" width="120" />
        <el-table-column prop="createdAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" :icon="Edit">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)" :icon="Delete">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 发布/编辑公告对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="100px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入公告内容"
          />
        </el-form-item>

        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="SYSTEM">系统公告（所有用户可见）</el-radio>
            <el-radio label="ROLE">角色公告（指定角色可见）</el-radio>
            <el-radio label="PERSONAL">个人通知（指定用户可见）</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 角色选择 -->
        <el-form-item v-if="form.type === 'ROLE'" label="目标角色">
          <el-checkbox-group v-model="form.targetRoles">
            <el-checkbox label="ADMIN">管理员</el-checkbox>
            <el-checkbox label="COACH">教练</el-checkbox>
            <el-checkbox label="STUDENT">学生</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <!-- 用户选择 -->
        <el-form-item v-if="form.type === 'PERSONAL'" label="目标用户">
          <el-select
            v-model="form.targetUserId"
            placeholder="请选择用户"
            style="width: 100%"
          >
            <el-option
              v-for="user in users"
              :key="user.id"
              :label="user.realName || user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width: 100%">
            <el-option label="高" value="HIGH" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="低" value="LOW" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="失效" value="INACTIVE" />
          </el-select>
        </el-form-item>

        <el-form-item label="过期时间">
          <el-date-picker
            v-model="form.expireAt"
            type="datetime"
            placeholder="选择过期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="800px"
      destroy-on-close
    >
      <div class="announcement-detail">
        <h2 class="detail-title">{{ viewAnnouncement?.title }}</h2>
        <div class="detail-meta">
          <span class="meta-item">
            <el-tag :type="getTypeTagType(viewAnnouncement?.type)">
              {{ getTypeText(viewAnnouncement?.type) }}
            </el-tag>
          </span>
          <span class="meta-item">
            <el-tag :type="getPriorityTagType(viewAnnouncement?.priority)">
              {{ getPriorityText(viewAnnouncement?.priority) }}
            </el-tag>
          </span>
          <span class="meta-item">
            发布人：{{ viewAnnouncement?.createdByName }}
          </span>
          <span class="meta-item">
            发布时间：{{ viewAnnouncement?.createdAt }}
          </span>
          <span class="meta-item">
            查看次数：{{ viewAnnouncement?.viewCount }}
          </span>
        </div>
        <div class="detail-content" v-html="viewAnnouncement?.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { announcementApi, userApi } from '@/api'

// 状态
const loading = ref(false)
const announcements = ref<any[]>([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const formRef = ref()
const currentId = ref<number | null>(null)

// 表单数据
const form = ref({
  title: '',
  content: '',
  type: 'SYSTEM',
  targetRoles: [] as string[],
  targetUserId: null as number | null,
  priority: 'MEDIUM',
  status: 'ACTIVE',
  expireAt: null as any
})

// 查看的公告
const viewAnnouncement = ref<any>(null)

// 验证规则
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }]
}

// 用户列表
const users = ref<any[]>([])

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

// 优先级文本
const getPriorityText = (priority: string) => {
  const priorityMap: Record<string, string> = {
    HIGH: '高',
    MEDIUM: '中',
    LOW: '低'
  }
  return priorityMap[priority] || priority
}

// 优先级标签类型
const getPriorityTagType = (priority: string) => {
  const priorityMap: Record<string, string> = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'success'
  }
  return priorityMap[priority] || 'info'
}

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    console.log('开始加载公告列表...')
    const response = await announcementApi.getAllAnnouncements()
    console.log('公告列表加载成功:', response)
    announcements.value = response.data.data
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
    console.log('公告列表加载完成')
  }
}

// 加载用户列表
const loadUsers = async () => {
  try {
    console.log('开始加载用户列表...')
    const response = await userApi.getUsers({ current: 1, size: 100 })
    console.log('用户列表加载成功:', response)
    users.value = response.data.records
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 处理创建公告
const handleCreate = () => {
  currentId.value = null
  dialogTitle.value = '发布公告'
  form.value = {
    title: '',
    content: '',
    type: 'SYSTEM',
    targetRoles: [],
    targetUserId: null,
    priority: 'MEDIUM',
    status: 'ACTIVE',
    expireAt: null
  }
  dialogVisible.value = true
}

// 处理编辑公告
const handleEdit = (row: any) => {
  currentId.value = row.id
  dialogTitle.value = '编辑公告'
  form.value = {
    title: row.title,
    content: row.content,
    type: row.type,
    targetRoles: row.targetRoles || [],
    targetUserId: row.targetUserId,
    priority: row.priority,
    status: row.status,
    expireAt: row.expireAt
  }
  dialogVisible.value = true
}

// 处理查看公告
const handleView = async (row: any) => {
  try {
    const response = await announcementApi.getAnnouncementById(row.id)
    viewAnnouncement.value = response.data.data
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取公告详情失败')
  }
}

// 处理删除公告
const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个公告吗？',
      '删除公告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await announcementApi.deleteAnnouncement(id)
    ElMessage.success('删除成功')
    loadAnnouncements()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (currentId.value) {
          await announcementApi.updateAnnouncement(currentId.value, form.value)
          ElMessage.success('更新成功')
        } else {
          await announcementApi.createAnnouncement(form.value)
          ElMessage.success('发布成功')
        }
        dialogVisible.value = false
        loadAnnouncements()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

// 生命周期
onMounted(() => {
  loadAnnouncements()
  loadUsers()
})
</script>

<style scoped>
.announcements-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.announcement-detail {
  padding: 20px;
}

.detail-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
