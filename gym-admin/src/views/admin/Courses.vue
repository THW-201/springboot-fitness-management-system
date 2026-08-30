<template>
  <div class="courses-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>课程管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus />
            </el-icon>
            新增课程
          </el-button>
        </div>
      </template>

      <!-- 搜索和筛选 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入课程名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="可预约" value="AVAILABLE" />
            <el-option label="已满" value="FULL" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search />
            </el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh />
            </el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 课程表格 -->
      <el-table :data="courseList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="课程名称" min-width="150" />
        <el-table-column label="图片" width="100" align="center">
          <template #default="{ row }">
            <el-image :preview-teleported="true" v-if="row.imageUrl"
              :src="row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl"
              :preview-src-list="[row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl]"
              fit="cover" style="width: 60px; height: 60px; cursor: pointer; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="danger" size="small">
              {{ row.courseType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="教练" width="120" align="center" />
        <!-- <el-table-column prop="duration" label="时长" width="80" align="center">
          <template #default="{ row }">{{ row.duration }} 分钟</template>
        </el-table-column> -->
        <el-table-column prop="schedule" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="schedule" label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
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
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <!-- <el-button type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button> -->
            <el-button type="primary" link size="small" @click="handleEdit(row)">
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
        :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-input v-model="form.courseType" placeholder="请输入课程类型" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="教练" prop="coachId">
          <el-select v-model="form.coachId" placeholder="请选择教练" style="width: 100%" :loading="coachListLoading">
            <el-option v-for="coach in coachList" :key="coach.id" :label="coach.realName" :value="coach.id" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="课程时长" prop="duration">
          <el-input-number
            v-model="form.duration"
            :min="30"
            :max="180"
            :step="15"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item> -->
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="5" :max="50" style="width: 100%" />
        </el-form-item>
        <!-- <el-form-item label="时间安排" prop="schedule">
          <el-input v-model="form.schedule" placeholder="如：周一 18:00-19:00" />
        </el-form-item> -->
        <!-- 开始时间和结束时间 -->
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker style="width: 100%;" v-model="form.startTime" type="datetime" placeholder="请选择开始时间"
            :disabled-date="(time: Date) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker style="width: 100%;" v-model="form.endTime" type="datetime" placeholder="请选择结束时间"
            :disabled-date="(time: Date) => form.startTime ? time.getTime() <= new Date(form.startTime).getTime() : false" />
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
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Upload, ZoomIn, Delete } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { userApi } from '@/api/user'
import type { User } from '@/types'
import dayjs from 'dayjs'

const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const coachList = ref<User[]>([])
const coachListLoading = ref(false)
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload") // 上传的图片服务器地址
const imageFileList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const imageDialogVisible = ref(false)
const dialogImageUrl = ref('')

const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null as number | null,
  name: '',
  description: '',
  courseType: '',
  coachId: null as number | null,
  duration: 60,
  capacity: 20,
  startTime: '',
  endTime: '',
  location: '',
  status: 'AVAILABLE' as string,
  imageUrl: ''
})

const validateStartTime = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请选择开始时间'))
  } else {
    const today = dayjs().startOf('day')
    const selectedDate = dayjs(value).startOf('day')
    if (selectedDate.isBefore(today)) {
      callback(new Error('开始时间不能早于今天'))
    } else {
      if (form.endTime) {
        formRef.value?.validateField('endTime')
      }
      callback()
    }
  }
}

const validateEndTime = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请选择结束时间'))
  } else if (form.startTime && dayjs(value).isBefore(dayjs(form.startTime)) || dayjs(value).isSame(dayjs(form.startTime))) {
    callback(new Error('结束时间必须大于开始时间'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  name: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseType: [{ required: true, message: '请输入课程类型', trigger: 'change' }],
  coachId: [{ required: true, message: '请选择教练', trigger: 'change' }],
  capacity: [{ required: true, message: '请输入课程容量', trigger: 'blur' }],
  location: [{ required: true, message: '请输入课程地点', trigger: 'blur' }],
  startTime: [{ required: true, validator: validateStartTime, trigger: 'change' }],
  endTime: [{ required: true, validator: validateEndTime, trigger: 'change' }]
}

const dialogTitle = computed(() => (form.id ? '编辑课程' : '新增课程'))

const courseList = ref<any[]>([])

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: 'success',
    FULL: 'warning',
    CANCELLED: 'info',
    COMPLETED: 'danger'
  }
  return map[status] || ''
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    AVAILABLE: '可预约',
    FULL: '已满',
    CANCELLED: '已取消',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

const loadCourses = async () => {
  try {
    loading.value = true
    const params: any = {
      current: pagination.page,
      size: pagination.pageSize
    }

    if (searchForm.keyword) params.keyword = searchForm.keyword
    if (searchForm.type) params.courseType = searchForm.type
    if (searchForm.status) params.status = searchForm.status

    const res = await courseApi.getCourses(params)
    if (res.data) {
      courseList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (error: any) {
    // ElMessage.error(error.message || '加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCourses()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.status = ''
  pagination.page = 1
  loadCourses()
}

const handleAdd = () => {
  resetForm()
  imageFileList.value = []
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(form, row)

  // 初始化文件列表
  if (row.imageUrl) {
    const fullUrl = row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl
    imageFileList.value = [{ url: fullUrl, name: 'image' }]
  } else {
    imageFileList.value = []
  }

  dialogVisible.value = true
}

const handleView = (row: any) => {
  ElMessage.info(`查看课程：${row.name}`)
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
        // ElMessage.error(error.message || '删除失败')
      }
    })
    .catch(() => { })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        const data: any = {
          name: form.name,
          description: form.description,
          coachId: form.coachId,
          courseType: form.courseType,
          capacity: form.capacity,
          startTime: form.startTime,
          endTime: form.endTime,
          location: form.location,
          status: form.status,
          imageUrl: form.imageUrl
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
        // ElMessage.error(error.message || '操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
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

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    name: '',
    description: '',
    coachId: null,
    capacity: 5,
    location: '',
    status: 'AVAILABLE',
    courseType: '',
    startTime: '',
    endTime: '',
    imageUrl: ''
  })
  imageFileList.value = []
}

watch(() => pagination.page, () => {
  loadCourses()
})

watch(() => pagination.pageSize, () => {
  pagination.page = 1
  loadCourses()
})

const fetchCoachList = async () => {
  coachListLoading.value = true
  try {
    const res = await userApi.getUsers({ role: 'COACH', size: 1000 })
    coachList.value = res.data.records
  } catch (error: any) {
    // ElMessage.error(error.message || '获取教练列表失败')
  } finally {
    coachListLoading.value = false
  }
}

onMounted(() => {
  loadCourses()
  fetchCoachList()
})
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

.search-form {
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-upload--picture-card) {
  width: 150px;
  height: 150px;
}

:deep(.el-upload-list__item) {
  width: 150px;
  height: 150px;
}

:deep(.el-upload-list__item img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 当文件列表达到限制时，隐藏上传按钮 */
.hide-upload :deep(.el-upload--picture-card) {
  display: none !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .courses-page {
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
}
</style>
