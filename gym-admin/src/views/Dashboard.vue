<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-info">
          <h2>欢迎回来，{{ userInfo?.realName || '用户' }}！</h2>
          <p>今天是 {{ currentDate }}，祝您健身愉快！</p>
        </div>
        <div class="welcome-actions">
          <el-button v-if='userStore.isStudent' type="primary" @click="goToBooking">立即预约</el-button>
        </div>
      </div>
    </el-card>

    <!-- 管理员/教练统计 -->
    <div v-if="userStore.isAdmin || userStore.isCoach" v-loading="loading">
      <!-- 课程统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">课程统计</span>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="loadStatistics"
              size="small"
            />
          </div>
        </template>
        <el-table :data="courseStats" stripe>
          <el-table-column prop="courseName" label="课程名称" min-width="150" />
          <el-table-column prop="totalReservations" label="总预约数" width="100" align="center" />
          <el-table-column prop="cancelledReservations" label="取消人数" width="100" align="center" />
          <el-table-column prop="actualReservations" label="实际人数" width="100" align="center" />
          <el-table-column prop="totalCheckIns" label="签到数" width="100" align="center" />
          <el-table-column prop="checkInRate" label="签到率" width="100" align="center">
            <template #default="{ row }">
              {{ Number(row.checkInRate).toFixed(1) ?? '0.0' }}%
            </template>
          </el-table-column>
          <el-table-column prop="capacity" label="容量" width="80" align="center" />
          <el-table-column prop="utilizationRate" label="利用率" width="100" align="center">
            <template #default="{ row }">
              {{ Number(row.utilizationRate).toFixed(1) ?? '0.0' }}%
            </template>
          </el-table-column>
        </el-table>
        <div ref="courseChartRef" class="chart" style="height: 300px; margin-top: 20px;"></div>
      </el-card>

      <!-- 器材统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span>器材统计</span>
        </template>
        <el-table :data="equipmentStats" stripe>
          <el-table-column prop="equipmentName" label="器材名称" min-width="150" />
          <el-table-column prop="totalReservations" label="总预约数" width="100" align="center" />
          <el-table-column prop="totalUsageMinutes" label="总使用时长(分钟)" width="150" align="center" />
          <el-table-column prop="averageUsageMinutes" label="平均使用时长" width="130" align="center">
            <template #default="{ row }">
              {{ Number(row.averageUsageMinutes).toFixed(1) }} 分钟
            </template>
          </el-table-column>
          <el-table-column prop="usageFrequency" label="使用频次" width="100" align="center" />
        </el-table>
        <div ref="equipmentChartRef" class="chart" style="height: 300px; margin-top: 20px;"></div>
      </el-card>

      <!-- 学生统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span>学生统计</span>
        </template>
        <el-table :data="studentStats" stripe>
          <el-table-column prop="studentName" label="学生姓名" min-width="120" />
          <el-table-column prop="totalReservations" label="总预约数" width="100" align="center" />
          <el-table-column prop="totalCheckIns" label="签到数" width="100" align="center" />
          <el-table-column prop="totalExerciseMinutes" label="运动时长(分钟)" width="130" align="center" />
          <el-table-column prop="totalCaloriesBurned" label="消耗卡路里" width="120" align="center">
            <template #default="{ row }">
              {{ Number(row.totalCaloriesBurned).toFixed(1) }}
            </template>
          </el-table-column>
        </el-table>
        <el-pagination 
          v-model:current-page="studentPagination.page" 
          v-model:page-size="studentPagination.pageSize"
          :total="studentPagination.total" 
          :page-sizes="[10, 20, 50, 100]" 
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination" 
        />
        <div ref="studentChartRef" class="chart" style="height: 300px; margin-top: 20px;"></div>
      </el-card>
    </div>

    <!-- 学生个人健康统计 -->
    <div v-if="userStore.isStudent" v-loading="loading">
      <el-row :gutter="20" class="stats-row">
        <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
          <div class="stat-card stat-card-1">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="32"><TrendCharts /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ personalStats?.totalExerciseCount || 0 }}</div>
                <div class="stat-label">运动次数</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
          <div class="stat-card stat-card-2">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="32"><Timer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ personalStats?.totalExerciseMinutes || 0 }}</div>
                <div class="stat-label">运动时长(分钟)</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
          <div class="stat-card stat-card-3">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="32"><Odometer /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ Number(personalStats?.totalCaloriesBurned).toFixed(0) || 0 }}</div>
                <div class="stat-label">消耗卡路里</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
          <div class="stat-card stat-card-4">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="32"><DataAnalysis /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ latestWeight }}</div>
                <div class="stat-label">当前体重(kg)</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 体重趋势图 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span>体重变化趋势</span>
        </template>
        <div ref="weightChartRef" class="chart" style="height: 300px;"></div>
      </el-card>

      <!-- 课程参与历史 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span>课程参与历史</span>
        </template>
        <el-table :data="personalStats?.courseHistory || []" stripe>
          <el-table-column prop="courseName" label="课程名称" min-width="150" />
          <el-table-column prop="date" label="日期" width="120" />
        </el-table>
      </el-card>

      <!-- 器材使用历史 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span>器材使用历史</span>
        </template>
        <el-table :data="personalStats?.equipmentHistory || []" stripe>
          <el-table-column prop="equipmentName" label="器材名称" min-width="150" />
          <el-table-column prop="date" label="日期" width="120" />
        </el-table>
        <div ref="equipmentUsageChartRef" class="chart" style="height: 300px; margin-top: 20px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, onActivated, computed, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { statisticsApi } from '@/api/statistics'
