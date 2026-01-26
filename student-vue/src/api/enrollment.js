import request from './request'

// 选课管理相关API

// 获取学生已选课程
export function getSelectedCourses(studentId) {
  return request({
    url: `/student-course/selected/${studentId}`,
    method: 'get'
  })
}

// 选课
export function selectCourse(studentId, courseId) {
  return request({
    url: '/student-course/select',
    method: 'post',
    params: {
      studentId,
      courseId
    }
  })
}

// 退课
export function dropCourse(studentId, courseId) {
  return request({
    url: '/student-course/drop',
    method: 'delete',
    params: {
      studentId,
      courseId
    }
  })
}

// 获取学生已选课程总学分
export function getTotalCredits(studentId) {
  return request({
    url: `/student-course/credits/${studentId}`,
    method: 'get'
  })
}

// 获取所有选课信息
export function getAllEnrollments() {
  return request({
    url: '/enrollment/all',
    method: 'get'
  })
}

// 根据学生ID获取选课信息
export function getEnrollmentsByStudentId(studentId) {
  return request({
    url: `/enrollment/student/${studentId}`,
    method: 'get'
  })
}

// 根据课程ID获取选课信息
export function getEnrollmentsByCourseId(courseId) {
  return request({
    url: `/enrollment/course/${courseId}`,
    method: 'get'
  })
}

// 获取选课统计信息
export function getEnrollmentStatistics() {
  return request({
    url: '/enrollment/statistics',
    method: 'get'
  })
}