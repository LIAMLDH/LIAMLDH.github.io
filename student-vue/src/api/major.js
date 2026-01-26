import request from './request'

// 专业管理相关API

// 获取所有专业
export function getAllMajors() {
  return request({
    url: '/major/list',
    method: 'get'
  })
}

// 根据ID获取专业
export function getMajorById(id) {
  return request({
    url: `/major/${id}`,
    method: 'get'
  })
}

// 保存专业（创建或更新）
export function saveMajor(major) {
  return request({
    url: '/major/save',
    method: 'post',
    data: major
  })
}

// 删除专业
export function deleteMajor(id) {
  return request({
    url: `/major/${id}`,
    method: 'delete'
  })
}