import type { 
  CourseStatisticsDTO, 
  EquipmentStatisticsDTO, 
  StudentStatisticsDTO,
  PersonalHealthStatistics 
} from '@/types'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { TrendCharts, Timer, Odometer, DataAnalysis } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const currentDate = ref('')
const loading = ref(false)
const dateRange = ref<[string, string]>()

// 管理员/教练统计数据
const courseStats = ref<CourseStatisticsDTO[]>([])
const equipmentStats = ref<EquipmentStatisticsDTO[]>([])
const studentStats = ref<StudentStatisticsDTO[]>([])

// 学生统计分页
const studentPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// 学生个人统计数据
const personalStats = ref<PersonalHealthStatistics | null>(null)

// 图表引用
const courseChartRef = ref<HTMLElement>()
const equipmentChartRef = ref<HTMLElement>()
const studentChartRef = ref<HTMLElement>()
const weightChartRef = ref<HTMLElement>()
const equipmentUsageChartRef = ref<HTMLElement>()

// 图表实例
let courseChartInstance: ECharts | null = null
let equipmentChartInstance: ECharts | null = null
let studentChartInstance: ECharts | null = null
let weightChartInstance: ECharts | null = null
let equipmentUsageChartInstance: ECharts | null = null

// 计算最新体重
const latestWeight = computed(() => {
  if (!personalStats.value?.weightHistory || personalStats.value.weightHistory.length === 0) {
    return '-'
  }
  const latest = personalStats.value.weightHistory[personalStats.value.weightHistory.length - 1]
  return latest.weight
})

// 初始化日期
const initDate = () => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  currentDate.value = now.toLocaleDateString('zh-CN', options)
}

