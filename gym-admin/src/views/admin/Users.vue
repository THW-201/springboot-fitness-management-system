<template>
  <div class="users-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
        </div>
      </template>

      <div class="filter-section">
        <el-form :inline="true" :model="queryParams">
          <el-form-item label="角色筛选">
            <el-select v-model="queryParams.role" placeholder="全部角色" clearable style="width: 150px"
              @change="handleQuery">
              <el-option label="管理员" value="ADMIN" />
              <el-option label="教练" value="COACH" />
              <el-option label="学生" value="STUDENT" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="queryParams.keyword" placeholder="搜索用户名/姓名/邮箱" clearable style="width: 250px"
              @keyup.enter="handleQuery">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">
              <el-icon>
                <Search />
              </el-icon>
              查询
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <el-table :data="userList" stripe border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" align="center" />
        <el-table-column prop="realName" label="真实姓名" width="120" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" align="center" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleText(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色信息" width="200" align="center">
          <template #default="{ row }">
            <div v-if="row.role === 'STUDENT' && row.studentProfile">
              <div>学号: {{ row.studentProfile.studentNumber }}</div>
              <div v-if="row.studentProfile.coachName">教练: {{ row.studentProfile.coachName }}</div>
            </div>
            <div v-else-if="row.role === 'COACH' && row.coachProfile">
              <div v-if="row.coachProfile.specialization">专业: {{ row.coachProfile.specialization }}</div>
              <div v-if="row.coachProfile.experienceYears">经验: {{ row.coachProfile.experienceYears }}年</div>
            </div>
            <div v-else-if="row.role === 'ADMIN'">
              <el-tag type="danger" size="small">系统管理员</el-tag>
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }" >
            <el-button v-if="row.id != 1" type="primary" link size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-if="row.role === 'STUDENT'" type="success" link size="small" @click="handleAssignCoach(row)">
              分配教练
            </el-button>
            <el-button v-if="row.id != 1" type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="pagination.current" v-model:page-size="pagination.size"
        :total="pagination.total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
        class="pagination" @size-change="handleSizeChange" @current-change="handlePageChange" />
    </el-card>

    <!-- 查看用户详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="用户详情" width="600px">
      <el-descriptions v-if="currentUser" :column="2" border>
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(currentUser.role)">{{ getRoleText(currentUser.role) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(currentUser.createdAt) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ formatDateTime(currentUser.updatedAt) }}</el-descriptions-item>

        <el-descriptions-item v-if="currentUser.studentProfile" label="学生信息" :span="2">
          <div>
            <p>学号: {{ currentUser.studentProfile.studentNumber }}</p>
            <p v-if="currentUser.studentProfile.coachName">负责教练: {{ currentUser.studentProfile.coachName }}</p>
            <p v-if="currentUser.studentProfile.gender">性别: {{ getGenderText(currentUser.studentProfile.gender) }}</p>
            <p v-if="currentUser.studentProfile.age">年龄: {{ currentUser.studentProfile.age }}岁</p>
            <p v-if="currentUser.studentProfile.height">身高: {{ currentUser.studentProfile.height }}cm</p>
            <p v-if="currentUser.studentProfile.weight">体重: {{ currentUser.studentProfile.weight }}kg</p>
          </div>
        </el-descriptions-item>

        <el-descriptions-item v-if="currentUser.coachProfile" label="教练信息" :span="2">
          <div>
            <p v-if="currentUser.coachProfile.specialization">专业领域: {{ currentUser.coachProfile.specialization }}</p>
            <p v-if="currentUser.coachProfile.certification">资格证书: {{ currentUser.coachProfile.certification }}</p>
            <p v-if="currentUser.coachProfile.experienceYears">从业年限: {{ currentUser.coachProfile.experienceYears }}年</p>
            <p v-if="currentUser.coachProfile.bio">个人简介: {{ currentUser.coachProfile.bio }}</p>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="头像" prop="avatarUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :data="{ folder: 'avatar' }"
            :file-list="avatarFileList"
            :on-success="handleAvatarUploadSuccess"
            :on-remove="handleAvatarRemove"
            :before-upload="beforeUpload"
            :limit="1"
            list-type="picture-card"
            :class="{ 'hide-upload': avatarFileList.length >= 1 }"
          >
            <template v-if="avatarFileList.length < 1">
              <el-icon class="avatar-uploader-icon"><Upload /></el-icon>
            </template>
            <template #file="{ file }">
              <div>
                <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
                <span class="el-upload-list__item-actions">
                  <span
                    class="el-upload-list__item-preview"
                    @click="handlePictureCardPreview(file)"
                  >
                    <el-icon><ZoomIn /></el-icon>
                  </span>
                  <span
                    class="el-upload-list__item-delete"
                    @click="handleAvatarRemove"
                  >
                    <el-icon><Delete /></el-icon>
                  </span>
                </span>
              </div>
            </template>
          </el-upload>
          <el-dialog v-model="dialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%;"/>
          </el-dialog>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'COACH'" label="专业领域" prop="specialization">
          <el-input v-model="editForm.specialization" placeholder="请输入专业领域" />
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'COACH'" label="资格证书" prop="certification">
          <el-input v-model="editForm.certification" placeholder="请输入资格证书" />
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'STUDENT'" label="分配教练" prop="coachId">
          <el-select v-model="editForm.coachId" placeholder="请选择教练" style="width: 100%" clearable v-loading="coachListLoading">
            <el-option v-for="coach in coachList" :key="coach.id" :label="coach.realName" :value="coach.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'STUDENT'" label="性别" prop="gender">
          <el-select v-model="editForm.gender" placeholder="请选择性别" style="width: 100%" clearable>
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'STUDENT'" label="年龄" prop="age">
          <el-input v-model.number="editForm.age" type="number" placeholder="请输入年龄" @blur="handleNumberBlur('age')" />
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'STUDENT'" label="身高(cm)" prop="height">
          <el-input v-model.number="editForm.height" type="number" step="0.1" placeholder="请输入身高" @blur="handleNumberBlur('height')" />
        </el-form-item>
        <el-form-item v-if="currentUser?.role === 'STUDENT'" label="体重(kg)" prop="weight">
          <el-input v-model.number="editForm.weight" type="number" step="0.1" placeholder="请输入体重" @blur="handleNumberBlur('weight')" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="editForm.newPassword" type="password" placeholder="留空则不修改密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配教练对话框 -->
    <el-dialog v-model="assignCoachDialogVisible" title="分配教练" width="400px">
      <el-form :model="assignCoachForm" label-width="100px">
        <el-form-item label="学生">
          <el-input :value="currentUser?.realName" disabled />
        </el-form-item>
        <el-form-item label="选择教练">
          <el-select v-model="assignCoachForm.coachId" placeholder="请选择教练" style="width: 100%"
            v-loading="coachListLoading">
            <el-option v-for="coach in coachList" :key="coach.id" :label="coach.realName" :value="coach.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignCoachDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignCoachSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Upload, ZoomIn, Delete } from '@element-plus/icons-vue'
