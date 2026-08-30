<template>
  <div class="social-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>社交管理</span>
        </div>
      </template>
      


      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="帖子管理" name="posts">
          <div class="posts-management">
            <el-table :data="postsData" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column label="发布者" width="120">
                <template #default="scope">
                  <span>{{ scope.row.userName || scope.row.userId || '未知' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="内容" show-overflow-tooltip />
              <el-table-column label="图片" width="100">
                <template #default="scope">
                  <div v-if="scope.row.images && scope.row.images.length > 0" class="image-preview">
                    <el-image
                      v-for="(img, index) in scope.row.images.slice(0, 2)"
                      :key="index"
                      :src="img.startsWith('http') ? img : baseUrl + '/' + img"
                      :preview-src-list="scope.row.images.map((src: string) => src.startsWith('http') ? src : baseUrl + '/' + src)"
                      fit="cover"
                      class="mini-image"
                    />
                    <span v-if="scope.row.images.length > 2" class="image-count">
                      +{{ scope.row.images.length - 2 }}
                    </span>
                  </div>
                  <span v-else class="no-content">无</span>
                </template>
              </el-table-column>
              <el-table-column label="视频" width="100">
                <template #default="scope">
                  <div v-if="scope.row.video" class="video-preview">
                    <el-icon class="video-icon"><VideoCamera /></el-icon>
                  </div>
                  <span v-else class="no-content">无</span>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="发布时间" width="180" />
              <el-table-column prop="likes" label="点赞数" width="80" />
              <el-table-column prop="commentsCount" label="评论数" width="80" />
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="danger" size="small" @click="deletePost(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination">
              <el-pagination
                v-model:current-page="postsCurrentPage"
                v-model:page-size="postsPageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="postsTotal"
                @size-change="handlePostsSizeChange"
                @current-change="handlePostsCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="评论管理" name="comments">
          <div class="comments-management">
            <el-table :data="commentsData" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column label="评论者" width="120">
                <template #default="scope">
                  <span>{{ scope.row.userName || scope.row.userId || '未知' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
              <el-table-column prop="postId" label="帖子ID" width="100" />
              <el-table-column prop="createdAt" label="评论时间" width="180" />
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="danger" size="small" @click="deleteComment(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination">
              <el-pagination
                v-model:current-page="commentsCurrentPage"
                v-model:page-size="commentsPageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="commentsTotal"
                @size-change="handleCommentsSizeChange"
                @current-change="handleCommentsCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="话题管理" name="topics">
          <div class="topics-management">
            <div class="topic-actions">
              <el-button type="primary" @click="dialogVisible = true">
                <el-icon><Plus /></el-icon> 新建话题
              </el-button>
            </div>
            
            <el-table :data="topicsData" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="话题标题" />
              <el-table-column prop="posts" label="帖子数" width="100" />
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="editTopic(scope.row)">
                    编辑
                  </el-button>
                  <el-button type="danger" size="small" @click="deleteTopic(scope.row.id)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="举报管理" name="reports">
          <div class="reports-management">
            <el-table :data="reportsData" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="postId" label="帖子ID" width="100" />
              <el-table-column prop="userId" label="举报用户" width="100" />
              <el-table-column prop="reason" label="举报原因" width="150" />
              <el-table-column prop="description" label="详细描述" show-overflow-tooltip />
              <el-table-column prop="status" label="状态" width="120">
                <template #default="scope">
                  <el-tag :type="getStatusTagType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="举报时间" width="180" />
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="processReport(scope.row)">
                    处理
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="pagination">
              <el-pagination
                v-model:current-page="reportsCurrentPage"
                v-model:page-size="reportsPageSize"
                :page-sizes="[10, 20, 30, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="reportsTotal"
                @size-change="handleReportsSizeChange"
                @current-change="handleReportsCurrentChange"
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 话题编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑话题' : '新建话题'"
      width="500px"
    >
      <el-form :model="topicForm" label-width="80px">
        <el-form-item label="话题标题">
          <el-input v-model="topicForm.title" placeholder="请输入话题标题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveTopic">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 举报处理对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="处理举报"
      width="500px"
    >
      <el-form :model="reportForm" label-width="80px">
        <el-form-item label="举报原因">
          <el-input v-model="reportForm.reason" disabled />
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input v-model="reportForm.description" type="textarea" disabled />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-radio-group v-model="reportForm.status">
            <el-radio label="PROCESSED">已处理</el-radio>
            <el-radio label="IGNORED">忽略</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveReportProcess">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { socialApi } from '@/api/social'
import { Plus, VideoCamera } from '@element-plus/icons-vue'

const baseUrl = import.meta.env.VITE_BASE_URL

// 标签页
const activeTab = ref('posts')

// 帖子管理
const postsData = ref<any[]>([])
const postsCurrentPage = ref(1)
const postsPageSize = ref(10)
const postsTotal = ref(0)

// 评论管理
const commentsData = ref<any[]>([])
const commentsCurrentPage = ref(1)
const commentsPageSize = ref(10)
const commentsTotal = ref(0)

// 话题管理
const topicsData = ref<any[]>([])
const dialogVisible = ref(false)
const isEditing = ref(false)
const topicForm = ref({
  id: 0,
  title: ''
})

// 举报管理
const reportsData = ref<any[]>([])
const reportsCurrentPage = ref(1)
const reportsPageSize = ref(10)
const reportsTotal = ref(0)
const reportDialogVisible = ref(false)
const reportForm = ref({
  id: 0,
  reason: '',
  description: '',
  status: 'PROCESSED'
})

// 加载帖子列表
const loadPosts = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const res = await socialApi.getAdminPosts({
      current: postsCurrentPage.value,
      size: postsPageSize.value
    })
    console.log('帖子数据:', res.data)
    if (res.data && res.data.records) {
      console.log('第一条帖子:', res.data.records[0])
      console.log('userName:', res.data.records[0].userName)
      console.log('userId:', res.data.records[0].userId)
      postsData.value = res.data.records
      postsTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载帖子失败')
  } finally {
    loading.close()
  }
}

// 加载评论列表
const loadComments = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const res = await socialApi.getAdminComments({
      current: commentsCurrentPage.value,
      size: commentsPageSize.value
    })
    console.log('评论数据:', res.data)
    if (res.data && res.data.records) {
      console.log('第一条评论:', res.data.records[0])
      console.log('userName:', res.data.records[0].userName)
      console.log('userId:', res.data.records[0].userId)
      commentsData.value = res.data.records
      commentsTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载评论失败')
  } finally {
    loading.close()
  }
}

// 加载话题列表
const loadTopics = async () => {
  try {
    const res = await socialApi.getAdminTopics()
    if (res.data) {
      topicsData.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载话题失败')
  }
}

// 加载举报列表
const loadReports = async () => {
  const loading = ElLoading.service({ fullscreen: true })
  try {
    const res = await socialApi.getAdminReports({
      current: reportsCurrentPage.value,
      size: reportsPageSize.value
    })
    if (res.data) {
      reportsData.value = res.data.records
      reportsTotal.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载举报失败')
  } finally {
    loading.close()
  }
}

// 删除帖子
const deletePost = async (id: number) => {
  try {
    await socialApi.deleteAdminPost(id)
    ElMessage.success('删除成功')
    loadPosts()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 删除评论
const deleteComment = async (id: number) => {
  try {
    await socialApi.deleteAdminComment(id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 编辑话题
const editTopic = (topic: any) => {
  isEditing.value = true
  topicForm.value = { ...topic }
  dialogVisible.value = true
}

// 保存话题
const saveTopic = async () => {
  if (!topicForm.value.title) {
    ElMessage.warning('请输入话题标题')
    return
  }
  
  try {
    if (isEditing.value) {
      await socialApi.updateTopic(topicForm.value.id, { title: topicForm.value.title })
      ElMessage.success('更新成功')
    } else {
      await socialApi.createTopic({ title: topicForm.value.title })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadTopics()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除话题
const deleteTopic = async (id: number) => {
  try {
    await socialApi.deleteTopic(id)
    ElMessage.success('删除成功')
    loadTopics()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// 处理举报
const processReport = (report: any) => {
  reportForm.value = { ...report }
  reportDialogVisible.value = true
}

// 保存举报处理
const saveReportProcess = async () => {
  try {
    await socialApi.processReport(reportForm.value.id, { status: reportForm.value.status })
    ElMessage.success('处理成功')
    reportDialogVisible.value = false
    loadReports()
  } catch (error) {
    ElMessage.error('处理失败')
  }
}

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  switch (status) {
    case 'PENDING': return 'warning'
    case 'PROCESSED': return 'success'
    case 'IGNORED': return 'info'
    default: return 'default'
  }
}

// 获取状态文本
const getStatusText = (status: string) => {
  switch (status) {
    case 'PENDING': return '待处理'
    case 'PROCESSED': return '已处理'
    case 'IGNORED': return '已忽略'
    default: return status
  }
}

// 分页处理
const handlePostsSizeChange = (size: number) => {
  postsPageSize.value = size
  loadPosts()
}

const handlePostsCurrentChange = (current: number) => {
  postsCurrentPage.value = current
  loadPosts()
}

const handleCommentsSizeChange = (size: number) => {
  commentsPageSize.value = size
  loadComments()
}

const handleCommentsCurrentChange = (current: number) => {
  commentsCurrentPage.value = current
  loadComments()
}

const handleReportsSizeChange = (size: number) => {
  reportsPageSize.value = size
  loadReports()
}

const handleReportsCurrentChange = (current: number) => {
  reportsCurrentPage.value = current
  loadReports()
}

// 初始化
onMounted(() => {
  loadPosts()
  loadComments()
  loadTopics()
  loadReports()
})
</script>

<style scoped>
.social-management {
  padding: 20px;
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

.topic-actions {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.image-preview {
  position: relative;
  display: inline-block;
}

.mini-image {
  width: 40px;
  height: 40px;
  margin-right: 5px;
  border-radius: 4px;
}

.image-count {
  position: absolute;
  right: -10px;
  top: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.video-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #f0f0f0;
  border-radius: 4px;
}

.video-icon {
  font-size: 20px;
  color: #409eff;
}

.no-content {
  color: #999;
  font-size: 12px;
}
</style>