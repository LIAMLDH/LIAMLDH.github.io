<template>
  <div class="course-selection">
    <h2>课程选择</h2>
    
    <!-- 已选课程面板 -->
    <div class="panel">
      <h3>已选课程 (总学分: {{ selectedTotalCredits }})</h3>
      <div v-if="selectedCourses.length === 0" class="empty-message">
        暂无已选课程
      </div>
      <div v-else class="course-list">
        <div 
          v-for="studentCourse in selectedCourses" 
          :key="studentCourse.id" 
          class="course-item selected-course"
        >
          <div class="course-info">
            <h4>{{ studentCourse.course?.courseName || '未知课程' }}</h4>
            <p>课程代码: {{ studentCourse.course?.courseCode || '未知' }}</p>
            <p>学分: {{ studentCourse.course?.credits || 0 }}</p>
            <p>简介: {{ studentCourse.course?.description || '暂无简介' }}</p>
          </div>
          <button 
            @click="dropCourse(studentCourse.course.id)" 
            class="btn btn-danger"
          >
            退选
          </button>
        </div>
      </div>
    </div>
    
    <!-- 可选课程面板 -->
    <div class="panel">
      <h3>可选课程</h3>
      <div v-if="availableCourses.length === 0" class="empty-message">
        暂无可选课程
      </div>
      <div v-else class="course-list">
        <div 
          v-for="course in availableCourses" 
          :key="course.id" 
          class="course-item available-course"
        >
          <div class="course-info">
            <h4>{{ course.courseName }}</h4>
            <p>课程代码: {{ course.courseCode }}</p>
            <p>学分: {{ course.credits }}</p>
            <p>简介: {{ course.description }}</p>
          </div>
          <button 
            @click="selectCourse(course.id)" 
            class="btn btn-primary"
          >
            选修
          </button>
        </div>
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
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/store/login'
import { 
  getSelectedCourses, 
  selectCourse as selectCourseApi, 
  dropCourse as dropCourseApi, 
  getTotalCredits
} from '@/api/enrollment.js'
import { getAllCourses } from '@/api/course.js'

// 响应式数据
const selectedCourses = ref([])
const allCourses = ref([])
const selectedTotalCredits = ref(0)
const loading = ref(false)
const error = ref('')

// 计算可选课程（所有课程中未选择的课程）
const availableCourses = computed(() => {
  // 确保 selectedCourses.value 是数组后再进行 map 操作
  if (!Array.isArray(selectedCourses.value)) {
    return allCourses.value;
  }
  
  const selectedCourseIds = selectedCourses.value.map(sc => sc.course.id)
  return allCourses.value.filter(course => !selectedCourseIds.includes(course.id))
})

// 获取学生已选课程
const fetchSelectedCourses = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const authStore = useAuthStore()
    const studentId = authStore.user?.student?.id
    
    if (!studentId) {
      throw new Error('无法获取学生信息')
    }
    
    const response = await getSelectedCourses(studentId)
    if (response.code === 200) {
      // 确保返回的数据是数组
      selectedCourses.value = Array.isArray(response.data) ? response.data : []
    } else {
      throw new Error(response.message || '获取已选课程失败')
    }
  } catch (err) {
    error.value = err.message || '获取已选课程时发生错误'
  } finally {
    loading.value = false
  }
}

// 获取所有课程
const fetchAllCourses = async () => {
  try {
    loading.value = true
    error.value = ''
    
    const response = await getAllCourses()
    if (response.code === 200) {
      // 确保返回的数据是数组
      allCourses.value = Array.isArray(response.data) ? response.data : []
    } else {
      throw new Error(response.message || '获取课程列表失败')
    }
  } catch (err) {
    error.value = err.message || '获取课程列表时发生错误'
  } finally {
    loading.value = false
  }
}

// 获取学生已选课程总学分
const fetchTotalCredits = async () => {
  try {
    const authStore = useAuthStore()
    const studentId = authStore.user?.student?.id
    
    if (!studentId) {
      throw new Error('无法获取学生信息')
    }
    
    const response = await getTotalCredits(studentId)
    if (response.code === 200) {
      selectedTotalCredits.value = response.data
    } else {
      throw new Error(response.message || '获取总学分失败')
    }
  } catch (err) {
    error.value = err.message || '获取总学分时发生错误'
  }
}

// 选课
const selectCourse = async (courseId) => {
  try {
    loading.value = true
    error.value = ''
    
    const authStore = useAuthStore()
    const studentId = authStore.user?.student?.id
    
    if (!studentId) {
      throw new Error('无法获取学生信息')
    }
    
    const response = await selectCourseApi(studentId, courseId)
    if (response.code === 200) {
      // 选课成功，刷新数据
      await Promise.all([
        fetchSelectedCourses(),
        fetchTotalCredits()
      ])
    } else {
      throw new Error(response.message || '选课失败')
    }
  } catch (err) {
    error.value = err.message || '选课时发生错误'
  } finally {
    loading.value = false
  }
}

// 退课
const dropCourse = async (courseId) => {
  try {
    loading.value = true
    error.value = ''
    
    const authStore = useAuthStore()
    const studentId = authStore.user?.student?.id
    
    if (!studentId) {
      throw new Error('无法获取学生信息')
    }
    
    const response = await dropCourseApi(studentId, courseId)
    if (response.code === 200) {
      // 退课成功，刷新数据
      await Promise.all([
        fetchSelectedCourses(),
        fetchTotalCredits()
      ])
    } else {
      throw new Error(response.message || '退课失败')
    }
  } catch (err) {
    error.value = err.message || '退课时发生错误'
  } finally {
    loading.value = false
  }
}

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchSelectedCourses(),
    fetchAllCourses(),
    fetchTotalCredits()
  ])
})
</script>

<script>
import { computed } from 'vue'

export default {
  computed: {
    availableCourses() {
      // 确保 selectedCourses 是数组后再进行 map 操作
      if (!Array.isArray(this.selectedCourses)) {
        return this.allCourses;
      }
      
      const selectedCourseIds = this.selectedCourses.map(sc => sc.course.id)
      return this.allCourses.filter(course => !selectedCourseIds.includes(course.id))
    }
  },
  methods: {
    async selectCourse(courseId) {
      await this.selectCourseHandler(courseId)
    },
    async dropCourse(courseId) {
      await this.dropCourseHandler(courseId)
    }
  }
}
</script>

<style scoped>
.course-selection {
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

.course-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: white;
}

.course-info h4 {
  margin: 0 0 10px 0;
  color: #333;
}

.course-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.selected-course {
  border-color: #28a745;
}

.available-course {
  border-color: #007bff;
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

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn:hover {
  opacity: 0.8;
}

.empty-message {
  text-align: center;
  color: #999;
  font-style: italic;
  padding: 20px;
}

.loading, .error-message {
  text-align: center;
  padding: 20px;
}

.loading {
  color: #007bff;
}

.error-message {
  color: #dc3545;
}
</style>