<template>
  <div class="student-home">
    <!-- 公告轮播图 -->
    <section class="banner-section">
      <div class="container">
        <el-carousel :interval="5000" height="300px" type="card">
          <el-carousel-item v-for="(item, index) in banners" :key="index">
            <img :src="item.image" :alt="item.title" class="banner-image" />
          </el-carousel-item>
        </el-carousel>
      </div>
    </section>

    <!-- 功能卡片 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">健身服务</h2>
        <div class="features-grid">
          <router-link to="/student/courses" class="feature-card">
            <el-icon class="feature-icon">
              <Calendar />
            </el-icon>
            <h3>课程预约</h3>
            <p>浏览和预约各类健身课程</p>
          </router-link>
          <router-link to="/student/equipment" class="feature-card">
            <el-icon class="feature-icon">
              <Basketball />
            </el-icon>
            <h3>器材预约</h3>
            <p>预约健身器材使用时间</p>
          </router-link>

          <router-link to="/student/social" class="feature-card">
            <el-icon class="feature-icon">
              <ChatLineRound />
            </el-icon>
            <h3>社交广场</h3>
            <p>分享健身心得，互动交流</p>
          </router-link>
          <router-link to="/student/plans" class="feature-card">
            <el-icon class="feature-icon">
              <Document />
            </el-icon>
            <h3>健身计划</h3>
            <p>制定和跟踪个人健身计划</p>
          </router-link>
        </div>
      </div>
    </section>

    <!-- 健身数据 -->
    <section class="stats-section">
      <div class="container">
        <h2 class="section-title">个人健身数据</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <el-icon class="stat-icon">
              <Timer />
            </el-icon>
            <div class="stat-info">
              <h3>{{ totalMinutes }}</h3>
              <p>总运动分钟</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon">
              <TrendCharts />
            </el-icon>
            <div class="stat-info">
              <h3>{{ totalCalories }}</h3>
              <p>总消耗卡路里</p>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-icon">
              <Check />
            </el-icon>
            <div class="stat-info">
              <h3>{{ totalCheckIns }}</h3>
              <p>总打卡次数</p>
            </div>
          </div>
          <!-- <div class="stat-card">
            <el-icon class="stat-icon"><Star /></el-icon>
            <div class="stat-info">
              <h3>{{ activityScore }}</h3>
              <p>活跃度评分</p>
            </div>
          </div> -->
        </div>
      </div>
    </section>

    <!-- 课程推荐 -->
    <section class="courses-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">热门课程</h2>
          <router-link to="/student/courses" class="view-more">查看全部</router-link>
        </div>
        <div class="courses-grid">
          <div v-for="course in popularCourses" :key="course.id" class="course-card">
            <div class="course-image-container" v-if="course.imageUrl">
              <img :src="course.imageUrl.startsWith('http') ? course.imageUrl : baseUrl + '/' + course.imageUrl" alt="" class="course-image">
            </div>
            <div class="course-image-container" v-else>
              <div class="no-image">
                <el-icon class="no-image-icon"><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
            </div>
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p class="course-type">{{ course.courseType }}</p>
              <p class="course-coach">教练：{{ course.coachName }}</p>
              <p class="course-time"><el-icon>
                  <Clock />
                </el-icon> {{ formatTime(course.startTime) }} - {{ formatTime(course.endTime) }}</p>
              <p class="course-location"><el-icon>
                  <Location />
                </el-icon> {{ course.location }}</p>
              <div class="course-status">
                <el-progress :percentage="getProgress(course)" :stroke-width="8" />
                <span>{{ (course.totalReservations - course.cancelledReservations) || 0 }}/{{ course.capacity }}人</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 器材状态 -->
    <section class="equipment-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">器材状态</h2>
          <router-link to="/student/equipment" class="view-more">查看全部</router-link>
        </div>
        <div class="equipment-grid">
          <div v-for="equipment in equipmentStatus" :key="equipment.id" class="equipment-card">
            <div class="equipment-image-container" v-if="equipment.imageUrl">
              <img :src="equipment.imageUrl.startsWith('http') ? equipment.imageUrl : baseUrl + '/' + equipment.imageUrl" alt="" class="equipment-image">
            </div>
            <div class="equipment-image-container" v-else>
              <div class="no-image">
                <el-icon class="no-image-icon"><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
            </div>
            <div class="equipment-info">
              <h3>{{ equipment.name }}</h3>
              <p class="equipment-type">{{ equipment.equipmentType }}</p>
              <p class="equipment-location"><el-icon>
                  <Location />
                </el-icon> {{ equipment.location }}</p>
              <el-tag :type="getStatusType(equipment.status)">{{ getStatusText(equipment.status) }}</el-tag>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Picture } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { EquipmentStatus } from '@/types'
import { statisticsApi } from '@/api/statistics'
import { courseApi } from '@/api/course'
import { equipmentApi } from '@/api/equipment'

