<template>
  <div class="login-container">
    <!-- 左侧背景图区域 -->
    <div class="login-banner">
      <div class="banner-content">
        <h1>基于Spring Boot的大学生健身管理系统的设计与实现</h1>
        <p>科学健身 · 专业指导 · 健康生活</p>
        <div class="banner-features">
          <div class="feature-item">
            <el-icon><Trophy /></el-icon>
            <span>专业课程</span>
          </div>
          <div class="feature-item">
            <el-icon><Tools /></el-icon>
            <span>优质器材</span>
          </div>
          <div class="feature-item">
            <el-icon><User /></el-icon>
            <span>个性化指导</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="login-form-wrapper">
      <div class="login-form-container">
        <div class="login-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号信息</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="form.rememberMe">记住密码</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-button"
              :loading="loading"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>


          <div class="login-divider">
            <span>或</span>
          </div>

          <el-form-item>
            <el-button class="register-button" @click="goToRegister">
              注册新账号
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>© {{ year }} 大学生健身管理系统 · All Rights Reserved</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'
import { encrypt, decrypt } from '@/utils/crypto'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const year = dayjs().year()
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 调用真实登录API
      const response = await authApi.login({
        username: form.username,
        password: form.password
      })

      // 使用 handleLoginResponse 处理登录响应
      userStore.handleLoginResponse(response.data)

      // 获取当前用户信息
      const userInfoResponse = await authApi.getCurrentUser()
      userStore.setUserInfo(userInfoResponse.data)

      // 记住密码功能
      if (form.rememberMe) {
        localStorage.setItem('rememberedUsername', form.username)
        localStorage.setItem('rememberedPassword', encrypt(form.password))
      } else {
        localStorage.removeItem('rememberedUsername')
        localStorage.removeItem('rememberedPassword')
      }

      ElMessage.success({
        message: '登录成功',
        type: 'success',
        duration:2000
      })

      // 根据用户角色跳转到不同页面
      setTimeout(() => {
        if (userInfoResponse.data.role === 'STUDENT') {
          router.push('/student/home')
        } else {
          router.push('/dashboard')
        }
      }, 300)
    } catch (error: any) {
      console.error('登录失败:', error)
      // 错误已在request拦截器中处理,这里不需要额外提示
    } finally {
      loading.value = false
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}


// 初始化时加载记住的用户名和密码
const rememberedUsername = localStorage.getItem('rememberedUsername')
const rememberedPassword = localStorage.getItem('rememberedPassword')
if (rememberedUsername && rememberedPassword) {
  form.username = rememberedUsername
  form.password = decrypt(rememberedPassword)
  form.rememberMe = true
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  background: #f5f7fa;
}

/* 左侧背景图区域 */
.login-banner {
  flex: 0 0 50%;
  background: url('/imgs/login-bg.jpg') center/cover no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  overflow: hidden;
  border-right: 2px solid rgba(255, 255, 255, 0.1);
}

.login-banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.banner-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  max-width: 500px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 10px;
}

.banner-content h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 2px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-content > p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
  font-weight: 300;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

.banner-features {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  min-width: 100px;
}

.feature-item .el-icon {
  font-size: 40px;
  opacity: 0.9;
  color: white;
}

.feature-item span {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 500;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
}

/* 右侧登录表单 */
.login-form-wrapper {
  flex: 0 0 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: white;
  box-shadow: -5px 0 15px rgba(0, 0, 0, 0.05);
}

.login-form-container {
  width: 100%;
  max-width: 450px;
  padding: 30px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.login-form-container:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  border-color: #c0c4cc;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.login-header p {
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-top: 30px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;
  border-top: 1px solid #f0f2f5;
}

/* 复选框样式 */
.login-form .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #667eea;
  border-color: #667eea;
}

.login-form .el-checkbox__inner {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  transition: all 0.3s;
}

.login-form .el-checkbox__inner:hover {
  border-color: #667eea;
}

/* 链接样式 */
.login-form .el-link {
  color: #667eea;
  font-weight: 500;
  transition: all 0.3s;
}

.login-form .el-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

.login-button,
.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
  margin-bottom: 15px;
}

/* 表单输入框样式 */
.login-form .el-input {
  height: 48px;
  font-size: 16px;
  margin-bottom: 15px;
}

/* 输入框边框样式 */
.login-form .el-input__wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s;
}

.login-form .el-input__wrapper:hover {
  border-color: #c0c4cc;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.login-form .el-input__wrapper.is-focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 表单标签样式 */
.login-form .el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.login-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 1px solid #667eea;
  transition: all 0.3s;
  border-radius: 8px;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  border-color: #764ba2;
}

.login-divider {
  text-align: center;
  margin: 20px 0;
  position: relative;
}

.login-divider::before,
.login-divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: #dcdfe6;
}

.login-divider::before {
  left: 0;
}

.login-divider::after {
  right: 0;
}

.login-divider span {
  color: #909399;
  font-size: 14px;
  background: #fff;
  padding: 0 10px;
}

.register-button {
  background: white;
  border: 1px solid #dcdfe6;
  color: #606266;
  transition: all 0.3s;
  border-radius: 8px;
}

.register-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f5f7ff;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
}

.login-footer {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.login-footer p {
  font-size: 12px;
  color: #c0c4cc;
  margin: 0;
}

/* 响应式 */
@media (max-width: 1024px) {
  .login-banner {
    flex: 1;
    padding: 40px;
  }

  .banner-content h1 {
    font-size: 36px;
  }

  .banner-content > p {
    font-size: 16px;
  }

  .login-form-wrapper {
    flex: 0 0 400px;
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-banner {
    flex: 0 0 auto;
    padding: 40px 20px;
    min-height: 200px;
  }

  .banner-content h1 {
    font-size: 28px;
  }

  .banner-features {
    gap: 20px;
  }

  .login-form-wrapper {
    flex: 1;
    padding: 30px 20px;
  }
}

/* 打印样式 */
@media print {
  .login-container {
    height: 100vh;
    page-break-inside: avoid;
  }

  .login-banner,
  .login-form-wrapper {
    flex: 0 0 50%;
  }

  .login-banner {
    background: #333 !important;
    color: white !important;
  }

  .login-form-wrapper {
    background: white !important;
    color: black !important;
  }

  .login-button {
    background: #667eea !important;
    color: white !important;
  }

  .register-button {
    background: white !important;
    color: #667eea !important;
    border: 1px solid #667eea !important;
  }

  /* 确保所有元素都可见 */
  * {
    visibility: visible !important;
  }
}
</style>