import { userApi, type GetUsersParams, type UpdateUserRequest } from '@/api/user'
import type { User } from '@/types'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const coachListLoading = ref(false)
const viewDialogVisible = ref(false)
const editDialogVisible = ref(false)
const assignCoachDialogVisible = ref(false)
const currentUser = ref<User | null>(null)
const editFormRef = ref<FormInstance>()
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload") // 上传的图片服务器地址
const avatarFileList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

const queryParams = reactive<GetUsersParams>({
  current: 1,
  size: 10,
  role: undefined,
  keyword: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const userList = ref<User[]>([])
const coachList = ref<User[]>([])

const editForm = reactive<UpdateUserRequest & { coachId?: number; specialization?: string; certification?: string; gender?: 'MALE' | 'FEMALE' | 'OTHER'; age?: number; height?: number; weight?: number }>({
  realName: '',
  email: '',
  phone: '',
  avatarUrl: '',
  newPassword: '',
  coachId: undefined,
  specialization: '',
  certification: '',
  gender: undefined,
  age: undefined,
  height: undefined,
  weight: undefined
})

const assignCoachForm = reactive({
  coachId: undefined as number | undefined
})

const editRules: FormRules = {
  realName: [
    { max: 50, message: '真实姓名不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  newPassword: [
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

const getRoleType = (role: string) => {
  const map: Record<string, any> = {
    ADMIN: 'danger',
    COACH: 'warning',
    STUDENT: 'primary'
  }
  return map[role] || ''
}

const getRoleText = (role: string) => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    COACH: '教练',
    STUDENT: '学生'
  }
  return map[role] || role
}

const getGenderText = (gender: string) => {
  const map: Record<string, string> = {
    MALE: '男',
    FEMALE: '女',
    OTHER: '其他'
  }
  return map[gender] || gender
}

const formatDateTime = (dateTime: string | undefined) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      role: queryParams.role,
      keyword: queryParams.keyword || undefined
    }
    const res = await userApi.getUsers(params)
    userList.value = res.data.records
    pagination.total = res.data.total
    pagination.current = res.data.current
    pagination.size = res.data.size
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户列表失败')
  } finally {
    await userStore.fetchCurrentUser()
    loading.value = false
  }
}

const fetchCoachList = async () => {
  coachListLoading.value = true
  try {
    const res = await userApi.getUsers({ role: 'COACH', size: 1000 })
    coachList.value = res.data.records
  } catch (error: any) {
    ElMessage.error(error.message || '获取教练列表失败')
  } finally {
    coachListLoading.value = false
  }
}

const handleQuery = () => {
  pagination.current = 1
  fetchUserList()
}

const handleReset = () => {
  queryParams.role = undefined
  queryParams.keyword = ''
  handleQuery()
}

const handlePageChange = () => {
  fetchUserList()
}

const handleSizeChange = () => {
  pagination.current = 1
  fetchUserList()
}

