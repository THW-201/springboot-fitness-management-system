<template>
  <div class="dashboard">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-info">
          <h2>欢迎回来，{{ userInfo?.realName || '教练' }}！</h2>
          <p>今天是 {{ displayDate }}，祝您教学愉快！</p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" @click="goToCourses">管理课程</el-button>
        </div>
      </div>
    </el-card>

    <!-- 教练专属统计 -->
    <div v-loading="loading">
      <!-- 我的课程统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">我的课程统计</span>
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
        <div ref="courseChartRef" class="chart" style="height: 400px; margin-top: 20px;"></div>
      </el-card>

      <!-- 我的学生统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <div class="card-header">
            <span class="card-title">我的学生统计</span>
            <el-button size="small" @click="loadStatistics">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </template>
        
        <!-- KPI卡片 -->
        <div class="kpi-cards">
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-content">
              <div class="kpi-value">{{ studentStats.length }}</div>
              <div class="kpi-label">总学生数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-content">
              <div class="kpi-value">{{ totalReservations }}</div>
              <div class="kpi-label">总预约次数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-content">
              <div class="kpi-value">{{ totalCheckIns }}</div>
              <div class="kpi-label">总签到次数</div>
            </div>
          </el-card>
          <el-card shadow="hover" class="kpi-card">
            <div class="kpi-content">
              <div class="kpi-value">{{ averageActivityScore.toFixed(1) }}</div>
              <div class="kpi-label">平均活跃度</div>
            </div>
          </el-card>
        </div>
        
        <el-table :data="studentStats" stripe style="margin-top: 20px;">
          <el-table-column prop="studentName" label="学生姓名" min-width="120" />
          <el-table-column prop="totalReservations" label="总预约数" width="100" align="center" />
          <el-table-column prop="totalCheckIns" label="签到数" width="100" align="center" />
          <el-table-column prop="totalExerciseMinutes" label="运动时长(分钟)" width="130" align="center" />
          <el-table-column prop="totalCaloriesBurned" label="消耗卡路里" width="120" align="center">
            <template #default="{ row }">
              {{ Number(row.totalCaloriesBurned).toFixed(1) }}
            </template>
          </el-table-column>
          <el-table-column prop="activityScore" label="活跃度评分" width="100" align="center" />
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

      <!-- 课程出勤率分析 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span class="card-title">课程出勤率分析</span>
        </template>
        <div ref="attendanceChartRef" class="chart" style="height: 300px;"></div>
      </el-card>

      <!-- 器材使用统计 -->
      <el-card shadow="hover" class="stats-card">
        <template #header>
          <span class="card-title">负责器材使用统计</span>
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
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusColor(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <div ref="equipmentChartRef" class="chart" style="height: 300px; margin-top: 20px;"></div>
      </el-card>

      <!-- 器材维护提醒 -->
      <el-card shadow="hover" class="stats-card" v-if="maintenanceReminders.length > 0">
        <template #header>
          <span class="card-title">器材维护提醒</span>
        </template>
        <el-alert
          v-for="(reminder, index) in maintenanceReminders"
          :key="index"
          :title="reminder"
          type="warning"
          show-icon
        />
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
  StudentStatisticsDTO,
  EquipmentStatisticsDTO
} from '@/types'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'
import { Refresh } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const displayDate = ref('')
const loading = ref(false)
const dateRange = ref<[string, string] | undefined>()

// 课程统计数据
const courseStats = ref<CourseStatisticsDTO[]>([])
const studentStats = ref<StudentStatisticsDTO[]>([])
const equipmentStats = ref<EquipmentStatisticsDTO[]>([])

// 学生统计分页
const studentPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

// KPI计算属性
const totalReservations = computed(() => {
  return studentStats.value.reduce((sum, student) => sum + (student.totalReservations || 0), 0)
})

const totalCheckIns = computed(() => {
  return studentStats.value.reduce((sum, student) => sum + (student.totalCheckIns || 0), 0)
})

const averageActivityScore = computed(() => {
  if (studentStats.value.length === 0) return 0
  const sum = studentStats.value.reduce((sum, student) => sum + (student.activityScore || 0), 0)
  return sum / studentStats.value.length
})

// 器材维护提醒
const maintenanceReminders = ref<string[]>([])

// 图表引用
const courseChartRef = ref<HTMLElement>()
const studentChartRef = ref<HTMLElement>()
const attendanceChartRef = ref<HTMLElement>()
const equipmentChartRef = ref<HTMLElement>()

// 图表实例
let courseChartInstance: ECharts | null = null
let studentChartInstance: ECharts | null = null
let attendanceChartInstance: ECharts | null = null
let equipmentChartInstance: ECharts | null = null

