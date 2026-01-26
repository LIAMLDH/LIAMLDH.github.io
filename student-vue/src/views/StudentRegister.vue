<template>
  <div class="register">
    <div class="register-form">
      <h2>学生注册</h2>
      <form @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="name">姓名:</label>
          <input 
            type="text" 
            id="name" 
            v-model="registerForm.name" 
            required 
            :class="{ 'error': errors.name }"
          />
          <div v-if="errors.name" class="field-error">{{ errors.name }}</div>
        </div>
        
        <div class="form-group">
          <label for="age">年龄:</label>
          <input 
            type="number" 
            id="age" 
            v-model.number="registerForm.age" 
            required 
            min="15" 
            max="100"
            :class="{ 'error': errors.age }"
          />
          <div v-if="errors.age" class="field-error">{{ errors.age }}</div>
        </div>
        
        <div class="form-group">
          <label for="phone">手机号:</label>
          <input 
            type="tel" 
            id="phone" 
            v-model="registerForm.phone" 
            required 
            :class="{ 'error': errors.phone }"
          />
          <div v-if="errors.phone" class="field-error">{{ errors.phone }}</div>
        </div>
        
        <div class="form-group">
          <label for="enrollmentDate">入学时间:</label>
          <input 
            type="date" 
            id="enrollmentDate" 
            v-model="registerForm.enrollmentDate" 
            required 
            :class="{ 'error': errors.enrollmentDate }"
          />
          <div v-if="errors.enrollmentDate" class="field-error">{{ errors.enrollmentDate }}</div>
        </div>
        
        <div class="form-group">
          <label for="majorId">专业 (已加载 {{ majors.length }} 个专业):</label>
          <select 
            id="majorId" 
            v-model="registerForm.majorId" 
            required 
            :class="{ 'error': errors.majorId }"
          >
            <option value="" disabled selected>请选择专业</option>
            <option v-for="major in majors" :key="major.id" :value="major.id">
              {{ major.majorName }}
            </option>
          </select>
          <div v-if="errors.majorId" class="field-error">{{ errors.majorId }}</div>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="isSubmitting">
            {{ isSubmitting ? '注册中...' : '注册' }}
          </button>
        </div>
        
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>
        
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>
      </form>
      
      <div class="form-footer">
        <p>已有账户? <router-link to="/user/login">立即登录</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllMajors } from '@/api/major.js'
import request from '@/api/request.js'

// 定义响应式数据
const registerForm = ref({
  name: '',
  age: null,
  phone: '',
  enrollmentDate: '',
  majorId: ''
})

const errors = ref({
  name: '',
  age: '',
  phone: '',
  enrollmentDate: '',
  majorId: ''
})

const majors = ref([])
const errorMessage = ref('')
const successMessage = ref('')
const isSubmitting = ref(false)

// 获取路由实例
const router = useRouter()

// 页面加载时获取专业列表
onMounted(async () => {
  try {
    console.log('开始获取专业列表...');
    const response = await getAllMajors()
    console.log('获取专业列表原始响应:', response);
    
    // 正确处理响应数据 (axios拦截器已经处理了响应，直接返回data部分)
    if (response && response.code === 200) {
      majors.value = response.data || []
      console.log('专业列表数据:', majors.value);
      console.log('专业列表长度:', majors.value.length);
      
      // 输出每个专业的详细信息
      if (majors.value.length > 0) {
        majors.value.forEach((major, index) => {
          console.log(`专业 ${index + 1}:`, major);
        });
      }
    } else {
      console.error('响应数据格式不正确:', response);
      errorMessage.value = '获取专业列表失败：数据格式不正确'
    }
  } catch (error) {
    console.error('获取专业列表失败:', error)
    errorMessage.value = '获取专业列表失败: ' + (error.message || '未知错误')
  }
})

// 表单验证函数
const validateForm = () => {
  // 清除之前的错误
  Object.keys(errors.value).forEach(key => {
    errors.value[key] = '';
  });
  
  let isValid = true;
  
  // 验证姓名
  if (!registerForm.value.name) {
    errors.value.name = '姓名不能为空';
    isValid = false;
  } else if (registerForm.value.name.length < 2) {
    errors.value.name = '姓名至少2个字符';
    isValid = false;
  }
  
  // 验证年龄
  if (!registerForm.value.age) {
    errors.value.age = '年龄不能为空';
    isValid = false;
  } else if (registerForm.value.age < 15 || registerForm.value.age > 100) {
    errors.value.age = '年龄必须在15-100之间';
    isValid = false;
  }
  
  // 验证手机号
  if (!registerForm.value.phone) {
    errors.value.phone = '手机号不能为空';
    isValid = false;
  } else if (!/^1[3-9]\d{9}$/.test(registerForm.value.phone)) {
    errors.value.phone = '请输入正确的手机号';
    isValid = false;
  }
  
  // 验证入学时间
  if (!registerForm.value.enrollmentDate) {
    errors.value.enrollmentDate = '入学时间不能为空';
    isValid = false;
  }
  
  // 验证专业
  if (!registerForm.value.majorId) {
    errors.value.majorId = '请选择专业';
    isValid = false;
  }
  
  return isValid;
}

// 注册处理函数
const handleRegister = async () => {
  // 清除之前的消息
  errorMessage.value = '';
  successMessage.value = '';
  
  // 验证表单
  if (!validateForm()) {
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    // 准备注册数据
    const registerData = {
      name: registerForm.value.name,
      age: registerForm.value.age,
      phone: registerForm.value.phone,
      enrollmentDate: registerForm.value.enrollmentDate,
      majorId: parseInt(registerForm.value.majorId)
    };
    
    console.log('发送注册请求数据:', registerData);
    
    // 确保日期格式正确
    if (registerData.enrollmentDate instanceof Date) {
      registerData.enrollmentDate = registerData.enrollmentDate.toISOString().split('T')[0];
    }
    
    const response = await request.post('/user/student/register', registerData)
    
    console.log('注册响应:', response);
    
    if (response.code === 200) {
      // 注册成功
      successMessage.value = `注册成功！您的学号是：${response.data.studentId}，请使用该学号登录`
      
      // 3秒后跳转到登录页面，并传递学号参数
      setTimeout(() => {
        router.push({
          path: '/user/login',
          query: { 
            username: response.data.studentId,
            message: '注册成功，请使用学号和默认密码登录'
          }
        })
      }, 3000)
    } else {
      errorMessage.value = response.message || '注册失败'
    }
  } catch (error) {
    console.error('注册错误:', error)
    if (error.response && error.response.data) {
      errorMessage.value = error.response.data.message || '注册过程中发生错误，请稍后重试'
    } else {
      errorMessage.value = '注册过程中发生错误，请稍后重试'
    }
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 1rem;
}

.register-form {
  width: 100%;
  max-width: 500px;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
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

input, select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

input.error, select.error {
  border-color: #dc3545;
}

.field-error {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.form-actions {
  margin-top: 1.5rem;
  text-align: center;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s;
  width: 100%;
}

.btn-primary {
  background-color: #28a745;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #218838;
}

.btn-primary:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.form-footer {
  text-align: center;
  margin-top: 1.5rem;
}

.form-footer a {
  color: #007bff;
  text-decoration: none;
}

.form-footer a:hover {
  text-decoration: underline;
}

.error-message {
  color: #dc3545;
  text-align: center;
  margin-top: 1rem;
}

.success-message {
  color: #28a745;
  text-align: center;
  margin-top: 1rem;
}
</style>