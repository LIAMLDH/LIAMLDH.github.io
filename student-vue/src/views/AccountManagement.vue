<template>
  <div class="account-management">
    <h2>账户管理</h2>
    
    <!-- 账户表单 -->
    <div class="panel">
      <h3>{{ isEditing ? '编辑账户' : '添加账户' }}</h3>
      <form @submit.prevent="handleSaveAccount">
        <div class="form-group">
          <label for="username">用户名:</label>
          <input 
            type="text" 
            id="username" 
            v-model="accountForm.username" 
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="password">
            {{ isEditing ? '新密码（留空则不更改）' : '密码:' }}
          </label>
          <input 
            type="password" 
            id="password" 
            v-model="accountForm.password" 
            :required="!isEditing"
          />
        </div>
        
        <div class="form-group">
          <label for="role">角色:</label>
          <select id="role" v-model="accountForm.role" required>
            <option value="STUDENT">学生</option>
            <option value="ADMIN">管理员</option>
          </select>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn btn-primary">
            {{ isEditing ? '更新' : '添加' }}
          </button>
          <button 
            type="button" 
            @click="resetForm" 
            class="btn btn-secondary"
          >
            取消
          </button>
        </div>
      </form>
    </div>
    
    <!-- 账户列表 -->
    <div class="panel">
      <h3>账户列表</h3>
      <div v-if="accounts.length === 0" class="empty-message">
        暂无账户信息
      </div>
      <div v-else>
        <table class="account-table">
          <thead>
            <tr>
              <th>用户名</th>
              <th>角色</th>
              <th>学生信息</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="account in accounts" :key="account.id">
              <td>{{ account.username }}</td>
              <td>{{ account.role === 'ADMIN' ? '管理员' : '学生' }}</td>
              <td>
                <div v-if="account.student">
                  <div>学号: {{ account.student.studentId }}</div>
                  <div>姓名: {{ account.student.name }}</div>
                  <div>电话: {{ account.student.phone }}</div>
                </div>
                <div v-else>
                  无关联学生
                </div>
              </td>
              <td>
                <button 
                  @click="handleEditAccount(account)" 
                  class="btn btn-small btn-primary"
                >
                  编辑
                </button>
                <button 
                  @click="handleDeleteAccount(account.id)" 
                  class="btn btn-small btn-danger"
                >
                  删除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <!-- 错误信息 -->
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    
    <!-- 成功信息 -->
    <div v-if="successMessage" class="success-message">
      {{ successMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  getAllAccounts, 
  saveAccount as saveAccountApi,
  deleteAccount as deleteAccountApi,
  getAccountById
} from '@/api/account.js'

// 响应式数据
const accounts = ref([])
const accountForm = ref({
  id: null,
  username: '',
  password: '',
  role: 'STUDENT'
})
const isEditing = ref(false)
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// 安全地提取账户信息，避免循环引用
const extractAccountInfo = (account) => {
  if (!account) return null;
  
  const extracted = {
    id: account.id,
    username: account.username,
    role: account.role,
    firstLogin: account.firstLogin,
    createdTime: account.createdTime,
    updatedTime: account.updatedTime,
    student: null
  };
  
  // 安全地处理学生信息
  if (account.student) {
    extracted.student = {
      id: account.student.id,
      studentId: account.student.studentId,
      name: account.student.name,
      age: account.student.age,
      phone: account.student.phone,
      enrollmentDate: account.student.enrollmentDate,
      major: null
    };
    
    // 安全地处理专业信息
    if (account.student.major) {
      extracted.student.major = {
        id: account.student.major.id,
        majorCode: account.student.major.majorCode,
        majorName: account.student.major.majorName,
        description: account.student.major.description
      };
    }
  }
  
  return extracted;
};

// 获取所有账户
const fetchAccounts = async () => {
  try {
    console.log('开始获取账户列表...');
    loading.value = true
    error.value = ''
    
    const response = await getAllAccounts()
    console.log('获取账户列表响应:', response);
    
    // 检查响应数据格式
    if (!response) {
      throw new Error('响应为空');
    }
    
    console.log('响应类型:', typeof response);
    console.log('响应内容:', response);
    
    if (response.code === 200) {
      // 解析嵌套的JSON字符串
      let data = response.data;
      
      // 如果data是字符串，尝试解析它
      if (typeof data === 'string') {
        try {
          const parsedData = JSON.parse(data);
          if (parsedData.code === 200 && parsedData.data) {
            data = parsedData.data;
          }
        } catch (e) {
          console.error('解析嵌套JSON失败:', e);
        }
      }
      
      // 确保 data 是一个数组
      if (!Array.isArray(data)) {
        console.warn('响应数据不是数组格式，尝试转换:', data);
        // 如果data是对象且包含数组属性，尝试提取
        if (data && typeof data === 'object') {
          // 如果data本身就是一个账户对象，将其放入数组中
          if (data.id !== undefined) {
            data = [data];
          } else {
            // 否则尝试查找数组属性
            const arrayKeys = Object.keys(data).filter(key => Array.isArray(data[key]));
            if (arrayKeys.length > 0) {
              data = data[arrayKeys[0]];
            } else {
              data = [];
            }
          }
        } else {
          data = [];
        }
      }
      
      // 处理循环引用问题，只保留需要的字段
      accounts.value = data.map(account => extractAccountInfo(account));
      console.log('处理后的账户列表数据:', accounts.value);
    } else {
      throw new Error(response.message || '获取账户列表失败')
    }
  } catch (err) {
    console.error('获取账户列表错误:', err);
    if (err.message) {
      error.value = err.message;
    } else {
      error.value = '获取账户列表时发生未知错误';
    }
  } finally {
    loading.value = false
  }
}

// 保存账户（创建或更新）
const saveAccountHandler = async () => {
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    // 如果是编辑状态且密码为空，则不发送密码字段
    const accountData = { ...accountForm.value }
    if (isEditing.value && !accountData.password) {
      delete accountData.password
    }
    
    const response = await saveAccountApi(accountData)
    
    if (response.code === 200) {
      successMessage.value = isEditing.value ? '账户更新成功' : '账户添加成功'
      resetForm()
      await fetchAccounts()
    } else {
      throw new Error(response.message || (isEditing.value ? '更新账户失败' : '添加账户失败'))
    }
  } catch (err) {
    error.value = err.message || (isEditing.value ? '更新账户时发生错误' : '添加账户时发生错误')
  } finally {
    loading.value = false
  }
}

