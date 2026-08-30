<template>
  <div class="role-test-container">
    <el-container>
      <el-header class="test-header">
        <h1>🎭 角色切换测试页面</h1>
        <p>快速切换不同角色，查看完整的系统功能</p>
      </el-header>

      <el-main>
        <el-row :gutter="20">
          <!-- 角色选择器 -->
          <el-col :span="24">
            <el-card class="role-selector">
              <template #header>
                <span>选择角色并登录</span>
              </template>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-button
                    type="danger"
                    size="large"
                    :icon="User"
                    @click="quickLogin('admin')"
                    :loading="loading"
                  >
                    <div class="button-content">
                      <span class="role-name">管理员</span>
                      <span class="role-desc">Admin</span>
                    </div>
                  </el-button>
                </el-col>
                <el-col :span="8">
                  <el-button
                    type="warning"
                    size="large"
                    :icon="Trophy"
                    @click="quickLogin('coach')"
                    :loading="loading"
                  >
                    <div class="button-content">
                      <span class="role-name">教练</span>
                      <span class="role-desc">Coach</span>
                    </div>
                  </el-button>
                </el-col>
                <el-col :span="8">
                  <el-button
                    type="primary"
                    size="large"
                    :icon="UserFilled"
                    @click="quickLogin('student')"
                    :loading="loading"
                  >
                    <div class="button-content">
                      <span class="role-name">学生</span>
                      <span class="role-desc">Student</span>
                    </div>
                  </el-button>
                </el-col>
              </el-row>
            </el-card>
          </el-col>

          <!-- 当前状态 -->
          <el-col :span="24">
            <el-card class="status-card">
              <template #header>
                <span>当前登录状态</span>
              </template>
              <el-descriptions :column="3" border>
                <el-descriptions-item label="用户名">
                  {{ userInfo?.username || '未登录' }}
                </el-descriptions-item>
                <el-descriptions-item label="角色">
                  <el-tag v-if="userInfo?.role" :type="getRoleTagType(userInfo.role)">
                    {{ getRoleLabel(userInfo.role) }}
                  </el-tag>
                  <span v-else>未登录</span>
                </el-descriptions-item>
                <el-descriptions-item label="Token状态">
                  <el-tag :type="token ? 'success' : 'info'">
                    {{ token ? '有效' : '无效' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="姓名">
                  {{ userInfo?.name || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="学号/工号">
                  {{ userInfo?.studentId || userInfo?.employeeId || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="联系方式">
                  {{ userInfo?.phone || '-' }}
                </el-descriptions-item>
              </el-descriptions>

              <div style="margin-top: 20px; text-align: center;">
                <el-button-group>
                  <el-button @click="goToDashboard" :disabled="!token">
                    <el-icon><HomeFilled /></el-icon>
                    去仪表盘
                  </el-button>
                  <el-button @click="viewMenus" :disabled="!token">
                    <el-icon><Menu /></el-icon>
                    查看菜单
                  </el-button>
                  <el-button @click="viewAllPages" :disabled="!token">
                    <el-icon><View /></el-icon>
                    查看所有页面
                  </el-button>
                  <el-button type="danger" @click="logout" :disabled="!token">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-button>
                </el-button-group>
              </div>
            </el-card>
          </el-col>

          <!-- 角色权限说明 -->
          <el-col :span="24">
            <el-card class="permissions-card">
              <template #header>
                <span>各角色权限说明</span>
              </template>
              <el-row :gutter="20">
                <el-col :span="8">
                  <div class="role-permission admin">
                    <h3>👨‍💼 管理员</h3>
                    <ul>
                      <li>✅ 课程管理</li>
                      <li>✅ 器材管理</li>
                      <li>✅ 用户管理</li>
                      <li>✅ 数据统计</li>
                      <li>✅ 系统配置</li>
                    </ul>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="role-permission coach">
                    <h3>🏋️ 教练</h3>
                    <ul>
                      <li>✅ 学生管理</li>
                      <li>✅ 我的课程</li>
                      <li>✅ 课程排期</li>
                      <li>✅ 学员训练计划</li>
                      <li>✅ 指导记录</li>
                    </ul>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="role-permission student">
                    <h3>👨‍🎓 学生</h3>
                    <ul>
                      <li>✅ 课程预约</li>
                      <li>✅ 器材预约</li>
                      <li>✅ 健身计划</li>
                      <li>✅ 个人中心</li>
                      <li>✅ AI健身助手</li>
                    </ul>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </el-col>

          <!-- 页面导航 -->
          <el-col :span="24" v-if="token">
            <el-card class="pages-nav">
              <template #header>
                <span>快速导航到各个页面</span>
              </template>
              <el-menu mode="horizontal" :default-active="activeMenu" router>
                <el-menu-item index="/dashboard">
                  <el-icon><HomeFilled /></el-icon>
                  仪表盘
                </el-menu-item>
                
                <el-sub-menu index="admin" v-if="userInfo?.role === 'ADMIN'">
                  <template #title>
                    <el-icon><Setting /></el-icon>
                    管理功能
                  </template>
                  <el-menu-item index="/admin/courses">课程管理</el-menu-item>
                  <el-menu-item index="/admin/equipment">器材管理</el-menu-item>
                  <el-menu-item index="/admin/users">用户管理</el-menu-item>
                  <el-menu-item index="/admin/statistics">数据统计</el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="coach" v-if="userInfo?.role === 'COACH'">
                  <template #title>
                    <el-icon><Trophy /></el-icon>
                    教练功能
                  </template>
                  <el-menu-item index="/coach/students">学生管理</el-menu-item>
                  <el-menu-item index="/coach/courses">我的课程</el-menu-item>
                </el-sub-menu>

                <el-sub-menu index="student" v-if="userInfo?.role === 'STUDENT'">
                  <template #title>
                    <el-icon><UserFilled /></el-icon>
                    学生功能
                  </template>
                  <el-menu-item index="/student/courses">课程预约</el-menu-item>
                  <el-menu-item index="/student/equipment">器材预约</el-menu-item>
                  <el-menu-item index="/student/plans">健身计划</el-menu-item>
                  <el-menu-item index="/profile">个人中心</el-menu-item>
                  <el-menu-item index="/student/ai-assistant">AI助手</el-menu-item>
                </el-sub-menu>

                <el-menu-item index="/role-test">
                  <el-icon><User /></el-icon>
                  返回测试页
                </el-menu-item>
              </el-menu>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserRole } from '@/types'
import { ElMessage } from 'element-plus'
import {
  User,
  UserFilled,
  Trophy,
  HomeFilled,
  Menu,
  View,
  SwitchButton,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const activeMenu = ref('')

const token = computed(() => userStore.token)
const userInfo = computed(() => userStore.userInfo)

const getRoleLabel = (role: string) => {
  const labels: Record<string, string> = {
    ADMIN: '管理员',
    COACH: '教练',
    STUDENT: '学生'
  }
  return labels[role] || role
}

const getRoleTagType = (role: string) => {
  const types: Record<string, any> = {
    ADMIN: 'danger',
    COACH: 'warning',
    STUDENT: 'primary'
  }
  return types[role] || ''
}

const quickLogin = async (role: string) => {
  loading.value = true
  
  try {
    // 模拟不同角色的用户数据
    const mockUsers: Record<string, any> = {
      admin: {
        id: 1,
        username: 'admin',
        password: 'admin123',
        name: '管理员',
        role: UserRole.ADMIN,
        email: 'admin@university.edu.cn',
        phone: '13800138000',
        employeeId: 'T20240001'
      },
      coach: {
        id: 2,
        username: 'coach01',
        password: 'coach123',
        name: '李教练',
        role: UserRole.COACH,
        email: 'coach@university.edu.cn',
        phone: '13800138001',
        employeeId: 'T20240002',
        specialties: ['力量训练', '有氧运动', '瑜伽']
      },
      student: {
        id: 3,
        username: 'student01',
        password: 'student123',
        name: '张同学',
        role: UserRole.STUDENT,
        email: 'student@university.edu.cn',
        phone: '13800138002',
        studentId: 'S20240001',
        major: '计算机科学与技术',
        grade: '2024级'
      }
    }

    const user = mockUsers[role]
    const mockToken = `mock_token_${role}_${Date.now()}`

    // 设置用户信息
    userStore.setToken(mockToken)
    userStore.setUserInfo(user)

    console.log(`快速登录 - ${role}:`, { token: mockToken, user })

    ElMessage.success(`登录成功！当前角色：${getRoleLabel(user.role)}`)

    // 延迟跳转，确保状态更新
    setTimeout(() => {
      loading.value = false
      ElMessage.info('您现在可以查看各个页面了')
    }, 500)
  } catch (error) {
    loading.value = false
    ElMessage.error('登录失败')
  }
}

const logout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
}

const goToDashboard = () => {
  router.push('/dashboard')
}

const viewMenus = () => {
  ElMessage.info('请查看左侧菜单栏')
}

const viewAllPages = () => {
  const role = userInfo.value?.role
  if (!role) return

  const paths: Record<string, string[]> = {
    ADMIN: [
      '/admin/courses',
      '/admin/equipment',
      '/admin/users',
      '/admin/statistics'
    ],
    COACH: [
      '/coach/students',
      '/coach/courses'
    ],
    STUDENT: [
      '/student/courses',
      '/student/equipment',
      '/student/plans',
      '/profile',
      '/student/ai-assistant'
    ]
  }

  const rolePaths = paths[role]
  ElMessage.success(`当前角色可访问 ${rolePaths.length} 个页面，请使用上方导航`)
}
</script>

<style scoped>
.role-test-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.test-header {
  background: white;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.test-header h1 {
  margin: 0;
  font-size: 28px;
  color: #333;
}

.test-header p {
  margin: 10px 0 0;
  color: #666;
}

.el-main {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.role-selector {
  margin-bottom: 20px;
}

.button-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.role-name {
  font-size: 18px;
  font-weight: bold;
}

.role-desc {
  font-size: 12px;
  opacity: 0.8;
}

.status-card,
.permissions-card,
.pages-nav {
  margin-bottom: 20px;
}

.role-permission {
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.role-permission h3 {
  margin: 0 0 15px;
  font-size: 18px;
}

.role-permission ul {
  list-style: none;
  padding: 0;
  margin: 0;
  text-align: left;
}

.role-permission li {
  padding: 8px 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.role-permission li:last-child {
  border-bottom: none;
}

.role-permission.admin {
  background: linear-gradient(135deg, #ffe6e6 0%, #ffcccc 100%);
  border: 2px solid #ff4d4d;
}

.role-permission.coach {
  background: linear-gradient(135deg, #fff7e6 0%, #ffeccc 100%);
  border: 2px solid #ffaa00;
}

.role-permission.student {
  background: linear-gradient(135deg, #e6f7ff 0%, #cceeff 100%);
  border: 2px solid #1890ff;
}

.el-button {
  width: 100%;
  height: 80px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-icon {
  font-size: 24px;
  margin-right: 8px;
}
</style>