// 初始化日期
const initDate = () => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  }
  displayDate.value = now.toLocaleDateString('zh-CN', options)
  return displayDate.value
}

// 加载统计数据
const loadStatistics = async () => {
  loading.value = true
  try {
    // 修复TS类型错误：始终返回对象，无日期时传空字符串，避免undefined
    const params = dateRange.value
      ? { startDate: dateRange.value[0], endDate: dateRange.value[1], coachId: userStore.userId }
      : { startDate: '', endDate: '', coachId: userStore.userId }

    // 拼接分页参数，确保startDate/endDate为字符串类型
    const studentParams = {
      ...params,
      current: studentPagination.page,
      size: studentPagination.pageSize
    }

    const [courseRes, studentRes, equipmentRes] = await Promise.all([
      statisticsApi.getCourseStatistics(params),
      statisticsApi.getStudentStatistics(studentParams),
      statisticsApi.getEquipmentStatistics(params)
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
    
    // 生成器材维护提醒
    generateMaintenanceReminders(equipmentStats.value)

    setTimeout(() => {
      initCourseChart()
      initStudentChart()
      initAttendanceChart()
      initEquipmentChart()
    }, 100)
  } catch (error: any) {
    ElMessage.error(error.message || '加载统计数据失败')
  } finally {
    loading.value = false
  }
}

// 初始化课程统计图表 - 分组条形图
const initCourseChart = () => {
  if (!courseChartRef.value || courseStats.value.length === 0) return
  
  if (courseChartInstance) {
    courseChartInstance.dispose()
  }
  
  courseChartInstance = echarts.init(courseChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: function(params: any) {
        const dataIndex = params[0].dataIndex
        const course = courseStats.value[dataIndex]
        return `
          <div style="padding: 10px;">
            <div style="font-weight: bold; margin-bottom: 8px;">${course.courseName}</div>
            <div style="margin-bottom: 4px;">预约人数: <span style="color: #667eea; font-weight: bold;">${course.totalReservations}</span></div>
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
      data: courseStats.value.map(item => item.courseName),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '预约数',
        type: 'bar',
        data: courseStats.value.map(item => item.totalReservations),
        itemStyle: { color: '#667eea' }
      },
      {
        name: '实际人数',
        type: 'bar',
        data: courseStats.value.map(item => item.actualReservations),
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '签到数',
        type: 'bar',
        data: courseStats.value.map(item => item.totalCheckIns),
        itemStyle: { color: '#e6a23c' }
      }
    ]
  }
  
  courseChartInstance.setOption(option)
}

// 初始化学生统计图表 - 环形图
const initStudentChart = () => {
  if (!studentChartRef.value || studentStats.value.length === 0) return
  
  if (studentChartInstance) {
    studentChartInstance.dispose()
  }
  
  studentChartInstance = echarts.init(studentChartRef.value)
  
  // 计算学生活跃度分布
  const activeStudents = studentStats.value.filter(s => s.activityScore >= 80).length
  const mediumStudents = studentStats.value.filter(s => s.activityScore >= 50 && s.activityScore < 80).length
  const inactiveStudents = studentStats.value.filter(s => s.activityScore < 50).length
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: '5%',
      left: 'center',
      data: ['高活跃度', '中等活跃度', '低活跃度']
    },
    series: [{
      name: '学生活跃度',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
        color: function(params: any) {
          const colors = ['#67c23a', '#409eff', '#f56c6c']
          return colors[params.dataIndex]
        }
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 20,
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: activeStudents, name: '高活跃度' },
        { value: mediumStudents, name: '中等活跃度' },
        { value: inactiveStudents, name: '低活跃度' }
      ]
    }]
  }
  
  studentChartInstance.setOption(option)
}

// 初始化出勤率分析图表 - 双折线图
const initAttendanceChart = () => {
  if (!attendanceChartRef.value || courseStats.value.length === 0) return
  
  if (attendanceChartInstance) {
    attendanceChartInstance.dispose()
  }
  
  attendanceChartInstance = echarts.init(attendanceChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      formatter: function(params: any) {
        const dataIndex = params[0].dataIndex
        const course = courseStats.value[dataIndex]
        return `
          <div style="padding: 10px;">
            <div style="font-weight: bold; margin-bottom: 8px;">${course.courseName}</div>
            <div style="margin-bottom: 4px;">签到率: <span style="color: #67c23a; font-weight: bold;">${Number(course.checkInRate).toFixed(1)}%</span></div>
            <div>利用率: <span style="color: #409eff; font-weight: bold;">${Number(course.utilizationRate).toFixed(1)}%</span></div>
          </div>
        `
      }
    },
    legend: {
      data: ['签到率', '利用率']
    },
    xAxis: {
      type: 'category',
      data: courseStats.value.map(item => item.courseName),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: '签到率',
        type: 'line',
        data: courseStats.value.map(item => Number(item.checkInRate)),
        itemStyle: { color: '#67c23a' },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8,
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
          ])
        }
      },
      {
        name: '利用率',
        type: 'line',
        data: courseStats.value.map(item => Number(item.utilizationRate)),
        itemStyle: { color: '#409eff' },
        lineStyle: {
          width: 3
        },
        symbol: 'circle',
        symbolSize: 8,
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      }
    ]
  }
  
  attendanceChartInstance.setOption(option)
}

// 初始化器材统计图表 - 水平条形图
const initEquipmentChart = () => {
  if (!equipmentChartRef.value || equipmentStats.value.length === 0) return
  
  if (equipmentChartInstance) {
    equipmentChartInstance.dispose()
  }
  
  equipmentChartInstance = echarts.init(equipmentChartRef.value)
  
  const option = {
    tooltip: { 
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['使用次数', '总使用时长(分钟)']
    },
    xAxis: [
      {
        type: 'value',
        name: '使用次数',
        position: 'top'
      },
      {
        type: 'value',
        name: '使用时长(分钟)',
        position: 'bottom'
      }
    ],
    yAxis: {
      type: 'category',
      data: equipmentStats.value.map(item => item.equipmentName),
      axisLabel: {
        interval: 0
      }
    },
    series: [
      {
        name: '使用次数',
        type: 'bar',
        data: equipmentStats.value.map(item => item.usageFrequency),
        itemStyle: { color: '#764ba2' },
        barWidth: '30%'
      },
      {
        name: '总使用时长(分钟)',
        type: 'bar',
        xAxisIndex: 1,
        data: equipmentStats.value.map(item => item.totalUsageMinutes),
        itemStyle: { color: '#f093fb' },
        barWidth: '30%'
      }
    ]
  }
  
  equipmentChartInstance.setOption(option)
}

// 生成器材维护提醒
const generateMaintenanceReminders = (equipmentStats: EquipmentStatisticsDTO[]) => {
  const reminders: string[] = []
  
  equipmentStats.forEach(equipment => {
    // 这里可以根据实际的维护规则生成提醒
    // 例如：使用频率高的器材需要更频繁维护
    if (equipment.usageFrequency > 5) {
      reminders.push(`${equipment.equipmentName} 使用频率较高，建议进行维护检查`)
    }
    
    // 可以根据器材状态生成提醒
    if (equipment.status === 'MAINTENANCE') {
      reminders.push(`${equipment.equipmentName} 正在维护中`)
    } else if (equipment.status === 'DAMAGED') {
      reminders.push(`${equipment.equipmentName} 已损坏，需要维修`)
    }
  })
  
  maintenanceReminders.value = reminders
}

// 获取器材状态颜色
const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: 'success',
    IN_USE: 'warning',
    MAINTENANCE: 'info',
    DAMAGED: 'danger'
  }
  return map[status] || 'info'
}

// 获取器材状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: '可用',
    IN_USE: '使用中',
    MAINTENANCE: '维护中',
    DAMAGED: '已损坏'
  }
  return map[status] || status
}

// 窗口大小调整处理
const handleResize = () => {
  courseChartInstance?.resize()
  studentChartInstance?.resize()
  attendanceChartInstance?.resize()
  equipmentChartInstance?.resize()
}

// 跳转到课程管理页面
const goToCourses = () => {
  router.push('/coach/courses')
}

// 监听学生统计分页变化
watch([() => studentPagination.page, () => studentPagination.pageSize], () => {
  loadStatistics()
})

onMounted(() => {
  initDate()
  loadStatistics()
  window.addEventListener('resize', handleResize)
})



onActivated(() => {
  // 当组件被 keep-alive 缓存并重新激活时，重新加载数据
  loadStatistics()
})

onUnmounted(() => {
  courseChartInstance?.dispose()
  studentChartInstance?.dispose()
  attendanceChartInstance?.dispose()
  equipmentChartInstance?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
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

.stats-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: bold;
  font-size: 16px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.chart {
  width: 100%;
  height: 100%;
}

/* KPI卡片样式 */
.kpi-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.kpi-card {
  transition: all 0.3s ease;
}

.kpi-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.kpi-content {
  text-align: center;
  padding: 20px;
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 8px;
}

.kpi-label {
  font-size: 14px;
  color: #606266;
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

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
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