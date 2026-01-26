// 导入Vue Router相关函数和组件
import { createRouter, createWebHistory } from 'vue-router'
// 导入各个页面组件
import Login from '../views/Login.vue'     // 登录页面
import Dashboard from '../views/Dashboard.vue' // 仪表板页面
import StudentRegister from '../views/StudentRegister.vue' // 学生注册页面
import CourseSelection from '../views/CourseSelection.vue' // 课程选择页面
import CourseManagement from '../views/CourseManagement.vue' // 课程管理页面
import MajorManagement from '../views/MajorManagement.vue' // 专业管理页面
import AccountManagement from '../views/AccountManagement.vue' // 账户管理页面
import EnrollmentQuery from '../views/EnrollmentQuery.vue' // 选课查询页面

// 定义路由配置
const routes = [
  // 首页路由配置，重定向到登录页面
  {
    path: '/',
    redirect: '/user/login'
  },
  // 登录页面路由配置
  {
    path: '/user/login', // 登录页面路径
    name: 'Login',       // 路由名称
    component: Login     // 对应的登录组件
  },
  // 注册页面路由配置（指向学生注册）
  {
    path: '/user/register', // 注册页面路径
    name: 'Register',       // 路由名称
    component: StudentRegister     // 对应的学生注册组件
  },
  // 学生注册页面路由配置
  {
    path: '/user/student-register', // 学生注册页面路径
    name: 'StudentRegister',        // 路由名称
    component: StudentRegister      // 对应的学生注册组件
  },
  // 仪表板路由配置（需要认证的页面）
  {
    path: '/dashboard',      // 仪表板路径
    name: 'Dashboard',       // 路由名称
    component: Dashboard,    // 对应的仪表板组件
    meta: { requiresAuth: true },
    // 定义子路由（嵌套路由）
    children: [
      {
        // 子路径/course-selection，完整路径为/dashboard/course-selection
        path: 'course-selection',
        name: 'CourseSelection',
        component: CourseSelection,
        meta: { role: 'STUDENT' }
      },
      {
        // 课程管理页面（仅管理员）
        path: 'course-management',
        name: 'CourseManagement',
        component: CourseManagement,
        meta: { role: 'ADMIN' }
      },
      {
        // 专业管理页面（仅管理员）
        path: 'major-management',
        name: 'MajorManagement',
        component: MajorManagement,
        meta: { role: 'ADMIN' }
      },
      {
        // 账户管理页面（仅管理员）
        path: 'account-management',
        name: 'AccountManagement',
        component: AccountManagement,
        meta: { role: 'ADMIN' }
      },
      {
        // 选课查询页面（仅管理员）
        path: 'enrollment-query',
        name: 'EnrollmentQuery',
        component: EnrollmentQuery,
        meta: { role: 'ADMIN' }
      }
    ]
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(), // 使用HTML5 History模式
  routes                       // 使用上面定义的路由配置
})

// 添加全局路由守卫（导航守卫）
router.beforeEach((to, from, next) => {
  // 检查用户是否已认证（通过检查本地存储中的token）
  const isAuthenticated = localStorage.getItem('token')
  
  // 如果目标路由需要认证且用户未认证，则重定向到登录页面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/user/login')
  } else {
    next()
  }
})

export default router