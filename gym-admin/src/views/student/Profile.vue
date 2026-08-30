<template>
  <div class="profile-page">
    <div class="container">
      <h2 class="page-title">个人中心</h2>
      
      <!-- 个人信息 -->
      <section class="profile-section">
        <h3 class="section-title">个人信息</h3>
        <div class="profile-info">
          <div class="avatar-section">
            <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :data="{ folder: 'avatar' }"
              :show-file-list="false"
              :on-success="handleAvatarUploadSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <el-avatar :size="100" :src="avatarUrl" class="avatar-clickable">
                <el-icon>
                  <UserFilled />
                </el-icon>
              </el-avatar>
              <div class="avatar-overlay">
                <el-icon><Upload /></el-icon>
                <span>点击上传</span>
              </div>
            </el-upload>
            <h4>{{ userInfo?.realName || userInfo?.username }}</h4>
            <p class="user-role">{{ getRoleText(userInfo?.role) }}</p>
          </div>
          <div class="info-details">
            <div class="info-item" v-if="!isEditing">
              <span class="info-label">用户名：</span>
              <span class="info-value">{{ userInfo?.username }}</span>
            </div>
            <div class="info-item" v-if="isEditing">
              <span class="info-label">用户名：</span>
              <span class="info-value">{{ userInfo?.username }} <span class="text-gray">(不可修改)</span></span>
            </div>
            
            <div class="info-item" v-if="!isEditing">
              <span class="info-label">邮箱：</span>
              <span class="info-value">{{ userInfo?.email }}</span>
            </div>
            <div class="info-item" v-if="isEditing">
              <span class="info-label">邮箱：</span>
              <el-input v-model="editForm.email" placeholder="请输入邮箱" size="small" />
            </div>
            
            <div class="info-item" v-if="!isEditing">
              <span class="info-label">电话：</span>
              <span class="info-value">{{ userInfo?.phone || '未设置' }}</span>
            </div>
            <div class="info-item" v-if="isEditing">
              <span class="info-label">电话：</span>
              <el-input v-model="editForm.phone" placeholder="请输入电话" size="small" />
            </div>
            
            <div class="info-item" v-if="studentProfile">
              <span class="info-label">学号：</span>
              <span class="info-value">{{ studentProfile.studentNumber }}</span>
            </div>
            
            <div class="info-item" v-if="studentProfile?.age && !isEditing">
              <span class="info-label">年龄：</span>
              <span class="info-value">{{ studentProfile.age }}岁</span>
            </div>
            
            <div class="info-item" v-if="studentProfile?.height && !isEditing">
              <span class="info-label">身高：</span>
              <span class="info-value">{{ studentProfile.height }}cm</span>
            </div>
            
            <div class="info-item" v-if="studentProfile?.weight && !isEditing">
              <span class="info-label">体重：</span>
              <span class="info-value">{{ studentProfile.weight }}kg</span>
            </div>
            
            <div class="info-item" v-if="isEditing">
              <span class="info-label">年龄：</span>
              <el-input v-model="editForm.age" placeholder="请输入年龄" size="small" type="number" />
            </div>
            
            <div class="info-item" v-if="isEditing">
              <span class="info-label">身高：</span>
              <el-input v-model="editForm.height" placeholder="请输入身高(cm)" size="small" type="number" step="0.1" />
            </div>
            
            <div class="info-item" v-if="isEditing">
              <span class="info-label">体重：</span>
              <el-input v-model="editForm.weight" placeholder="请输入体重(kg)" size="small" type="number" step="0.1" />
            </div>
            
            <div class="info-item" v-if="studentProfile?.coachName">
              <span class="info-label">教练：</span>
              <span class="info-value">{{ coachInfo.realName }}</span>
            </div>
            <div class="info-item" v-if="studentProfile?.coachName">
              <span class="info-label">专业领域：</span>
              <span class="info-value">{{ coachInfo.coachProfile?.specialization || '--' }}</span>
            </div>
            <div class="info-item" v-if="studentProfile?.coachName">
              <span class="info-label">资格证书：</span>
              <span class="info-value">{{ coachInfo.coachProfile?.certification || '--' }}</span>
            </div>
            
            <div class="info-actions" v-if="isEditing">
              <el-button type="primary" size="small" @click="saveChanges" :loading="saving">保存</el-button>
              <el-button size="small" @click="cancelEdit">取消</el-button>
            </div>
            <div class="info-actions" v-else>
              <el-button type="primary" size="small" @click="startEdit">编辑资料</el-button>
            </div>
          </div>
        </div>
      </section>

      <!-- 健身数据 -->
      <section class="profile-section">
        <h3 class="section-title">健身数据</h3>
        <div class="stats-grid">
          <div class="stat-card">
            <el-icon class="stat-icon"><Timer /></el-icon>
            <div class="stat-info">
              <h4>{{ totalMinutes }}</h4>
              <p>总运动分钟</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><TrendCharts /></el-icon>
            <div class="stat-info">
              <h4>{{ totalCalories }}</h4>
              <p>总消耗卡路里</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon"><Check /></el-icon>
            <div class="stat-info">
              <h4>{{ totalCheckIns }}</h4>
              <p>总打卡次数</p>
            </div>
          </div>
          <!-- <div class="stat-card">
            <el-icon class="stat-icon"><Star /></el-icon>
            <div class="stat-info">
              <h4>{{ activityScore }}</h4>
              <p>活跃度评分</p>
            </div>
          </div> -->
        </div>
      </section>

      <!-- 最近活动 -->
      <!-- <section class="profile-section">
        <h3 class="section-title">最近活动</h3>
        <el-table :data="recentActivities" style="width: 100%">
          <el-table-column prop="type" label="活动类型" width="120" />
          <el-table-column prop="content" label="内容" />
          <el-table-column prop="time" label="时间" width="180" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </section> -->

      <!-- 签到记录 -->
      <section class="profile-section">
        <h3 class="section-title">签到记录</h3>
        <el-table :data="checkInRecords" v-loading="checkInLoading" style="width: 100%">
          <el-table-column prop="location" label="位置" />
          <el-table-column prop="checkInTime" label="签到时间">
            <template #default="{ row }">
              {{ formatDateTime(row.checkInTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="checkOutTime" label="签退时间">
            <template #default="{ row }">
              {{ row.checkOutTime ? formatDateTime(row.checkOutTime) : '-' }}
            </template>
          </el-table-column>
          <!-- <el-table-column prop="durationMinutes" label="时长(分钟)" align="center">
            <template #default="{ row }">
              {{ row.durationMinutes || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="caloriesBurned" label="消耗卡路里" align="center">
            <template #default="{ row }">
              {{ row.caloriesBurned ? row.caloriesBurned.toFixed(1) : '-' }}
            </template>
          </el-table-column> -->
        </el-table>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api'
import { checkInApi } from '@/api/checkIn'
import { statisticsApi } from '@/api/statistics'

interface CochInfo {
  realName?: string
  username?: string
  avatarUrl?: string
  coachProfile?:{
    specialization?: string
    certification?: string
  }
}

const userStore = useUserStore()
const coachInfo = ref<CochInfo>({})
const userInfo = computed(() => userStore.userInfo)
const studentProfile = computed(() => userInfo.value?.studentProfile)
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload")
const baseUrl = import.meta.env.VITE_APP_BASE_API
const avatarUrl = computed(() => {
  if (!userInfo.value?.avatarUrl) return ''
  return userInfo.value.avatarUrl.startsWith('http') 
    ? userInfo.value.avatarUrl 
    : baseUrl + userInfo.value.avatarUrl
})
const checkInRecords = ref<any[]>([])
const checkInLoading = ref(false)

// 编辑状态
const isEditing = ref(false)
const saving = ref(false)
const editForm = ref({
  email: '',
  phone: '',
  age: '',
  height: '',
  weight: ''
})

// 健身数据统计
const totalMinutes = ref(0)
const totalCalories = ref(0)
const totalCheckIns = ref(0)

const getCoachInfo = async () => {
  try {
    const res = await userApi.getUserById(12)
    console.log('获取教练信息成功', res)
    coachInfo.value = res.data
  } catch (error: any) {
    console.log('获取教练信息失败', error.message)
  }
}

// 加载学生个人统计数据
const loadPersonalStatistics = async () => {
  try {
    const res = await statisticsApi.getPersonalHealthData()
    console.log("个人健身数据", res)
    totalMinutes.value = res.data.totalExerciseMinutes
    totalCalories.value = res.data.totalCaloriesBurned
    totalCheckIns.value = res.data.totalExerciseCount
  } catch (error: any) {
    console.log('获取个人健身数据失败', error.message)
  } finally {
  }
}

const loadCheckInRecords = async () => {
  try {
    checkInLoading.value = true
    const res = await checkInApi.getMyCheckIns()
    if (res.data) {
      checkInRecords.value = Array.isArray(res.data) ? res.data : []
    }
  } catch (error: any) {
    console.log('获取签到记录失败', error.message)
  } finally {
    checkInLoading.value = false
  }
}

const beforeAvatarUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG 图片！')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
  }
  return isJpgOrPng && isLt2M
}

const handleAvatarUploadSuccess = async (response: any) => {
  if (response.code === 200) {
    const newAvatarUrl = response.data
    try {
      if (userInfo.value?.id) {
        await userApi.updateUser(userInfo.value.id, { avatarUrl: newAvatarUrl })
        await userStore.fetchCurrentUser()
        ElMessage.success('头像上传成功')
      }
    } catch (error: any) {
      ElMessage.error(error.message || '更新头像失败')
    }
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const formatDateTime = (dateTime: string) => {
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm')
}

// 开始编辑
const startEdit = () => {
  // 填充表单数据
  editForm.value.email = userInfo.value?.email || ''
  editForm.value.phone = userInfo.value?.phone || ''
  editForm.value.age = studentProfile.value?.age ? studentProfile.value.age.toString() : ''
  editForm.value.height = studentProfile.value?.height ? studentProfile.value.height.toString() : ''
  editForm.value.weight = studentProfile.value?.weight ? studentProfile.value.weight.toString() : ''
  isEditing.value = true
}

// 保存修改
const saveChanges = async () => {
  if (!userInfo.value?.id) return
  
  saving.value = true
  try {
    // 更新基本用户信息
    await userApi.updateUser(userInfo.value.id, {
      email: editForm.value.email,
      phone: editForm.value.phone
    })
    
    // 更新学生档案信息
    await userApi.updateStudentProfile(userInfo.value.id, {
      age: editForm.value.age ? Number(editForm.value.age) : undefined,
      height: editForm.value.height ? Number(editForm.value.height) : undefined,
      weight: editForm.value.weight ? Number(editForm.value.weight) : undefined
    })
    
    // 重新获取用户信息
    await userStore.fetchCurrentUser()
    ElMessage.success('资料更新成功')
    isEditing.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '更新失败，请重试')
  } finally {
    saving.value = false
  }
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  // 重置表单
  editForm.value.email = ''
  editForm.value.phone = ''
  editForm.value.age = ''
  editForm.value.height = ''
  editForm.value.weight = ''
}

// 获取角色文本
const getRoleText = (role?: string) => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    COACH: '教练',
    STUDENT: '学生'
  }
  return map[role || ''] || '未知'
}

onMounted(() => {
  getCoachInfo()
  loadPersonalStatistics()
  loadCheckInRecords()
  console.log('个人中心页面加载完成')
})
</script>

<style scoped>
.profile-page {
  background-color: #f5f7fa;
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
  text-align: center;
}

.profile-section {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

/* 个人信息 */
.profile-info {
  display: flex;
  gap: 40px;
  align-items: center;
}

.avatar-section {
  text-align: center;
  position: relative;
}

.avatar-uploader {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-clickable {
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
  box-sizing: border-box;
}

.avatar-overlay .el-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.avatar-section h4 {
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.user-role {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.info-details {
  flex: 1;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  align-items: center;
}

.info-label {
  width: 80px;
  font-size: 14px;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #333;
}

.text-gray {
  color: #999;
  font-size: 12px;
}

.info-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.info-item .el-input {
  width: 300px;
}

/* 健身数据 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
  color: #409eff;
}

.stat-info h4 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-info p {
  font-size: 14px;
  color: #666;
}

/* 最近活动 */
/* 健身计划 */
.plans-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.plan-card {
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
}

.plan-card h4 {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.plan-description {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.plan-progress {
  margin-bottom: 16px;
}

.progress-text {
  font-size: 12px;
  color: #666;
  display: block;
  text-align: right;
  margin-top: 8px;
}

.plan-info p {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 打卡记录 */

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-info {
    flex-direction: column;
    text-align: center;
  }

  .info-item {
    justify-content: center;
  }

  .stats-grid,
  .plans-list {
    grid-template-columns: 1fr;
  }
}
</style>