// 编辑账户
const editAccountHandler = (account) => {
  accountForm.value = { 
    id: account.id,
    username: account.username,
    password: '',
    role: account.role
  }
  isEditing.value = true
}

// 删除账户
const deleteAccountHandler = async (id) => {
  if (!confirm('确定要删除这个账户吗？')) {
    return
  }
  
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    const response = await deleteAccountApi(id)
    if (response.code === 200) {
      successMessage.value = '账户删除成功'
      await fetchAccounts()
    } else {
      throw new Error(response.message || '删除账户失败')
    }
  } catch (err) {
    error.value = err.message || '删除账户时发生错误'
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  accountForm.value = {
    id: null,
    username: '',
    password: '',
    role: 'STUDENT'
  }
  isEditing.value = false
}

// 页面加载时获取数据
onMounted(() => {
  fetchAccounts()
})

// 暴露方法给模板
const handleSaveAccount = () => {
  saveAccountHandler()
}

const handleEditAccount = (account) => {
  editAccountHandler(account)
}

const handleDeleteAccount = (id) => {
  deleteAccountHandler(id)
}
</script>

<style scoped>
.account-management {
  padding: 20px;
}

.panel {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.panel h3 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input, select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
}

.btn:hover {
  opacity: 0.8;
}

.account-table {
  width: 100%;
  border-collapse: collapse;
}

.account-table th,
.account-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.account-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.empty-message {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 20px;
}

.loading, .error-message, .success-message {
  text-align: center;
  padding: 10px;
  margin: 10px 0;
}

.loading {
  color: #007bff;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}

.success-message {
  color: #155724;
  background-color: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}
</style>