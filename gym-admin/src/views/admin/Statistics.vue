<template>
  <div class="statistics-page">
    <!-- 统计卡片 -->
    <el-card shadow="hover" class="mb-20">
      <template #header>
        <span>数据概览</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="总用户数" :value="156" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="总预约数" :value="892" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="总课程数" :value="32" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="总器材数" :value="48" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <!-- 课程类型分布 - 饼图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>课程类型分布</span>
          </template>
          <div ref="courseTypeChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 月度课程趋势 - 折线图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>月度课程趋势</span>
          </template>
          <div ref="courseTrendChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 器材状态分布 - 环形图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>器材状态分布</span>
          </template>
          <div ref="equipmentStatusChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 最常用器材 - 雷达图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>最常用器材</span>
          </template>
          <div ref="topEquipmentChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 学生活动趋势 - 折线图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>学生周活动趋势</span>
          </template>
          <div ref="studentActivityChart" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 教练学生分布 - 饼图 -->
      <el-col :span="12">
        <el-card shadow="hover" class="mb-20">
          <template #header>
            <span>教练学生分布</span>
          </template>
          <div ref="coachDistributionChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { ECharts } from 'echarts'

// 图表实例
const courseTypeChart = ref<HTMLElement>()
const courseTrendChart = ref<HTMLElement>()
const equipmentStatusChart = ref<HTMLElement>()
const topEquipmentChart = ref<HTMLElement>()
const studentActivityChart = ref<HTMLElement>()
const coachDistributionChart = ref<HTMLElement>()

let courseTypeChartInstance: ECharts | null = null
let courseTrendChartInstance: ECharts | null = null
let equipmentStatusChartInstance: ECharts | null = null
let topEquipmentChartInstance: ECharts | null = null
let studentActivityChartInstance: ECharts | null = null
let coachDistributionChartInstance: ECharts | null = null

// 初始化图表
const initCharts = () => {
  // 课程类型分布 - 饼图
  if (courseTypeChart.value) {
    courseTypeChartInstance = echarts.init(courseTypeChart.value)
    courseTypeChartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['瑜伽', '健身', '游泳', '拳击', '舞蹈']
      },
      series: [
        {
          name: '课程类型',
          type: 'pie',
          radius: '60%',
          center: ['50%', '50%'],
          data: [
            { value: 12, name: '瑜伽' },
            { value: 8, name: '健身' },
            { value: 6, name: '游泳' },
            { value: 4, name: '拳击' },
            { value: 2, name: '舞蹈' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    })
  }

  // 月度课程趋势 - 折线图
  if (courseTrendChart.value) {
    courseTrendChartInstance = echarts.init(courseTrendChart.value)
    courseTrendChartInstance.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['课程数量']
      },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '课程数量',
          type: 'line',
          data: [5, 8, 12, 15, 20, 32],
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#409EFF'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(64, 158, 255, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(64, 158, 255, 0.1)'
              }
            ])
          }
        }
      ]
    })
  }

  // 器材状态分布 - 环形图
  if (equipmentStatusChart.value) {
    equipmentStatusChartInstance = echarts.init(equipmentStatusChart.value)
    equipmentStatusChartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['可用', '使用中', '维护中', '损坏']
      },
      series: [
        {
          name: '器材状态',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 30, name: '可用' },
            { value: 12, name: '使用中' },
            { value: 4, name: '维护中' },
            { value: 2, name: '损坏' }
          ]
        }
      ]
    })
  }

  // 最常用器材 - 雷达图
  if (topEquipmentChart.value) {
    topEquipmentChartInstance = echarts.init(topEquipmentChart.value)
    topEquipmentChartInstance.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        bottom: '0%',
        data: ['使用次数']
      },
      radar: {
        indicator: [
          { name: '跑步机', max: 150 },
          { name: '哑铃', max: 150 },
          { name: '动感单车', max: 150 },
          { name: '椭圆机', max: 150 },
          { name: '划船机', max: 150 }
        ],
        radius: '65%'
      },
      series: [
        {
          name: '使用次数',
          type: 'radar',
          data: [
            {
              value: [120, 98, 85, 72, 65],
              name: '使用次数',
              areaStyle: {
                color: 'rgba(64, 158, 255, 0.3)'
              },
              lineStyle: {
                color: '#409EFF',
                width: 2
              },
              itemStyle: {
                color: '#409EFF'
              }
            }
          ]
        }
      ]
    })
  }

  // 学生活动趋势 - 折线图
  if (studentActivityChart.value) {
    studentActivityChartInstance = echarts.init(studentActivityChart.value)
    studentActivityChartInstance.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['活动次数']
      },
      xAxis: {
        type: 'category',
        data: ['第1周', '第2周', '第3周', '第4周']
      },
      yAxis: {
        type: 'value',
        name: '活动次数'
      },
      series: [
        {
          name: '活动次数',
          type: 'line',
          data: [120, 132, 101, 134],
          smooth: true,
          lineStyle: {
            width: 3,
            color: '#67C23A'
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: 'rgba(103, 194, 58, 0.5)'
              },
              {
                offset: 1,
                color: 'rgba(103, 194, 58, 0.1)'
              }
            ])
          }
        }
      ]
    })
  }

  // 教练学生分布 - 饼图
  if (coachDistributionChart.value) {
    coachDistributionChartInstance = echarts.init(coachDistributionChart.value)
    coachDistributionChartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: ['张教练', '李教练', '王教练', '赵教练']
      },
      series: [
        {
          name: '学生分布',
          type: 'pie',
          radius: '60%',
          center: ['50%', '50%'],
          data: [
            { value: 45, name: '张教练' },
            { value: 35, name: '李教练' },
            { value: 40, name: '王教练' },
            { value: 36, name: '赵教练' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    })
  }
}

// 监听窗口大小变化
const handleResize = () => {
  courseTypeChartInstance?.resize()
  courseTrendChartInstance?.resize()
  equipmentStatusChartInstance?.resize()
  topEquipmentChartInstance?.resize()
  studentActivityChartInstance?.resize()
  coachDistributionChartInstance?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  courseTypeChartInstance?.dispose()
  courseTrendChartInstance?.dispose()
  equipmentStatusChartInstance?.dispose()
  topEquipmentChartInstance?.dispose()
  studentActivityChartInstance?.dispose()
  coachDistributionChartInstance?.dispose()
})
</script>

<style scoped>
.statistics-page {
  padding: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>
