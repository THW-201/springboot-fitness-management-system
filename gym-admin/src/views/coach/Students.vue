<template>
  <div class="students-container">
    <div class="students-page">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <span>我的学生</span>
                <div class="header-actions">
                  <el-tabs v-model="activeTab" @tab-click="handleTabChange" style="margin-right: 20px">
                    <el-tab-pane label="分配的学生" name="assigned"></el-tab-pane>
                    <el-tab-pane label="课程学生" name="course"></el-tab-pane>
                    <el-tab-pane label="全部学生" name="all"></el-tab-pane>
                  </el-tabs>
                  <el-input
                    v-model="searchKeyword"
                    placeholder="搜索学生"
                    style="width: 200px"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Search /></el-icon>
                    </template>
                  </el-input>
                </div>
              </div>
            </template>

            <el-table :data="filteredStudentList" stripe border v-loading="loading" empty-text="暂无学生数据">
              <el-table-column prop="id" label="ID" width="80" align="center" />
              <el-table-column prop="realName" label="姓名" width="120" align="center" />
              <el-table-column prop="username" label="用户名" width="120" align="center" />
              <el-table-column prop="email" label="邮箱" min-width="180" />
              <el-table-column label="学生类型" width="120" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.isAssigned ? 'success' : 'primary'">
                    {{ row.isAssigned ? '分配的' : '课程的' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="phone" label="手机号" width="130" align="center" />

              <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
                <template #default="{ row }">
                  {{ formatDateTime(row.createdAt) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="300" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" @click="handleViewPlans(row)">
                    查看计划
                  </el-button>
                  <el-button type="success" link size="small" @click="handleCreatePlan(row)">
                    创建计划
                  </el-button>
                  <el-button type="danger" link size="small" @click="handleDeleteStudent(row)" v-if="!row.isAssigned">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-model:current-page="currentPagination.page"
              v-model:page-size="currentPagination.pageSize"
              :total="currentPagination.total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              class="pagination"
              @size-change="handleSizeChange"
              @current-change="handlePageChange"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>

  <!-- 查看学生计划列表对话框 -->
  <el-dialog
    v-model="plansDialogVisible"
    :title="`${currentStudent?.realName} 的健康计划`"
    width="900px"
    :close-on-click-modal="false"
  >
    <el-table :data="studentPlans" stripe border v-loading="plansLoading" empty-text="该学生还没有健康计划">
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
          <div v-if="row.targetDurationMinutes">
            目标: {{ row.targetDurationMinutes }}分钟/周
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120" align="center" />
      <el-table-column prop="endDate" label="结束日期" width="120" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="getPlanStatusType(row.status)">{{ getPlanStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="完成度" width="100" align="center">
        <template #default="{ row }">
          {{ row.completionPercentage }}%
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="plansDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>

  <!-- 创建健康计划对话框 -->
  <el-dialog
    v-model="createPlanDialogVisible"
    title="创建健康计划"
    width="600px"
    :close-on-click-modal="false"
  >
    <el-form
      ref="planFormRef"
      :model="planForm"
      :rules="planFormRules"
      label-width="120px"
    >
      <el-form-item label="学生">
        <el-input :value="currentStudent?.realName" disabled />
      </el-form-item>
      <el-form-item label="计划名称" prop="planName">
        <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
      </el-form-item>
      <el-form-item label="计划描述" prop="description">
        <el-input
          v-model="planForm.description"
          type="textarea"
          :rows="3"
          placeholder="请输入计划描述"
        />
      </el-form-item>
      <el-form-item label="当前体重" prop="currentWeight">
        <el-input-number
          v-model="planForm.currentWeight"
          :min="0.01"
          :max="999"
          :precision="2"
          :step="0.1"
          placeholder="请输入当前体重"
        />
        <span style="margin-left: 10px">kg</span>
      </el-form-item>
      <el-form-item label="目标体重" prop="targetWeight">
        <el-input-number
          v-model="planForm.targetWeight"
          :min="0.01"
          :max="999"
          :precision="2"
          :step="0.1"
          placeholder="请输入目标体重"
        />
        <span style="margin-left: 10px">kg</span>
      </el-form-item>
      <el-form-item label="目标运动时长" prop="targetDurationMinutes">
        <el-input-number
          v-model="planForm.targetDurationMinutes"
          :min="1"
          :max="9999"
          :step="10"
          placeholder="请输入目标运动时长"
        />
        <span style="margin-left: 10px">分钟/周</span>
      </el-form-item>
      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="planForm.startDate"
          type="date"
          placeholder="选择开始日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="planForm.endDate"
          type="date"
          placeholder="选择结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createPlanDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleCreatePlanSubmit" :loading="submitLoading">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { healthPlanApi } from '@/api/healthPlan'
import { reservationApi } from '@/api/reservation'
import type { User, CreateHealthPlanRequest } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const submitLoading = ref(false)
const plansLoading = ref(false)
const searchKeyword = ref('')
const createPlanDialogVisible = ref(false)
const plansDialogVisible = ref(false)
const currentStudent = ref<User | null>(null)
const planFormRef = ref<FormInstance>()
const studentPlans = ref<any[]>([])
const studentList = ref<User[]>([])
const courseStudents = ref<User[]>([])
const allStudents = ref<User[]>([])
const activeTab = ref('assigned')

// 分页类型定义
interface Pagination {
  page: number
  pageSize: number
  total: number
}

// 为不同标签页设置独立的分页状态
const pagination = reactive<{
  assigned: Pagination
  course: Pagination
  all: Pagination
}>({
  assigned: {
    page: 1,
    pageSize: 10,
    total: 0
  },
  course: {
    page: 1,
    pageSize: 10,
    total: 0
  },
  all: {
    page: 1,
    pageSize: 10,
    total: 0
  }
})

// 当前标签页的分页状态
const currentPagination = computed((): Pagination => {
  switch (activeTab.value) {
    case 'assigned':
      return pagination.assigned
    case 'course':
      return pagination.course
    case 'all':
      return pagination.all
    default:
      return pagination.assigned
  }
})

const planForm = reactive<CreateHealthPlanRequest>({
  studentId: 0,
  planName: '',
  description: '',
  currentWeight: undefined,
  targetWeight: undefined,
  targetDurationMinutes: undefined,
  startDate: '',
  endDate: ''
})

const planFormRules: FormRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' },
    {
      validator: (_rule, value, callback) => {
        if (value && planForm.startDate && value <= planForm.startDate) {
          callback(new Error('结束日期必须晚于开始日期'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const getPlanStatusType = (status: string) => {
  const map: Record<string, any> = {
    ACTIVE: 'success',
    COMPLETED: 'info',
    ABANDONED: 'warning'
  }
  return map[status] || ''
}

const getPlanStatusText = (status: string) => {
  const map: Record<string, string> = {
    ACTIVE: '进行中',
    COMPLETED: '已完成',
    ABANDONED: '已放弃'
  }
  return map[status] || status
}

const filteredStudentList = computed(() => {
  let students: User[] = []
  switch (activeTab.value) {
    case 'assigned':
      students = studentList.value
      break
    case 'course':
      students = courseStudents.value
      break
    case 'all':
      students = allStudents.value
      break
    default:
      students = studentList.value
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    students = students.filter(student => 
      student.realName?.toLowerCase().includes(keyword) ||
      student.username?.toLowerCase().includes(keyword) ||
      student.email?.toLowerCase().includes(keyword)
    )
  }
  
  return students
})

const fetchAssignedStudents = async () => {
  try {
    console.log('开始获取分配的学生...')
    const params: any = {
      current: pagination.assigned.page,
      size: pagination.assigned.pageSize
    }
    if (searchKeyword.value) {
      params.realName = searchKeyword.value
    }
    const res = await userApi.getCoachStudents(params)
    console.log('分配的学生API返回:', res)
    
    studentList.value = (res.data?.records || []).map((student: User) => ({
      ...student,
      isAssigned: true
    }))
    pagination.assigned.total = res.data?.total || 0
    console.log('分配的学生数量:', studentList.value.length)
  } catch (error: any) {
    console.error('获取分配的学生失败:', error)
    ElMessage.error(error.message || '获取分配的学生列表失败')
  }
}

const fetchCourseStudents = async () => {
  try {
    console.log('开始获取课程学生...')
    const res = await reservationApi.getReservations({ reservationType: 'COURSE' })
    console.log('预约API返回:', res)
    
    // 确保正确访问响应数据
    const courseReservations = res.data || []
    console.log('课程预约数量:', courseReservations.length)
    
    // 提取学生ID并去重
    const studentIds = [...new Set(courseReservations.map((res: any) => Number(res.studentId)).filter(Boolean))]
    console.log('学生ID列表:', studentIds)
    
    // 分页处理
    const startIndex = (pagination.course.page - 1) * pagination.course.pageSize
    const endIndex = startIndex + pagination.course.pageSize
    const paginatedStudentIds = studentIds.slice(startIndex, endIndex)
    console.log('分页后的学生ID列表:', paginatedStudentIds)
    
    const studentsMap = new Map<number, User>()
    
    for (const studentId of paginatedStudentIds) {
      try {
        console.log('获取学生详情:', studentId)
        const userRes = await userApi.getUserById(Number(studentId))
        console.log('学生详情API返回:', userRes)
        if (userRes.data) {
          console.log('学生详情数据:', userRes.data)
          console.log('学生详情数据是否包含studentProfile:', 'studentProfile' in userRes.data)
          console.log('学生详情数据的studentProfile值:', userRes.data.studentProfile)
          studentsMap.set(Number(studentId), {
            ...userRes.data,
            studentProfile: userRes.data.studentProfile || null,
            isAssigned: false
          } as User)
        } else {
          // 如果没有学生详情，尝试从预约信息中获取
          console.log('学生详情为空，尝试从预约信息中获取')
          const reservation = courseReservations.find((r: any) => Number(r.studentId) === Number(studentId))
          if (reservation) {
            studentsMap.set(Number(studentId), {
              id: Number(studentId),
              realName: reservation.studentName || reservation.student?.realName || '未知',
              username: `student_${studentId}`,
              email: `student${studentId}@example.com`,
              phone: reservation.student?.phone || '',
              role: 'STUDENT' as const,
              status: 1,
              createdAt: reservation.createdAt,
              updatedAt: reservation.updatedAt,
              studentProfile: reservation.student?.studentProfile || null,
              isAssigned: false
            } as User)
          }
        }
      } catch (error) {
        console.error(`获取学生 ${studentId} 详情失败:`, error)
        // 尝试从预约信息中获取学生信息
        const reservation = courseReservations.find((r: any) => Number(r.studentId) === Number(studentId))
        if (reservation) {
          studentsMap.set(Number(studentId), {
            id: Number(studentId),
            realName: reservation.studentName || reservation.student?.realName || '未知',
            username: `student_${studentId}`,
            email: `student${studentId}@example.com`,
            phone: reservation.student?.phone || '',
            role: 'STUDENT' as const,
            status: 1,
            createdAt: reservation.createdAt,
            updatedAt: reservation.updatedAt,
            studentProfile: reservation.student?.studentProfile || null,
            isAssigned: false
          } as User)
        }
      }
    }
    
    courseStudents.value = Array.from(studentsMap.values())
    pagination.course.total = studentIds.length
    console.log('课程学生数量:', courseStudents.value.length)
    console.log('课程学生详情:', courseStudents.value)
  } catch (error: any) {
    console.error('获取课程学生失败:', error)
    ElMessage.error(error.message || '获取课程学生列表失败')
    courseStudents.value = []
    pagination.course.total = 0
  }
}

const fetchAllStudents = () => {
  const assignedMap = new Map<number, User>()
  studentList.value.forEach(student => {
    assignedMap.set(student.id, student)
  })
  
  courseStudents.value.forEach(student => {
    if (!assignedMap.has(student.id)) {
      assignedMap.set(student.id, student)
    }
  })
  
  allStudents.value = Array.from(assignedMap.values())
}

const fetchStudentList = async () => {
  loading.value = true
  try {
    console.log('开始获取学生数据...')
    
    switch (activeTab.value) {
      case 'assigned':
        await fetchAssignedStudents()
        console.log('分配的学生数量:', studentList.value.length)
        break
      case 'course':
        await fetchCourseStudents()
        console.log('课程学生数量:', courseStudents.value.length)
        break
      case 'all':
        await fetchAssignedStudents()
        await fetchCourseStudents()
        fetchAllStudents()
        console.log('所有学生数量:', allStudents.value.length)
        pagination.all.total = allStudents.value.length
        break
    }
  } catch (error: any) {
    console.error('获取学生列表失败:', error)
    ElMessage.error(error.message || '获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = async () => {
  await nextTick()
  fetchStudentList()
}

const handlePageChange = () => {
  fetchStudentList()
}

const handleSizeChange = () => {
  // 计算属性必须用 .value
  currentPagination.value.page = 1
  fetchStudentList()
}

const handleViewPlans = async (row: User) => {
  currentStudent.value = row
  plansDialogVisible.value = true
  plansLoading.value = true
  
  try {
    const res = await healthPlanApi.getHealthPlans({ 
      studentId: row.id,
      size:9999
    })
    studentPlans.value = res.data.records
    plansLoading.value = false
  } catch (error: any) {
    ElMessage.error(error.message || '获取学生计划失败')
    plansLoading.value = false
  }
}

const handleCreatePlan = (row: User) => {
  currentStudent.value = row
  planForm.studentId = row.id
  planForm.planName = ''
  planForm.description = ''
  planForm.currentWeight = row.studentProfile?.weight
  planForm.targetWeight = undefined
  planForm.targetDurationMinutes = undefined
  planForm.startDate = dayjs().format('YYYY-MM-DD')
  planForm.endDate = dayjs().add(3, 'month').format('YYYY-MM-DD')
  createPlanDialogVisible.value = true
  
  if (planFormRef.value) {
    planFormRef.value.clearValidate()
  }
}

const handleCreatePlanSubmit = async () => {
  if (!planFormRef.value) return
  
  await planFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await healthPlanApi.createHealthPlan(planForm)
      ElMessage.success('创建健康计划成功')
      createPlanDialogVisible.value = false
    } catch (error: any) {
      ElMessage.error(error.message || '创建健康计划失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDeleteStudent = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除学生 ${row.realName} 吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const res = await reservationApi.getReservations({ 
        studentId: row.id, 
        reservationType: 'COURSE' 
      })
      const reservations = res.data || []
      
      for (const reservation of reservations) {
        await reservationApi.cancelReservation(reservation.id, '教练删除学生')
      }
      
      const index = courseStudents.value.findIndex(s => s.id === row.id)
      if (index > -1) {
        courseStudents.value.splice(index, 1)
        fetchAllStudents()
      }
      
      ElMessage.success('删除成功')
    } catch (error: any) {
      console.error('删除学生失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }).catch(() => {
  })
}

// 搜索防抖
let searchTimer: NodeJS.Timeout | null = null
watch(searchKeyword, () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    currentPagination.value.page = 1
    fetchStudentList()
  }, 500)
})

onMounted(() => {
  fetchStudentList()
})
</script>

<style scoped>
.students-page {
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
  gap: 20px;
  flex-wrap: wrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #ebeef5;
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 响应式 */
@media (max-width: 768px) {
  .students-page {
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
  
  :deep(.el-descriptions__label) {
    width: 80px !important;
  }
  
  .stat-value {
    font-size: 24px;
  }
}
</style>