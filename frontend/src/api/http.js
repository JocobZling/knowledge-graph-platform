import axios from 'axios'

const http = axios.create({ baseURL: '/api', timeout: 10000 })

http.interceptors.response.use((response) => {
  const body = response.data
  if (body && body.code === 0) return body.data
  return Promise.reject(new Error(body?.message || 'request failed'))
})

export default http
