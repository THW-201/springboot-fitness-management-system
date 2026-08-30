<template>
  <div class="register-container">
    <!-- 左侧背景图区域 -->
    <div class="register-banner">
      <div class="banner-content">
        <h1>大学生健身管理系统</h1>
        <p>开启您的健康生活之旅</p>
        <div class="banner-features">
          <div class="feature-item">
            <el-icon><Trophy /></el-icon>
            <span>免费课程</span>
          </div>
          <div class="feature-item">
            <el-icon><DataLine /></el-icon>
            <span>数据追踪</span>
          </div>
          <div class="feature-item">
            <el-icon><ChatLineSquare /></el-icon>
            <span>AI 助手</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单 -->
    <div class="register-form-wrapper">
      <div class="register-form-container">
        <div class="register-header">
          <h2>创建新账号</h2>
          <p>填写信息完成注册</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="register-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="邮箱"
              prefix-icon="Message"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="手机号（可选）"
              prefix-icon="Phone"
              clearable
              maxlength="11"
            />
          </el-form-item>

          <el-form-item prop="realName">
            <el-input
              v-model="form.realName"
              placeholder="真实姓名（可选）"
              prefix-icon="UserFilled"
              clearable
            />
          </el-form-item>

          <el-form-item prop="role">
            <el-select
              v-model="form.role"
              placeholder="选择角色"
              style="width: 100%"
              @change="handleRoleChange"
            >
              <el-option label="学生" :value="UserRole.STUDENT">
                <div class="role-option">
                  <el-icon><User /></el-icon>
                  <span>学生</span>
                </div>
              </el-option>
              <el-option label="教练" :value="UserRole.COACH">
                <div class="role-option">
                  <el-icon><Trophy /></el-icon>
                  <span>教练</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 学生角色专属字段 -->
          <template v-if="isStudent">
            <el-form-item prop="studentNumber">
              <el-input
                v-model="form.studentNumber"
                placeholder="学号（学生必填）"
                prefix-icon="Postcard"
                clearable
              />
            </el-form-item>

            <el-form-item prop="coachId">
              <el-select
                v-model="form.coachId"
                placeholder="选择负责教练（可选）"
                style="width: 100%"
                clearable
              >
                <el-option label="暂不选择" :value="undefined" />
              </el-select>
            </el-form-item>
          </template>

          <!-- 教练角色专属字段 -->
          <template v-if="isCoach">
            <el-form-item prop="specialization">
              <el-input
                v-model="form.specialization"
                placeholder="专业领域（如：力量训练）"
                prefix-icon="Medal"
                clearable
              />
            </el-form-item>

            <el-form-item prop="certification">
              <el-input
                v-model="form.certification"
                placeholder="资格证书（如：国家一级健身教练）"
                prefix-icon="Tickets"
                clearable
              />
            </el-form-item>

            <el-form-item prop="experienceYears">
              <el-input-number
                v-model="form.experienceYears"
                placeholder="从业年限"
                :min="0"
                :max="50"
                style="width: 100%"
              />
            </el-form-item>
          </template>

          <el-form-item>
            <el-checkbox v-model="form.agreeTerms">
              我已阅读并同意
              <el-link type="primary" :underline="false">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="false">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="register-button"
              :loading="loading"
              @click="handleRegister"
            >
              立即注册
            </el-button>
          </el-form-item>

          <div class="register-divider">
            <span>或</span>
          </div>

          <el-form-item>
            <el-button class="login-link-button" @click="goToLogin">
              已有账号？立即登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'
import { UserRole } from '@/types'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: '',
  realName: '',
  role: UserRole.STUDENT,
  // 学生角色字段
  studentNumber: '',
  coachId: undefined as number | undefined,
  // 教练角色字段
  specialization: '',
  certification: '',
  experienceYears: undefined as number | undefined,
  agreeTerms: false
})

const validatePassword = (rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreeTerms = (rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议和隐私政策'))
  } else {
    callback()
  }
}

