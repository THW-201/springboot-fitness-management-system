<template>
  <div class="checkins-management">
    <div class="page-header">
      <h2>签到管理</h2>
      <p>管理学生的签到记录，查看统计信息</p>
    </div>

    <div class="content-container">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ stats.totalCheckIns }}</h3>
            <p>总签到次数</p>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ stats.completedCheckIns }}</h3>
            <p>已完成签到</p>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ stats.pendingCheckIns }}</h3>
            <p>未签退</p>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ stats.avgDuration }}</h3>
            <p>平均时长(分钟)</p>
          </div>
        </el-card>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="课程">
            <el-select v-model="searchForm.courseId" placeholder="选择课程">
              <el-option label="全部" value="" />
              <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="学生">
            <el-select v-model="searchForm.studentId" placeholder="选择学生">
              <el-option label="全部" value="" />
              <el-option v-for="student in studentList" :key="student.id" :label="student.realName || student.username" :value="student.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="选择状态">
              <el-option label="全部" value="" />
              <el-option label="已签到" value="CHECKED_IN" />
              <el-option label="已签退" value="CHECKED_OUT" />
            </el-select>
          </el-form-item>
          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="loadCheckIns" :icon="Refresh">刷新</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 签到记录列表 -->
      <el-card class="checkins-list-card">
        <template #header>
          <div class="card-header">
            <span>签到记录列表</span>
          </div>
        </template>
        <el-table :data="checkInsList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="签到ID" width="100" />
          <el-table-column prop="studentId" label="学生ID" width="100" />
          <el-table-column label="学生姓名" width="120">
            <template #default="scope">
              {{ scope.row.studentName || scope.row.student?.realName || scope.row.student?.username || '未知' }}
            </template>
          </el-table-column>
          <el-table-column label="课程名称" width="150">
            <template #default="scope">
              {{ scope.row.courseName || scope.row.course?.name || '未知' }}
            </template>
          </el-table-column>
          <el-table-column prop="checkInTime" label="签到时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.checkInTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="checkOutTime" label="签退时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.checkOutTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="durationMinutes" label="时长(分钟)" width="100" />
          <el-table-column prop="caloriesBurned" label="消耗卡路里" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="handleViewDetail(scope.row)"
              >
                查看详情
              </el-button>
              <el-button
                type="warning"
                size="small"
                @click="handleCheckOut(scope.row)"
                v-if="scope.row.status === 'CHECKED_IN'"
              >
                签退
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="pagination.total"
          />
        </div>
      </el-card>
    </div>

    <!-- 签到详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="签到详情"
      width="600px"
    >
      <div class="checkin-detail" v-if="selectedCheckIn">
        <el-descriptions :column="2">
          <el-descriptions-item label="签到ID">{{ selectedCheckIn.id }}</el-descriptions-item>
          <el-descriptions-item label="学生ID">{{ selectedCheckIn.studentId }}</el-descriptions-item>
          <el-descriptions-item label="学生姓名">{{ selectedCheckIn.studentName || selectedCheckIn.student?.realName || selectedCheckIn.student?.username || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="课程ID">{{ selectedCheckIn.courseId }}</el-descriptions-item>
          <el-descriptions-item label="课程名称">{{ selectedCheckIn.courseName || selectedCheckIn.course?.name || '未知' }}</el-descriptions-item>
          <el-descriptions-item label="签到时间">{{ formatDateTime(selectedCheckIn.checkInTime) }}</el-descriptions-item>
          <el-descriptions-item label="签退时间">{{ formatDateTime(selectedCheckIn.checkOutTime) }}</el-descriptions-item>
          <el-descriptions-item label="时长(分钟)">{{ selectedCheckIn.durationMinutes || '-' }}</el-descriptions-item>
          <el-descriptions-item label="消耗卡路里">{{ selectedCheckIn.caloriesBurned || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusText(selectedCheckIn.status) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 签退对话框 -->
    <el-dialog
      v-model="checkOutDialogVisible"
      title="签退"
      width="400px"
    >
      <div class="checkout-form" v-if="selectedCheckIn">
        <el-form :model="checkOutForm">
          <el-form-item label="签退时间">
            <el-datetime-picker v-model="checkOutForm.checkOutTime" type="datetime" placeholder="选择签退时间" style="width: 100%" />
          </el-form-item>
          <el-form-item label="时长(分钟)">
            <el-input-number v-model="checkOutForm.durationMinutes" :min="1" :step="1" placeholder="输入时长" style="width: 100%" />
          </el-form-item>
          <el-form-item label="消耗卡路里">
            <el-input-number v-model="checkOutForm.caloriesBurned" :min="0" :step="10" placeholder="输入消耗卡路里" style="width: 100%" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="checkOutDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCheckOutSubmit" :loading="submitLoading">确认签退</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { checkInApi } from '@/api/checkIn'
import { courseApi } from '@/api/course'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

// 类型定义保持不变
interface Course {
  id: number
  name: string
  location?: string
  startTime?: string
}

interface Student {
  id: number
  realName?: string
  username: string
  studentNumber?: string
}

interface CheckInRecord {
  id: number
  studentId: number
  courseId: number
  checkInTime: string
  checkOutTime?: string
  durationMinutes?: number
  caloriesBurned?: number
  status: 'CHECKED_IN' | 'CHECKED_OUT'
  location?: string
  student?: Student
  course?: Course
  studentName?: string
  courseName?: string
}

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const detailDialogVisible = ref(false)
const checkOutDialogVisible = ref(false)
const selectedCheckIn = ref<CheckInRecord | null>(null)

const checkInsList = ref<CheckInRecord[]>([])
const courseList = ref<Course[]>([])
const studentList = ref<Student[]>([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  courseId: '',
  studentId: '',
  status: '',
  dateRange: [] as string[]
})

const checkOutForm = reactive({
  checkOutTime: '',
  durationMinutes: 60,
  caloriesBurned: 200
})

const stats = reactive({
  totalCheckIns: 0,
  completedCheckIns: 0,
  pendingCheckIns: 0,
  avgDuration: 0
})

// 辅助方法保持不变
const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    'CHECKED_IN': 'warning',
    'CHECKED_OUT': 'success'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    'CHECKED_IN': '已签到',
    'CHECKED_OUT': '已签退'
  }
  return map[status] || status
}

const calculateStats = (records: CheckInRecord[]) => {
  stats.totalCheckIns = records.length
  stats.completedCheckIns = records.filter(r => r.status === 'CHECKED_OUT').length
  stats.pendingCheckIns = records.filter(r => r.status === 'CHECKED_IN').length
  
  const completedRecords = records.filter(r => r.durationMinutes)
  if (completedRecords.length > 0) {
    const totalDuration = completedRecords.reduce((sum, r) => sum + (r.durationMinutes || 0), 0)
    stats.avgDuration = Math.round(totalDuration / completedRecords.length)
  } else {
    stats.avgDuration = 0
  }
}

// 加载课程（修复 page 参数）
const loadCourses = async () => {
  try {
    const res = await courseApi.getCoachCourses({ 
      page: 1,                  // 补充 page 参数
      pageSize: 1000,
      coachId: userStore.userId || undefined
    })
    courseList.value = res.data?.records || []
  } catch (error: any) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败，请稍后重试')
  }
}

const loadStudents = async () => {
  try {
    const res = await userApi.getCoachStudents({ size: 1000 })
    studentList.value = res.data?.records || []
  } catch (error: any) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败，请稍后重试')
  }
}

