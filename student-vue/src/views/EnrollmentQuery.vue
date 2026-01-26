<template>
  <div class="enrollment-query">
    <h2>选课信息查询</h2>
    
    <!-- 统计信息 -->
    <div class="panel">
      <h3>选课统计</h3>
      <div class="statistics">
        <div class="stat-item">
          <span class="stat-label">总选课数:</span>
          <span class="stat-value">{{ statistics.totalEnrollments }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">学生数:</span>
          <span class="stat-value">{{ statistics.uniqueStudents }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">课程数:</span>
          <span class="stat-value">{{ statistics.uniqueCourses }}</span>
        </div>
      </div>
    </div>
    
    <!-- 查询条件 -->
    <div class="panel">
      <h3>查询条件</h3>
      <div class="query-form">
        <div class="form-group">
          <label for="studentId">按学生ID查询:</label>
          <input 
            type="number" 
            id="studentId" 
            v-model.number="queryForm.studentId" 
          />
        </div>
        
        <div class="form-group">
          <label for="courseId">按课程ID查询:</label>
          <input 
            type="number" 
            id="courseId" 
            v-model.number="queryForm.courseId" 
          />
        </div>
        
        <div class="form-actions">
          <button @click="queryEnrollments" class="btn btn-primary">
            查询
          </button>
          <button @click="resetQuery" class="btn btn-secondary">
            重置
          </button>
          <button @click="fetchAllEnrollments" class="btn btn-info">
            显示全部
          </button>
        </div>
      </div>
    </div>
    
    <!-- 选课列表 -->
    <div class="panel">
      <h3>选课列表</h3>
      <div v-if="enrollments.length === 0" class="empty-message">
        暂无选课信息
      </div>
      <div v-else>
        <table class="enrollment-table">
          <thead>
            <tr>
              <th>学生ID</th>
              <th>学生姓名</th>
              <th>课程ID</th>
              <th>课程名称</th>
              <th>选课时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="enrollment in enrollments" :key="enrollment.id">
              <td>{{ enrollment.student?.id || 'N/A' }}</td>
              <td>{{ enrollment.student?.name || 'N/A' }}</td>
              <td>{{ enrollment.course?.id || 'N/A' }}</td>
              <td>{{ enrollment.course?.courseName || 'N/A' }}</td>
              <td>{{ formatDate(enrollment.selectedTime) }}</td>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  getAllEnrollments,
  getEnrollmentsByStudentId,
  getEnrollmentsByCourseId,
  getEnrollmentStatistics
} from '@/api/enrollment.js'

// 响应式数据
const enrollments = ref([])
const statistics = ref({
  totalEnrollments: 0,
  uniqueStudents: 0,
  uniqueCourses: 0
})
const queryForm = ref({
  studentId: null,
  courseId: null
})
const loading = ref(false)
const error = ref('')

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN')
}

// 获取所有选课信息
const fetchAllEnrollments = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getAllEnrollments()
    if (response.code === 200) {
      // 确保返回的数据是数组
      enrollments.value = Array.isArray(response.data) ? response.data : []
    } else {
      throw new Error(response.message || '获取选课信息失败')
    }
  } catch (err) {
    error.value = err.message || '获取选课信息时发生错误'
  } finally {
    loading.value = false
  }
}

// 根据学生ID查询选课信息
const fetchEnrollmentsByStudentId = async (studentId) => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getEnrollmentsByStudentId(studentId)
    if (response.code === 200) {
      // 确保返回的数据是数组
      enrollments.value = Array.isArray(response.data) ? response.data : []
    } else {
      throw new Error(response.message || '根据学生ID获取选课信息失败')
    }
  } catch (err) {
    error.value = err.message || '根据学生ID获取选课信息时发生错误'
  } finally {
    loading.value = false
  }
}

// 根据课程ID查询选课信息
const fetchEnrollmentsByCourseId = async (courseId) => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getEnrollmentsByCourseId(courseId)
    if (response.code === 200) {
      // 确保返回的数据是数组
      enrollments.value = Array.isArray(response.data) ? response.data : []
    } else {
      throw new Error(response.message || '根据课程ID获取选课信息失败')
    }
  } catch (err) {
    error.value = err.message || '根据课程ID获取选课信息时发生错误'
  } finally {
    loading.value = false
  }
}

// 获取选课统计信息
const fetchEnrollmentStatistics = async () => {
  try {
    const response = await getEnrollmentStatistics()
    if (response.code === 200) {
      statistics.value = response.data
    } else {
      throw new Error(response.message || '获取统计信息失败')
    }
  } catch (err) {
    error.value = err.message || '获取统计信息时发生错误'
  }
}

// 查询选课信息
const queryEnrollments = () => {
  if (queryForm.value.studentId) {
    fetchEnrollmentsByStudentId(queryForm.value.studentId)
  } else if (queryForm.value.courseId) {
    fetchEnrollmentsByCourseId(queryForm.value.courseId)
  } else {
    fetchAllEnrollments()
  }
}

// 重置查询条件
const resetQuery = () => {
  queryForm.value = {
    studentId: null,
    courseId: null
  }
}

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchAllEnrollments(),
    fetchEnrollmentStatistics()
  ])
})
</script>

<style scoped>
.enrollment-query {
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

.statistics {
  display: flex;
  gap: 30px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-label {
  font-weight: bold;
  color: #555;
}

.stat-value {
  font-size: 1.5em;
  font-weight: bold;
  color: #007bff;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 5px;
  font-weight: bold;
  color: #555;
}

input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
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

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn:hover {
  opacity: 0.8;
}

.enrollment-table {
  width: 100%;
  border-collapse: collapse;
}

.enrollment-table th,
.enrollment-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.enrollment-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.empty-message {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 20px;
}

.loading, .error-message {
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
</style>