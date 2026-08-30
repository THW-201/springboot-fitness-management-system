<template>
  <div class="courses-page">
    <el-row :gutter="24">
      <el-col :xl="5" :lg="6" :md="7" :sm="24" :xs="24">
        <!-- 筛选面板 -->
        <el-card shadow="hover" class="filter-card">
          <template #header>
            <div class="filter-header">
              <h3 class="filter-title">课程筛选</h3>
              <el-button size="small" @click="handleReset" class="reset-btn">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
            </div>
          </template>
          <el-form label-position="top" class="filter-form">
            <el-form-item label="课程状态" class="filter-item">
              <el-select v-model="filterStatus" placeholder="全部状态" class="filter-select" @change="loadCourses">
                <el-option label="全部" value="" />
                <el-option label="可预约" value="AVAILABLE" />
                <el-option label="已满" value="FULL" />
                <el-option label="已取消" value="CANCELLED" />
                <el-option label="已完成" value="COMPLETED" />
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <el-col :xl="19" :lg="18" :md="17" :sm="24" :xs="24">
        <!-- 课程卡片列表 -->
        <el-card shadow="hover" class="courses-list-card">
          <template #header>
            <div class="card-header">
              <h2 class="page-title">可选课程</h2>
              <div class="header-actions">
                <el-button type="success" size="small" @click="showMyBookings" class="my-bookings-btn">
                  <el-icon><Ticket /></el-icon>
                  我的预约
                </el-button>
              </div>
            </div>
          </template>

          <el-row :gutter="24" v-loading="loading" class="courses-grid">
            <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8" v-for="course in courses" :key="course.id">
              <el-card :body-style="{ padding: '0px' }" shadow="hover" class="course-card">
                <div class="course-image-container">
                  <img :src="baseUrl + '/' + course.imageUrl" alt="" class="course-image">
                  <div class="course-badge">
                    <el-tag type="danger" size="small" class="course-type-tag">
                      {{ course.courseType }}
                    </el-tag>
                  </div>
                </div>
                <div class="course-content">
                  <h3 class="course-title" :title="course.name">{{ course.name }}</h3>
                  <p class="course-desc" :title="course.description">{{ course.description }}</p>
                  <div class="course-info">
                    <el-icon class="info-icon"><User /></el-icon>
                    <span>{{ course.coachName }}</span>
                  </div>
                  <div class="course-info">
                    <el-icon class="info-icon"><Clock /></el-icon>
                    <span>{{ formatDateTime(course.startTime) }}</span>
                  </div>
                  <div class="course-info">
                    <el-icon class="info-icon"><Clock /></el-icon>
                    <span>{{ formatDateTime(course.endTime) }}</span>
                  </div>
                  <div class="course-info">
                    <el-icon class="info-icon"><Location /></el-icon>
                    <span>{{ course.location }}</span>
                  </div>
                  <div class="course-footer">
                    <div class="progress">
                      <div class="progress-info">
                        <span class="progress-text">{{ (course.totalReservations - course.cancelledReservations) || 0 }} / {{ course.capacity }} 人</span>
                        <span class="progress-percentage">{{ getProgress(course) }}%</span>
                      </div>
                      <el-progress :percentage="getProgress(course)"
                        :status="(course.totalReservations - course.cancelledReservations) >= course.capacity ? 'warning' : 'success'"
                        :show-text="false" class="progress-bar" />
                    </div>
                    <el-button type="primary" size="small" :disabled="course.status !== 'AVAILABLE'"
                      @click="handleBook(course)" class="book-btn">
                      {{ course.status === 'AVAILABLE' ? '立即预约' : '不可预约' }}
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <div class="pagination-container">
            <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size"
              :total="pagination.total" :page-sizes="[9, 18, 27]" layout="total, sizes, prev, pager, next, jumper"
              class="pagination" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 我的预约对话框 -->
    <el-dialog v-model="showBookingsDialog" title="我的预约" width="900px" class="bookings-dialog">
      <el-table :data="myBookingsList" v-loading="bookingsLoading" stripe class="bookings-table">
        <el-table-column prop="reservationType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.reservationType === 'COURSE' ? 'primary' : 'success'" size="small" class="status-tag">
              {{ row.reservationType === 'COURSE' ? '课程' : '器材' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="预约项目" min-width="150">
          <template #default="{ row }">
            {{ row.courseName || row.equipmentName }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
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
            <el-button 
              v-if="!row.checkedIn && row.status === 'CONFIRMED' " 
              type="success" 
              link 
              size="small" 
              @click="handleCheckIn(row)"
              class="action-btn"
            >
              签到
            </el-button>
            <el-button 
              v-if="row.checkedIn && !row.checkedOut" 
              type="primary" 
              link 
              size="small" 
              @click="handleCheckOut(row)"
              class="action-btn"
            >
              签退
            </el-button>
            <el-button 
              type="danger" 
              link 
              size="small" 
              @click="handleCancelBooking(row)"
              :disabled="row.status === 'CANCELLED' || row.status === 'COMPLETED' || row.checkedIn || row.checkedOut"
              class="action-btn"
            >
              取消预约
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Ticket, User, Clock, Location } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { reservationApi } from '@/api/reservation'
import { checkInApi } from '@/api/checkIn'
import dayjs from 'dayjs'

const baseUrl = import.meta.env.VITE_BASE_URL
const loading = ref(false)
const filterStatus = ref('')
const viewMode = ref('card')
const showBookingsDialog = ref(false)
const myBookingsList = ref<any[]>([])
const bookingsLoading = ref(false)

const courses = ref<any[]>([])

const pagination = reactive({
  current: 1,
  size: 9,
  total: 0
})

const getProgress = (course: any) => {
  const actualStudents = (course.totalReservations - course.cancelledReservations) || 0
  return Number(((actualStudents / course.capacity) * 100).toFixed(0))
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
const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
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

const loadCourses = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.current,
      size: pagination.size
    }

    if (filterStatus.value) {
      params.status = filterStatus.value
    }

    const res = await courseApi.getCourses(params)
    if (res.data) {
      courses.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  filterStatus.value = ''
  pagination.current = 1
  loadCourses()
}

watch(() => pagination.current, () => {
  loadCourses()
})

watch(() => pagination.size, () => {
  pagination.current = 1
  loadCourses()
})

const handleBook = async (course: any) => {
  try {
    await ElMessageBox.confirm(`确定预约课程"${course.name}"吗？`, '预约确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await reservationApi.reserveCourse({ courseId: course.id })
    ElMessage.success(`预约成功：${course.name}`)
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      // ElMessage.error(error.message || '预约失败')
    }
  }
}

const showMyBookings = async () => {
  try {
    bookingsLoading.value = true
    showBookingsDialog.value = true
    const res = await reservationApi.getMyReservations({ reservationType: 'COURSE' })
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
    await ElMessageBox.confirm(`确定签到"${reservation.courseName || reservation.equipmentName}"吗？`, '签到确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    const location = reservation.courseLocation || reservation.equipmentLocation || reservation.location || '默认位置'
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
    await ElMessageBox.confirm(`确定签退"${reservation.courseName || reservation.equipmentName}"吗？`, '签退确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })

    await checkInApi.checkOut(reservation.id)
    ElMessage.success('签退成功')
    showMyBookings()
  } catch (error: any) {
    if (error !== 'cancel') {
    }
  }
}

const handleCancelBooking = async (reservation: any) => {
  try {
    await ElMessageBox.confirm(`确定取消预约"${reservation.courseName || reservation.equipmentName}"吗？`, '取消预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await reservationApi.cancelReservation(reservation.id)
    ElMessage.success('取消预约成功')
    showMyBookings()
    loadCourses()
  } catch (error: any) {
    if (error !== 'cancel') {
      // ElMessage.error(error.message || '取消预约失败')
    }
  }
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.courses-page {
  padding: 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.filter-card {
  position: sticky;
  top: 20px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #ffffff;
  border-bottom: 1px solid #f0f0f0;
}

.filter-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.reset-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.filter-form {
  padding: 20px;
}

.filter-item {
  margin-bottom: 16px;
}

.filter-select {
  width: 100%;
}

.courses-list-card {
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

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.my-bookings-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.courses-grid {
  padding: 20px;
}

.course-card {
  margin-bottom: 24px;
  transition: all 0.3s ease;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.course-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image {
  transform: scale(1.08);
}

.course-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 1;
}

.course-type-tag {
  border-radius: 12px;
  padding: 4px 12px;
  font-size: 12px;
}

.course-content {
  padding: 20px;
  position: relative;
  box-sizing: border-box;
  background-color: #ffffff;
}

.course-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  height: 52px;
  line-height: 26px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  box-sizing: border-box;
}

.course-desc {
  font-size: 14px;
  color: #909399;
  height: 66px;
  line-height: 22px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  box-sizing: border-box;
  margin-bottom: 16px;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
}

.info-icon {
  font-size: 16px;
  color: #909399;
}

.course-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.progress {
  margin-bottom: 12px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.progress-text {
  font-size: 12px;
  color: #909399;
}

.progress-percentage {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

.progress-bar {
  height: 6px;
  border-radius: 3px;
}

.book-btn {
  width: 100%;
  padding: 8px 0;
  font-size: 14px;
  font-weight: 500;
}

.pagination-container {
  padding: 20px;
  background-color: #ffffff;
  border-top: 1px solid #f0f0f0;
}

.pagination {
  display: flex;
  justify-content: flex-end;
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

/* 响应式 */
@media (max-width: 1200px) {
  .courses-page {
    padding: 16px;
  }
  
  .card-header {
    padding: 0 16px;
    height: 70px;
  }
  
  .page-title {
    font-size: 18px;
  }
  
  .courses-grid {
    padding: 16px;
  }
  
  .course-card {
    margin-bottom: 20px;
  }
  
  .course-content {
    padding: 16px;
  }
  
  .pagination-container {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .courses-page {
    padding: 10px;
  }

  .filter-card {
    position: static;
    margin-bottom: 16px;
  }

  .filter-header {
    padding: 0 16px;
    height: 56px;
  }

  .filter-title {
    font-size: 15px;
  }

  .filter-form {
    padding: 16px;
  }

  .card-header {
    flex-direction: column;
    height: auto;
    padding: 16px;
    align-items: flex-start;
    gap: 12px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .courses-grid {
    padding: 12px;
  }

  .course-card {
    margin-bottom: 16px;
  }

  .course-image-container {
    height: 180px;
  }

  .course-content {
    padding: 16px;
  }

  .course-title {
    font-size: 16px;
    height: 44px;
    line-height: 22px;
  }

  .course-desc {
    font-size: 13px;
    height: 58px;
    line-height: 19px;
  }

  .course-info {
    font-size: 12px;
    margin-bottom: 8px;
  }

  .pagination-container {
    padding: 16px 12px;
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
}

/* 修复图片预览器层级问题 */
:deep(.el-image-viewer__wrapper) {
  z-index: 9999 !important;
}
</style>
