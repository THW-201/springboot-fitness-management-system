<template>
  <div class="plans-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的健康计划</span>
          <el-button type="primary" @click="handleCreate">创建计划</el-button>
        </div>
      </template>

      <el-table :data="planList" stripe border v-loading="loading" empty-text="还没有健康计划">
        <el-table-column prop="planName" label="计划名称" min-width="150" />
        <el-table-column prop="description" label="计划描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="体重" width="150" align="center">
          <template #default="{ row }">
            <div v-if="row.currentWeight || row.targetWeight">
              <div v-if="row.currentWeight">当前: {{ row.currentWeight }}kg</div>
              <div v-if="row.targetWeight">目标: {{ row.targetWeight }}kg</div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="运动时长" width="150" align="center">
          <template #default="{ row }">
            <div v-if="row.currentDurationMinutes !== undefined || row.targetDurationMinutes !== undefined">
              <div v-if="row.currentDurationMinutes !== undefined">当前: {{ (row.currentDurationMinutes / 60).toFixed(1) }}小时/周</div>
              <div v-if="row.targetDurationMinutes !== undefined">目标: {{ (row.targetDurationMinutes / 60).toFixed(1) }}小时/周</div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" align="center" />
        <el-table-column prop="endDate" label="结束日期" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="完成度" width="100" align="center">
          <template #default="{ row }">
            {{ row.completionPercentage }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
      />
    </el-card>
    <!-- 查看计划详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="健康计划详情" width="800px" :close-on-click-modal="false">
      <div v-if="currentPlan" class="plan-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划名称" :span="2">
            {{ currentPlan.planName }}
          </el-descriptions-item>
          <el-descriptions-item label="计划描述" :span="2">
            {{ currentPlan.description || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="当前体重">
            {{ currentPlan.currentWeight ? currentPlan.currentWeight + 'kg' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="目标体重">
            {{ currentPlan.targetWeight ? currentPlan.targetWeight + 'kg' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="当前运动时长">
            {{ currentPlan.currentDurationMinutes !== undefined ? (currentPlan.currentDurationMinutes / 60).toFixed(1) + '小时/周' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="目标运动时长">
            {{ currentPlan.targetDurationMinutes ? (currentPlan.targetDurationMinutes / 60).toFixed(1) + '小时/周' : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">
            {{ currentPlan.startDate }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">
            {{ currentPlan.endDate }}
          </el-descriptions-item>
          <el-descriptions-item label="计划状态">
            <el-tag :type="getStatusType(currentPlan.status)">{{ getStatusText(currentPlan.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="完成百分比">
            <el-progress :percentage="currentPlan.completionPercentage" />
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(currentPlan.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDateTime(currentPlan.updatedAt) }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 打卡区域 -->
        <div class="check-in-section">
          <h3>打卡记录</h3>
          <div class="check-in-calendar">
            <el-calendar v-model="currentDate">
              <template #dateCell="{ date, data }">
                <div class="calendar-cell">
                  <span :class="['date-number', { 'checked': isChecked(date) }]">
                    {{ data.day }}
                  </span>
                  <span v-if="getCheckInDuration(date) > 0" class="duration">
                    {{ getCheckInDuration(date) }}分钟
                  </span>
                </div>
              </template>
            </el-calendar>
          </div>

          <!-- 运动方式输入 -->
          <div class="exercise-type-input" v-if="!isCheckingIn">
            <el-input v-model="selectedExerciseType" placeholder="请输入运动方式" class="exercise-type-input-field" />
          </div>

          <!-- 打卡按钮 -->
          <div class="check-in-button-container">
            <div class="check-in-button" @click="handleCheckIn">
              <div class="button-content">
                <i class="el-icon-time"></i>
                <span>{{ isCheckingIn ? '结束打卡' : '开始打卡' }}</span>
                <div v-if="isCheckingIn" class="timer">{{ formatTime(elapsedTime) }}</div>
              </div>
            </div>
          </div>

          <!-- 打卡记录列表 -->
          <div class="check-in-list">
            <h4>近期打卡记录</h4>
            <el-table :data="checkInList" stripe border size="small">
              <el-table-column prop="checkDate" label="打卡日期" width="120" />
              <el-table-column prop="exerciseType" label="运动方式" width="100" />
              <el-table-column prop="caloriesBurned" label="消耗卡路里" width="100">
                <template #default="{ row }">
                  {{ row.caloriesBurned ? row.caloriesBurned + ' kcal' : '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="checkInTime" label="开始时间" width="150" />
              <el-table-column prop="checkOutTime" label="结束时间" width="150" />
              <el-table-column prop="durationMinutes" label="打卡时长" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">
                    {{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 创建健康计划对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建健康计划" width="600px" :close-on-click-modal="false">
      <el-form :model="createForm" label-width="120px" :rules="formRules" ref="createFormRef">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="createForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入计划描述" />
        </el-form-item>
        <el-form-item label="当前体重" prop="currentWeight">
          <el-input-number v-model="createForm.currentWeight" :min="0" :precision="1" placeholder="kg" />
        </el-form-item>
        <el-form-item label="目标体重" prop="targetWeight">
          <el-input-number v-model="createForm.targetWeight" :min="0" :precision="1" placeholder="kg" />
        </el-form-item>
        <el-form-item label="目标运动时长" prop="targetDurationHours">
          <el-input-number v-model="createForm.targetDurationHours" :min="0" :step="0.5" :precision="1" placeholder="小时/周" />
          <span class="form-tip">小时/周</span>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="createForm.startDate" type="date" placeholder="选择开始日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="createForm.endDate" type="date" placeholder="选择结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateSubmit" :loading="submitLoading">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { healthPlanApi } from '@/api/healthPlan'
import { planCheckInApi } from '@/api/planCheckIn'
import type { HealthPlan, PlanCheckIn } from '@/types'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const detailDialogVisible = ref(false)
const createDialogVisible = ref(false)
const planList = ref<HealthPlan[]>([])
const currentPlan = ref<HealthPlan | null>(null)
const createFormRef = ref<any>(null)

// 打卡相关状态
const currentDate = ref(new Date())
const checkInList = ref<PlanCheckIn[]>([])
const isCheckingIn = ref(false)
const currentCheckIn = ref<PlanCheckIn | null>(null)
const elapsedTime = ref(0)
const selectedExerciseType = ref('')
let timer: number | null = null

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 创建表单
const createForm = reactive({
  planName: '',
  description: '',
  currentWeight: undefined as number | undefined,
  targetWeight: undefined as number | undefined,
  targetDurationHours: undefined as number | undefined,
  startDate: '',
  endDate: ''
})

// 表单验证规则
const formRules = {
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    ACTIVE: 'success',
    COMPLETED: 'info',
    ABANDONED: 'warning'
  }
  return map[status] || ''
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    ACTIVE: '进行中',
    COMPLETED: '已完成',
    ABANDONED: '已放弃'
  }
  return map[status] || status
}

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const formatTime = (seconds: number) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const fetchPlanList = async () => {
  loading.value = true
  try {
    const studentId = userStore.userId
    if (!studentId) {
      ElMessage.error('未获取到用户信息')
      return
    }
    const res = await healthPlanApi.getHealthPlans({ 
      studentId,
      current: pagination.current,
      size: pagination.size
    })
    planList.value = res.data.records
    pagination.total = res.data.total || 0
  } catch (error: any) {
    ElMessage.error(error.message || '获取健康计划失败')
  } finally {
    loading.value = false
  }
}

const fetchCheckInList = async (planId: number) => {
  try {
    const res = await planCheckInApi.getCheckInsByPlanId(planId)
    checkInList.value = res.data
  } catch (error: any) {
    ElMessage.error(error.message || '获取打卡记录失败')
  }
}

const fetchTodayCheckIn = async (planId: number, studentId: number) => {
  try {
    const res = await planCheckInApi.getTodayCheckIn(planId, studentId)
    if (res.data && res.data.status === 'IN_PROGRESS') {
      currentCheckIn.value = res.data
      isCheckingIn.value = true
      startTimer()
    }
  } catch (error: any) {
    // 没有打卡记录是正常的，不需要报错
  }
}

const startTimer = () => {
  if (currentCheckIn.value) {
    const checkInTime = dayjs(currentCheckIn.value.checkInTime)
    const now = dayjs()
    elapsedTime.value = now.diff(checkInTime, 'second')
    
    timer = window.setInterval(() => {
      elapsedTime.value++
    }, 1000)
  }
}

const stopTimer = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
  elapsedTime.value = 0
}

const handleCheckIn = async () => {
  if (!currentPlan.value) return
  
  const studentId = userStore.userId
  if (!studentId) {
    ElMessage.error('未获取到用户信息')
    return
  }

  if (isCheckingIn.value) {
    // 结束打卡
    if (currentCheckIn.value) {
      try {
        await planCheckInApi.endCheckIn(currentCheckIn.value.id)
        ElMessage.success('打卡结束')
        isCheckingIn.value = false
        stopTimer()
        fetchCheckInList(currentPlan.value.id)
        fetchPlanList() // 刷新计划列表以更新完成度
      } catch (error: any) {
        ElMessage.error(error.message || '结束打卡失败')
      }
    }
  } else {
    // 开始打卡
    if (!selectedExerciseType.value) {
      ElMessage.error('请输入运动方式')
      return
    }
    try {
      const res = await planCheckInApi.startCheckIn(currentPlan.value.id, studentId, selectedExerciseType.value)
      currentCheckIn.value = res.data
      isCheckingIn.value = true
      startTimer()
      ElMessage.success('开始打卡')
    } catch (error: any) {
      ElMessage.error(error.message || '开始打卡失败')
    }
  }
}

const isChecked = (date: Date) => {
  const dateStr = dayjs(date).format('YYYY-MM-DD')
  return checkInList.value.some(checkIn => checkIn.checkDate === dateStr)
}

const getCheckInDuration = (date: Date) => {
  const dateStr = dayjs(date).format('YYYY-MM-DD')
  const checkIn = checkInList.value.find(checkIn => checkIn.checkDate === dateStr)
  return checkIn?.durationMinutes || 0
}

watch(() => pagination.current, () => {
  fetchPlanList()
})

watch(() => pagination.size, () => {
  pagination.current = 1
  fetchPlanList()
})

const handleViewDetail = async (row: HealthPlan) => {
  currentPlan.value = row
  detailDialogVisible.value = true
  
  // 重置打卡状态
  isCheckingIn.value = false
  currentCheckIn.value = null
  selectedExerciseType.value = ''
  stopTimer()
  
  // 获取打卡记录
  await fetchCheckInList(row.id)
  
  // 检查今天是否有进行中的打卡
  const studentId = userStore.userId
  if (studentId) {
    await fetchTodayCheckIn(row.id, studentId)
  }
}

const handleCreate = () => {
  // 重置表单
  createForm.planName = ''
  createForm.description = ''
  createForm.currentWeight = undefined
  createForm.targetWeight = undefined
  createForm.targetDurationMinutes = undefined
  createForm.startDate = ''
  createForm.endDate = ''
  createDialogVisible.value = true
}

const handleCreateSubmit = async () => {
  if (!createFormRef.value) return
  
  await createFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await healthPlanApi.createHealthPlan({
        planName: createForm.planName,
        description: createForm.description,
        currentWeight: createForm.currentWeight,
        targetWeight: createForm.targetWeight,
        targetDurationMinutes: createForm.targetDurationHours ? Math.round(createForm.targetDurationHours * 60) : undefined,
        startDate: createForm.startDate,
        endDate: createForm.endDate
      })
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      fetchPlanList()
    } catch (error: any) {
      ElMessage.error(error.message || '创建失败')
    } finally {
      submitLoading.value = false
    }
  })
}

onBeforeUnmount(() => {
  stopTimer()
})

onMounted(() => {
  fetchPlanList()
})
</script>

<style scoped>
.plans-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.form-tip {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
}

/* 打卡区域样式 */
.check-in-section {
  margin-top: 30px;
}

.check-in-section h3 {
  margin-bottom: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.check-in-calendar {
  margin-bottom: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 10px;
  background: #fff;
}

:deep(.el-calendar-day) {
  height: 80px !important;
  padding: 5px;
}

.calendar-cell {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s;
}

.calendar-cell:hover {
  background: #f5f7fa;
}

.date-number {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
}

.date-number.checked {
  color: #409eff;
  font-weight: bold;
}

.duration {
  font-size: 12px;
  color: #67c23a;
}

/* 打卡按钮样式 */
.exercise-type-input {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.exercise-type-input-field {
  width: 200px;
}

.check-in-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.check-in-button {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.check-in-button:hover {
  transform: scale(1.05);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
}

.check-in-button:active {
  transform: scale(0.95);
}

.button-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: white;
}

.button-content i {
  font-size: 32px;
  margin-bottom: 10px;
}

.button-content span {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.timer {
  font-size: 14px;
  font-family: monospace;
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
}

/* 打卡记录列表 */
.check-in-list {
  margin-top: 30px;
}

.check-in-list h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* 响应式 */
@media (max-width: 768px) {
  .plans-page {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .search-bar {
    width: 100%;
    flex-direction: column;
    gap: 10px;
  }
  
  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
  }
  
  .search-bar .el-button {
    width: 100%;
  }
  
  .pagination {
    justify-content: center;
  }
  
  .pagination :deep(.el-pagination__sizes) {
    display: none;
  }
  
  /* 表格移动端优化 */
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 8px 5px;
  }
  
  :deep(.el-table .cell) {
    padding: 0 5px;
  }
  
  /* 对话框移动端优化 */
  :deep(.el-dialog) {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  
  :deep(.el-dialog__body) {
    padding: 15px;
  }
  
  :deep(.el-form-item) {
    margin-bottom: 15px;
  }
  
  /* 描述列表移动端优化 */
  :deep(.el-descriptions__body) {
    font-size: 13px;
  }
  
  :deep(.el-descriptions__label) {
    width: 80px !important;
  }
  
  /* 打卡按钮移动端优化 */
  .check-in-button {
    width: 120px;
    height: 120px;
  }
  
  .button-content i {
    font-size: 24px;
  }
  
  .button-content span {
    font-size: 16px;
  }
  
  /* 日历移动端优化 */
  :deep(.el-calendar-day) {
    height: 60px !important;
  }
  
  .date-number {
    font-size: 14px;
  }
  
  .duration {
    font-size: 10px;
  }
}
</style>
