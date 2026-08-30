<template>
  <div class="courses-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的课程</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon>
                <Plus />
              </el-icon>
              新增课程
            </el-button>
            <el-button @click="loadCourses">
              <el-icon>
                <Refresh />
              </el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      <el-table :data="courseList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              :preview-teleported="true"
              v-if="row.imageUrl"
              :src="row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl"
              :preview-src-list="[row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl]"
              fit="cover"
              style="width: 60px; height: 60px; cursor: pointer; border-radius: 4px;"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.courseType)" size="small">
              {{ getTypeText(row.courseType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="预约开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime || row.start_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="预约结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime || row.end_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" width="120" />
        <el-table-column prop="capacity" label="容量" width="80" align="center">
          <template #default="{ row }">
            {{ (row.totalReservations - row.cancelledReservations) || 0 }} / {{ row.capacity }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上课时间" min-width="200">
          <template #default="{ row }">
            <div v-if="getClassSchedule(row).length > 0">
              <div v-for="(item, index) in getClassSchedule(row)" :key="index" class="schedule-item">
                {{ getDayOfWeekText(item.dayOfWeek || item.day_of_week) }} {{ item.startTime || item.start_time }} - {{ item.endTime || item.end_time }}
              </div>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewStudents(row)">
              查看学员
            </el-button>
            <el-button type="success" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="pagination.page" v-model:page-size="pagination.pageSize"
        :total="pagination.total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" />
    </el-card>

    <!-- 学员列表对话框 -->
    <el-dialog v-model="studentsDialogVisible" :title="`${currentCourse?.name} - 学员列表`" width="1200px">
      <el-table :data="studentsList" v-loading="studentsLoading" stripe>
        <el-table-column label="学员姓名">
          <template #default="{ row }">
            {{ row.user?.real_name || row.user?.realName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话">
          <template #default="{ row }">
            {{ row.user?.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="邮箱">
          <template #default="{ row }">
            {{ row.user?.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="预约开始时间" >
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="预约结束时间">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="预约状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getBookingStatusColor(row.status)" size="small">
              {{ getBookingStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增/编辑课程对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-input v-model="form.courseType" placeholder="请输入课程类型" style="width: 100%" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="5" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预约开始时间" prop="bookingStartTime">
          <el-date-picker style="width: 100%;" v-model="form.bookingStartTime" type="datetime" placeholder="请选择预约开始时间"
            :disabled-date="(time: Date) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000" />
        </el-form-item>
        <el-form-item label="预约结束时间" prop="bookingEndTime">
          <el-date-picker style="width: 100%;" v-model="form.bookingEndTime" type="datetime" placeholder="请选择预约结束时间"
            :disabled-date="(time: Date) => form.bookingStartTime ? time.getTime() <= new Date(form.bookingStartTime).getTime() : false" />
        </el-form-item>
        <el-form-item label="上课时间" prop="classSchedule">
          <div v-for="(item, index) in form.classSchedule" :key="index" class="schedule-item">
            <el-select v-model="item.dayOfWeek" placeholder="选择星期" style="width: 100px; margin-right: 10px;">
              <el-option label="周一" value="1" />
              <el-option label="周二" value="2" />
              <el-option label="周三" value="3" />
              <el-option label="周四" value="4" />
              <el-option label="周五" value="5" />
              <el-option label="周六" value="6" />
              <el-option label="周日" value="7" />
            </el-select>
            <el-time-picker v-model="item.startTime" placeholder="开始时间" style="width: 120px; margin-right: 10px;" />
            <el-time-picker v-model="item.endTime" placeholder="结束时间" style="width: 120px; margin-right: 10px;" />
            <el-button type="danger" size="small" @click="removeScheduleItem(index)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addScheduleItem">
            <el-icon><Plus /></el-icon> 添加上课时间
          </el-button>
        </el-form-item>
        <el-form-item label="课程地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入课程地点" />
        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择课程状态" style="width: 100%">
            <el-option label="可预约" value="AVAILABLE" />
            <el-option label="已满" value="FULL" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程图片" prop="imageUrl">
          <el-upload class="avatar-uploader" :action="uploadUrl" :data="{ folder: 'course' }" :file-list="imageFileList"
            :on-success="handleImageUploadSuccess" :on-remove="handleImageRemove" :before-upload="beforeUpload"
            :limit="1" list-type="picture-card" :class="{ 'hide-upload': imageFileList.length >= 1 }">
            <template v-if="imageFileList.length < 1">
              <el-icon class="avatar-uploader-icon">
                <Upload />
              </el-icon>
            </template>
            <template #file="{ file }">
              <div>
                <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                <span class="el-upload-list__item-actions">
                  <span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
                    <el-icon>
                      <ZoomIn />
                    </el-icon>
                  </span>
                  <span class="el-upload-list__item-delete" @click="handleImageRemove">
                    <el-icon>
                      <Delete />
                    </el-icon>
                  </span>
                </span>
              </div>
            </template>
          </el-upload>
          <el-dialog v-model="imageDialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%;" />
          </el-dialog>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Upload, ZoomIn, Delete, Refresh } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const userStore = useUserStore()
const loading = ref(false)
const studentsLoading = ref(false)
const studentsDialogVisible = ref(false)
const currentCourse = ref<any>(null)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const imageFileList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload")
const imageDialogVisible = ref(false)
const dialogImageUrl = ref('')

const courseList = ref<any[]>([])
const studentsList = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  name: '',
  description: '',
  courseType: '',
  coachId: null as number | null,
  capacity: 20,
  bookingStartTime: null as Date | null,
  bookingEndTime: null as Date | null,
  classSchedule: [] as Array<{dayOfWeek: string, startTime: string, endTime: string}>,
  location: '',
  status: 'AVAILABLE' as string,
  imageUrl: ''
})

// 修复：兼容字符串时间验证，统一dayjs处理
const validateBookingStartTime = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请选择预约开始时间'))
  } else {
    // 编辑课程时允许预约开始时间在过去
    if (!form.id) {
      const today = dayjs().startOf('day')
      const selectedDate = dayjs(value)
      if (selectedDate.isBefore(today)) {
        callback(new Error('预约开始时间不能早于今天'))
      } else {
        if (form.bookingEndTime) {
          formRef.value?.validateField('bookingEndTime')
        }
        callback()
      }
    } else {
      if (form.bookingEndTime) {
        formRef.value?.validateField('bookingEndTime')
      }
      callback()
    }
  }
}

// 修复：兼容字符串时间验证，统一dayjs处理
const validateBookingEndTime = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请选择预约结束时间'))
  } else if (dayjs(value).isBefore(dayjs(form.bookingStartTime)) || dayjs(value).isSame(dayjs(form.bookingStartTime))) {
    callback(new Error('预约结束时间必须大于预约开始时间'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请输入课程类型', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入课程容量', trigger: 'blur' }],
  location: [{ required: true, message: '请输入课程地点', trigger: 'blur' }],
  bookingStartTime: [{ required: true, validator: validateBookingStartTime, trigger: 'change' }],
  bookingEndTime: [{ required: true, validator: validateBookingEndTime, trigger: 'change' }]
}

const dialogTitle = computed(() => (form.id ? '编辑课程' : '新增课程'))

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  // 确保正确处理ISO格式的时间字符串
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const getTypeColor = (type: string) => {
  const colors = ['success', 'warning', 'primary', 'danger', 'info']
  let hash = 0
  for (let i = 0; i < type.length; i++) {
    hash = type.charCodeAt(i) + ((hash << 5) - hash)
  }
  const index = Math.abs(hash) % colors.length
  return colors[index]
}

const getTypeText = (type: string) => {
  return type
}

const getStatusColor = (status: string) => {
  const map: Record<string, any> = {
    AVAILABLE: 'success',
    FULL: 'warning',
    CANCELLED: 'danger',
    COMPLETED: 'info'
  }
  return map[status] || ''
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: '可预约',
    FULL: '已满员',
    CANCELLED: '已取消',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

const getBookingStatusText = (status: string) => {
  const map: Record<string, string> = {
    CONFIRMED: '已确认',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    PENDING: '待确认'
  }
  return map[status] || status
}

const getBookingStatusColor = (status: string) => {
  const map: Record<string, string> = {
    CONFIRMED: 'success',
    COMPLETED: 'info',
    CANCELLED: 'danger',
    PENDING: 'warning'
  }
  return map[status] || 'info'
}

const getDayOfWeekText = (day: string) => {
  const map: Record<string, string> = {
    '1': '周一',
    '2': '周二',
    '3': '周三',
    '4': '周四',
    '5': '周五',
    '6': '周六',
    '7': '周日'
  }
  return map[day] || day
}

const getClassSchedule = (row: any) => {
  try {
    let scheduleData = row.classSchedule || row.class_schedule || []
    return Array.isArray(scheduleData) ? scheduleData : []
  } catch (e) {
    console.error('解析上课时间失败：', e)
    return []
  }
}

const loadCourses = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.page,
      size: pagination.pageSize,
      coachId: userStore.userId
    }

    const res = await courseApi.getCourses(params)
    console.log('课程列表API响应:', res)
    if (res.data) {
      console.log('课程列表数据:', res.data.records)
      // 打印每个课程的classSchedule字段
      if (res.data.records && res.data.records.length > 0) {
        res.data.records.forEach((course: any, index: number) => {
          console.log(`课程 ${index + 1} classSchedule:`, course.classSchedule)
          console.log(`课程 ${index + 1} class_schedule:`, course.class_schedule)
        })
      }
      courseList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error: any) {
    console.error('加载课程列表失败:', error)
    ElMessage.error(error.message || '加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const viewStudents = async (course: any) => {
  try {
    currentCourse.value = course
    studentsDialogVisible.value = true
    studentsLoading.value = true
    console.log('查看课程学员，课程ID:', course.id)
    const res = await courseApi.getCourseStudents(course.id, { page: 1, pageSize: 100 })
    console.log('学员列表API响应:', res)
    if (res.data) {
      console.log('学员列表数据:', res.data)
      studentsList.value = Array.isArray(res.data) ? res.data : []
    } else {
      studentsList.value = []
    }
    console.log('最终学员列表:', studentsList.value)
  } catch (error: any) {
    console.error('加载学员列表失败:', error)
    ElMessage.error(error.message || '加载学员列表失败')
    studentsList.value = []
  } finally {
    studentsLoading.value = false
  }
}

watch(() => pagination.page, () => {
  loadCourses()
})

watch(() => pagination.pageSize, () => {
  pagination.page = 1
  loadCourses()
})

onMounted(() => {
  loadCourses()
})

const handleAdd = () => {
  resetForm()
  form.coachId = userStore.userId
  imageFileList.value = []
  dialogVisible.value = true
}

// 🔥 核心修复：上课时间回显 - 强兼容后端所有格式（数组/JSON字符串、驼峰/下划线字段）
const handleEdit = (row: any) => {
  resetForm()
  
  // 基础属性赋值
  form.id = row.id
  form.name = row.name
  form.description = row.description
  form.courseType = row.courseType
  form.coachId = row.coachId
  form.capacity = row.capacity
  form.location = row.location
  form.status = row.status
  form.imageUrl = row.imageUrl
  
  // 兼容预约时间的驼峰/下划线字段
  const startTime = row.startTime || row.start_time
  const endTime = row.endTime || row.end_time
  
  if (startTime) {
    form.bookingStartTime = dayjs(startTime).toDate()
  }
  if (endTime) {
    form.bookingEndTime = dayjs(endTime).toDate()
  }
  
  // 🔥 上课时间回显核心修复：兼容所有后端返回格式
  form.classSchedule = []; // 先清空初始化
  try {
    // 兼容classSchedule/class_schedule两种字段名
    let scheduleData = row.classSchedule || row.class_schedule || [];
    // 如果是JSON字符串，自动解析为数组
    if (typeof scheduleData === 'string' && scheduleData) {
      scheduleData = JSON.parse(scheduleData);
    }
    // 仅当为有效数组时，处理赋值（兼容驼峰/下划线子字段）
    if (Array.isArray(scheduleData) && scheduleData.length > 0) {
      form.classSchedule = scheduleData.map((item: any) => ({
        dayOfWeek: item.dayOfWeek || item.day_of_week || '',
        startTime: item.startTime || item.start_time || '',
        endTime: item.endTime || item.end_time || ''
      }));
    }
  } catch (e) {
    console.error('上课时间解析失败：', e);
    form.classSchedule = []; // 解析失败仍保证数组格式，避免页面报错
  }
  
  // 处理图片回显
  if (row.imageUrl) {
    const fullUrl = row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl
    imageFileList.value = [{ url: fullUrl, name: 'course_image' }]
  } else {
    imageFileList.value = []
  }
  
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定删除课程"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      try {
        await courseApi.deleteCourse(row.id)
        ElMessage.success('删除成功')
        loadCourses()
      } catch (error: any) {
        ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => { })
}

// 🔥 优化：上课时间提交格式统一，移除多余的dayjs格式化（直接使用表单绑定值）
const handleSubmit = async () => {
  if (!formRef.value) return

  const valid = await formRef.value.validate()
  if (valid) {
    try {
      submitLoading.value = true
      // 上课时间提交优化：直接使用表单值，保证与回显格式一致
      const processedClassSchedule = form.classSchedule ? form.classSchedule.map((item: any) => ({
        dayOfWeek: item.dayOfWeek,
        startTime: item.startTime,
        endTime: item.endTime
      })) : []
      
      // 统一提交字段格式，兼容后端
      const data: any = {
        name: form.name,
        description: form.description,
        coachId: form.coachId || userStore.userId,
        courseType: form.courseType,
        capacity: form.capacity,
        startTime: form.bookingStartTime ? dayjs(form.bookingStartTime).format('YYYY-MM-DDTHH:mm:ss') : null,
        endTime: form.bookingEndTime ? dayjs(form.bookingEndTime).format('YYYY-MM-DDTHH:mm:ss') : null,
        location: form.location,
        status: form.status,
        imageUrl: form.imageUrl,
        classSchedule: processedClassSchedule
      }

      if (form.id) {
        await courseApi.updateCourse(form.id, data)
        ElMessage.success('更新成功')
      } else {
        await courseApi.createCourse(data)
        ElMessage.success('创建成功')
      }

      dialogVisible.value = false
      loadCourses()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  }
}

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG 图片！')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB！')
  }
  return isJpgOrPng && isLt2M
}

const handleImageUploadSuccess = (response: any, uploadFile: any) => {
  if (response.code === 200) {
    const imageUrl = response.data
    form.imageUrl = imageUrl
    const fullUrl = imageUrl.startsWith('http') ? imageUrl : baseUrl + '/' + imageUrl
    imageFileList.value = [{ url: fullUrl, name: uploadFile.name }]
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleImageRemove = () => {
  form.imageUrl = ''
  imageFileList.value = []
  ElMessage.success('图片已移除')
}

const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  imageDialogVisible.value = true
}

// 添加上课时间
const addScheduleItem = () => {
  form.classSchedule.push({ dayOfWeek: '', startTime: '', endTime: '' })
}

// 移除上课时间
const removeScheduleItem = (index: number) => {
  form.classSchedule.splice(index, 1)
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    description: '',
    courseType: '',
    coachId: userStore.userId,
    capacity: 20,
    bookingStartTime: null,
    bookingEndTime: null,
    classSchedule: [],
    location: '',
    status: 'AVAILABLE',
    imageUrl: ''
  })
  imageFileList.value = []
}
</script>

<style scoped>
.courses-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.schedule-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.schedule-item .el-button {
  margin-top: 0;
}

.hide-upload .el-upload--picture-card {
  display: none;
}

@media (max-width: 768px) {
  .courses-page {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .pagination {
    justify-content: center;
  }
  
  .pagination :deep(.el-pagination__sizes) {
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
  
  :deep(.el-form-item) {
    margin-bottom: 15px;
  }
  
  .schedule-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .schedule-item .el-select,
  .schedule-item .el-time-picker {
    width: 100% !important;
    margin-right: 0 !important;
    margin-bottom: 5px;
  }
  
  .schedule-item .el-button {
    width: 100%;
    margin-top: 5px;
  }
}
</style>