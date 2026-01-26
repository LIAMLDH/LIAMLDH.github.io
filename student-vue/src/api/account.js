import request from './request'

// 账户管理相关API

// 获取所有账户
export function getAllAccounts() {
  return request({
    url: '/account/list',
    method: 'get'
  })
}

// 根据ID获取账户
export function getAccountById(id) {
  return request({
    url: `/account/${id}`,
    method: 'get'
  })
}

// 保存账户（创建或更新）
export function saveAccount(account) {
  return request({
    url: '/account/save',
    method: 'post',
    data: account
  })
}

// 删除账户
export function deleteAccount(id) {
  return request({
    url: `/account/${id}`,
    method: 'delete'
  })
}