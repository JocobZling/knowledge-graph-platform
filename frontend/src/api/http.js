import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 10000 })
const friendlyError = '操作没有完成，请稍后重试'

http.interceptors.response.use((response) => {
  const body = response.data
  if (body && body.code === 0) return body.data
  return Promise.reject(new Error(friendlyError))
}, () => Promise.reject(new Error(friendlyError)))

export default http
