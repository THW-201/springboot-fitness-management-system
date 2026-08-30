<template>
  <div class="health-plans-management">
    <div class="page-header">
      <h2>健身计划管理</h2>
      <p>创建、编辑、删除健身计划，管理计划状态</p>
    </div>

    <div class="content-container">
      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="openCreateDialog">
          <el-icon>
            <Plus />
          </el-icon>
          新建健身计划
        </el-button>
      </div>

      <!-- 搜索和筛选 -->
      <div class="search-filter">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="计划名称">
            <el-input v-model="searchForm.planName" placeholder="输入计划名称" />
          </el-form-item>
          <el-form-item label="学生ID">
            <el-input v-model.number="searchForm.studentId" placeholder="输入学生ID" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="选择状态">
              <el-option label="全部" value="" />
              <el-option label="启用" value="ACTIVE" />
              <el-option label="完成" value="COMPLETED" />
              <el-option label="放弃" value="ABANDONED" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchPlans">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 健身计划列表 -->
      <el-card class="plans-list-card">
        <template #header>
          <div class="card-header">
            <span>健身计划列表</span>
          </div>
        </template>
        <el-table :data="plansList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="计划ID" width="100" />
          <el-table-column prop="planName" label="计划名称" width="200" />
          <el-table-column prop="studentId" label="学生ID" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusCode(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180" />
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="viewPlanDetail(scope.row.id)"
              >
                查看详情
              </el-button>
              <el-button
                type="warning"
                size="small"
                @click="openEditDialog(scope.row)"
                style="margin-left: 8px"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="deletePlan(scope.row.id)"
                style="margin-left: 8px"
              >
                删除
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
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑健身计划' : '新建健身计划'"
      width="600px"
    >
      <div class="plan-form">
        <el-form :model="planForm" :rules="rules" ref="planFormRef">
          <el-form-item label="计划名称" prop="planName">
            <el-input v-model="planForm.planName" placeholder="输入计划名称" />
          </el-form-item>
          <el-form-item label="学生ID" prop="studentId">
            <el-input v-model.number="planForm.studentId" placeholder="输入学生ID" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="planForm.status" placeholder="选择状态">
              <el-option label="启用" value="ACTIVE" />
              <el-option label="完成" value="COMPLETED" />
              <el-option label="放弃" value="ABANDONED" />
            </el-select>
          </el-form-item>
          <el-form-item label="计划描述" prop="description">
            <el-input v-model="planForm.description" type="textarea" placeholder="输入计划描述" rows="4" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePlan">保存</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 计划详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="健身计划详情"
      width="600px"
    >
      <div class="plan-detail" v-if="selectedPlan">
        <el-descriptions :column="2">
          <el-descriptions-item label="计划ID">{{ selectedPlan.id }}</el-descriptions-item>
          <el-descriptions-item label="计划名称">{{ selectedPlan.planName || selectedPlan.name }}</el-descriptions-item>
          <el-descriptions-item label="学生ID">{{ selectedPlan.studentId }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ getStatusText(selectedPlan.status) }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ selectedPlan.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="计划描述" :span="2">{{ selectedPlan.description || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElLoading, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { healthPlanApi } from '@/api/healthPlan'

// 搜索表单
const searchForm = ref({
  planName: '',
  status: '',
  studentId: ''
})

// 健身计划列表
const plansList = ref<any[]>([])

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载状态
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEditing = ref(false)
const selectedPlan = ref<any>(null)

// 表单
const planForm = ref({
  id: 0,
  planName: '',
  description: '',
  studentId: 0,
  status: 'ACTIVE'
})

const planFormRef = ref<any>(null)

// 表单验证规则
const rules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 2, max: 50, message: '计划名称长度应在 2-50 个字符之间', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学生ID', trigger: 'blur' },
    { type: 'number', message: '学生ID必须是数字', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 加载健身计划列表
const loadPlans = async () => {
  loading.value = true
  try {
    const params = {
      planName: searchForm.value.planName || undefined,
      studentId: searchForm.value.studentId ? Number(searchForm.value.studentId) : undefined,
      status: searchForm.value.status || undefined,
      current: currentPage.value,
      size: pageSize.value
    }
    const res = await healthPlanApi.getAllHealthPlans(params)
    if (res.data) {
      plansList.value = res.data.records || res.data
      total.value = res.data.total || res.data.length
    }
  } catch (error) {
    ElMessage.error('加载健身计划失败')
  } finally {
    loading.value = false
  }
}

// 搜索健身计划
const searchPlans = () => {
  currentPage.value = 1
  loadPlans()
}

// 重置搜索
const resetSearch = () => {
  searchForm.value = {
    planName: '',
    studentId: '',
    status: ''
  }
  currentPage.value = 1
  loadPlans()
}

// 打开创建对话框
const openCreateDialog = () => {
  isEditing.value = false
  planForm.value = {
    id: 0,
    planName: '',
    description: '',
    studentId: 0,
    status: 'ACTIVE'
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (plan: any) => {
  isEditing.value = true
  planForm.value = {
    id: plan.id || 0,
    planName: plan.planName || plan.name || '',
    description: plan.description || '',
    studentId: plan.studentId || 0,
    status: plan.status || 'ACTIVE'
  }
  dialogVisible.value = true
}

// 保存健身计划
const savePlan = async () => {
  if (!planFormRef.value) return
  
  await planFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      const loadingInstance = ElLoading.service({
        lock: true,
        text: '正在保存...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      
      try {
        let res
        if (isEditing.value) {
          res = await healthPlanApi.updateHealthPlan(planForm.value)
        } else {
          res = await healthPlanApi.createHealthPlan(planForm.value)
        }
        
        if (res.data) {
          ElMessage.success(isEditing.value ? '健身计划已更新' : '健身计划已创建')
          dialogVisible.value = false
          loadPlans()
        }
      } catch (error: any) {
        ElMessage.error(isEditing.value ? '更新健身计划失败' : '创建健身计划失败')
        console.error('保存健身计划失败:', error)
      } finally {
        loadingInstance.close()
      }
    }
  })
}

// 查看计划详情
const viewPlanDetail = async (id: number) => {
  try {
    const res = await healthPlanApi.getHealthPlanDetail(id)
    if (res.data) {
      selectedPlan.value = res.data
      detailDialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取健身计划详情失败')
  }
}

// 删除健身计划
const deletePlan = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这个健身计划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await healthPlanApi.deleteHealthPlan(id)
    if (res.data) {
      ElMessage.success('健身计划已删除')
      loadPlans()
    }
  } catch (error) {
    // 取消删除时不显示错误
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadPlans()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  loadPlans()
}

// 获取目标文本
const getGoalText = (goal?: string) => {
  const map: Record<string, string> = {
    MUSCLE_GAIN: '增肌',
    FAT_LOSS: '减脂',
    BODY_SHAPING: '塑形',
    ENDURANCE: '耐力'
  }
  return map[goal || ''] || '未知'
}

// 获取难度文本
const getDifficultyText = (difficulty?: string) => {
  const map: Record<string, string> = {
    BEGINNER: '初级',
    INTERMEDIATE: '中级',
    ADVANCED: '高级'
  }
  return map[difficulty || ''] || '未知'
}

// 获取状态文本
const getStatusText = (status?: string) => {
  const map: Record<string, string> = {
    ACTIVE: '启用',
    COMPLETED: '完成',
    ABANDONED: '放弃'
  }
  return map[status || ''] || '未知'
}

// 获取状态类型
const getStatusCode = (status?: string) => {
  const map: Record<string, string> = {
    ACTIVE: 'success',
    COMPLETED: 'info',
    ABANDONED: 'warning'
  }
  return map[status || ''] || ''
}

// 初始加载
onMounted(() => {
  loadPlans()
})
</script>

<style scoped>
.health-plans-management {
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

.action-buttons {
  margin-bottom: 20px;
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

.plans-list-card {
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

.plan-form {
  padding: 20px 0;
}

.plan-detail {
  padding: 20px 0;
}

.dialog-footer {
  text-align: right;
}
</style>