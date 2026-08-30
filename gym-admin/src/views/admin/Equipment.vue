<template>
  <div class="equipment-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>器材管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增器材
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="searchParams.name"
          placeholder="搜索器材名称、描述、地点"
          clearable
          style="width: 300px; margin-right: 10px"
          @clear="loadEquipmentList"
        />
        <el-select
          v-model="searchParams.equipmentType"
          placeholder="器材类型"
          clearable
          style="width: 150px; margin-right: 10px"
          @change="loadEquipmentList"
        >
          <el-option label="有氧器材" value="有氧器材" />
          <el-option label="力量器材" value="力量器材" />
          <el-option label="自由重量" value="自由重量" />
          <el-option label="综合训练" value="综合训练" />
          <el-option label="便携辅助小器材" value="便携辅助小器材" />
          <el-option label="瑜伽器材" value="瑜伽器材" />
          <el-option label="普拉提器材" value="普拉提器材" />
        </el-select>
        <el-select
          v-model="searchParams.status"
          placeholder="器材状态"
          clearable
          style="width: 150px; margin-right: 10px"
          @change="loadEquipmentList"
        >
          <el-option label="可用" value="AVAILABLE" />
          <el-option label="使用中" value="IN_USE" />
          <el-option label="维护中" value="MAINTENANCE" />
          <el-option label="损坏" value="DAMAGED" />
        </el-select>
        <el-button type="primary" @click="loadEquipmentList">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="equipmentList" stripe border v-loading="loading">
        <el-table-column prop="name" label="器材名称" min-width="120" />
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
        <el-table-column prop="equipmentType" label="类型" width="120" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="purchaseDate" label="购买日期" width="120" />
        <!-- <el-table-column prop="lastMaintenanceDate" label="最后维护" width="120" /> -->
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadEquipmentList"
        @current-change="loadEquipmentList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="器材名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入器材名称" />
        </el-form-item>
        <el-form-item label="器材类型" prop="equipmentType">
          <el-select v-model="formData.equipmentType" placeholder="请选择器材类型" style="width: 100%">
            <el-option label="有氧器材" value="有氧器材" />
            <el-option label="力量器材" value="力量器材" />
            <el-option label="自由重量" value="自由重量" />
            <el-option label="综合训练" value="综合训练" />
            <el-option label="便携辅助小器材" value="便携辅助小器材" />
            <el-option label="瑜伽器材" value="瑜伽器材" />
            <el-option label="普拉提器材" value="普拉提器材" />
          </el-select>
        </el-form-item>
        <el-form-item label="器材描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入器材描述"
          />
        </el-form-item>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="formData.location" placeholder="请输入存放位置" />
        </el-form-item>
        <el-form-item label="器材状态" prop="status" v-if="isEdit">
          <el-select v-model="formData.status" placeholder="请选择器材状态" style="width: 100%">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="维护中" value="MAINTENANCE" />
            <el-option label="损坏" value="DAMAGED" />
          </el-select>
        </el-form-item>
        <el-form-item label="购买日期" prop="purchaseDate">
          <el-date-picker
            v-model="formData.purchaseDate"
            type="date"
            placeholder="选择购买日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="最后维护日期" prop="lastMaintenanceDate">
          <el-date-picker
            v-model="formData.lastMaintenanceDate"
            type="date"
            placeholder="选择最后维护日期"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="器材图片" prop="imageUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :data="{ folder: 'equipment' }"
            :file-list="imageFileList"
            :on-success="handleImageUploadSuccess"
            :on-remove="handleImageRemove"
            :before-upload="beforeUpload"
            :limit="1"
            list-type="picture-card"
            :class="{ 'hide-upload': imageFileList.length >= 1 }"
          >
            <template v-if="imageFileList.length < 1">
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
                    @click="handleImageRemove"
                  >
                    <el-icon><Delete /></el-icon>
                  </span>
                </span>
              </div>
            </template>
          </el-upload>
          <el-dialog v-model="imageDialogVisible">
            <img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%;"/>
          </el-dialog>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Upload, ZoomIn, Delete } from '@element-plus/icons-vue'
import { equipmentApi } from '@/api/equipment'
import type { Equipment, EquipmentStatus, CreateEquipmentRequest, UpdateEquipmentRequest } from '@/types'

