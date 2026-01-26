<template>
  <div class="login">
    <div class="login-container">
      <div class="login-box">
        <h2>学生选课系统</h2>
        
        <!-- 修改密码界面 -->
        <div v-if="changePasswordMode" class="change-password-form">
          <h3>首次登录 - 修改密码</h3>
          <p class="info-message">{{ infoMessage }}</p>
          
          <form @submit.prevent="handleChangePassword">
            <div class="form-group">
              <label for="currentPassword">当前密码:</label>
              <input 
                type="password" 
                id="currentPassword" 
                v-model="changePasswordForm.currentPassword" 
                required 
              />
            </div>
            
            <div class="form-group">
              <label for="newPassword">新密码:</label>
              <input 
                type="password" 
                id="newPassword" 
                v-model="changePasswordForm.newPassword" 
                required 
              />
              <small>密码必须包含数字、字母、特殊字符，且长度大于7位</small>
            </div>
            
            <div class="form-group">
              <label for="confirmPassword">确认新密码:</label>
              <input 
                type="password" 
                id="confirmPassword" 
                v-model="changePasswordForm.confirmPassword" 
                required 
              />
            </div>
            
            <div v-if="changePasswordError" class="error-message">
              {{ changePasswordError }}
            </div>
            
            <button type="submit" class="btn btn-primary">修改密码</button>
          </form>
        </div>
        
        <!-- 登录界面 -->
        <div v-else class="login-form">
          <form @submit.prevent="handleLogin">
            <div class="form-group">
              <label for="username">用户名:</label>
              <input 
                type="text" 
                id="username" 
                v-model="loginForm.username" 
                required 
              />
            </div>
            
            <div class="form-group">
              <label for="password">密码:</label>
              <input 
                type="password" 
                id="password" 
                v-model="loginForm.password" 
                required 
              />
            </div>
            
            <div v-if="errorMessage" class="error-message">
              {{ errorMessage }}
            </div>
            
            <div v-if="infoMessage" class="info-message">
              {{ infoMessage }}
            </div>
            
            <button type="submit" class="btn btn-primary">登录</button>
          </form>
          
          <p>还没有账户? <router-link to="/user/register">学生注册</router-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/store/login'
import axios from '@/api/request'
import { getAllCourses } from '@/api/course.js'

// 定义响应式数据
const loginForm = ref({
  username: '',
  password: ''
})

const changePasswordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const errorMessage = ref('')
const infoMessage = ref('')
const changePasswordError = ref('')
const changePasswordMode = ref(false)
const isFirstLogin = ref(false)

// 在组件挂载前检查用户状态，判断是否需要显示修改密码界面
onBeforeMount(() => {
  const token = localStorage.getItem('token')
  // 如果存在token，说明用户已登录
  if (token) {
    const authStore = useAuthStore()
    // 如果store中还没有用户信息，尝试从localStorage获取
    if (!authStore.user) {
      const username = localStorage.getItem('username')
      // 这里我们无法直接获取用户详细信息，所以需要调用API获取
      authStore.fetchUserInfo().then(() => {
        // 获取用户信息后检查是否是首次登录
        if (authStore.user && authStore.user.firstLogin) {
          // 如果是首次登录，显示修改密码界面
          isFirstLogin.value = true
          changePasswordMode.value = true
          infoMessage.value = '检测到您是首次登录，请修改默认密码'
        } else {
          // 跳转到仪表板
          router.push('/dashboard')
        }
      }).catch(() => {
        // 获取用户信息失败，清除认证状态并跳转到登录页
        authStore.userLogout()
      })
    } else if (authStore.user.firstLogin) {
      // 如果store中已有用户信息且是首次登录，显示修改密码界面
      isFirstLogin.value = true
      changePasswordMode.value = true
      infoMessage.value = '检测到您是首次登录，请修改默认密码'
    } else {
      // 跳转到仪表板
      router.push('/dashboard')
    }
  }
})

