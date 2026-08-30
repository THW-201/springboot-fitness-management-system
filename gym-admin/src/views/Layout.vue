<template>
  <el-container class="layout-container" :style="{
    'padding-left': isMobile ? '64px' : '0'
  }">
    <!-- 移动端遮罩层 -->
    <div v-if="isMobile && !isCollapse" class="mobile-mask" @click="toggleCollapse"></div>

    <el-aside :width="isCollapse ? '64px' : '200px'" class="aside" :class="{ 'mobile-aside': isMobile }">
      <div class="logo">
        <el-icon :size="24">
          <Trophy />
        </el-icon>
        <span v-if="!isCollapse">健身系统</span>
      </div>
      <el-menu :default-active="activeMenu" :collapse="isCollapse" :unique-opened="true" router>
        <el-menu-item :index="isCoach ? '/coach/dashboard' : '/dashboard'">
          <el-icon>
            <Odometer />
          </el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <!-- 管理员菜单 -->
        <template v-if="isAdmin">
          <el-menu-item index="/admin/courses">
            <el-icon>
              <Reading />
            </el-icon>
            <template #title>课程管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/equipment">
            <el-icon>
              <Basketball />
            </el-icon>
            <template #title>器材管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon>
              <User />
            </el-icon>
            <template #title>用户管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/social">
            <el-icon>
              <ChatLineSquare />
            </el-icon>
            <template #title>社交管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/checkins">
            <el-icon>
              <Timer />
            </el-icon>
            <template #title>签到管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/health-plans">
            <el-icon>
              <Document />
            </el-icon>
            <template #title>健身计划管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/reservations">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>预约管理</template>
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <el-icon>
              <Bell />
            </el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          <!-- <el-menu-item index="/admin/statistics">
            <el-icon>
              <TrendCharts />
            </el-icon>
            <template #title>数据统计</template>
          </el-menu-item> -->
        </template>

        <!-- 教练菜单 -->
        <template v-if="isCoach">
          <el-menu-item index="/coach/students">
            <el-icon>
              <UserFilled />
            </el-icon>
            <template #title>学生管理</template>
          </el-menu-item>
          <el-menu-item index="/coach/courses">
            <el-icon>
              <Reading />
            </el-icon>
            <template #title>我的课程</template>
          </el-menu-item>
          <el-menu-item index="/coach/equipment">
            <el-icon>
              <Basketball />
            </el-icon>
            <template #title>器材管理</template>
          </el-menu-item>
          <el-menu-item index="/coach/reservations">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>预约管理</template>
          </el-menu-item>
          <el-menu-item index="/coach/checkins">
            <el-icon>
              <Timer />
            </el-icon>
            <template #title>签到管理</template>
          </el-menu-item>
        </template>

        <!-- 学生菜单 -->
        <template v-if="isStudent">
          <el-menu-item index="/student/courses">
            <el-icon>
              <Calendar />
            </el-icon>
            <template #title>课程预约</template>
          </el-menu-item>
          <el-menu-item index="/student/equipment">
            <el-icon>
              <Basketball />
            </el-icon>
            <template #title>器材预约</template>
          </el-menu-item>
          <el-menu-item index="/student/plans">
            <el-icon>
              <Document />
            </el-icon>
            <template #title>健身计划</template>
          </el-menu-item>
          <!-- <el-menu-item index="/profile">
            <el-icon>
              <User />
            </el-icon>
            <template #title>个人中心</template>
          </el-menu-item> -->
        </template>

        <!-- AI 助手 -->
        <el-menu-item index="/ai-assistant">
          <el-icon>
            <ChatLineSquare />
          </el-icon>
          <template #title>AI健身助手</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <notification-center class="notification-center" />
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="baseUrl + '/' + userInfo?.avatarUrl">
                <el-icon>
                  <UserFilled />
                </el-icon>
              </el-avatar>
              <span class="username">{{ userInfo?.realName || userInfo?.username }}</span>
              <el-tag size="small" :type="getRoleTagType(userInfo?.role)">
                {{ getRoleText(userInfo?.role) }}
              </el-tag>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon>
                    <User />
                  </el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon>
                    <SwitchButton />
                  </el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import {
  Trophy,
  Odometer,
  Reading,
  Basketball,
  User,
  ChatLineSquare,
  Timer,
  UserFilled,
  Calendar,
  Document,
  Fold,
  Expand,
  SwitchButton,
  Bell
} from '@element-plus/icons-vue'
import NotificationCenter from '@/components/NotificationCenter.vue'
const baseUrl = import.meta.env.VITE_BASE_URL

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const isMobile = ref(false)
const userInfo = computed(() => userStore.userInfo)
const isAdmin = computed(() => userStore.isAdmin)
const isCoach = computed(() => userStore.isCoach)
const isStudent = computed(() => userStore.isStudent)
const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