// 计算属性：判断当前角色
const isStudent = computed(() => form.role === UserRole.STUDENT)
const isCoach = computed(() => form.role === UserRole.COACH)

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度为 3-50 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度为 6-100 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  studentNumber: [
    { required: true, message: '学生必须填写学号', trigger: 'blur' }
  ],
  agreeTerms: [
    { required: true, validator: validateAgreeTerms, trigger: 'change' }
  ]
}

const handleRoleChange = () => {
  // 切换角色时清空角色专属字段
  form.studentNumber = ''
  form.coachId = undefined
  form.specialization = ''
  form.certification = ''
  form.experienceYears = undefined
}

const handleRegister = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 构建注册请求数据
      const registerData: any = {
        username: form.username,
        password: form.password,
        email: form.email,
        role: form.role
      }

      // 添加可选字段
      if (form.phone) registerData.phone = form.phone
      if (form.realName) registerData.realName = form.realName

      // 学生角色专属字段
      if (isStudent.value) {
        registerData.studentNumber = form.studentNumber
        if (form.coachId) registerData.coachId = form.coachId
      }

      // 教练角色专属字段
      if (isCoach.value) {
        if (form.specialization) registerData.specialization = form.specialization
        if (form.certification) registerData.certification = form.certification
        if (form.experienceYears) registerData.experienceYears = form.experienceYears
      }

      // 调用真实注册API
      await authApi.register(registerData)

      ElMessage.success('注册成功，请登录')
      
      // 跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 500)
    } catch (error: any) {
      console.error('注册失败:', error)
      // 错误已在request拦截器中处理
    } finally {
      loading.value = false
    }
  })
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  background: #f5f7fa;
}

/* 左侧背景图区域 */
.register-banner {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  background: url('/imgs/login-bg.jpg') center/cover no-repeat;
  overflow: hidden;
}

.register-banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.15) 0%, transparent 40%);
}

.banner-content {
  position: relative;
  z-index: 1;
  color: white;
  text-align: center;
  max-width: 500px;
}

.banner-content h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  letter-spacing: 2px;
}

.banner-content > p {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 60px;
  font-weight: 300;
}

.banner-features {
  display: flex;
  justify-content: center;
  gap: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-item .el-icon {
  font-size: 40px;
  opacity: 0.9;
}

.feature-item span {
  font-size: 16px;
  opacity: 0.8;
}

/* 右侧注册表单 */
.register-form-wrapper {
  flex: 0 0 520px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: white;
  overflow-y: auto;
  box-sizing: border-box;
}

.register-form-container {
  width: 100%;
  max-width: 440px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 12px;
  font-weight: 600;
}

.register-header p {
  font-size: 14px;
  color: #909399;
}

.register-form {
  margin-top: 20px;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.register-button,
.login-link-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 1px;
}

.register-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.register-divider {
  text-align: center;
  margin: 20px 0;
  position: relative;
}

.register-divider::before,
.register-divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 40%;
  height: 1px;
  background: #dcdfe6;
}

.register-divider::before {
  left: 0;
}

.register-divider::after {
  right: 0;
}

.register-divider span {
  color: #909399;
  font-size: 14px;
  background: #fff;
  padding: 0 10px;
}

.login-link-button {
  background: white;
  border: 1px solid #dcdfe6;
  color: #606266;
  transition: all 0.3s;
}

.login-link-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: #f5f7ff;
}

/* 响应式 */
@media (max-width: 1024px) {
  .register-banner {
    flex: 1;
    padding: 40px;
  }

  .banner-content h1 {
    font-size: 36px;
  }

  .register-form-wrapper {
    flex: 0 0 440px;
  }
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }

  .register-banner {
    flex: 0 0 auto;
    padding: 40px 20px;
    min-height: 200px;
  }

  .banner-content h1 {
    font-size: 28px;
  }

  .register-form-wrapper {
    flex: 1;
    padding: 30px 20px;
  }
}
</style>
