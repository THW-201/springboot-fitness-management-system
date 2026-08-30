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
        v-loading="reservationStore.loading"
        :data="reservationStore.reservations"
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
          :total="reservationStore.total"
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
import { ref, onMounted, computed, watch } from 'vue'
import { useReservationStore } from '@/stores/reservation'
import type { Reservation, ReservationStatus } from '@/types'
import { ElMessage, ElMessageBox } from 'element-plus'

const reservationStore = useReservationStore()

// 筛选条件
const filterType = ref<string>('')
const filterStatus = ref<string>('')

// 分页
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const currentReservation = ref<Reservation | null>(null)

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
    await reservationStore.fetchReservations({
      page: currentPage.value,
      pageSize: pageSize.value,
      type: filterType.value,
      status: filterStatus.value
    })
  } catch (error) {
    ElMessage.error('获取预约列表失败')
  }
}

// 确认预约
const handleConfirm = async (reservation: Reservation) => {
  try {
    await reservationStore.confirmReservation(reservation.id)
    ElMessage.success('预约确认成功')
    fetchReservations()
  } catch (error) {
    ElMessage.error('确认预约失败')
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
    await reservationStore.rejectReservation(reservation.id, reason)
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
    await reservationStore.completeReservation(reservation.id)
    ElMessage.success('预约完成成功')
    fetchReservations()
  } catch (error) {
    ElMessage.error('完成预约失败')
  }
}

// 查看详情
const handleView = async (reservation: Reservation) => {
  try {
    await reservationStore.fetchReservationById(reservation.id)
    currentReservation.value = reservationStore.currentReservation
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取预约详情失败')
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
</style>