// 简化版 main.ts 用于诊断问题
console.log('=== 开始加载 main.ts ===')

import { createApp } from 'vue'
console.log('Vue 已导入')

import { createPinia } from 'pinia'
console.log('Pinia 已导入')

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
console.log('Element Plus 已导入')

import * as ElementPlusIconsVue from '@element-plus/icons-vue'
console.log('Element Plus 图标已导入')

import App from './App.vue'
console.log('App 组件已导入')

import router from './router'
console.log('Router 已导入')

console.log('=== 开始创建应用 ===')
const app = createApp(App)
console.log('应用已创建')

const pinia = createPinia()
console.log('Pinia 实例已创建')

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
console.log('图标已注册')

app.use(pinia)
console.log('Pinia 已挂载')

app.use(router)
console.log('Router 已挂载')

app.use(ElementPlus)
console.log('Element Plus 已挂载')

console.log('=== 准备挂载到 DOM ===')
app.mount('#app')

console.log('✅ 应用已成功挂载到 #app')
