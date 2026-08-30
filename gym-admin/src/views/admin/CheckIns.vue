<template>
  <div class="checkins-management">
    <div class="page-header">
      <h2>签到打卡管理</h2>
      <p>管理学生的签到记录，查看统计信息</p>
    </div>

    <div class="content-container">
      <!-- 统计卡片 -->
      <div class="stats-cards">
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ totalCheckIns }}</h3>
            <p>总签到次数</p>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ completedCheckIns }}</h3>
            <p>已完成签到</p>
          </div>
        </el-card>
        <el-card class="stat-card">
          <div class="stat-content">
            <h3>{{ pendingCheckIns }}</h3>
            <p>未签退</p>
          </div>
        </el-card>
      </div>

      <!-- 标签页 -->
      <el-tabs v-model="activeTab" class="tabs" @tab-change="handleTabChange">
        <el-tab-pane label="签到记录" name="checkins">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-form :inline="true" :model="searchForm" class="search-form">
              <el-form-item label="学生ID">
                <el-input v-model="searchForm.studentId" placeholder="输入学生ID" />
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
                <el-button type="primary" @click="searchCheckIns">搜索</el-button>
                <el-button @click="resetSearch">重置</el-button>
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
              <el-table-column prop="studentName" label="学生姓名" width="120" />
              <el-table-column prop="reservationId" label="预约ID" width="100" />
              <el-table-column prop="reservationType" label="预约类型" width="100">
                <template #default="scope">
                  {{ scope.row.reservationType === 'COURSE' ? '课程' : '器材' }}
                </template>
              </el-table-column>
              <el-table-column prop="checkInTime" label="签到时间" width="180" />
              <el-table-column prop="checkOutTime" label="签退时间" width="180" />
              <el-table-column prop="duration" label="时长(分钟)" width="100" />
              <el-table-column prop="calories" label="消耗卡路里" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 'CHECKED_IN' ? 'warning' : 'success'">
                    {{ scope.row.status === 'CHECKED_IN' ? '已签到' : '已签退' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button
                    type="primary"
                    size="small"
                    @click="viewCheckInDetail(scope.row.id)"
                    v-if="scope.row.status === 'CHECKED_OUT'"
                  >
                    查看详情
                  </el-button>
                  <el-button
                    type="warning"
                    size="small"
                    @click="manageCheckIn(scope.row.id)"
                    v-else
                  >
                    管理
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="未签到预约" name="unchecked">
          <!-- 搜索和筛选 -->
          <div class="search-filter">
            <el-form :inline="true" :model="uncheckedSearchForm" class="search-form">
              <el-form-item label="学生ID">
                <el-input v-model="uncheckedSearchForm.studentId" placeholder="输入学生ID" />
              </el-form-item>
              <el-form-item label="日期范围">
                <el-date-picker
                  v-model="uncheckedSearchForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="searchUncheckedReservations">搜索</el-button>
                <el-button @click="resetUncheckedSearch">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 未签到预约列表 -->
          <el-card class="checkins-list-card">
            <template #header>
              <div class="card-header">
                <span>未签到预约列表</span>
              </div>
            </template>
            <el-table :data="uncheckedReservations" style="width: 100%" v-loading="uncheckedLoading">
              <el-table-column prop="id" label="预约ID" width="100" />
              <el-table-column prop="studentId" label="学生ID" width="100" />
              <el-table-column prop="studentName" label="学生姓名" width="120" />
              <el-table-column prop="reservationType" label="预约类型" width="100">
                <template #default="scope">
                  {{ scope.row.reservationType === 'COURSE' ? '课程' : '器材' }}
                </template>
              </el-table-column>
              <el-table-column prop="courseName" label="课程名称" width="150" />
              <el-table-column prop="startTime" label="开始时间" width="180" />
              <el-table-column prop="endTime" label="结束时间" width="180" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag type="info">{{ scope.row.status === 'CONFIRMED' ? '已确认' : scope.row.status }}</el-tag>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination">
              <el-pagination
                v-model:current-page="uncheckedCurrentPage"
                v-model:page-size="uncheckedPageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="uncheckedTotal"
                @size-change="handleUncheckedSizeChange"
                @current-change="handleUncheckedCurrentChange"
              />
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
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
          <el-descriptions-item label="学生姓名">{{ selectedCheckIn.studentName }}</el-descriptions-item>
          <el-descriptions-item label="预约ID">{{ selectedCheckIn.reservationId }}</el-descriptions-item>
          <el-descriptions-item label="预约类型">{{ selectedCheckIn.reservationType === 'COURSE' ? '课程' : '器材' }}</el-descriptions-item>
          <el-descriptions-item label="签到时间">{{ selectedCheckIn.checkInTime }}</el-descriptions-item>
          <el-descriptions-item label="签退时间">{{ selectedCheckIn.checkOutTime }}</el-descriptions-item>
          <el-descriptions-item label="时长(分钟)">{{ selectedCheckIn.duration }}</el-descriptions-item>
          <el-descriptions-item label="消耗卡路里">{{ selectedCheckIn.calories }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ selectedCheckIn.status === 'CHECKED_IN' ? '已签到' : '已签退' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 管理签到对话框 -->
    <el-dialog
      v-model="manageDialogVisible"
      title="管理签到"
      width="400px"
    >
      <div class="manage-checkin" v-if="selectedCheckIn">
        <el-form :model="manageForm">
          <el-form-item label="签到状态">
            <el-select v-model="manageForm.status" placeholder="选择状态">
              <el-option label="已签到" value="CHECKED_IN" />
              <el-option label="已签退" value="CHECKED_OUT" />
            </el-select>
          </el-form-item>
          <el-form-item label="签退时间" v-if="manageForm.status === 'CHECKED_OUT'">
            <el-datetime-picker v-model="manageForm.checkOutTime" type="datetime" placeholder="选择签退时间" />
          </el-form-item>
          <el-form-item label="消耗卡路里" v-if="manageForm.status === 'CHECKED_OUT'">
            <el-input-number v-model="manageForm.calories" :min="0" :step="10" placeholder="输入消耗卡路里" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="manageDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateCheckInStatus">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { checkInApi } from '@/api/checkIn'

// 标签页
const activeTab = ref('checkins')

// 统计数据
const totalCheckIns = ref(0)
const completedCheckIns = ref(0)
const pendingCheckIns = ref(0)

// 搜索表单
const searchForm = ref({
  studentId: '',
  status: '',
  dateRange: []
})

// 签到记录列表
const checkInsList = ref<any[]>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 未签到预约搜索表单
const uncheckedSearchForm = ref({
  studentId: '',
  dateRange: []
})

// 未签到预约列表
const uncheckedReservations = ref<any[]>([])

// 未签到预约分页
const uncheckedCurrentPage = ref(1)
const uncheckedPageSize = ref(10)
const uncheckedTotal = ref(0)

// 对话框
const detailDialogVisible = ref(false)
const manageDialogVisible = ref(false)
const selectedCheckIn = ref<any>(null)

// 管理表单
const manageForm = ref({
  status: '',
  checkOutTime: '',
  calories: 0
})

// 加载状态
const loading = ref(false)
const uncheckedLoading = ref(false)

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await checkInApi.getCheckInStatistics()
    if (res.data) {
      totalCheckIns.value = res.data.totalCheckIns
      completedCheckIns.value = res.data.completedCheckIns
      pendingCheckIns.value = res.data.pendingCheckIns
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载签到记录
const loadCheckIns = async () => {
  loading.value = true
  try {
    const params = {
      studentId: searchForm.value.studentId ? Number(searchForm.value.studentId) : undefined,
      status: searchForm.value.status,
      startDate: searchForm.value.dateRange && searchForm.value.dateRange[0] ? (searchForm.value.dateRange[0] as Date).toISOString() : undefined,
      endDate: searchForm.value.dateRange && searchForm.value.dateRange[1] ? (searchForm.value.dateRange[1] as Date).toISOString() : undefined
    }
    const res = await checkInApi.getAllCheckIns(params)
    if (res.data) {
      checkInsList.value = res.data
      total.value = res.data.length
    }
  } catch (error) {
    ElMessage.error('加载签到记录失败')
  } finally {
    loading.value = false
  }
}

// 加载未签到预约记录
const loadUncheckedReservations = async () => {
  uncheckedLoading.value = true
  try {
    const params = {
      studentId: uncheckedSearchForm.value.studentId ? Number(uncheckedSearchForm.value.studentId) : undefined,
      startDate: uncheckedSearchForm.value.dateRange && uncheckedSearchForm.value.dateRange[0] ? (uncheckedSearchForm.value.dateRange[0] as Date).toISOString() : undefined,
      endDate: uncheckedSearchForm.value.dateRange && uncheckedSearchForm.value.dateRange[1] ? (uncheckedSearchForm.value.dateRange[1] as Date).toISOString() : undefined
    }
    const res = await checkInApi.getUncheckedInReservations(params)
    if (res.data) {
      uncheckedReservations.value = res.data
      uncheckedTotal.value = res.data.length
    }
  } catch (error) {
    ElMessage.error('加载未签到预约记录失败')
  } finally {
    uncheckedLoading.value = false
  }
}

// 搜索签到记录
const searchCheckIns = () => {
  currentPage.value = 1
  loadCheckIns()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    studentId: '',
    status: '',
    dateRange: []
  }
  currentPage.value = 1
  loadCheckIns()
}

// 搜索未签到预约记录
const searchUncheckedReservations = () => {
  uncheckedCurrentPage.value = 1
  loadUncheckedReservations()
}

// 重置未签到预约搜索
const resetUncheckedSearch = () => {
  uncheckedSearchForm.value = {
    studentId: '',
    dateRange: []
  }
  uncheckedCurrentPage.value = 1
  loadUncheckedReservations()
}

// 查看签到详情
const viewCheckInDetail = async (id: number) => {
  try {
    const res = await checkInApi.getCheckInDetail(id)
    if (res.data) {
      selectedCheckIn.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取签到详情失败')
  }
}

// 管理签到
const manageCheckIn = async (id: number) => {
  try {
    const res = await checkInApi.getCheckInDetail(id)
    if (res.data) {
      selectedCheckIn.value = res.data
      manageForm.value = {
        status: selectedCheckIn.value?.status || 'CHECKED_IN',
        checkOutTime: selectedCheckIn.value?.checkOutTime || '',
        calories: selectedCheckIn.value?.calories || 0
      }
      manageDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取签到详情失败')
  }
}

// 更新签到状态
const updateCheckInStatus = async () => {
  if (!selectedCheckIn.value) return
  
  const loadingInstance = ElLoading.service({
    lock: true,
    text: '正在更新状态...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    const res = await checkInApi.updateCheckInStatus(selectedCheckIn.value.id, {
      status: manageForm.value.status,
      checkOutTime: manageForm.value.checkOutTime,
      calories: manageForm.value.calories
    })
    if (res.data) {
      ElMessage.success('签到状态已更新')
      manageDialogVisible.value = false
      loadCheckIns()
      loadStatistics()
    }
  } catch (error) {
    ElMessage.error('更新签到状态失败')
  } finally {
    loadingInstance.close()
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadCheckIns()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  loadCheckIns()
}

// 未签到预约分页处理
const handleUncheckedSizeChange = (size: number) => {
  uncheckedPageSize.value = size
  loadUncheckedReservations()
}

const handleUncheckedCurrentChange = (current: number) => {
  uncheckedCurrentPage.value = current
  loadUncheckedReservations()
}

// 标签页切换处理
const handleTabChange = (tab: string) => {
  if (tab === 'unchecked') {
    loadUncheckedReservations()
  }
}

// 初始加载
onMounted(() => {
  loadStatistics()
  loadCheckIns()
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
.manage-checkin {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>