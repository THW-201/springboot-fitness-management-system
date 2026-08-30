<template>
  <div class="social-square">
    <!-- 页面标题 -->
    <section class="page-header">
      <div class="container">
        <h1>社交广场</h1>
        <p>分享你的健身心得，与同学互动交流</p>
      </div>
    </section>

    <!-- 主内容区 -->
    <div class="main-content">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧内容 -->
          <div class="left-content">
            <!-- 发布动态 -->
            <section class="post-section">
              <div class="post-form">
                <div class="user-avatar">
                  <el-avatar :size="48" :src="baseUrl + userInfo?.avatarUrl">
                    <el-icon>
                      <UserFilled />
                    </el-icon>
                  </el-avatar>
                </div>
                <div class="post-content">
                  <el-input
                    v-model="newPost.content"
                    type="textarea"
                    :rows="3"
                    placeholder="分享你的健身心得..."
                  />
                  <div class="post-actions">
                    <div class="post-tools">
                      <el-button size="small" type="text" @click="selectImage">
                        <el-icon><Picture /></el-icon> 图片
                      </el-button>
                      <el-button size="small" type="text" @click="selectVideo">
                        <el-icon><VideoCamera /></el-icon> 视频
                      </el-button>

                    </div>
                    <el-button type="primary" @click="submitPost" :loading="submitting">
                      发布
                    </el-button>
                  </div>
                </div>
              </div>
            </section>

            <!-- 动态列表 -->
            <section class="posts-section">
              <!-- 标签切换 -->
              <div class="tabs">
                <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                  <el-tab-pane label="全部" name="all">
                  </el-tab-pane>
                  <el-tab-pane label="关注" name="following">
                  </el-tab-pane>
                  <el-tab-pane label="我的" name="my">
                  </el-tab-pane>
                </el-tabs>
              </div>
              
              <!-- 关注用户列表 (仅在关注标签页显示) -->
              <div v-if="activeTab === 'following'" class="following-users-section">
                <h3 class="section-title">我关注的用户</h3>
                <div class="following-users-list">
                  <el-skeleton v-if="followingUsersLoading" :rows="3" animated />
                  <div v-else-if="followingUsers.length === 0" class="empty-following">
                    <el-empty description="还没有关注任何人，去推荐关注中关注一些用户吧！" />
                  </div>
                  <div v-else class="following-users-grid">
                    <div v-for="(user, index) in followingUsers" :key="user.id" class="following-user-card">
                      <el-avatar :size="56" :src="baseUrl + user.avatar">
                        <el-icon>
                          <UserFilled />
                        </el-icon>
                      </el-avatar>
                      <h4 class="user-name">{{ user.name }}</h4>
                      <p class="user-desc">{{ user.desc || '暂无简介' }}</p>
                      <el-button 
                        type="primary" 
                        size="small" 
                        :plain="!user.isFollowing"
                        @click="toggleFollow(user.id, index)"
                      >
                        {{ user.isFollowing ? '已关注' : '关注' }}
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="posts-list">
                <el-skeleton v-if="loading" :rows="3" animated />
                <div v-else-if="posts.length === 0 && activeTab === 'following'" class="empty-state">
                  <el-empty description="关注的用户还没有发布动态" />
                </div>
                <div v-else-if="posts.length === 0" class="empty-state">
                  <el-empty description="还没有动态，快来发布第一条吧！" />
                </div>
                <div v-else v-for="post in posts" :key="post.id" class="post-card">
                  <!-- 帖子头部 -->
                  <div class="post-header">
                    <div class="user-info">
                      <el-avatar :size="40" :src="baseUrl + post.userAvatar">
                        <el-icon>
                          <UserFilled />
                        </el-icon>
                      </el-avatar>
                      <div class="user-details">
                        <h3>{{ post.userName }}</h3>
                        <p>{{ formatTime(post.createdAt) }}</p>
                      </div>
                    </div>
                    <el-dropdown @command="(command: 'edit' | 'delete' | 'report') => handlePostCommand(command, post.id)">
                      <el-button size="small" type="text">
                        <el-icon><More /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="edit" v-if="post.userId === userInfo?.id">编辑</el-dropdown-item>
                          <el-dropdown-item command="delete" v-if="post.userId === userInfo?.id">删除</el-dropdown-item>
                          <el-dropdown-item command="report">举报</el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </div>

                  <!-- 帖子内容 -->
                  <div class="post-body">
                    <p class="post-text">{{ post.content }}</p>
                    <div v-if="post.images && post.images.length > 0" class="post-images">
                      <img v-for="(image, index) in post.images" :key="index" :src="baseUrl + image" alt="Post image" class="post-image" />
                    </div>
                    <div v-if="post.video" class="post-video">
                      <video :src="baseUrl + post.video" controls class="video-player"></video>
                    </div>

                  </div>

                  <!-- 帖子底部 -->
                  <div class="post-footer">
                    <div class="post-actions">
                      <button class="action-btn" @click="toggleLike(post.id)" :class="{ active: post.liked }">
                        <el-icon><Star /></el-icon>
                        <span>{{ post.likes }}</span>
                      </button>
                      <button class="action-btn" @click="showComments(post.id)">
                        <el-icon><ChatLineRound /></el-icon>
                        <span>{{ post.commentsCount }}</span>
                      </button>
                      <button class="action-btn" @click="sharePost(post.id)">
                        <el-icon><Share /></el-icon>
                        <span>分享</span>
                      </button>
                    </div>
                  </div>

                  <!-- 评论区 -->
                  <div v-if="showingComments === post.id" class="comments-section">
                    <div class="comments-list">
                      <div v-for="comment in post.comments" :key="comment.id" class="comment-item">
                        <el-avatar :size="32" :src="baseUrl + comment.userAvatar">
                          <el-icon>
                            <UserFilled />
                          </el-icon>
                        </el-avatar>
                        <div class="comment-content">
                          <div class="comment-header">
                            <h4>{{ comment.userName }}</h4>
                            <p>{{ formatTime(comment.createdAt) }}</p>
                          </div>
                          <p>{{ comment.content }}</p>
                        </div>
                      </div>
                    </div>
                    <div class="comment-form">
                      <el-input
                        v-model="newComment.content"
                        placeholder="写下你的评论..."
                        @keyup.enter="submitComment(post.id)"
                      />
                      <el-button type="primary" size="small" @click="submitComment(post.id)">
                        评论
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 分页 -->
              <div class="pagination">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 30, 50]"
                  layout="total, sizes, prev, pager, next, jumper"
                  :total="totalPosts"
                  @size-change="handleSizeChange"
                  @current-change="handleCurrentChange"
                />
              </div>
            </section>
          </div>

          <!-- 右侧内容 -->
          <div class="right-content">
            <!-- 个人信息卡片 -->
            <div class="sidebar-card">
              <div class="user-profile">
                <el-avatar :size="64" :src="baseUrl + userInfo?.avatarUrl">
                  <el-icon>
                    <UserFilled />
                  </el-icon>
                </el-avatar>
                <h3>{{ userInfo?.realName || userInfo?.username }}</h3>
                <p class="user-role">学生</p>
                <el-button type="primary" size="small" class="profile-btn" @click="goToProfile">
                  编辑资料
                </el-button>
              </div>
            </div>

            <!-- 热门话题 -->
            <div class="sidebar-card">
              <h3 class="card-title">热门话题</h3>
              <div class="hot-topics">
                <el-skeleton v-if="loading" :rows="5" animated />
                <div v-else-if="hotTopics.length === 0" class="empty-sidebar">
                  <el-empty :description="'暂无热门话题'" />
                </div>
                <div v-else v-for="(topic, index) in hotTopics" :key="index" class="topic-item">
                  <span class="topic-rank">{{ index + 1 }}</span>
                  <div class="topic-content">
                    <h4>{{ topic.title }}</h4>
                    <p>{{ topic.posts }} 帖子</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 推荐用户 -->
            <div class="sidebar-card">
              <h3 class="card-title">推荐关注</h3>
              <div class="recommended-users">
                <el-skeleton v-if="loading" :rows="4" animated />
                <div v-else-if="recommendedUsers.length === 0" class="empty-sidebar">
                  <el-empty :description="'暂无推荐用户'" />
                </div>
                <div v-else v-for="(user, index) in recommendedUsers" :key="index" class="user-item">
                  <el-avatar :size="40" :src="baseUrl + user.avatar">
                    <el-icon>
                      <UserFilled />
                    </el-icon>
                  </el-avatar>
                  <div class="user-info">
                    <h4>{{ user.name }}</h4>
                    <p class="user-desc">{{ user.desc }}</p>
                  </div>
                  <el-button 
                    type="primary" 
                    size="small" 
                    :plain="!user.isFollowing"
                    @click="toggleFollow(user.id, index)"
                  >
                    {{ user.isFollowing ? '已关注' : '关注' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 编辑帖子弹窗 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑帖子"
    width="500px"
  >
    <div class="edit-form">
      <el-input
        v-model="editingPost.content"
        type="textarea"
        :rows="4"
        placeholder="编辑你的健身心得..."
      />
      <div class="post-actions" style="margin-top: 15px;">
        <div class="post-tools">
          <el-button size="small" type="text" @click="selectImageForEdit">
            <el-icon><Picture /></el-icon> 图片
          </el-button>
          <el-button size="small" type="text" @click="selectVideoForEdit">
            <el-icon><VideoCamera /></el-icon> 视频
          </el-button>
        </div>
      </div>
      <div v-if="editingPost.images && editingPost.images.length > 0" class="edit-images">
        <div v-for="(image, index) in editingPost.images" :key="index" class="edit-image-item">
          <img :src="baseUrl + image" alt="Post image" class="edit-image" />
          <el-button type="danger" size="small" @click="removeImageForEdit(index)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-if="editingPost.video" class="edit-video">
        <video :src="baseUrl + editingPost.video" controls class="video-player"></video>
        <el-button type="danger" size="small" @click="removeVideoForEdit">
          <el-icon><Delete /></el-icon> 删除视频
        </el-button>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updatePost" :loading="updating">保存修改</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 举报帖子弹窗 -->
  <el-dialog
    v-model="reportDialogVisible"
    title="举报帖子"
    width="400px"
  >
    <div class="report-form">
      <p>请选择举报原因：</p>
      <el-radio-group v-model="reportReason">
        <el-radio label="spam">垃圾内容</el-radio>
        <el-radio label="inappropriate">不当内容</el-radio>
        <el-radio label="harassment">骚扰</el-radio>
        <el-radio label="other">其他原因</el-radio>
      </el-radio-group>
      <el-input
        v-model="reportDescription"
        type="textarea"
        :rows="3"
        placeholder="请详细描述举报原因（可选）"
        style="margin-top: 15px;"
      />
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport" :loading="reporting">提交举报</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 分享帖子弹窗 -->
  <el-dialog
    v-model="shareDialogVisible"
    title="分享帖子"
    width="400px"
  >
    <div class="share-form">
      <p>选择分享方式：</p>
      <div class="share-options">
        <div class="share-option" @click="shareToQQ">
          <el-icon class="share-icon"><ChatLineSquare /></el-icon>
          <span>分享到QQ好友</span>
        </div>
        <div class="share-option" @click="shareToWeChat">
          <el-icon class="share-icon"><ChatDotRound /></el-icon>
          <span>分享到微信好友</span>
        </div>
        <div class="share-option" @click="copyLink">
          <el-icon class="share-icon"><DocumentCopy /></el-icon>
          <span>复制链接</span>
        </div>
      </div>
      <div class="share-link" v-if="shareLink">
        <p>分享链接：</p>
        <el-input
          v-model="shareLink"
          readonly
          style="margin-bottom: 15px;"
        />
        <el-button type="primary" size="small" @click="copyLink">
          <el-icon><DocumentCopy /></el-icon> 复制链接
        </el-button>
      </div>
      <div class="wechat-qrcode" v-if="shareLink">
        <p>微信扫码分享：</p>
        <div class="qrcode-container">
          <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=${encodeURIComponent(shareLink)}`" alt="微信分享二维码" class="qrcode" />
        </div>
        <p class="qrcode-tip">请使用微信扫描二维码分享给好友</p>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="shareDialogVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'
import { socialApi } from '@/api/social'
import { uploadFile } from '@/api/upload'

const router = useRouter()

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const baseUrl = import.meta.env.VITE_APP_BASE_API

// 帖子相关
const posts = ref<any[]>([])
const totalPosts = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const showingComments = ref<number | null>(null)
const activeTab = ref('all')

// 热门话题
const hotTopics = ref<Array<{ title: string; posts: number }>>([])

// 推荐用户
const recommendedUsers = ref<Array<{ id: number; name: string; avatar: string; desc: string; isFollowing: boolean }>>([])

// 关注用户
const followingUsers = ref<Array<{ id: number; name: string; avatar: string; desc: string; isFollowing: boolean }>>([])
const followingUsersLoading = ref(false)

// 发布新帖
const newPost = ref({
  content: '',
  images: [] as string[],
  video: ''
})

const submitting = ref(false)

// 评论
const newComment = ref({
  content: ''
})

// 编辑帖子
const editDialogVisible = ref(false)
const editingPost = ref({
  id: 0,
  content: '',
  images: [] as string[],
  video: ''
})
const updating = ref(false)

// 举报帖子
const reportDialogVisible = ref(false)
const reportingPostId = ref(0)
const reportReason = ref('spam')
const reportDescription = ref('')
const reporting = ref(false)

// 分享帖子
const shareDialogVisible = ref(false)
const sharingPostId = ref(0)
const shareLink = ref('')

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 提交帖子
const submitPost = async () => {
  // 检查是否有内容或图片/视频
  if (!newPost.value.content.trim() && newPost.value.images.length === 0 && !newPost.value.video) {
    ElMessage.warning('请输入内容或选择图片/视频')
    return
  }

  submitting.value = true
  try {
    const postData: any = {
      content: newPost.value.content,
      images: newPost.value.images,
      video: newPost.value.video
    }



    const res = await socialApi.createPost(postData)
    
    if (res.data) {
      posts.value.unshift(res.data)
      totalPosts.value++
      
      // 重置表单
      newPost.value = {
        content: '',
        images: [],
        video: ''
      }

      ElMessage.success('发布成功')
    }
  } catch (error) {
    ElMessage.error('发布失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 选择图片
const selectImage = () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.multiple = true
  
  // 监听文件选择事件
  input.onchange = async (e) => {
    const files = (e.target as HTMLInputElement).files
    if (!files || files.length === 0) return
    
    const loading = ElLoading.service({
      lock: true,
      text: '正在上传图片...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 上传每个选中的图片
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        // 限制文件大小为5MB
        if (file.size > 5 * 1024 * 1024) {
          ElMessage.warning(`图片 ${file.name} 超过5MB，已跳过`)
          continue
        }
        
        const imagePath = await uploadFile(file, 'social')
        newPost.value.images.push(imagePath)
      }
      
      if (files.length > 0) {
        ElMessage.success(`成功上传 ${newPost.value.images.length} 张图片`)
      }
    } catch (error) {
      ElMessage.error('上传图片失败，请重试')
    } finally {
      loading.close()
    }
  }
  
  // 触发文件选择
  input.click()
}

// 选择视频
const selectVideo = () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'video/*'
  input.multiple = false
  
  // 监听文件选择事件
  input.onchange = async (e) => {
    const files = (e.target as HTMLInputElement).files
    if (!files || files.length === 0) return
    
    const file = files[0]
    // 限制视频大小为50MB
    if (file.size > 50 * 1024 * 1024) {
      ElMessage.warning('视频超过50MB，无法上传')
      return
    }
    
    const loading = ElLoading.service({
      lock: true,
      text: '正在上传视频...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      const videoPath = await uploadFile(file, 'social')
      newPost.value.video = videoPath
      ElMessage.success('视频上传成功')
    } catch (error) {
      ElMessage.error('上传视频失败，请重试')
    } finally {
      loading.close()
    }
  }
  
  // 触发文件选择
  input.click()
}



// 切换点赞状态
const toggleLike = async (postId: number) => {
  try {
    const res = await socialApi.toggleLike(postId)
    if (res.data !== undefined) {
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        post.liked = res.data
        post.likes += res.data ? 1 : -1
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 显示评论
const showComments = async (postId: number) => {
  if (showingComments.value === postId) {
    showingComments.value = null
  } else {
    showingComments.value = postId
    // 加载评论
    const post = posts.value.find(p => p.id === postId)
    if (post && (!post.comments || post.comments.length === 0)) {
      try {
        const res = await socialApi.getComments(postId)
        if (res.data) {
          post.comments = res.data
        }
      } catch (error) {
        ElMessage.error('加载评论失败')
      }
    }
  }
}

// 提交评论
const submitComment = async (postId: number) => {
  if (!newComment.value.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await socialApi.createComment(postId, {
      content: newComment.value.content
    })
    
    const post = posts.value.find(p => p.id === postId)
    if (post) {
      const newCommentData = {
        id: Date.now(),
        userId: userInfo.value?.id,
        userName: userInfo.value?.realName || userInfo.value?.username,
        userAvatar: userInfo.value?.avatarUrl,
        content: newComment.value.content,
        createdAt: new Date().toISOString()
      }

      if (!post.comments) {
        post.comments = []
      }
      post.comments.push(newCommentData)
      post.commentsCount++
      newComment.value.content = ''
      ElMessage.success('评论成功')
    }
  } catch (error) {
    ElMessage.error('评论失败，请重试')
  }
}

// 分享帖子
const sharePost = (postId: number) => {
  sharingPostId.value = postId
  // 生成分享链接
  const baseUrl = window.location.origin
  shareLink.value = `${baseUrl}/student/social?postId=${postId}`
  shareDialogVisible.value = true
}

// 分享到QQ好友
const shareToQQ = () => {
  const url = encodeURIComponent(shareLink.value)
  const title = encodeURIComponent('健身分享')
  const desc = encodeURIComponent('快来看看这个健身分享！')
  // 使用QQ协议直接打开QQ客户端
  const qqUrl = `tencent://message/?uin=0&Site=qq&Menu=yes&title=${title}&desc=${desc}&url=${url}`
  // 尝试打开QQ客户端
  window.open(qqUrl)
  // 同时打开网页版作为备选
  const webQQUrl = `https://connect.qq.com/widget/shareqq/index.html?url=${url}&title=${title}&desc=${desc}`
  window.open(webQQUrl, '_blank', 'width=600,height=400')
}

// 分享到微信好友
const shareToWeChat = () => {
  // 生成微信分享链接
  const url = encodeURIComponent(shareLink.value)
  const title = encodeURIComponent('健身分享')
  // 尝试使用微信协议打开微信客户端（仅在移动端有效）
  const wechatUrl = `weixin://share?url=${url}&title=${title}`
  window.open(wechatUrl)
  // 提示用户扫描二维码或复制链接
  ElMessage.info('请在微信中扫描二维码或复制链接分享给好友')
  copyLink()
}

// 复制链接到剪贴板
const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 处理帖子操作
const handlePostCommand = async (command: 'edit' | 'delete' | 'report', postId: number) => {
  switch (command) {
    case 'edit':
      // 打开编辑弹窗
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        editingPost.value = {
          id: post.id,
          content: post.content,
          images: [...(post.images || [])],
          video: post.video || ''
        }
        editDialogVisible.value = true
      }
      break
    case 'delete':
      try {
        await socialApi.deletePost(postId)
        posts.value = posts.value.filter(p => p.id !== postId)
        totalPosts.value--
        ElMessage.success('删除成功')
      } catch (error) {
        ElMessage.error('删除失败，请重试')
      }
      break
    case 'report':
      // 打开举报弹窗
      reportingPostId.value = postId
      reportReason.value = 'spam'
      reportDescription.value = ''
      reportDialogVisible.value = true
      break
  }
}

