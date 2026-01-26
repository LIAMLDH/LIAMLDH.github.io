<template>
  <div class="course-management">
    <h2>课程管理</h2>
    
    <!-- 课程表单 -->
    <div class="panel">
      <h3>{{ isEditing ? '编辑课程' : '添加课程' }}</h3>
      <form @submit.prevent="handleSaveCourse">
        <div class="form-group">
          <label for="courseName">课程名称:</label>
          <input 
            type="text" 
            id="courseName" 
            v-model="courseForm.courseName" 
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="courseCode">课程代码:</label>
          <input 
            type="text" 
            id="courseCode" 
            v-model="courseForm.courseCode" 
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="credits">学分:</label>
          <input 
            type="number" 
            id="credits" 
            v-model.number="courseForm.credits" 
            min="0"
            required 
          />
        </div>
        
        <div class="form-group">
          <label for="description">简介:</label>
          <textarea 
            id="description" 
            v-model="courseForm.description" 
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
    
    <!-- 课程列表 -->
    <div class="panel">
      <h3>课程列表</h3>
      <div v-if="courses.length === 0" class="empty-message">
        暂无课程信息
      </div>
      <div v-else>
        <table class="course-table">
          <thead>
            <tr>
              <th>课程名称</th>
              <th>课程代码</th>
              <th>学分</th>
              <th>简介</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courses" :key="course.id">
              <td>{{ course.courseName }}</td>
              <td>{{ course.courseCode }}</td>
              <td>{{ course.credits }}</td>
              <td>{{ course.description }}</td>
              <td>
                <button 
                  @click="handleEditCourse(course)" 
                  class="btn btn-small btn-primary"
                >
                  编辑
                </button>
                <button 
                  @click="handleDeleteCourse(course.id)" 
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
  getAllCourses, 
  saveCourse as saveCourseApi,
  deleteCourse as deleteCourseApi,
  getCourseById
} from '@/api/course.js'

// 响应式数据
const courses = ref([])
const courseForm = ref({
  id: null,
  courseName: '',
  courseCode: '',
  credits: 0,
  description: ''
})
const isEditing = ref(false)
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// 获取所有课程
const fetchCourses = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getAllCourses()
    if (response.code === 200) {
      courses.value = response.data
    } else {
      throw new Error(response.message || '获取课程列表失败')
    }
  } catch (err) {
    error.value = err.message || '获取课程列表时发生错误'
  } finally {
    loading.value = false
  }
}

// 保存课程（创建或更新）
const saveCourseHandler = async () => {
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    const courseData = { ...courseForm.value }
    const response = await saveCourseApi(courseData)
    
    if (response.code === 200) {
      successMessage.value = isEditing.value ? '课程更新成功' : '课程添加成功'
      resetForm()
      await fetchCourses()
    } else {
      throw new Error(response.message || (isEditing.value ? '更新课程失败' : '添加课程失败'))
    }
  } catch (err) {
    error.value = err.message || (isEditing.value ? '更新课程时发生错误' : '添加课程时发生错误')
  } finally {
    loading.value = false
  }
}

// 编辑课程
const editCourseHandler = (course) => {
  courseForm.value = { ...course }
  isEditing.value = true
}

// 删除课程
const deleteCourseHandler = async (id) => {
  if (!confirm('确定要删除这个课程吗？')) {
    return
  }
  
  try {
    loading.value = true
    error.value = ''
    successMessage.value = ''
    
    const response = await deleteCourseApi(id)
    if (response.code === 200) {
      successMessage.value = '课程删除成功'
      await fetchCourses()
    } else {
      throw new Error(response.message || '删除课程失败')
    }
  } catch (err) {
    error.value = err.message || '删除课程时发生错误'
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  courseForm.value = {
    id: null,
    courseName: '',
    courseCode: '',
    credits: 0,
    description: ''
  }
  isEditing.value = false
}

// 页面加载时获取数据
onMounted(() => {
  fetchCourses()
})

// 暴露方法给模板
const handleSaveCourse = () => {
  saveCourseHandler()
}

const handleEditCourse = (course) => {
  editCourseHandler(course)
}

const handleDeleteCourse = (id) => {
  deleteCourseHandler(id)
}
</script>

<style scoped>
.course-management {
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

.course-table {
  width: 100%;
  border-collapse: collapse;
}

.course-table th,
.course-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.course-table th {
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