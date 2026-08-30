import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserRole } from '@/types'

const routes: RouteRecordRaw[] = [
  // 首页重定向到登录页
  {
    path: '/',
    redirect: '/login'
  },
  // 公共路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      requiresAuth: false,
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: {
      requiresAuth: false,
      title: '注册'
    }
  },
  {
    path: '/role-test',
    name: 'RoleTest',
    component: () => import('@/views/RoleTest.vue'),
    meta: {
      requiresAuth: false,
      title: '角色测试'
    }
  },
  {
    path: '/showcase',
    name: 'Showcase',
    component: () => import('@/views/Showcase.vue'),
    meta: {
      requiresAuth: false,
      title: '功能展示'
    }
  },
  // 主应用布局（管理员和教练）
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      // 仪表盘 - 所有角色可访问
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: {
          title: '首页'
        }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: {
          title: '个人中心'
        }
      },
      {
        path: 'ai-assistant',
        name: 'AIAssistant',
        component: () => import('@/views/AIAssistant.vue'),
        meta: {
          title: 'AI健身助手'
        }
      },
      // 管理员路由
      {
        path: 'admin',
        children: [
          {
            path: 'courses',
            name: 'AdminCourses',
            component: () => import('@/views/admin/Courses.vue'),
            meta: {
              title: '课程管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'equipment',
            name: 'AdminEquipment',
            component: () => import('@/views/admin/Equipment.vue'),
            meta: {
              title: '器材管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'users',
            name: 'AdminUsers',
            component: () => import('@/views/admin/Users.vue'),
            meta: {
              title: '用户管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'statistics',
            name: 'AdminStatistics',
            component: () => import('@/views/admin/Statistics.vue'),
            meta: {
              title: '数据统计',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'social',
            name: 'AdminSocial',
            component: () => import('@/views/admin/Social.vue'),
            meta: {
              title: '社交管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'checkins',
            name: 'AdminCheckIns',
            component: () => import('@/views/admin/CheckIns.vue'),
            meta: {
              title: '签到管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'health-plans',
            name: 'AdminHealthPlans',
            component: () => import('@/views/admin/HealthPlans.vue'),
            meta: {
              title: '健身计划管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'reservations',
            name: 'AdminReservations',
            component: () => import('@/views/admin/Reservations.vue'),
            meta: {
              title: '预约管理',
              roles: [UserRole.ADMIN]
            }
          },
          {
            path: 'announcements',
            name: 'AdminAnnouncements',
            component: () => import('@/views/admin/Announcements.vue'),
            meta: {
              title: '公告管理',
              roles: [UserRole.ADMIN]
            }
          }
        ]
      },

      // 教练路由
      {
        path: 'coach',
        children: [
          {
            path: 'dashboard',
            name: 'CoachDashboard',
            component: () => import('@/views/coach/CoachDashboard.vue'),
            meta: {
              title: '教练首页',
              roles: [UserRole.COACH]
            }
          },
          {
            path: 'students',
            name: 'CoachStudents',
            component: () => import('@/views/coach/Students.vue'),
            meta: {
              title: '学生管理',
              roles: [UserRole.COACH]
            }
          },
          {
            path: 'courses',
            name: 'CoachCourses',
            component: () => import('@/views/coach/Courses.vue'),
            meta: {
              title: '我的课程',
              roles: [UserRole.COACH]
            }
          },
          {
            path: 'equipment',
            name: 'CoachEquipment',
            component: () => import('@/views/coach/Equipment.vue'),
            meta: {
              title: '器材管理',
              roles: [UserRole.COACH, UserRole.ADMIN]
            }
          },
          {
            path: 'reservations',
            name: 'CoachReservations',
            component: () => import('@/views/coach/Reservations.vue'),
            meta: {
              title: '预约管理',
              roles: [UserRole.COACH]
            }
          },
          {
            path: 'checkins',
            name: 'CoachCheckIns',
            component: () => import('@/views/coach/CheckIns.vue'),
            meta: {
              title: '签到管理',
              roles: [UserRole.COACH]
            }
          }
        ]
      }
    ]
  },

  // 学生端布局
  {
    path: '/student',
    component: () => import('@/views/StudentLayout.vue'),
    redirect: '/student/home',
    meta: { requiresAuth: true, roles: [UserRole.STUDENT] },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/Home.vue'),
        meta: {
          title: '首页'
        }
      },
      {
        path: 'courses',
        name: 'StudentCourses',
        component: () => import('@/views/student/Courses.vue'),
        meta: {
          title: '课程预约'
        }
      },
      {
        path: 'equipment',
        name: 'StudentEquipment',
        component: () => import('@/views/student/Equipment.vue'),
        meta: {
          title: '器材预约'
        }
      },
      {
        path: 'plans',
        name: 'StudentPlans',
        component: () => import('@/views/student/Plans.vue'),
        meta: {
          title: '健身计划'
        }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/student/Profile.vue'),
        meta: {
          title: '个人中心'
        }
      },
      {
        path: 'ai-assistant',
        name: 'StudentAIAssistant',
        component: () => import('@/views/AIAssistant.vue'),
        meta: {
          title: 'AI健身助手'
        }
      },
      {
        path: 'social',
        name: 'StudentSocial',
        component: () => import('@/views/student/SocialSquare.vue'),
        meta: {
          title: '社交广场'
        }
      },
      {
        path: 'reservations',
        name: 'StudentReservations',
        component: () => import('@/views/student/Reservations.vue'),
        meta: {
          title: '我的预约'
        }
      }
    ]
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: {
      title: '404'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫 - 简化版本
 */
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const userRole = userStore.userInfo?.role

  console.log('路由守卫:', { to: to.path, from: from.path, hasToken: !!token, userRole })

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 大学生健身管理系统`
  }

  // 1. 未登录访问需要认证的页面，跳转到登录页
  if (to.meta.requiresAuth !== false && !token) {
    console.log('未登录，跳转到登录页')
    next('/login')
    return
  }

  // 2. 已登录访问登录页，跳转到仪表盘
  if (token && (to.path === '/login' || to.path === '/register')) {
    console.log('已登录访问登录页，跳转到仪表盘')
    if (userRole === 'STUDENT') {
      next('/student/home')
    } else if (userRole === 'COACH') {
      next('/coach/dashboard')
    } else {
      next('/dashboard')
    }
    return
  }

  // 3. 如果有 token，每次路由变化时获取最新的用户信息
  if (token && to.meta.requiresAuth !== false) {
    console.log('获取当前用户信息...')
    await userStore.fetchCurrentUser()
    // 重新获取用户角色，因为用户信息可能已更新
    const updatedUserRole = userStore.userInfo?.role
    
    // 4. 检查教练访问通用dashboard，重定向到教练专属dashboard
    if (updatedUserRole === 'COACH' && to.path === '/dashboard') {
      console.log('教练访问通用dashboard，重定向到教练专属dashboard')
      next('/coach/dashboard')
      return
    }
  }

  console.log('通过检查，继续导航')
  next()
})

/**
 * 路由后置守卫
 */
router.afterEach((_to) => {
  // 滚动到顶部
  window.scrollTo(0, 0)
})

export default router
