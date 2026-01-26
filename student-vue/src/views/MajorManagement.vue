<template>
  <div class="major-management">
    <h2>专业管理</h2>
    
    <!-- 专业表单 -->
    <div class="panel">
      <h3>{{ isEditing ? '编辑专业' : '添加专业' }}</h3>
      <form @submit.prevent="handleSaveMajor">
        <div class="form-group">
          <label for="majorName">专业名称:</label>
          <input 
            type="text" 
            id="majorName" 
            v-model="majorForm.majorName" 
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="majorCode">专业代码:</label>
          <input 
            type="text" 
            id="majorCode" 
            v-model="majorForm.majorCode" 
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="description">简介:</label>
          <textarea 
            id="description" 
            v-model="majorForm.description" 
            rows="3"
          ></textarea>
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
    
    <!-- 专业列表 -->
    <div class="panel">
      <h3>专业列表</h3>
      <div v-if="majors.length === 0" class="empty-message">
        暂无专业信息
      </div>
      <div v-else>
        <table class="major-table">
          <thead>
            <tr>
              <th>专业名称</th>
              <th>专业代码</th>
              <th>简介</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="major in majors" :key="major.id">
              <td>{{ major.majorName }}</td>
              <td>{{ major.majorCode }}</td>
              <td>{{ major.description }}</td>
              <td>
                <button 
                  @click="handleEditMajor(major)" 
                  class="btn btn-small btn-primary"
                >
                  编辑
                </button>
                <button 
                  @click="handleDeleteMajor(major.id)" 
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
  getAllMajors, 
  saveMajor as saveMajorApi,
  deleteMajor as deleteMajorApi,
  getMajorById
} from '@/api/major.js'

// 响应式数据
const majors = ref([])
const majorForm = ref({
  id: null,
  majorName: '',
  majorCode: '',
  description: ''
})
const isEditing = ref(false)
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// 获取所有专业
const fetchMajors = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getAllMajors()
    if (response.code === 200) {
      majors.value = response.data
    } else {
      throw new Error(response.message || '获取专业列表失败')
    }
  } catch (err) {
    error.value = err.message || '获取专业列表时发生错误'
  } finally {
    loading.value = false
  }
}

// 保存专业（创建或更新）
const saveMajorHandler = async () => {
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    const majorData = { ...majorForm.value }
    const response = await saveMajorApi(majorData)
    
    if (response.code === 200) {
      successMessage.value = isEditing.value ? '专业更新成功' : '专业添加成功'
      resetForm()
      await fetchMajors()
    } else {
      throw new Error(response.message || (isEditing.value ? '更新专业失败' : '添加专业失败'))
    }
  } catch (err) {
    error.value = err.message || (isEditing.value ? '更新专业时发生错误' : '添加专业时发生错误')
  } finally {
    loading.value = false
  }
}

// 编辑专业
const editMajorHandler = (major) => {
  majorForm.value = { ...major }
  isEditing.value = true
}

// 删除专业
const deleteMajorHandler = async (id) => {
  if (!confirm('确定要删除这个专业吗？')) {
    return
  }
  
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    const response = await deleteMajorApi(id)
    if (response.code === 200) {
      successMessage.value = '专业删除成功'
      await fetchMajors()
    } else {
      throw new Error(response.message || '删除专业失败')
    }
  } catch (err) {
    error.value = err.message || '删除专业时发生错误'
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  majorForm.value = {
    id: null,
    majorName: '',
    majorCode: '',
    description: ''
  }
  isEditing.value = false
}

// 页面加载时获取数据
onMounted(() => {
  fetchMajors()
})

// 暴露方法给模板
const handleSaveMajor = () => {
  saveMajorHandler()
}

const handleEditMajor = (major) => {
  editMajorHandler(major)
}

const handleDeleteMajor = (id) => {
  deleteMajorHandler(id)
}
</script>

<style scoped>
.major-management {
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

input, textarea {
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

.major-table {
  width: 100%;
  border-collapse: collapse;
}

.major-table th,
.major-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.major-table th {
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