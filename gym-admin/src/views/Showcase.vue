<template>
  <div class="showcase-container">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="showcase-header">
        <div class="header-content">
          <div class="header-left">
            <h1>🏋️ 大学生健身管理系统</h1>
            <span class="subtitle">完整功能演示</span>
          </div>
          <div class="header-right">
            <el-button @click="goToLogin">
              <el-icon><User /></el-icon>
              返回登录
            </el-button>
            <el-button @click="goToRoleTest">
              <el-icon><MagicStick /></el-icon>
              角色测试
            </el-button>
          </div>
        </div>
      </el-header>

      <el-main class="showcase-main">
        <!-- 系统介绍 -->
        <el-card class="intro-card" shadow="hover">
          <template #header>
            <span>📖 系统简介</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <h3>🎯 系统目标</h3>
              <p>为高校提供全面的健身管理解决方案，涵盖课程管理、器材预约、健身计划、数据统计及 AI 智能助手。</p>
              
              <h3>👥 三种角色</h3>
              <ul>
                <li>👨‍💼 <strong>管理员</strong>：系统管理、数据统计</li>
                <li>🏋️ <strong>教练</strong>：课程管理、学员指导</li>
                <li>👨‍🎓 <strong>学生</strong>：预约课程、健身计划</li>
              </ul>
            </el-col>
            <el-col :span="12">
              <h3>🔧 技术栈</h3>
              <ul>
                <li>前端：Vue 3 + Vite + TypeScript</li>
                <li>UI框架：Element Plus</li>
                <li>图表库：ECharts</li>
                <li>状态管理：Pinia</li>
                <li>后端：Spring Boot + MyBatis Plus</li>
                <li>安全：Spring Security + JWT</li>
                <li>缓存：Redis</li>
                <li>数据库：MySQL 8.0</li>
              </ul>
            </el-col>
          </el-row>
        </el-card>

        <!-- 功能模块展示 -->
        <el-divider content-position="left">
          <h2>🚀 功能模块展示</h2>
        </el-divider>

        <!-- 管理员功能 -->
        <el-card class="role-showcase admin-showcase" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>👨‍💼 管理员功能</span>
              <el-button type="danger" @click="showAdminDemo">
                查看演示
              </el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="6" v-for="(feature, index) in adminFeatures" :key="index">
              <div class="feature-card">
                <div class="feature-icon">{{ feature.icon }}</div>
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 教练功能 -->
        <el-card class="role-showcase coach-showcase" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>🏋️ 教练功能</span>
              <el-button type="warning" @click="showCoachDemo">
                查看演示
              </el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="6" v-for="(feature, index) in coachFeatures" :key="index">
              <div class="feature-card">
                <div class="feature-icon">{{ feature.icon }}</div>
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 学生功能 -->
        <el-card class="role-showcase student-showcase" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>👨‍🎓 学生功能</span>
              <el-button type="primary" @click="showStudentDemo">
                查看演示
              </el-button>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="6" v-for="(feature, index) in studentFeatures" :key="index">
              <div class="feature-card">
                <div class="feature-icon">{{ feature.icon }}</div>
                <h4>{{ feature.title }}</h4>
                <p>{{ feature.desc }}</p>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 页面预览 -->
        <el-divider content-position="left">
          <h2>📱 页面预览</h2>
        </el-divider>

        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('dashboard')">
              <div class="preview-content">
                <div class="preview-icon">📊</div>
                <h4>仪表盘</h4>
                <p>数据可视化概览</p>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('course')">
              <div class="preview-content">
                <div class="preview-icon">📅</div>
                <h4>课程管理</h4>
                <p>课程发布与预约</p>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('equipment')">
              <div class="preview-content">
                <div class="preview-icon">🏃</div>
                <h4>器材预约</h4>
                <p>健身器材预约</p>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('plan')">
              <div class="preview-content">
                <div class="preview-icon">📝</div>
                <h4>健身计划</h4>
                <p>个性化训练计划</p>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('ai')">
              <div class="preview-content">
                <div class="preview-icon">🤖</div>
                <h4>AI 助手</h4>
                <p>智能健身指导</p>
              </div>
            </el-card>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-card class="preview-card" shadow="hover" @click="previewPage('profile')">
              <div class="preview-content">
                <div class="preview-icon">👤</div>
                <h4>个人中心</h4>
                <p>个人信息管理</p>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 快速开始 -->
        <el-card class="quick-start" shadow="hover">
          <template #header>
            <span>🎮 快速开始</span>
          </template>
          <el-steps :active="activeStep" align-center finish-status="success">
            <el-step title="选择角色" description="选择管理员、教练或学生" />
            <el-step title="登录系统" description="使用模拟数据登录" />
            <el-step title="探索功能" description="查看各种功能模块" />
            <el-step title="体验完整" description="享受完整的健身管理服务" />
          </el-steps>
          <div class="step-actions">
            <el-button type="primary" @click="quickStart" size="large">
              <el-icon><MagicStick /></el-icon>
              开始体验
            </el-button>
          </div>
        </el-card>
      </el-main>
    </el-container>

    <!-- 页面预览对话框 -->
    <el-dialog v-model="previewDialogVisible" :title="previewTitle" width="80%" top="5vh">
      <div class="preview-dialog-content" v-html="previewContent"></div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="previewPage">去页面</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, MagicStick } from '@element-plus/icons-vue'