const baseUrl = import.meta.env.VITE_BASE_URL

// 模拟数据
const banners = ref([
  { title: 'Banner 1', image: '/imgs/banner-1.jpg' },
  { title: 'Banner 2', image: '/imgs/banner-2.jpg' },
  { title: 'Banner 3', image: '/imgs/banner-3.jpg' }
])

const popularCourses = ref<any[]>([])
const equipmentStatus = ref<any[]>([])

const totalMinutes = ref(0)
const totalCalories = ref(0)
const totalCheckIns = ref(0)

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('HH:mm')
}

// 加载学生个人统计数据
const loadPersonalStatistics = async () => {
  try {
    const res = await statisticsApi.getPersonalHealthData()
    console.log("个人健身数据", res)
    totalMinutes.value = res.data.totalExerciseMinutes || 0
    totalCalories.value = res.data.totalCaloriesBurned || 0
    totalCheckIns.value = res.data.totalExerciseCount || 0
  } catch (error: any) {
    console.error('加载个人统计数据失败', error.message)
  }
}

// 加载热门课程（前3条）
const loadPopularCourses = async () => {
  try {
    const res = await courseApi.getCourses({
      current: 1,
      size: 3
    } as any)
    if (res.data) {
      popularCourses.value = res.data.records || []
    }
  } catch (error: any) {
    console.log('加载热门课程失败', error.message)
  }
}

// 加载器材状态（前4条）
const loadEquipmentStatus = async () => {
  try {
    const res = await equipmentApi.getEquipmentList({
      current: 1,
      size: 4
    } as any)
    if (res.data) {
      equipmentStatus.value = res.data.records || []
    }
  } catch (error: any) {
    console.log('加载器材状态失败', error.message)
  }
}
// 获取器材状态类型
const getStatusType = (status: EquipmentStatus) => {
  const typeMap = {
    [EquipmentStatus.AVAILABLE]: 'success',
    [EquipmentStatus.IN_USE]: 'warning',
    [EquipmentStatus.MAINTENANCE]: 'info',
    [EquipmentStatus.DAMAGED]: 'danger'
  }
  return typeMap[status] || ''
}
const getProgress = (course: any) => {
  const actualStudents = (course.totalReservations - course.cancelledReservations) || 0
  return Number(((actualStudents / course.capacity) * 100).toFixed(0))
}
// 获取器材状态文本
const getStatusText = (status: EquipmentStatus) => {
  const textMap = {
    [EquipmentStatus.AVAILABLE]: '可用',
    [EquipmentStatus.IN_USE]: '使用中',
    [EquipmentStatus.MAINTENANCE]: '维护中',
    [EquipmentStatus.DAMAGED]: '损坏'
  }
  return textMap[status] || '未知'
}

onMounted(() => {
  console.log('学生首页加载完成')
  loadPersonalStatistics()
  loadPopularCourses()
  loadEquipmentStatus()
})
</script>

<style scoped>
.student-home {
  background-color: #f5f7fa;
}

/* 公告轮播图 */
.banner-section {
  margin-bottom: 40px;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

:deep(.el-carousel__item) {
  border-radius: 8px;
  overflow: hidden;
}

/* 功能卡片 */
.features-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.feature-card {
  background-color: white;
  border-radius: 8px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  text-decoration: none;
  color: #333;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.feature-card p {
  font-size: 14px;
  color: #666;
}

/* 课程推荐 */
.courses-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.view-more {
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.course-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.course-image-container {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image {
  transform: scale(1.05);
}

.no-image {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  color: #909399;
  border: 2px dashed #dcdfe6;
}

.no-image-icon {
  font-size: 48px;
  margin-bottom: 10px;
  opacity: 0.5;
}

.course-info {
  padding: 20px;
}

.course-info h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.course-type {
  font-size: 12px;
  color: #409eff;
  margin-bottom: 8px;
}

.course-coach {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.course-time,
.course-location {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-status {
  margin-top: 16px;
}

.course-status span {
  font-size: 12px;
  color: #666;
  display: block;
  margin-top: 8px;
  text-align: right;
}

/* 器材状态 */
.equipment-section {
  margin-bottom: 40px;
}

.equipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.equipment-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.equipment-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.equipment-image-container {
  position: relative;
  height: 140px;
  overflow: hidden;
}

.equipment-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.equipment-card:hover .equipment-image {
  transform: scale(1.05);
}

.equipment-info {
  padding: 20px;
}

.equipment-info h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.equipment-type {
  font-size: 12px;
  color: #409eff;
  margin-bottom: 8px;
}

.equipment-location {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 健身数据 */
.stats-section {
  margin-bottom: 40px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
  color: #409eff;
}

.stat-info h3 {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.stat-info p {
  font-size: 14px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-section {
    margin-bottom: 20px;
  }

  :deep(.el-carousel) {
    height: 200px !important;
  }

  .features-grid,
  .courses-grid,
  .equipment-grid,
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>