const loading = ref(false)
const equipmentList = ref<Equipment[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const currentId = ref<number>()
const submitting = ref(false)
const formRef = ref<FormInstance>()
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/files/upload") // 上传的图片服务器地址
const imageFileList = ref<any[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const imageDialogVisible = ref(false)
const dialogImageUrl = ref('')

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

const formData = reactive<CreateEquipmentRequest & { status?: EquipmentStatus }>({
  name: '',
  equipmentType: '',
  description: '',
  location: '',
  purchaseDate: '',
  lastMaintenanceDate: '',
  imageUrl: '',
  status: undefined
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入器材名称', trigger: 'blur' }],
  equipmentType: [{ required: true, message: '请选择器材类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入存放位置', trigger: 'blur' }]
}

const getStatusType = (status: EquipmentStatus) => {
  const map: Record<EquipmentStatus, string> = {
    AVAILABLE: 'success',
    IN_USE: 'primary',
    MAINTENANCE: 'warning',
    DAMAGED: 'danger'
  }
  return map[status] || ''
}

const getStatusText = (status: EquipmentStatus) => {
  const map: Record<EquipmentStatus, string> = {
    AVAILABLE: '可用',
    IN_USE: '使用中',
    MAINTENANCE: '维护中',
    DAMAGED: '损坏'
  }
  return map[status] || status
}

const loadEquipmentList = async () => {
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
    // ElMessage.error(error.message || '加载器材列表失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchParams.name = ''
  searchParams.equipmentType = ''
  searchParams.status = ''
  pagination.current = 1
  loadEquipmentList()
}

const handleAdd = () => {
  dialogTitle.value = '新增器材'
  isEdit.value = false
  imageFileList.value = []
  dialogVisible.value = true
}

const handleEdit = (row: Equipment) => {
  dialogTitle.value = '编辑器材'
  isEdit.value = true
  currentId.value = row.id
  Object.assign(formData, {
    name: row.name,
    equipmentType: row.equipmentType,
    description: row.description,
    location: row.location,
    status: row.status,
    purchaseDate: row.purchaseDate,
    lastMaintenanceDate: row.lastMaintenanceDate,
    imageUrl: row.imageUrl
  })
  
  // 初始化文件列表
  if (row.imageUrl) {
    const fullUrl = row.imageUrl.startsWith('http') ? row.imageUrl : baseUrl + '/' + row.imageUrl
    imageFileList.value = [{ url: fullUrl, name: 'image' }]
  } else {
    imageFileList.value = []
  }
  
  dialogVisible.value = true
}

const handleDelete = async (row: Equipment) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除器材 "${row.name}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await equipmentApi.deleteEquipment(row.id)
    ElMessage.success('删除成功')
    loadEquipmentList()
  } catch (error: any) {
    if (error !== 'cancel') {
      // ElMessage.error(error.message || '删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value && currentId.value) {
        const updateData: UpdateEquipmentRequest = {
          name: formData.name,
          equipmentType: formData.equipmentType,
          description: formData.description,
          location: formData.location,
          status: formData.status,
          purchaseDate: formData.purchaseDate,
          lastMaintenanceDate: formData.lastMaintenanceDate,
          imageUrl: formData.imageUrl
        }
        await equipmentApi.updateEquipment(currentId.value, updateData)
        ElMessage.success('更新成功')
      } else {
        const createData: CreateEquipmentRequest = {
          name: formData.name,
          equipmentType: formData.equipmentType,
          description: formData.description,
          location: formData.location,
          purchaseDate: formData.purchaseDate,
          lastMaintenanceDate: formData.lastMaintenanceDate,
          imageUrl: formData.imageUrl
        }
        await equipmentApi.createEquipment(createData)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadEquipmentList()
    } catch (error: any) {
      // ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    ElMessage.error('只能上传 JPG/PNG 图片！')
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB！')
  }
  return isJpgOrPng && isLt10M
}

const handleImageUploadSuccess = (response: any, uploadFile: any) => {
  if (response.code === 200) {
    const imageUrl = response.data
    formData.imageUrl = imageUrl
    const fullUrl = imageUrl.startsWith('http') ? imageUrl : baseUrl + '/' + imageUrl
    imageFileList.value = [{ url: fullUrl, name: uploadFile.name }]
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleImageRemove = () => {
  formData.imageUrl = ''
  imageFileList.value = []
  ElMessage.success('图片已移除')
}

const handlePictureCardPreview = (file: any) => {
  dialogImageUrl.value = file.url
  imageDialogVisible.value = true
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    name: '',
    equipmentType: '',
    description: '',
    location: '',
    purchaseDate: '',
    lastMaintenanceDate: '',
    imageUrl: '',
    status: undefined
  })
  imageFileList.value = []
  currentId.value = undefined
}

onMounted(() => {
  loadEquipmentList()
})
</script>

<style scoped>
.equipment-page {
  padding: 20px;
  box-sizing: border-box;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
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
  .equipment-page {
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