// 加载统计数据
const loadStatistics = async () => {
  if (!userStore.isAdmin && !userStore.isCoach) return
  
  loading.value = true
  try {
    // 修复TS类型错误：始终返回对象，无日期时传空字符串，避免undefined
    const params = dateRange.value
      ? { startDate: dateRange.value[0], endDate: dateRange.value[1] }
      : { startDate: '', endDate: '' }

    // 拼接分页参数，确保startDate/endDate为字符串类型
    const studentParams = {
      ...params,
      current: studentPagination.page,
      size: studentPagination.pageSize
    }

    const [courseRes, equipmentRes, studentRes] = await Promise.all([
      statisticsApi.getCourseStatistics(params),
      statisticsApi.getEquipmentStatistics(params),
      statisticsApi.getStudentStatistics(studentParams)
    ])

    // 处理课程统计数据 - 支持数组和分页格式
    if (courseRes.data) {
      if (Array.isArray(courseRes.data)) {
        courseStats.value = courseRes.data
      } else {
        const data = courseRes.data as any
        courseStats.value = data.records || []
      }
    }
    console.log('课程统计数据:', courseStats.value)
    
    // 处理器材统计数据 - 支持数组和分页格式
    if (equipmentRes.data) {
      if (Array.isArray(equipmentRes.data)) {
        equipmentStats.value = equipmentRes.data
      } else {
        const data = equipmentRes.data as any
        equipmentStats.value = data.records || []
      }
    }
    console.log('器材统计数据:', equipmentStats.value)
    
    // 处理学生统计数据 - 支持数组和分页格式
    if (studentRes.data) {
      if (Array.isArray(studentRes.data)) {
        studentStats.value = studentRes.data
        studentPagination.total = studentRes.data.length
      } else {
        const data = studentRes.data as any
        studentStats.value = data.records || []
        studentPagination.total = data.total || 0
      }
    }
    console.log('学生统计数据:', studentStats.value)

    setTimeout(() => {
      initCourseChart()
      initEquipmentChart()
      initStudentChart()
    }, 100)
  } catch (error: any) {
    ElMessage.error(error.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 加载学生个人统计数据
const loadPersonalStatistics = async () => {
  if (!userStore.isStudent) return
  
  loading.value = true
  try {
    // 假设 statisticsApi 中存在 getMyStatistics 方法
    const res = await statisticsApi.getPersonalHealthData()
    personalStats.value = res.data

    setTimeout(() => {
      initWeightChart()
      initEquipmentUsageChart()
    }, 100)
  } catch (error: any) {
    ElMessage.error(error.message || '加载个人统计数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化课程统计图表
const initCourseChart = () => {
  if (!courseChartRef.value || courseStats.value.length === 0) return
  
  if (courseChartInstance) {
    courseChartInstance.dispose()
  }
  
  courseChartInstance = echarts.init(courseChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      formatter: function(params: any) {
        const dataIndex = params[0].dataIndex
        const course = courseStats.value[dataIndex]
        return `
          <div style="padding: 10px;">
            <div style="font-weight: bold; margin-bottom: 8px;">${course.courseName}</div>
            <div style="margin-bottom: 4px;">预约人数: <span style="color: #667eea; font-weight: bold;">${course.totalReservations}</span></div>
            <div style="margin-bottom: 4px;">取消人数: <span style="color: #f56c6c; font-weight: bold;">${course.cancelledReservations}</span></div>
            <div style="margin-bottom: 4px;">实际人数: <span style="color: #67c23a; font-weight: bold;">${course.actualReservations}</span></div>
            <div>签到人数: <span style="color: #e6a23c; font-weight: bold;">${course.totalCheckIns}</span></div>
          </div>
        `
      }
    },
    legend: {
      data: ['预约数', '实际人数', '签到数']
    },
    xAxis: {
      type: 'category',
      data: courseStats.value.map(item => item.courseName)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '预约数',
        type: 'bar',
        data: courseStats.value.map(item => item.totalReservations),
        itemStyle: { color: '#667eea' },
        barWidth: '20%'
      },
      {
        name: '实际人数',
        type: 'bar',
        data: courseStats.value.map(item => item.actualReservations),
        itemStyle: { color: '#67c23a' },
        barWidth: '20%'
      },
      {
        name: '签到数',
        type: 'bar',
        data: courseStats.value.map(item => item.totalCheckIns),
        itemStyle: { color: '#e6a23c' },
        barWidth: '20%'
      }
    ]
  }
  
  courseChartInstance.setOption(option)
}

// 初始化器材统计图表
const initEquipmentChart = () => {
  if (!equipmentChartRef.value || equipmentStats.value.length === 0) return
  
  if (equipmentChartInstance) {
    equipmentChartInstance.dispose()
  }
  
  equipmentChartInstance = echarts.init(equipmentChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['使用频次', '总使用时长']
    },
    xAxis: {
      type: 'category',
      data: equipmentStats.value.map(item => item.equipmentName),
      axisLabel: { interval: 0, rotate: 0 }
    },
    yAxis: [
      {
        type: 'value',
        name: '使用频次',
        position: 'left'
      },
      {
        type: 'value',
        name: '时长(分钟)',
        position: 'right'
      }
    ],
    series: [
      {
        name: '使用频次',
        type: 'bar',
        data: equipmentStats.value.map(item => item.usageFrequency),
        itemStyle: { color: '#764ba2' },
        barWidth: '35%'
      },
      {
        name: '总使用时长',
        type: 'bar',
        yAxisIndex: 1,
        data: equipmentStats.value.map(item => item.totalUsageMinutes),
        itemStyle: { color: '#f093fb' },
        barWidth: '35%'
      }
    ]
  }
  
  equipmentChartInstance.setOption(option)
}

// 初始化学生统计图表
const initStudentChart = () => {
  if (!studentChartRef.value || studentStats.value.length === 0) return
  
  if (studentChartInstance) {
    studentChartInstance.dispose()
  }
  
  studentChartInstance = echarts.init(studentChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    legend: {
      data: ['总预约数', '签到数', '活跃度评分']
    },
    xAxis: {
      type: 'category',
      data: studentStats.value.map(item => item.studentName)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '总预约数',
        type: 'bar',
        data: studentStats.value.map(item => item.totalReservations),
        itemStyle: { color: '#667eea' },
        barWidth: '25%'
      },
      {
        name: '签到数',
        type: 'bar',
        data: studentStats.value.map(item => item.totalCheckIns),
        itemStyle: { color: '#67c23a' },
        barWidth: '25%'
      },
      {
        name: '活跃度评分',
        type: 'bar',
        data: studentStats.value.map(item => item.activityScore),
        itemStyle: { color: '#f093fb' },
        barWidth: '25%'
      }
    ]
  }
  
  studentChartInstance.setOption(option)
}

// 初始化体重趋势图
const initWeightChart = () => {
  if (!weightChartRef.value || !personalStats.value?.weightHistory) return
  
  if (weightChartInstance) {
    weightChartInstance.dispose()
  }
  
  weightChartInstance = echarts.init(weightChartRef.value)
  
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: personalStats.value.weightHistory.map(item => item.date)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '体重',
      type: 'bar',
      data: personalStats.value.weightHistory.map(item => item.weight),
      itemStyle: { color: '#667eea' }
    }]
  }
  
  weightChartInstance.setOption(option)
}