const router = useRouter()
const activeStep = ref(0)
const previewDialogVisible = ref(false)
const previewTitle = ref('')
const previewContent = ref('')

const adminFeatures = [
  { icon: '📊', title: '数据统计', desc: '全面的数据分析与报表' },
  { icon: '👥', title: '用户管理', desc: '管理所有系统用户' },
  { icon: '📚', title: '课程管理', desc: '发布和管理健身课程' },
  { icon: '🏋️', title: '器材管理', desc: '管理健身器材设备' }
]

const coachFeatures = [
  { icon: '🎓', title: '学生管理', desc: '管理指导的学生' },
  { icon: '📅', title: '我的课程', desc: '管理自己的课程' },
  { icon: '📝', title: '训练计划', desc: '制定学生训练计划' },
  { icon: '📈', title: '指导记录', desc: '记录学员训练情况' }
]

const studentFeatures = [
  { icon: '📚', title: '课程预约', desc: '预约健身课程' },
  { icon: '🏃', title: '器材预约', desc: '预约健身器材' },
  { icon: '📝', title: '健身计划', desc: '个性化训练计划' },
  { icon: '🤖', title: 'AI 助手', desc: '智能健身指导' }
]

const goToLogin = () => {
  router.push('/login')
}

const goToRoleTest = () => {
  router.push('/role-test')
}

const showAdminDemo = () => {
  // 模拟管理员登录
  localStorage.setItem('demo_role', 'admin')
  activeStep.value = 1
  ElMessage.info('正在演示管理员功能...')
  setTimeout(() => {
    router.push('/dashboard')
  }, 1000)
}

const showCoachDemo = () => {
  localStorage.setItem('demo_role', 'coach')
  activeStep.value = 1
  ElMessage.info('正在演示教练功能...')
  setTimeout(() => {
    router.push('/dashboard')
  }, 1000)
}

const showStudentDemo = () => {
  localStorage.setItem('demo_role', 'student')
  activeStep.value = 1
  ElMessage.info('正在演示学生功能...')
  setTimeout(() => {
    router.push('/dashboard')
  }, 1000)
}

const previewPage = (type: string) => {
  const pages: Record<string, { title: string; path: string }> = {
    dashboard: { title: '仪表盘预览', path: '/dashboard' },
    course: { title: '课程管理预览', path: '/admin/courses' },
    equipment: { title: '器材预约预览', path: '/student/equipment' },
    plan: { title: '健身计划预览', path: '/student/plans' },
    ai: { title: 'AI 助手预览', path: '/student/ai-assistant' },
    profile: { title: '个人中心预览', path: '/profile' }
  }

  const page = pages[type]
  if (page) {
    previewTitle.value = page.title
    previewContent.value = `
      <div style="text-align: center; padding: 40px;">
        <div style="font-size: 64px; margin-bottom: 20px;">📱</div>
        <h3>${page.title}</h3>
        <p>点击下方按钮跳转到实际页面</p>
      </div>
    `
    previewDialogVisible.value = true
  }
}

const quickStart = () => {
  router.push('/role-test')
}
</script>

<style scoped>
.showcase-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.showcase-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.header-left h1 {
  margin: 0;
  font-size: 24px;
}

.header-left .subtitle {
  margin-left: 10px;
  font-size: 14px;
  opacity: 0.8;
}

.header-right {
  display: flex;
  gap: 10px;
}

.showcase-main {
  max-width: 1400px;
  margin: 20px auto;
  padding: 0 20px;
}

.intro-card {
  margin-bottom: 20px;
}

.intro-card h3 {
  color: #333;
  margin: 20px 0 10px;
}

.intro-card ul {
  padding-left: 20px;
}

.intro-card li {
  margin-bottom: 8px;
  color: #666;
}

.el-divider {
  margin: 30px 0;
}

.el-divider h2 {
  margin: 0;
  color: #333;
}

.role-showcase {
  margin-bottom: 20px;
}

.role-showcase.admin-showcase {
  border-left: 4px solid #ff4d4d;
}

.role-showcase.coach-showcase {
  border-left: 4px solid #ffaa00;
}

.role-showcase.student-showcase {
  border-left: 4px solid #1890ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feature-card {
  padding: 20px;
  text-align: center;
  background: #f8f9fa;
  border-radius: 8px;
  transition: transform 0.3s, box-shadow 0.3s;
  height: 100%;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.feature-card h4 {
  margin: 10px 0;
  color: #333;
}

.feature-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.preview-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.preview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.preview-content {
  text-align: center;
  padding: 20px;
}

.preview-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.preview-content h4 {
  margin: 10px 0;
  color: #333;
}

.preview-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.quick-start {
  margin-bottom: 20px;
}

.step-actions {
  margin-top: 30px;
  text-align: center;
}

.preview-dialog-content {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 10px;
  }

  .showcase-main {
    padding: 0 10px;
  }
}
</style>
