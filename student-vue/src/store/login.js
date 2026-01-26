import { defineStore } from 'pinia'
import { login, register, logout, getUserInfo, changePassword } from '@/api/login.js'
import router from '@/router'

// 定义一个名为'auth'的store，用于管理用户认证状态
export const useAuthStore = defineStore('auth', {
  // 状态管理：定义store中需要管理的数据
  state: () => ({
    // 用户信息对象，初始为空
    user: null,
    // 认证令牌，从localStorage中获取，如果不存在则为空字符串
    token: localStorage.getItem('token') || '',
    // 是否已认证的标志，根据localStorage中是否存在token来判断
    isAuthenticated: !!localStorage.getItem('token')
  }),
  
  // 计算属性：用于获取store中的数据
  getters: {
    // 获取用户信息
    getUser: (state) => state.user,
    // 获取认证令牌
    getToken: (state) => state.token,
    // 判断用户是否已登录
    isLoggedIn: (state) => state.isAuthenticated
  },
  
  // 方法：用于处理业务逻辑，修改store中的状态
  actions: {
    // 用户登录方法
    async login(loginData) {
      try {
        // 调用API登录函数，发送登录请求
        const response = await login(loginData)
        console.log('登录响应:', response)
        // 从响应数据中解构出token和user信息
        const { token, user } = response.data
        
        // 更新store中的状态
        this.token = token
        this.user = user
        this.isAuthenticated = true
        
        // 将token保存到浏览器的localStorage中，以便页面刷新后仍保持登录状态
        localStorage.setItem('token', token)
        // 保存用户信息
        localStorage.setItem('user', JSON.stringify(user))
        // 保存用户名
        localStorage.setItem('username', user.username)
        // 返回响应数据
        return response
      } catch (error) {
        // 如果登录失败，打印错误信息到控制台
        console.error('登录失败:', error)
        // 抛出错误，让调用者处理
        throw error
      }
    },
    
    // 用户注册方法
    async register(registerData) {
      try {
        // 调用API注册函数，发送注册请求
        const response = await register(registerData)
        // 返回响应数据
        return response
      } catch (error) {
        // 如果注册失败，打印错误信息到控制台
        console.error('注册失败:', error)
        // 抛出错误，让调用者处理
        throw error
      }
    },
    
    // 获取用户信息方法
    async fetchUserInfo() {
      try {
        // 调用API获取用户信息函数
        const response = await getUserInfo()
        // 更新store中的用户信息
        this.user = response.data
        // 保存用户信息到localStorage
        localStorage.setItem('user', JSON.stringify(response.data))
        // 返回响应数据
        return response
      } catch (error) {
        // 如果获取用户信息失败，打印错误信息到控制台
        console.error('获取用户信息失败:', error)
        // 抛出错误，让调用者处理
        throw error
      }
    },
    
    // 用户退出登录方法
    async userLogout() {
      try {
        // 调用API退出登录函数
        await logout()
      } catch (error) {
        // 如果退出登录失败，打印错误信息到控制台
        console.error('退出登录失败:', error)
      } finally {
        // 无论退出登录成功与否，都要清除本地状态
        // 清除store中的token
        this.token = ''
        // 清除store中的用户信息
        this.user = null
        // 设置认证状态为未认证
        this.isAuthenticated = false
        
        // 清除localStorage中的token和用户信息
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('username')
        
        // 跳转到登录页面
        router.push('/user/login')
      }
    },
    
    // 修改密码方法
    async changeUserPassword(passwordData) {
      try {
        // 调用API修改密码函数
        const response = await changePassword(passwordData)
        
        // 如果修改成功且用户已登录，更新用户状态
        if (this.user) {
          this.user.firstLogin = false
          // 更新localStorage中的用户信息
          localStorage.setItem('user', JSON.stringify(this.user))
        }
        
        return response
      } catch (error) {
        throw error
      }
    },
    
    // 更新用户信息方法
    updateUserInfo(userInfo) {
      // 使用新信息更新用户对象，保留原有属性
      this.user = { ...this.user, ...userInfo }
      // 更新localStorage中的用户信息
      localStorage.setItem('user', JSON.stringify(this.user))
    },
    
    // 设置认证令牌方法
    setToken(token) {
      // 更新store中的token
      this.token = token
      // 更新认证状态，如果token存在则为已认证，否则为未认证
      this.isAuthenticated = !!token
      // 如果token存在，保存到localStorage中，否则从localStorage中移除
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    }
  },
  
  // 持久化配置：使store中的状态在页面刷新后仍然保持
  persist: true
})