// 初始化器材使用图表
const initEquipmentUsageChart = () => {
  if (!equipmentUsageChartRef.value || !personalStats.value?.equipmentHistory) return
  
  if (equipmentUsageChartInstance) {
    equipmentUsageChartInstance.dispose()
  }
  
  equipmentUsageChartInstance = echarts.init(equipmentUsageChartRef.value)
  
  const equipmentUsage = personalStats.value.equipmentHistory.reduce((acc, item) => {
    if (!acc[item.equipmentName]) {
      acc[item.equipmentName] = 0
    }
    acc[item.equipmentName] += item.durationMinutes
    return acc
  }, {} as Record<string, number>)
  
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      name: '使用时长',
      type: 'pie',
      radius: '50%',
      data: Object.entries(equipmentUsage).map(([name, value]) => ({
        name,
        value
      }))
    }]
  }
  
  equipmentUsageChartInstance.setOption(option)
}

// 窗口大小调整处理
const handleResize = () => {
  courseChartInstance?.resize()
  equipmentChartInstance?.resize()
  studentChartInstance?.resize()
  weightChartInstance?.resize()
  equipmentUsageChartInstance?.resize()
}

// 跳转到预约页面
const goToBooking = () => {
  router.push('/student/courses')
}

// 监听学生统计分页变化
watch([() => studentPagination.page, () => studentPagination.pageSize], () => {
  if (userStore.isAdmin || userStore.isCoach) {
    loadStatistics()
  }
})

onMounted(() => {
  initDate()
  
  if (userStore.isAdmin || userStore.isCoach) {
    loadStatistics()
  } else if (userStore.isStudent) {
    loadPersonalStatistics()
  }
  
  window.addEventListener('resize', handleResize)
})

onActivated(() => {
  // 当组件被 keep-alive 缓存并重新激活时，重新加载数据
  initDate()
  
  if (userStore.isAdmin || userStore.isCoach) {
    loadStatistics()
  } else if (userStore.isStudent) {
    loadPersonalStatistics()
  }
})

onUnmounted(() => {
  courseChartInstance?.dispose()
  equipmentChartInstance?.dispose()
  studentChartInstance?.dispose()
  weightChartInstance?.dispose()
  equipmentUsageChartInstance?.dispose()
  
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
/* 样式保持不变，与之前一致 */
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.welcome-card :deep(.el-card__body) {
  padding: 30px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-info h2 {
  color: white;
  font-size: 24px;
  margin-bottom: 8px;
}

.welcome-info p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 0;
}

.welcome-actions {
  display: flex;
  gap: 12px;
}

.welcome-actions .el-button {
  background: white;
  color: #667eea;
  border: none;
  font-weight: 500;
}

.welcome-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.card-title {
  margin-right: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.stat-card {
  padding: 20px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-card-1 .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card-2 .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card-3 .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card-4 .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #67c23a;
  font-weight: 500;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 400px;
}

.chart-card :deep(.el-card__body) {
  height: calc(100% - 60px);
  padding: 20px;
}

.chart {
  width: 100%;
  height: 100%;
}

.table-card :deep(.el-card__body) {
  padding: 0;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }

  .welcome-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .welcome-actions {
    width: 100%;
    flex-direction: column;
  }

  .welcome-actions .el-button {
    width: 100%;
  }

  .welcome-info h2 {
    font-size: 20px;
  }

  .chart-card {
    height: 350px;
  }
  
  .stats-row {
    margin-bottom: 15px;
  }
  
  .stat-card {
    padding: 15px;
    margin-bottom: 10px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .stats-card {
    margin-bottom: 15px;
  }
  
  .stats-card .card-header {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
  .stats-card :deep(.el-date-editor) {
    width: 100%;
  }
  
  .stats-card :deep(.el-table) {
    font-size: 12px;
  }
  
  .stats-card :deep(.el-table th),
  .stats-card :deep(.el-table td) {
    padding: 8px 5px;
  }
  
  .chart {
    height: 250px !important;
  }
}
</style>