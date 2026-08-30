<template>
  <div class="profile-page">
    <el-card shadow="hover">
      <template #header>
        <span>个人中心</span>
      </template>

      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="头像">
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
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item v-if="form.role === 'COACH'" label="专业领域">
          <el-input v-model="form.specialization" placeholder="请输入专业领域" />
        </el-form-item>
        <el-form-item v-if="form.role === 'COACH'" label="资格证书">
          <el-input v-model="form.certification" placeholder="请输入资格证书" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="getRoleType(form.role)">{{ getRoleText(form.role) }}</el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveInfo">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api'
import { ElMessage } from 'element-plus'
import { Upload, ZoomIn, Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload")
const avatarFileList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_BASE_URL
const dialogVisible = ref(false)
const dialogImageUrl = ref('')

const form = reactive({
  username: userStore.userInfo?.username || '',
  realName: userStore.userInfo?.realName || '',
  email: userStore.userInfo?.email || '',
  phone: userStore.userInfo?.phone || '',
  role: userStore.userInfo?.role || '',
  avatarUrl: userStore.userInfo?.avatarUrl || '',
  specialization: userStore.userInfo?.coachProfile?.specialization || '',
  certification: userStore.userInfo?.coachProfile?.certification || ''
})

const saveInfo = async () => {
  const updateData: any = {
    realName: form.realName,
    email: form.email,
    phone: form.phone,
    avatarUrl: form.avatarUrl
  }
  
  if (form.role === 'COACH') {
    updateData.specialization = form.specialization
    updateData.certification = form.certification
  }
  
  const res = await userApi.updateUser(userStore.userInfo?.id || 0, updateData)
  if (res.code === 200) {
    await userStore.fetchCurrentUser()
    ElMessage.success('更新信息成功')
    userStore.setUserInfo(res.data)
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

const handleAvatarUploadSuccess = (response: any, uploadFile: any) => {
  if (response.code === 200) {
    const imageUrl = response.data
    form.avatarUrl = imageUrl
    const fullUrl = imageUrl.startsWith('http') ? imageUrl : baseUrl + '/' + imageUrl
    avatarFileList.value = [{ url: fullUrl, name: uploadFile.name }]
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAvatarRemove = () => {
  form.avatarUrl = ''
  avatarFileList.value = []
  ElMessage.success('头像已移除')
}

const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
}

onMounted(() => {
  if (userStore.userInfo?.avatarUrl) {
    const fullUrl = userStore.userInfo.avatarUrl.startsWith('http') 
      ? userStore.userInfo.avatarUrl 
      : baseUrl + '/' + userStore.userInfo.avatarUrl
    avatarFileList.value = [{ url: fullUrl, name: 'avatar' }]
  }
})

const getRoleType = (role: string) => {
  const map: Record<string, any> = { 
    ADMIN: 'danger', 
    COACH: 'warning', 
    STUDENT: 'primary',
    admin: 'danger', 
    coach: 'warning', 
    student: 'primary' 
  }
  return map[role] || ''
}

const getRoleText = (role: string) => {
  const map: Record<string, string> = { 
    ADMIN: '管理员', 
    COACH: '教练', 
    STUDENT: '学生',
    admin: '管理员', 
    coach: '教练', 
    student: '学生' 
  }
  return map[role] || role
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
  box-sizing: border-box;
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

.hide-upload :deep(.el-upload--picture-card) {
  display: none !important;
}
</style>