// 获取路由实例
const router = useRouter()
const route = useRoute()


// 登录处理函数
const handleLogin = async () => {
  try {
    const authStore = useAuthStore()
    const response = await authStore.login(loginForm.value)
    
    // 检查是否是首次登录
    if (response.data && response.data.user && response.data.user.firstLogin) {
      isFirstLogin.value = true;
      changePasswordMode.value = true;
      infoMessage.value = '检测到您是首次登录，请修改默认密码';
    } else {
      // 登录成功，跳转到仪表板
      router.push('/dashboard')
    }
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response && error.response.status === 401) {
      errorMessage.value = '用户名或密码错误'
    } else if (error.response && error.response.status === 400) {
      // 显示服务器返回的具体错误信息
      if (error.response.data && error.response.data.message) {
        errorMessage.value = error.response.data.message;
      } else {
        errorMessage.value = '请求参数错误，请检查用户名和密码';
      }
    } else {
      errorMessage.value = error.message || '登录过程中发生错误，请稍后重试'
    }
  }
}

// 修改密码处理函数
const handleChangePassword = async () => {
  // 清除之前的错误消息
  changePasswordError.value = '';
  
  // 验证新密码复杂度
  if (!isValidPassword(changePasswordForm.value.newPassword)) {
    changePasswordError.value = '密码必须包含数字、字母、特殊字符，且长度大于7位';
    return;
  }
  
  // 验证确认密码
  if (changePasswordForm.value.newPassword !== changePasswordForm.value.confirmPassword) {
    changePasswordError.value = '两次输入的新密码不一致';
    return;
  }
  
  try {
    // 使用 store 中的方法修改密码
    const authStore = useAuthStore();
    const response = await authStore.changeUserPassword({
      currentPassword: changePasswordForm.value.currentPassword,
      newPassword: changePasswordForm.value.newPassword
    });

    if (response.data.code === 200) {
      // 修改密码成功，清除表单数据并返回登录界面
      changePasswordForm.value.currentPassword = '';
      changePasswordForm.value.newPassword = '';
      changePasswordForm.value.confirmPassword = '';
      changePasswordMode.value = false;
      
      // 填充用户名
      if (authStore.user && authStore.user.username) {
        loginForm.value.username = authStore.user.username;
      }
      
      // 显示成功信息
      infoMessage.value = '密码修改成功，请使用新密码登录';
    } else {
      changePasswordError.value = response.data.message || '修改密码失败';
    }
  } catch (error) {
    console.error('修改密码错误:', error);
    if (error.response && error.response.data) {
      changePasswordError.value = error.response.data.message || '修改密码过程中发生错误';
    } else {
      changePasswordError.value = '修改密码过程中发生错误，请稍后重试';
    }
  }
}

// 验证密码复杂度
const isValidPassword = (password) => {
  // 检查密码长度
  if (password.length <= 7) {
    return false;
  }
  
  // 检查是否包含数字
  if (!/\d/.test(password)) {
    return false;
  }
  
  // 检查是否包含字母
  if (!/[a-zA-Z]/.test(password)) {
    return false;
  }
  
  // 检查是否包含特殊字符
  if (!/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) {
    return false;
  }
  
  return true;
}

// 页面加载时检查是否有传入学号参数
onMounted(() => {
  if (route.query.username) {
    loginForm.value.username = route.query.username;
  }
  
  if (route.query.message) {
    infoMessage.value = route.query.message;
  }
})
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-box {
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.login-box h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #555;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

small {
  display: block;
  margin-top: 0.25rem;
  font-size: 0.8rem;
  color: #777;
}

.btn {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  margin-top: 1rem;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  padding: 0.75rem;
  margin-bottom: 1rem;
}

.info-message {
  color: #0c5460;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
  border-radius: 4px;
  padding: 0.75rem;
  margin-bottom: 1rem;
}

a {
  color: #007bff;
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>