// 防抖定时器
let resizeTimer: number | null = null

// 检查是否为移动端
const checkMobile = () => {
  const width = window.innerWidth
  const wasMobile = isMobile.value
  isMobile.value = width < 768

  // 如果切换到移动端，自动折叠侧边栏
  if (isMobile.value && !wasMobile) {
    isCollapse.value = true
  }
  // 如果切换到PC端，自动展开侧边栏
  if (!isMobile.value && wasMobile) {
    isCollapse.value = false
  }
}

// 防抖处理的checkMobile
const debouncedCheckMobile = () => {
  if (resizeTimer) {
    clearTimeout(resizeTimer)
  }
  resizeTimer = window.setTimeout(() => {
    checkMobile()
  }, 200)
}

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 监听窗口大小变化
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', debouncedCheckMobile)
})

onUnmounted(() => {
  if (resizeTimer) {
    clearTimeout(resizeTimer)
  }
  window.removeEventListener('resize', debouncedCheckMobile)
})

const getRoleText = (role?: string) => {
  const map: Record<string, string> = {
    ADMIN: '管理员',
    COACH: '教练',
    STUDENT: '学生'
  }
  return map[role || ''] || '未知'
}

const getRoleTagType = (role?: string) => {
  const map: Record<string, any> = {
    ADMIN: 'danger',
    COACH: 'warning',
    STUDENT: 'primary'
  }
  return map[role || ''] || ''
}

const handleCommand = (command: string) => {
  let loading: any
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
      .then(async () => {
        loading = ElLoading.service({
          lock: true,
          text: '正在退出登录...',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        try {
          await authApi.logout()
        } catch (error: any) {
          console.log(error.message || '退出登录失败');
          // ElMessage.error(error.message || '退出登录失败')
        }
        ElMessage.success({
          message: '退出登录成功',
          duration: 1500,
          type: 'success'
        })
        userStore.logout()
        router.push('/login')
      })
      .catch(() => { })
      .finally(() => {
        loading.close()
      })
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  box-sizing: border-box;
  height: 100vh;
}

.aside {
  background: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

/* 移动端侧边栏 */
.mobile-aside {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1000;
}

/* 移动端遮罩层 */
.mobile-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo span {
  margin-left: 10px;
}

.el-menu {
  border-right: none;
  background-color: #304156;
}

.el-menu :deep(.el-menu-item) {
  background-color: #304156;
  color: #bfcbd9;
}

.el-menu :deep(.el-menu-item:hover) {
  background-color: #263445;
  color: #ffffff;
}

.el-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: #ffffff;
}

.el-menu :deep(.el-sub-menu__title) {
  background-color: #304156;
  color: #bfcbd9;
}

.el-menu :deep(.el-sub-menu__title:hover) {
  background-color: #263445;
  color: #ffffff;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 20px;
}

@media (max-width: 768px) {
  .header {
    padding: 0 10px;
  }

  .header-left {
    flex: 1;
  }

  .el-breadcrumb {
    display: none;
  }

  .username {
    display: none;
  }
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #666;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.notification-center {
  margin-right: 10px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.main {
  background: #f0f2f5;
  padding: 5px;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}
</style>
