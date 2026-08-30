<template>
  <div class="reservations-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="按类型筛选" size="small" style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="课程预约" value="COURSE" />
              <el-option label="器材预约" value="EQUIPMENT" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="按状态筛选" size="small" style="width: 120px;">
              <el-option label="全部" value="" />
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="reservationList"
        style="width: 100%"
      >
        <el-table-column prop="id" label="预约ID" width="100" />
        <el-table-column prop="reservationType" label="预约类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.reservationType === 'COURSE' ? 'primary' : 'success'">
              {{ scope.row.reservationType === 'COURSE' ? '课程预约' : '器材预约' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="预约项目" v-if="filterType === 'COURSE' || !filterType" />
        <el-table-column prop="equipmentName" label="预约项目" v-if="filterType === 'EQUIPMENT' || !filterType" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag
              :type="getStatusType(scope.row.status)"
              :effect="scope.row.status === 'PENDING' ? 'dark' : 'plain'"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInStatus" label="签到状态" width="120">
          <template #default="scope">
            <el-tag :type="getCheckInStatusType(scope.row.checkInStatus)">
              {{ getCheckInStatusText(scope.row.checkInStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'CONFIRMED'"
              type="danger"
              size="small"
              @click="handleCancel(scope.row)"
              style="margin-right: 5px;"
            >
              取消预约
            </el-button>
            <el-button
              type="info"
              size="small"
              @click="handleView(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="预约详情"
      width="500px"
    >
      <el-descriptions :column="1" border>
        <el-descriptions-item label="预约ID">{{ currentReservation?.id }}</el-descriptions-item>
        <el-descriptions-item label="预约类型">
          <el-tag :type="currentReservation?.reservationType === 'COURSE' ? 'primary' : 'success'">
            {{ currentReservation?.reservationType === 'COURSE' ? '课程预约' : '器材预约' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="课程名称" v-if="currentReservation?.reservationType === 'COURSE'">
          {{ currentReservation?.courseName }}
        </el-descriptions-item>
        <el-descriptions-item label="器材名称" v-if="currentReservation?.reservationType === 'EQUIPMENT'">
          {{ currentReservation?.equipmentName }}
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDate(currentReservation?.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDate(currentReservation?.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="getStatusType(currentReservation?.status)"
            :effect="currentReservation?.status === 'PENDING' ? 'dark' : 'plain'"
          >
            {{ getStatusText(currentReservation?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签到状态">
          <el-tag :type="getCheckInStatusType(currentReservation?.checkInStatus)">
            {{ getCheckInStatusText(currentReservation?.checkInStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="上课时间" v-if="currentReservation?.reservationType === 'COURSE'">
          <div v-if="currentReservation?.courseSchedule && currentReservation.courseSchedule.length > 0">
            <div v-for="(item, index) in currentReservation.courseSchedule" :key="index" style="margin-bottom: 5px;">
              {{ getDayOfWeekText(item.dayOfWeek) }}: {{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}
            </div>
          </div>
          <div v-else>
            暂无上课时间安排
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="取消原因" v-if="currentReservation?.cancelReason">
          {{ currentReservation?.cancelReason }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDate(currentReservation?.createdAt) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reservationApi } from '@/api/reservation'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 筛选条件
const filterType = ref<string>('')
const filterStatus = ref<string>('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 预约列表
const reservationList = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const currentReservation = ref<any>(null)

// 格式化日期
const formatDate = (date?: string) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return ''
  return new Date(time).toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态文本
const getStatusText = (status?: string) => {
  const statusMap: Record<string, string> = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return statusMap[status || 'PENDING']
}

// 获取状态类型
const getStatusType = (status?: string) => {
  const typeMap: Record<string, string> = {
    PENDING: 'warning',
    CONFIRMED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status || 'PENDING']
}

// 获取签到状态文本
const getCheckInStatusText = (status?: string) => {
  const statusMap: Record<string, string> = {
    CHECKED_IN: '已签到',
    NOT_CHECKED_IN: '未签到',
    LATE: '迟到'
  }
  return statusMap[status || 'NOT_CHECKED_IN']
}

// 获取签到状态类型
const getCheckInStatusType = (status?: string) => {
  const typeMap: Record<string, string> = {
    CHECKED_IN: 'success',
    NOT_CHECKED_IN: 'warning',
    LATE: 'danger'
  }
  return typeMap[status || 'NOT_CHECKED_IN']
}

// 获取星期文本
const getDayOfWeekText = (day?: string) => {
  const dayMap: Record<string, string> = {
    '1': '周一',
    '2': '周二',
    '3': '周三',
    '4': '周四',
    '5': '周五',
    '6': '周六',
    '7': '周日'
  }
  return dayMap[day || '1']
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  fetchReservations()
}

// 处理页码变化
const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchReservations()
}

// 获取预约列表
const fetchReservations = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      type: filterType.value,
      status: filterStatus.value
    }

    const res = await reservationApi.getStudentReservations(params)
    if (res.data) {
      reservationList.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 取消预约
const handleCancel = async (reservation: any) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入取消原因', '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入取消原因'
    })
    await reservationApi.cancelReservation(reservation.id, reason)
    ElMessage.success('取消预约成功')
    fetchReservations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败')
    }
  }
}

// 查看详情
const handleView = async (reservation: any) => {
  try {
    const res = await reservationApi.getReservationById(reservation.id)
    if (res.data) {
      currentReservation.value = res.data
      dialogVisible.value = true
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取预约详情失败')
  }
}

// 监听筛选条件变化
watch([filterType, filterStatus], () => {
  currentPage.value = 1
  fetchReservations()
})

// 组件挂载时获取数据
onMounted(() => {
  fetchReservations()
})
</script>

<style scoped>
.reservations-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  text-align: right;
}

@media (max-width: 768px) {
  .reservations-container {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    gap: 10px;
  }
  
  .header-actions .el-select {
    width: 100% !important;
  }
  
  .pagination-container {
    justify-content: center;
  }
  
  .pagination-container :deep(.el-pagination__sizes) {
    display: none;
  }
  
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
  
  :deep(.el-dialog) {
    width: 95% !important;
    margin-top: 5vh !important;
  }
  
  :deep(.el-dialog__body) {
    padding: 15px;
  }
}
</style>