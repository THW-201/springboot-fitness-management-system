<template>
  <div class="equipment-page">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h2 class="page-title">器材预约</h2>
            <el-button type="success" size="small" @click="showMyBookings" class="my-bookings-btn">
              <el-icon><Ticket /></el-icon>
              我的预约
            </el-button>
          </div>
          <div class="search-bar">
            <el-input v-model="searchParams.name" placeholder="搜索器材名称" clearable
              class="search-input" @clear="loadEquipment" />
            <el-select v-model="searchParams.equipmentType" placeholder="器材类型" clearable
              class="search-select" @change="loadEquipment">
              <el-option label="全部" value="" />
              <el-option label="有氧器械" value="有氧器械" />
              <el-option label="力量器械" value="力量器械" />
              <el-option label="自由重量" value="自由重量" />
              <el-option label="综合训练" value="综合训练" />
              <el-option label="便携辅助小器材" value="便携辅助小器材" />
              <el-option label="瑜伽器材" value="瑜伽器材" />
              <el-option label="普拉提器材" value="普拉提器材" />
            </el-select>
            <el-select v-model="searchParams.status" placeholder="状态" clearable class="search-select"
              @change="loadEquipment">
              <el-option label="全部" value="" />
              <el-option label="可用" value="AVAILABLE" />
              <el-option label="使用中" value="IN_USE" />
              <el-option label="维护中" value="MAINTENANCE" />
              <el-option label="已损坏" value="DAMAGED" />
            </el-select>
            <el-button type="primary" @click="loadEquipment" class="search-btn">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset" class="reset-btn">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="equipmentList" stripe border v-loading="loading" class="equipment-table">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="器材名称" min-width="150" />
        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image :preview-teleported="true" v-if="row.imageUrl"
              :src="row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl"
              :preview-src-list="[row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl]"
              fit="cover" class="equipment-image" />
            <span v-else class="no-image">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="equipmentType" label="类型" width="120" align="center" />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" class="status-tag">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购买日期" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleViewDetail(row)" class="action-btn">
              详情
            </el-button>
            <el-button type="success" link size="small" @click="handleBook(row)" :disabled="row.status !== 'AVAILABLE'" class="action-btn">
              预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size"
        :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" />
    </el-card>

    <!-- 器材详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="器材详情" width="600px" class="detail-dialog">
      <div v-if="currentEquipment" class="equipment-detail-container">
        <!-- 器材图片 -->
        <div class="equipment-image-container" v-if="currentEquipment.imageUrl">
          <el-image :preview-teleported="true"
            :src="currentEquipment.imageUrl.startsWith('http') ? currentEquipment.imageUrl : baseUrl + '/' + currentEquipment.imageUrl"
            :preview-src-list="[currentEquipment.imageUrl.startsWith('http') ? currentEquipment.imageUrl : baseUrl + '/' + currentEquipment.imageUrl]"
            fit="cover" class="detail-equipment-image" />
        </div>
        <div class="equipment-image-container" v-else>
          <div class="no-image-large">
            <el-icon class="no-image-icon"><Picture /></el-icon>
            <span>暂无图片</span>
          </div>
        </div>
        
        <!-- 器材信息 -->
        <el-descriptions :column="2" border class="equipment-detail">
          <el-descriptions-item label="器材名称" :span="2" class="detail-item">
            {{ currentEquipment.name }}
          </el-descriptions-item>
          <el-descriptions-item label="器材类型" class="detail-item">
            {{ currentEquipment.equipmentType }}
          </el-descriptions-item>
          <el-descriptions-item label="状态" class="detail-item">
            <el-tag :type="getStatusType(currentEquipment.status)" size="small">
              {{ getStatusText(currentEquipment.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="位置" :span="2" class="detail-item">
            {{ currentEquipment.location }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2" class="detail-item">
            {{ currentEquipment.description || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="购买日期" class="detail-item">
            {{ currentEquipment.purchaseDate || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="最后维护日期" class="detail-item">
            {{ currentEquipment.lastMaintenanceDate || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleBookFromDetail" :disabled="currentEquipment?.status !== 'AVAILABLE'">
          预约
        </el-button>
      </template>
    </el-dialog>

    <!-- 预约对话框 -->
    <el-dialog v-model="bookDialogVisible" title="预约器材" width="500px" :close-on-click-modal="false" class="book-dialog">
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="100px" class="book-form">
        <el-form-item label="器材名称" class="form-item">
          <el-input :value="selectedEquipment?.name" disabled class="form-input" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime" class="form-item">
          <el-date-picker v-model="bookForm.startTime" type="datetime" placeholder="选择开始时间" class="form-input"
            :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime" class="form-item">
          <el-date-picker v-model="bookForm.endTime" type="datetime" placeholder="选择结束时间" class="form-input"
            :disabled-date="disabledDate" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitBook" :loading="bookLoading">确认预约</el-button>
      </template>
    </el-dialog>

    <!-- 我的预约对话框 -->
    <el-dialog v-model="showBookingsDialog" title="我的器材预约" width="900px" class="bookings-dialog">
      <el-table :data="myBookingsList" v-loading="bookingsLoading" stripe class="bookings-table">
        <el-table-column prop="id" label="预约ID" width="80" align="center" />
        <el-table-column prop="equipmentName" label="器材名称" min-width="150" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template v-slot="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template v-slot="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getReservationStatusColor(row.status)" size="small" class="status-tag">
              {{ getReservationStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkInStatus" label="签到状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.checkedIn && row.checkedOut" type="info" size="small" class="status-tag">已签退</el-tag>
            <el-tag v-else-if="row.checkedIn" type="success" size="small" class="status-tag">已签到</el-tag>
            <el-tag v-else type="warning" size="small" class="status-tag">未签到</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.checkedIn && row.status === 'CONFIRMED'" type="success" link size="small"
              @click="handleCheckIn(row)" class="action-btn">
              签到
            </el-button>
            <el-button v-if="row.checkedIn && !row.checkedOut" type="primary" link size="small"
              @click="handleCheckOut(row)" class="action-btn">
              签退
            </el-button>
            <el-button type="danger" link size="small" @click="handleCancelBooking(row)"
              :disabled="row.status === 'CANCELLED' || row.status === 'COMPLETED'" class="action-btn">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Ticket, Picture } from '@element-plus/icons-vue'
import { equipmentApi } from '@/api/equipment'
import { reservationApi } from '@/api/reservation'
import { checkInApi } from '@/api/checkIn'
import type { Equipment } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const detailDialogVisible = ref(false)
const bookDialogVisible = ref(false)
const bookLoading = ref(false)
const showBookingsDialog = ref(false)
const bookingsLoading = ref(false)
const currentEquipment = ref<Equipment | null>(null)
const selectedEquipment = ref<Equipment | null>(null)
const bookFormRef = ref<FormInstance>()

const equipmentList = ref<Equipment[]>([])
const myBookingsList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API

const searchParams = reactive({
  name: '',
  equipmentType: '',
  status: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const bookForm = reactive({
  startTime: '',
  endTime: ''
})

const bookRules: FormRules = {
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' },
    {
      validator: (rule, value, callback) => {
        if (value && bookForm.startTime && dayjs(value).isBefore(dayjs(bookForm.startTime))) {
          callback(new Error('结束时间不能早于开始时间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const getStatusType = (status: string) => {
  const map: Record<string, any> = {
    AVAILABLE: 'success',
    IN_USE: 'primary',
    MAINTENANCE: 'warning',
    DAMAGED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: '可用',
    IN_USE: '使用中',
    MAINTENANCE: '维护中',
    DAMAGED: '已损坏',
    RESERVATION: "已预约"
  }
  return map[status] || status
}

const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 8.64e7
}

const getReservationStatusColor = (status: string) => {
  const map: Record<string, any> = {
    PENDING: 'warning',
    CONFIRMED: 'success',
    CANCELLED: 'info',
    COMPLETED: 'primary'
  }
  return map[status] || ''
}

const getReservationStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    CANCELLED: '已取消',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

const loadEquipment = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }

    if (searchParams.name) params.name = searchParams.name
    if (searchParams.equipmentType) params.equipmentType = searchParams.equipmentType
    if (searchParams.status) params.status = searchParams.status

    const res = await equipmentApi.getEquipmentList(params)
    if (res.data) {
      equipmentList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载器材列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = async (row: Equipment) => {
  try {
    const res = await equipmentApi.getEquipmentById(row.id)
    currentEquipment.value = res.data
    detailDialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.message || '获取器材详情失败')
  }
}

const handleBook = (row: Equipment) => {
  if (row.status !== 'AVAILABLE') {
    ElMessage.warning('该器材当前不可预约')
    return
  }
  selectedEquipment.value = row
  bookForm.startTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
  bookForm.endTime = ''
  bookDialogVisible.value = true
}

const handleBookFromDetail = () => {
  if (currentEquipment.value) {
    detailDialogVisible.value = false
    handleBook(currentEquipment.value)
  }
}

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const handleSubmitBook = async () => {
  if (!bookFormRef.value) return

  await bookFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      bookLoading.value = true
      await reservationApi.reserveEquipment({
        equipmentId: selectedEquipment.value!.id,
        startTime: dayjs(bookForm.startTime).format('YYYY-MM-DD HH:mm:ss'),
        endTime: dayjs(bookForm.endTime).format('YYYY-MM-DD HH:mm:ss')
      })

      ElMessage.success('预约成功')
      bookDialogVisible.value = false
      loadEquipment()
    } catch (error: any) {
      // ElMessage.error(error.message || '预约失败')
    } finally {
      bookLoading.value = false
    }
  })
}

const handleReset = () => {
  searchParams.name = ''
  searchParams.equipmentType = ''
  searchParams.status = ''
  pagination.current = 1
  loadEquipment()
}

const showMyBookings = async () => {
  try {
    bookingsLoading.value = true
    showBookingsDialog.value = true
    const res = await reservationApi.getMyReservations({ reservationType: 'EQUIPMENT' })
    if (res.data) {
      myBookingsList.value = res.data || []
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载预约列表失败')
  } finally {
    bookingsLoading.value = false
  }
}

const handleCheckIn = async (reservation: any) => {
  try {
    await ElMessageBox.confirm(`确定签到"${reservation.equipmentName}"吗？`, '签到确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    const location = reservation.equipmentLocation || reservation.location || '默认位置'
    await checkInApi.checkIn(reservation.id, location)
    ElMessage.success('签到成功')
    showMyBookings()
  } catch (error: any) {
    if (error !== 'cancel') {
      // ElMessage.error(error.message || '签到失败')
    }
  }
}

const handleCheckOut = async (reservation: any) => {
  try {
    await ElMessageBox.confirm(`确定签退"${reservation.equipmentName}"吗？`, '签退确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await checkInApi.checkOut(reservation.id)
    ElMessage.success('签退成功')
    showMyBookings()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '签退失败')
    }
  }
}

const handleCancelBooking = async (reservation: any) => {
  try {
    await ElMessageBox.confirm(`确定取消预约"${reservation.equipmentName}"吗？`, '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await reservationApi.cancelReservation(reservation.id)
    ElMessage.success('取消预约成功')
    showMyBookings()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消预约失败')
    }
  }
}

watch(() => pagination.current, () => {
  loadEquipment()
})

watch(() => pagination.size, () => {
  pagination.current = 1
  loadEquipment()
})

onMounted(() => {
  loadEquipment()
})
</script>

<style scoped>
.equipment-page {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.main-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 80px;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.my-bookings-btn {
  margin-left: 0 !important;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 220px;
}

.search-select {
  width: 160px;
}

.search-btn,
.reset-btn {
  min-width: 90px;
}

.equipment-table {
  margin-top: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.equipment-image {
  width: 70px;
  height: 70px;
  border-radius: 6px;
  transition: transform 0.3s ease;
}

.equipment-image:hover {
  transform: scale(1.05);
}

.no-image {
  display: inline-block;
  width: 70px;
  height: 70px;
  line-height: 70px;
  text-align: center;
  background-color: #f5f7fa;
  border-radius: 6px;
  color: #909399;
}

.status-tag {
  border-radius: 12px;
  padding: 2px 10px;
}

.action-btn {
  margin-right: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  opacity: 0.8;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding-right: 20px;
}

/* 详情对话框 */
.detail-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.equipment-detail-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.equipment-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}

.detail-equipment-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.detail-equipment-image:hover {
  transform: scale(1.02);
}

.no-image-large {
  width: 200px;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  border-radius: 8px;
  color: #909399;
  border: 2px dashed #dcdfe6;
}

.no-image-icon {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.5;
}

.equipment-detail {
  margin-bottom: 0;
}

.detail-item {
  padding: 12px 16px;
}

.detail-item:nth-child(odd) {
  background-color: #f9f9f9;
}

/* 预约对话框 */
.book-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.book-form {
  padding: 0 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-input {
  width: 100%;
}

/* 我的预约对话框 */
.bookings-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.bookings-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 修复图片预览器层级问题 */
:deep(.el-image-viewer__wrapper) {
  z-index: 9999 !important;
}

/* 响应式 */
@media (max-width: 1200px) {
  .card-header {
    flex-direction: column;
    height: auto;
    padding: 16px 20px;
    gap: 16px;
    align-items: flex-start;
  }

  .search-bar {
    width: 100%;
    flex-wrap: wrap;
  }

  .search-input {
    width: 200px;
  }

  .search-select {
    width: 140px;
  }
}

@media (max-width: 768px) {
  .equipment-page {
    padding: 10px;
  }

  .main-card {
    border-radius: 8px;
  }

  .card-header {
    padding: 12px 16px;
  }

  .page-title {
    font-size: 18px;
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }

  .search-input,
  .search-select {
    width: 100% !important;
  }

  .search-btn,
  .reset-btn {
    width: 100%;
  }

  .equipment-table {
    margin-top: 16px;
  }

  .equipment-image {
    width: 60px;
    height: 60px;
  }

  .no-image {
    width: 60px;
    height: 60px;
    line-height: 60px;
  }

  .pagination {
    justify-content: center;
    padding-right: 0;
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

  .form-item {
    margin-bottom: 15px;
  }
}
</style>