// 为编辑选择图片
const selectImageForEdit = () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.multiple = true
  
  // 监听文件选择事件
  input.onchange = async (e) => {
    const files = (e.target as HTMLInputElement).files
    if (!files || files.length === 0) return
    
    const loading = ElLoading.service({
      lock: true,
      text: '正在上传图片...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      // 上传每个选中的图片
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        // 限制文件大小为5MB
        if (file.size > 5 * 1024 * 1024) {
          ElMessage.warning(`图片 ${file.name} 超过5MB，已跳过`)
          continue
        }
        
        const imagePath = await uploadFile(file, 'social')
        editingPost.value.images.push(imagePath)
      }
      
      if (files.length > 0) {
        ElMessage.success(`成功上传 ${files.length} 张图片`)
      }
    } catch (error) {
      ElMessage.error('上传图片失败，请重试')
    } finally {
      loading.close()
    }
  }
  
  // 触发文件选择
  input.click()
}

// 为编辑选择视频
const selectVideoForEdit = () => {
  // 创建隐藏的文件输入元素
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'video/*'
  input.multiple = false
  
  // 监听文件选择事件
  input.onchange = async (e) => {
    const files = (e.target as HTMLInputElement).files
    if (!files || files.length === 0) return
    
    const file = files[0]
    // 限制视频大小为50MB
    if (file.size > 50 * 1024 * 1024) {
      ElMessage.warning('视频超过50MB，无法上传')
      return
    }
    
    const loading = ElLoading.service({
      lock: true,
      text: '正在上传视频...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      const videoPath = await uploadFile(file, 'social')
      editingPost.value.video = videoPath
      ElMessage.success('视频上传成功')
    } catch (error) {
      ElMessage.error('上传视频失败，请重试')
    } finally {
      loading.close()
    }
  }
  
  // 触发文件选择
  input.click()
}

// 移除编辑中的图片
const removeImageForEdit = (index: number) => {
  editingPost.value.images.splice(index, 1)
}

// 移除编辑中的视频
const removeVideoForEdit = () => {
  editingPost.value.video = ''
}

// 更新帖子
const updatePost = async () => {
  // 检查是否有内容或图片/视频
  if (!editingPost.value.content.trim() && editingPost.value.images.length === 0 && !editingPost.value.video) {
    ElMessage.warning('请输入内容或选择图片/视频')
    return
  }

  updating.value = true
  try {
    const postData: any = {
      content: editingPost.value.content,
      images: editingPost.value.images,
      video: editingPost.value.video
    }

    const res = await socialApi.updatePost(editingPost.value.id, postData)
    
    if (res.data) {
      // 更新本地帖子列表
      const index = posts.value.findIndex(p => p.id === editingPost.value.id)
      if (index !== -1) {
        posts.value[index] = res.data
      }
      
      editDialogVisible.value = false
      ElMessage.success('编辑成功')
    }
  } catch (error) {
    ElMessage.error('编辑失败，请重试')
  } finally {
    updating.value = false
  }
}

// 提交举报
const submitReport = async () => {
  reporting.value = true
  try {
    const reportData = {
      reason: reportReason.value,
      description: reportDescription.value
    }

    // 调用举报API
    await socialApi.reportPost(reportingPostId.value, reportData)
    
    reportDialogVisible.value = false
    ElMessage.success('举报成功，我们会尽快处理')
  } catch (error) {
    ElMessage.error('举报失败，请重试')
  } finally {
    reporting.value = false
  }
}

// 加载帖子列表
const loading = ref(false)

const loadPosts = async () => {
  loading.value = true
  try {
    let res
    if (activeTab.value === 'all') {
      res = await socialApi.getPosts({
        current: currentPage.value,
        size: pageSize.value
      })
    } else if (activeTab.value === 'following') {
      res = await socialApi.getFollowingPosts({
        current: currentPage.value,
        size: pageSize.value
      })
    } else {
      res = await socialApi.getMyPosts({
        current: currentPage.value,
        size: pageSize.value
      })
    }
    
    if (res.data) {
      posts.value = res.data.records
      totalPosts.value = res.data.total
    }
  } catch (error) {
    ElMessage.error('加载帖子失败，请重试')
  } finally {
    loading.value = false
  }
}

// 标签切换处理
const handleTabChange = (tab: string) => {
  activeTab.value = tab
  currentPage.value = 1 // 切换标签时重置到第一页
  loadPosts()
  
  // 如果切换到关注标签，加载关注用户列表
  if (tab === 'following') {
    loadFollowingUsers()
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadPosts()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  loadPosts()
}

// 加载热门话题
const loadHotTopics = async () => {
  try {
    const res = await socialApi.getHotTopics()
    console.log('热门话题API返回:', res)
    if (res.data) {
      console.log('热门话题数据:', res.data)
      // 打印每个话题的具体内容
      res.data.forEach((topic: any, index: number) => {
        console.log(`话题${index + 1}:`, topic)
        console.log(`话题${index + 1}标题:`, topic.title)
        console.log(`话题${index + 1}帖子数:`, topic.posts)
      })
      hotTopics.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载热门话题失败')
  }
}

// 加载推荐用户
const loadRecommendedUsers = async () => {
  try {
    const res = await socialApi.getRecommendedUsers()
    if (res.data) {
      // 为每个用户添加isFollowing字段，并检查实际的关注状态
      const usersWithFollowStatus = await Promise.all(
        res.data.map(async (user: any) => {
          try {
            const followRes = await socialApi.checkFollow(user.id)
            return {
              ...user,
              isFollowing: followRes.data || false
            }
          } catch (error) {
            // 如果检查失败，默认为未关注
            return {
              ...user,
              isFollowing: false
            }
          }
        })
      )
      recommendedUsers.value = usersWithFollowStatus
    }
  } catch (error) {
    ElMessage.error('加载推荐用户失败')
  }
}

// 关注/取消关注用户
const toggleFollow = async (userId: number, index: number) => {
  try {
    const res = await socialApi.toggleFollow(userId)
    if (res.data !== undefined) {
      // 更新推荐用户的关注状态
      if (recommendedUsers.value[index]) {
        recommendedUsers.value[index].isFollowing = res.data
        ElMessage.success(res.data ? '关注成功' : '取消关注成功')
      }
      
      // 如果是从关注用户列表中操作，也更新关注用户列表的状态
      const followingIndex = followingUsers.value.findIndex(u => u.id === userId)
      if (followingIndex !== -1) {
        followingUsers.value[followingIndex].isFollowing = res.data
        // 如果取消关注，从关注用户列表中移除
        if (!res.data) {
          followingUsers.value.splice(followingIndex, 1)
        }
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 加载关注用户列表
const loadFollowingUsers = async () => {
  followingUsersLoading.value = true
  try {
    const res = await socialApi.getFollowingUsers()
    if (res.data) {
      followingUsers.value = res.data.map((user: any) => ({
        ...user,
        isFollowing: true
      }))
    }
  } catch (error) {
    ElMessage.error('加载关注用户失败')
  } finally {
    followingUsersLoading.value = false
  }
}

// 跳转到个人中心页面
const goToProfile = () => {
  router.push('/student/profile')
}

onMounted(() => {
  console.log('社交广场加载完成')
  loadPosts()
  loadHotTopics()
  loadRecommendedUsers()
})
</script>

<style scoped>
.social-square {
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  background-color: #409eff;
  color: white;
  padding: 60px 0;
  text-align: center;
}

.page-header h1 {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 16px;
  opacity: 0.9;
}

/* 主内容区 */
.main-content {
  padding: 20px 0 40px;
}

.content-layout {
  display: flex;
  gap: 20px;
}

/* 左侧内容 */
.left-content {
  flex: 1;
  min-width: 0;
}

/* 右侧内容 */
.right-content {
  width: 300px;
  flex-shrink: 0;
}

/* 发布动态 */
.post-section {
  margin-bottom: 20px;
}

.post-form {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 15px;
}

.user-avatar {
  flex-shrink: 0;
}

.post-content {
  flex: 1;
}

.post-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.post-tools {
  display: flex;
  gap: 15px;
}

/* 动态列表 */
.posts-section {
  width: 100%;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-details h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 2px;
}

.user-details p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.post-body {
  margin-bottom: 15px;
}

.post-text {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 10px;
  margin-bottom: 15px;
}

.post-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 4px;
}

.post-video {
  margin-bottom: 15px;
}

.video-player {
  width: 100%;
  max-height: 400px;
  border-radius: 4px;
}

.exercise-tag {
  background-color: #f0f9eb;
  color: #67c23a;
  padding: 8px 12px;
  border-radius: 16px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 5px;
  width: fit-content;
}

.tag-icon {
  font-size: 14px;
}

.post-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}

.post-actions {
  display: flex;
  gap: 30px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  background: none;
  border: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: color 0.3s;
  padding: 5px 0;
}

.action-btn:hover {
  color: #409eff;
}

.action-btn.active {
  color: #f56c6c;
}

/* 评论区 */
.comments-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.comments-list {
  margin-bottom: 15px;
}

.comment-item {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-header h4 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
}

.comment-header p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.comment-content p {
  font-size: 14px;
  line-height: 1.5;
  margin: 0;
}

.comment-form {
  display: flex;
  gap: 10px;
}

/* 标签切换 */
.tabs {
  margin-bottom: 20px;
  background-color: white;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 空状态 */
.empty-state {
  background-color: white;
  border-radius: 8px;
  padding: 60px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
  margin-bottom: 20px;
}

/* 分页 */
.pagination {
  margin-top: 30px;
  text-align: center;
}

/* 右侧边栏 */
.sidebar-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

/* 用户资料 */
.user-profile {
  text-align: center;
}

.user-profile h3 {
  font-size: 16px;
  font-weight: bold;
  margin: 10px 0 5px;
  color: #333;
}

.user-role {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
}

.profile-btn {
  width: 100%;
}

/* 热门话题 */
.hot-topics {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.topic-item {
  display: flex;
  gap: 10px;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.topic-item:last-child {
  border-bottom: none;
}

.topic-rank {
  width: 24px;
  height: 24px;
  background-color: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #666;
}

.topic-item:nth-child(1) .topic-rank {
  background-color: #f56c6c;
  color: white;
}

.topic-item:nth-child(2) .topic-rank {
  background-color: #e6a23c;
  color: white;
}

.topic-item:nth-child(3) .topic-rank {
  background-color: #409eff;
  color: white;
}

.topic-content h4 {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 2px;
  color: #333;
}

.topic-content p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

/* 推荐用户 */
.recommended-users {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-item .user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-item h4 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

.user-desc {
  font-size: 12px;
  color: #999;
  margin: 0;
}

/* 侧边栏空状态 */
.empty-sidebar {
  padding: 30px 0;
  text-align: center;
}

.empty-sidebar :deep(.el-empty__description) {
  font-size: 12px;
  color: #999;
}

/* 关注用户列表 */
.following-users-section {
  margin-bottom: 30px;
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.following-users-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.following-user-card {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  background-color: #f9f9f9;
  transition: all 0.3s;
}

.following-user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-name {
  font-size: 14px;
  font-weight: bold;
  margin: 10px 0 5px;
  color: #333;
}

.user-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 15px;
  height: 36px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.empty-following {
  padding: 40px 0;
  text-align: center;
}

.empty-following :deep(.el-empty__description) {
  font-size: 14px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .right-content {
    width: 250px;
  }
}

@media (max-width: 768px) {
  .content-layout {
    flex-direction: column;
  }

  .right-content {
    width: 100%;
  }

  .page-header {
    padding: 40px 0;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .post-images {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .post-image {
    height: 120px;
  }
}

@media (max-width: 480px) {
  .post-form {
    flex-direction: column;
    align-items: flex-start;
  }

  .user-avatar {
    align-self: flex-start;
  }

  .post-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .post-tools {
    width: 100%;
    justify-content: space-between;
  }

  .action-btn {
    font-size: 12px;
  }
}

/* 编辑弹窗样式 */
.edit-form {
  padding: 10px 0;
}

.edit-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}

.edit-image-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.edit-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.edit-image-item .el-button {
  position: absolute;
  top: -8px;
  right: -8px;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-video {
  margin-top: 15px;
  position: relative;
}

.edit-video .el-button {
  margin-top: 10px;
}

/* 分享弹窗样式 */
.share-form {
  padding: 10px 0;
}

.share-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 20px 0;
}

.share-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100px;
  height: 100px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  padding: 10px;
}

.share-option:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.share-icon {
  font-size: 32px;
  margin-bottom: 10px;
  color: #666;
}

.share-option:hover .share-icon {
  color: #409eff;
  transform: scale(1.1);
  transition: all 0.3s;
}

.share-option span {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.share-link {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.share-link p {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

/* 微信二维码样式 */
.wechat-qrcode {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  text-align: center;
}

.wechat-qrcode p {
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.qrcode-container {
  margin: 10px 0;
  display: flex;
  justify-content: center;
}

.qrcode {
  width: 150px;
  height: 150px;
  border-radius: 8px;
}

.qrcode-tip {
  font-size: 12px;
  color: #666;
  margin-top: 10px;
}
</style>