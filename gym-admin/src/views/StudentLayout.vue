<template>
  <div class="student-layout">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="container">
        <div class="navbar-left">
          <div class="logo">大学生健身管理系统</div>
        </div>
        <div class="navbar-center">
          <nav class="nav-menu">
            <router-link to="/student/home" class="nav-item">首页</router-link>
            <router-link to="/student/courses" class="nav-item">课程预约</router-link>
            <router-link to="/student/equipment" class="nav-item">器材预约</router-link>
            <router-link to="/student/reservations" class="nav-item">我的预约</router-link>
            <router-link to="/student/social" class="nav-item">社交广场</router-link>
            <router-link to="/student/plans" class="nav-item">健身计划</router-link>
            <router-link to="/student/ai-assistant" class="nav-item">AI健身助手</router-link>
          </nav>
        </div>
        <div class="navbar-right">
          <notification-center class="notification-center" />
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="baseUrl + userInfo?.avatarUrl">
                <el-icon>
                  <UserFilled />
                </el-icon>
              </el-avatar>
              <span class="username">{{ userInfo?.realName || userInfo?.username }}</span>
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
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部信息 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>联系我们</h3>
            <p><el-icon><Phone /></el-icon> 联系电话：1234567890</p>
            <p><el-icon><Location /></el-icon> 地址：大学校区体育馆</p>
            <p><el-icon><Monitor /></el-icon> 网站：https://gym.example.com</p>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <router-link to="/student/home">首页</router-link>
            <router-link to="/student/courses">课程预约</router-link>
            <router-link to="/student/equipment">器材预约</router-link>
            <router-link to="/student/reservations">我的预约</router-link>
            <router-link to="/student/profile">个人中心</router-link>
          </div>
          <div class="footer-section">
            <h3>关于我们</h3>
            <p>大学生健身管理系统致力于为学生提供优质的健身服务，包括课程预约、器材预约、健身计划制定等功能。</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 大学生健身管理系统 版权所有</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import { User, SwitchButton, UserFilled, Phone, Location, Monitor } from '@element-plus/icons-vue'
import NotificationCenter from '@/components/NotificationCenter.vue'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const baseUrl = import.meta.env.VITE_APP_BASE_API
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
        }
        ElMessage.success({
          message:'退出登录成功',
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
    router.push('/student/profile')
  }
}
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

/* 顶部导航栏 */
.navbar {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.navbar .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.nav-menu {
  display: flex;
  gap: 30px;
}

.nav-item {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 0;
  position: relative;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item.router-link-active {
  color: #409eff;
}

.nav-item.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #409eff;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 15px;
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

/* 主内容区 */
.main-content {
  flex: 1;
  padding: 10px;
  box-sizing: border-box;
}

/* 底部信息 */
.footer {
  background-color: #1F2937;
  color: #fff;
  padding: 40px 0 20px;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  margin-bottom: 30px;
}

.footer-section h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #fff;
}

.footer-section p {
  font-size: 14px;
  line-height: 1.6;
  color: #ccc;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.footer-section a { 
  color: #ccc;
  margin: 0 5px;
}

.footer-section router-link {
  display: block;
  color: #ccc;
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 10px;
  transition: color 0.3s;
}

.footer-section router-link:hover {
  color: #409eff;
}

.footer-bottom {
  border-top: 1px solid #444;
  padding-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #ccc;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar .container {
    flex-wrap: wrap;
    height: auto;
    padding: 10px 20px;
  }

  .navbar-center {
    width: 100%;
    order: 3;
    margin-top: 10px;
  }

  .nav-menu {
    justify-content: center;
    gap: 20px;
  }

  .footer-content {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>