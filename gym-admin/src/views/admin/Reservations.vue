<template>
  <div class="reservations-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <div class="header-actions">
            <el-select v-model="filterType" placeholder="按类型筛选" size="small" style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="课程预约" value="COURSE" />
              <el-option label="器材预约" value="EQUIPMENT" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="按状态筛选" size="small" style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="待确认" value="PENDING" />
              <el-option label="已确认" value="CONFIRMED" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
            <el-input v-model="searchKeyword" placeholder="搜索学生姓名或课程/器材名称" size="small" style="width: 200px; margin-right: 10px;">
              <template #append>
                <el-button @click="fetchReservations">
                  <el-icon>
                    <Search />
                  </el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="reservations"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="预约ID" width="100" />
        <el-table-column prop="reservationType" label="预约类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.reservationType === 'COURSE' ? 'primary' : 'success'">
              {{ scope.row.reservationType === 'COURSE' ? '课程预约' : '器材预约' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseName" label="课程名称" v-if="filterType === 'COURSE' || !filterType" />
        <el-table-column prop="equipmentName" label="器材名称" v-if="filterType === 'EQUIPMENT' || !filterType" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="coachName" label="教练姓名" v-if="filterType === 'COURSE' || !filterType" width="120" />
        <el-table-column prop="startTime" label="预约时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }}
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handleConfirm(scope.row)"
              style="margin-right: 5px;"
            >
              确认
            </el-button>
            <el-button
              v-if="scope.row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleReject(scope.row)"
              style="margin-right: 5px;"
            >
              拒绝
            </el-button>
            <el-button
              v-if="scope.row.status === 'CONFIRMED'"
              type="success"
              size="small"
              @click="handleComplete(scope.row)"
              style="margin-right: 5px;"
            >
              完成
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
        <el-descriptions-item label="教练姓名" v-if="currentReservation?.reservationType === 'COURSE'">
          {{ currentReservation?.coachName }}
        </el-descriptions-item>
        <el-descriptions-item label="器材名称" v-if="currentReservation?.reservationType === 'EQUIPMENT'">
          {{ currentReservation?.equipmentName }}
        </el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentReservation?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ formatDate(currentReservation?.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag
            :type="getStatusType(currentReservation?.status)"
            :effect="currentReservation?.status === 'PENDING' ? 'dark' : 'plain'"
          >
            {{ getStatusText(currentReservation?.status) }}
          </el-tag>
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
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { reservationApi } from '@/api/reservation'
import type { Reservation, ReservationStatus } from '@/types'

// 状态管理
const loading = ref(false)
const allReservations = ref<Reservation[]>([])
const total = ref(0)

// 过滤后的预约列表
const reservations = computed(() => {
  let filtered = [...allReservations.value]
  
  // 按类型筛选
  if (filterType.value) {
    filtered = filtered.filter(item => item.reservationType === filterType.value)
  }
  
  // 按状态筛选
  if (filterStatus.value) {
    filtered = filtered.filter(item => item.status === filterStatus.value)
  }
  
  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(item => {
      // 搜索学生姓名
      if (item.studentName?.toLowerCase().includes(keyword)) {
        return true
      }
      // 搜索课程名称
      if (item.courseName?.toLowerCase().includes(keyword)) {
        return true
      }
      // 搜索器材名称
      if (item.equipmentName?.toLowerCase().includes(keyword)) {
        return true
      }
      // 搜索教练姓名
      if (item.coachName?.toLowerCase().includes(keyword)) {
        return true
      }
      return false
    })
  }
  
  return filtered
})

// 筛选条件
const filterType = ref<string>('')
const filterStatus = ref<string>('')
const searchKeyword = ref<string>('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const currentReservation = ref<Reservation | null>(null)

// 格式化日期
const formatDate = (date?: string) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 获取状态文本
const getStatusText = (status?: ReservationStatus) => {
  const statusMap: Record<ReservationStatus, string> = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return statusMap[status || 'PENDING']
}

// 获取状态类型
const getStatusType = (status?: ReservationStatus) => {
  const typeMap: Record<ReservationStatus, string> = {
    PENDING: 'warning',
    CONFIRMED: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'danger'
  }
  return typeMap[status || 'PENDING']
}

// 处理选择
const handleSelectionChange = (val: Reservation[]) => {
  console.log('选中的预约:', val)
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
    const params: any = {}

    const res = await reservationApi.getAdminReservations(params)
    if (res.data) {
      allReservations.value = res.data || []
      total.value = allReservations.value.length || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '获取预约列表失败')
  } finally {
    loading.value = false
  }
}

// 确认预约
const handleConfirm = async (reservation: Reservation) => {
  try {
    await reservationApi.confirmReservation(reservation.id)
    ElMessage.success('预约确认成功')
    fetchReservations()
  } catch (error: any) {
    ElMessage.error(error.message || '确认预约失败')
  }
}

// 拒绝预约
const handleReject = async (reservation: Reservation) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入拒绝原因'
    })
    await reservationApi.rejectReservation(reservation.id, reason)
    ElMessage.success('预约拒绝成功')
    fetchReservations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('拒绝预约失败')
    }
  }
}

// 完成预约
const handleComplete = async (reservation: Reservation) => {
  try {
    await reservationApi.completeReservation(reservation.id)
    ElMessage.success('预约完成成功')
    fetchReservations()
  } catch (error: any) {
    ElMessage.error('完成预约失败')
  }
}

// 查看详情
const handleView = async (reservation: Reservation) => {
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
watch([filterType, filterStatus, searchKeyword], () => {
  currentPage.value = 1
  // 筛选条件变化时，不需要重新请求数据，直接通过计算属性过滤
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
  flex-wrap: wrap;
  gap: 10px;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  text-align: right;
}

/* 响应式 */
@media (max-width: 768px) {
  .reservations-container {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions .el-select,
  .header-actions .el-input {
    width: 100% !important;
    margin-right: 0 !important;
  }
  
  .pagination-container {
    justify-content: center;
  }
  
  .pagination-container :deep(.el-pagination__sizes) {
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
}
</style>