import request from './request'

// 课程管理相关API

// 获取所有课程
export function getAllCourses() {
  return request({
    url: '/course/list',
    method: 'get'
  })
}

// 根据ID获取课程
export function getCourseById(id) {
  return request({
    url: `/course/${id}`,
    method: 'get'
  })
}

// 保存课程（创建或更新）
export function saveCourse(course) {
  return request({
    url: '/course/save',
    method: 'post',
    data: course
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/course/${id}`,
    method: 'delete'
  })
}