const handleView = async (row: User) => {
  try {
    const res = await userApi.getUserById(row.id)
    currentUser.value = res.data
    viewDialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.message || '获取用户详情失败')
  }
}

const handleEdit = async (row: User) => {
  currentUser.value = row
  editForm.realName = row.realName
  editForm.email = row.email
  editForm.phone = row.phone || ''
  editForm.avatarUrl = row.avatarUrl || ''
  editForm.newPassword = ''
  editForm.coachId = row.studentProfile?.coachId
  editForm.specialization = row.coachProfile?.specialization || ''
  editForm.certification = row.coachProfile?.certification || ''
  editForm.gender = row.studentProfile?.gender
  editForm.age = row.studentProfile?.age
  editForm.height = row.studentProfile?.height
  editForm.weight = row.studentProfile?.weight
  
  // 初始化文件列表
  if (row.avatarUrl) {
    const fullUrl = row.avatarUrl.startsWith('http') ? row.avatarUrl : baseUrl + '/' + row.avatarUrl
    avatarFileList.value = [{ url: fullUrl, name: 'avatar' }]
  } else {
    avatarFileList.value = []
  }
  
  if (row.role === 'STUDENT') {
    await fetchCoachList()
  }
  
  editDialogVisible.value = true
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return

  await editFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const updateData: UpdateUserRequest = {
        realName: editForm.realName,
        email: editForm.email,
        phone: editForm.phone,
        avatarUrl: editForm.avatarUrl
      }

      if (editForm.newPassword) {
        updateData.newPassword = editForm.newPassword
      }

      if (currentUser.value!.role === 'COACH') {
        (updateData as any).specialization = editForm.specialization as string
        (updateData as any).certification = editForm.certification as string
      }

      console.log('更新用户信息请求数据:', updateData)
      await userApi.updateUser(currentUser.value!.id, updateData)
      
      if (currentUser.value!.role === 'STUDENT' && editForm.coachId && editForm.coachId !== currentUser.value!.studentProfile?.coachId) {
        console.log('分配教练请求数据:', currentUser.value!.id, editForm.coachId)
        await userApi.assignCoach(currentUser.value!.id, editForm.coachId)
      }

      if (currentUser.value!.role === 'STUDENT') {
        const profileData: any = {
          gender: editForm.gender,
          age: editForm.age,
          height: editForm.height !== undefined ? editForm.height : null,
          weight: editForm.weight !== undefined ? editForm.weight : null
        }
        console.log('更新学生档案请求数据:', profileData)
        const response = await userApi.updateStudentProfile(currentUser.value!.id, profileData)
        console.log('更新学生档案响应数据:', response)
      }
      
      ElMessage.success('更新用户信息成功')
      editDialogVisible.value = false
      fetchUserList()
    } catch (error: any) {
      console.error('更新用户信息失败:', error)
      ElMessage.error(error.message || '更新用户信息失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.realName}" 吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await userApi.deleteUser(row.id)
      ElMessage.success('删除用户成功')
      fetchUserList()
    } catch (error: any) {
      ElMessage.error(error.message || '删除用户失败')
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

const handleAssignCoach = async (row: User) => {
  currentUser.value = row
  assignCoachForm.coachId = row.studentProfile?.coachId
  await fetchCoachList()
  assignCoachDialogVisible.value = true
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

const handleAvatarUploadSuccess = (response: any, uploadFile: any) => {
  if (response.code === 200) {
    const imageUrl = response.data
    editForm.avatarUrl = imageUrl
    const fullUrl = imageUrl.startsWith('http') ? imageUrl : baseUrl + '/' + imageUrl
    avatarFileList.value = [{ url: fullUrl, name: uploadFile.name }]
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAvatarRemove = () => {
  editForm.avatarUrl = ''
  avatarFileList.value = []
  ElMessage.success('头像已移除')
}

const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

const handleNumberBlur = (field: 'age' | 'height' | 'weight') => {
  const value = editForm[field]
  const numValue = Number(value)
  if (value === undefined || value === null || isNaN(numValue) || !isFinite(numValue)) {
    editForm[field] = undefined
  }
}

const handleAssignCoachSubmit = async () => {
  if (!assignCoachForm.coachId) {
    ElMessage.warning('请选择教练')
    return
  }

  submitLoading.value = true
  try {
    await userApi.assignCoach(currentUser.value!.id, assignCoachForm.coachId)
    ElMessage.success('分配教练成功')
    assignCoachDialogVisible.value = false
    fetchUserList()
  } catch (error: any) {
    ElMessage.error(error.message || '分配教练失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchUserList()
})
</script>

<style scoped>
.users-page {
  padding: 20px;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  box-sizing: border-box;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}

:deep(.el-descriptions__content p) {
  margin: 5px 0;
  line-height: 1.6;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list__item img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

/* 当文件列表达到限制时，隐藏上传按钮 */
.hide-upload :deep(.el-upload--picture-card) {
  display: none !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .users-page {
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
}
</style>
