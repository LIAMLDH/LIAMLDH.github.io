import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || '',  // 使用环境变量，生产环境使用相对路径
  timeout: 60000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    console.log('发送请求:', config);
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    console.log('收到原始响应:', response);
    
    // 检查响应结构
    if (!response || !response.hasOwnProperty('data')) {
      console.error('响应结构不正确，缺少data字段:', response);
      return Promise.reject(new Error('响应结构不正确'));
    }
    
    let { data } = response;
    console.log('响应中的data字段:', data);
    console.log('data字段类型:', typeof data);
    
    // 如果data是字符串，尝试解析为JSON
    if (typeof data === 'string') {
      // 清理字符串中的可能损坏字符
      try {
        // 尝试修复可能的JSON格式问题
        data = data.trim();
        if (data.startsWith('{') || data.startsWith('[')) {
          data = JSON.parse(data);
          console.log('解析后的JSON数据:', data);
        } else {
          console.warn('响应数据不是JSON格式:', data);
          // 如果不是JSON格式，直接返回
          return {
            code: 200,
            message: '操作成功',
            data: data
          };
        }
      } catch (parseError) {
        console.error('JSON解析失败:', parseError);
        console.error('原始数据:', data);
        // 如果解析失败，返回原始数据
        return {
          code: 200,
          message: '操作成功',
          data: data
        };
      }
    }
    
    // 检查data是否存在以及是否为对象
    if (data && typeof data === 'object') {
      // 检查是否有code属性
      if (data.hasOwnProperty('code')) {
        // 请求成功
        if (data.code === 200) {
          console.log('请求成功，返回数据:', data);
          return data;
        }
        
        // 请求失败
        const errorMessage = data.message || '请求失败';
        console.error('请求失败，状态码:', data.code, '消息:', errorMessage);
        return Promise.reject(new Error(errorMessage));
      } else {
        // 如果没有code属性，但有data对象，可能直接返回了数据
        console.warn('响应中没有code属性，直接返回data:', data);
        return {
          code: 200,
          message: '操作成功',
          data: data
        };
      }
    } else {
      // 如果响应数据格式不正确
      console.error('响应数据格式不正确:', data);
      return Promise.reject(new Error('响应数据格式不正确: ' + typeof data));
    }
  },
  error => {
    console.error('响应错误详情:', error);
    
    // 处理HTTP错误状态码
    let message = '请求失败';
    if (error.response) {
      console.error('响应错误状态:', error.response.status);
      console.error('响应错误数据:', error.response.data);
      
      // 特别处理400错误
      if (error.response.status === 400) {
        if (error.response.data && error.response.data.message) {
          message = error.response.data.message;
        } else {
          message = '请求参数错误';
        }
      } else {
        switch (error.response.status) {
          case 401:
            message = '未授权，请重新登录';
            // 清除本地存储的token
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            break;
          case 403:
            message = '权限不足';
            break;
          case 404:
            message = '请求的资源不存在';
            break;
          case 500:
            message = '服务器内部错误';
            break;
          default:
            message = `请求失败，状态码：${error.response.status}`;
        }
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时';
    } else if (error.message === 'Network Error') {
      message = '网络连接异常';
    } else if (!error.response) {
      message = '网络连接错误';
    }
    
    console.error(message);
    return Promise.reject(new Error(message));
  }
);

export default request