// 加载签到记录（修复后端返回结构）
const loadCheckIns = async () => {
  try {
    loading.value = true
    
    const params: any = {
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    
    if (searchForm.courseId) params.courseId = searchForm.courseId
    if (searchForm.studentId) params.studentId = searchForm.studentId
    if (searchForm.status) params.status = searchForm.status
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    
    const res = await checkInApi.getAllCheckIns(params)
    
    // 处理返回数据：适应后端ApiResponse结构
    let records: CheckInRecord[] = []
    if (res && res.code === 200 && res.data) {
      // 检查返回的数据类型
      if (Array.isArray(res.data)) {
        // 直接返回数组
        records = res.data
        pagination.total = records.length
      } else if (res.data.records) {
        // 分页对象结构
        records = res.data.records
        pagination.total = res.data.total || records.length
      } else {
        records = []
        pagination.total = 0
      }
    } else {
      records = []
      pagination.total = 0
    }
    
    // 打印返回数据，方便调试
    console.log('签到记录数据:', records)
    
    checkInsList.value = records
    calculateStats(records)
  } catch (error: any) {
    console.error('加载签到记录失败:', error)
    ElMessage.error(error.message || '加载签到记录失败')
  } finally {
    loading.value = false
  }
}

// 事件处理函数（修复签退提交）
const handleSearch = () => {
  pagination.page = 1
  loadCheckIns()
}

const handleReset = () => {
  searchForm.courseId = ''
  searchForm.studentId = ''
  searchForm.status = ''
  searchForm.dateRange = []
  pagination.page = 1
  loadCheckIns()
}

const handleViewDetail = (row: CheckInRecord) => {
  selectedCheckIn.value = row
  detailDialogVisible.value = true
}

const handleCheckOut = (row: CheckInRecord) => {
  selectedCheckIn.value = row
  checkOutForm.checkOutTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
  
  if (row.checkInTime) {
    const duration = dayjs().diff(dayjs(row.checkInTime), 'minute')
    checkOutForm.durationMinutes = Math.min(Math.max(duration, 30), 180)
  } else {
    checkOutForm.durationMinutes = 60
  }
  checkOutForm.caloriesBurned = 200
  checkOutDialogVisible.value = true
}

// 监听签退时间变化，自动更新时长（仅用于显示，不提交）
watch(() => checkOutForm.checkOutTime, (newTime) => {
  if (selectedCheckIn.value?.checkInTime && newTime) {
    const duration = dayjs(newTime).diff(dayjs(selectedCheckIn.value.checkInTime), 'minute')
    if (duration > 0) {
      checkOutForm.durationMinutes = Math.min(Math.max(duration, 1), 300)
    }
  }
})

// 提交签退（移除 duration 字段）
const handleCheckOutSubmit = async () => {
  if (!selectedCheckIn.value) return
  
  try {
    submitLoading.value = true
    
    await checkInApi.updateCheckInStatus(selectedCheckIn.value.id, {
      status: 'CHECKED_OUT',
      checkOutTime: checkOutForm.checkOutTime,
      calories: checkOutForm.caloriesBurned
      // duration 字段已移除，因为类型定义中不支持；后端可根据签到/签退时间自动计算
    })
    
    ElMessage.success('签退成功')
    checkOutDialogVisible.value = false
    loadCheckIns()
  } catch (error: any) {
    console.error('签退失败:', error)
    ElMessage.error(error.message || '签退失败')
  } finally {
    submitLoading.value = false
  }
}

watch(() => pagination.page, () => loadCheckIns())
watch(() => pagination.pageSize, () => {
  pagination.page = 1
  loadCheckIns()
})

onMounted(async () => {
  await Promise.all([loadCourses(), loadStudents()])
  await loadCheckIns()
})
</script>

<style scoped>
.checkins-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.page-header p {
  font-size: 14px;
  color: #666;
}

.stats-cards {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  flex: 1;
  min-width: 200px;
}

.stat-content {
  text-align: center;
}

.stat-content h3 {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-content p {
  font-size: 14px;
  color: #666;
}

.search-filter {
  margin-bottom: 20px;
}

.search-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.checkins-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.checkin-detail,
.checkout-form {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>