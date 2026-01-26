<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <h1>{{ userRole === 'ADMIN' ? '管理员面板' : '学生面板' }}</h1>
      <div class="user-info">
        <span>欢迎, {{ username }}!</span>
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </header>
    
    <div class="dashboard-content">
      <div v-if="userRole === 'ADMIN'" class="admin-content">
        <div class="card-container">
          <div class="card" @click="goTo('/dashboard/course-management')">
            <h3>课程管理</h3>
            <p>管理课程信息</p>
          </div>
          <div class="card" @click="goTo('/dashboard/major-management')">
            <h3>专业管理</h3>
            <p>管理专业信息</p>
          </div>
          <div class="card" @click="goTo('/dashboard/account-management')">
            <h3>账户管理</h3>
            <p>管理系统账户</p>
          </div>
          <div class="card" @click="goTo('/dashboard/enrollment-query')">
            <h3>选课查询</h3>
            <p>查看选课信息</p>
          </div>
        </div>
      </div>
      
      <div v-else class="student-content">
        <div class="card" @click="goTo('/dashboard/course-selection')">
          <h3>课程选择</h3>
          <p>选择您的课程</p>
        </div>
      </div>
    </div>
    
    <!-- 子路由视图 -->
    <div class="route-view">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/login'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const userRole = ref('')

onMounted(() => {
  // 获取用户信息
  if (authStore.user) {
    username.value = authStore.user.username
    userRole.value = authStore.user.role
  } else {
    // 如果store中没有用户信息，尝试从localStorage获取
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (user && user.username) {
      username.value = user.username
      userRole.value = user.role
    } else {
      // 如果没有用户信息，跳转到登录页面
      router.push('/user/login')
    }
  }
  
  // 根据用户角色自动跳转到默认页面
  if (!router.currentRoute.value.path.endsWith('/dashboard')) {
    return;
  }
  
  if (userRole.value === 'ADMIN') {
    router.push('/dashboard/course-management')
  } else {
    router.push('/dashboard/course-selection')
  }
})

const logout = () => {
  authStore.userLogout()
}

const goTo = (path) => {
  router.push(path)
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.dashboard-header h1 {
  margin: 0;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.logout-btn:hover {
  background-color: #c82333;
}

.dashboard-content {
  padding: 2rem;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.card h3 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.card p {
  margin: 0;
  color: #666;
}

.student-content .card {
  max-width: 300px;
  margin: 0 auto;
}

.route-view {
  padding: 0 2rem 2rem 2rem;
}
</style>