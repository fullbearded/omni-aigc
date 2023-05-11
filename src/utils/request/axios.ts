import axios from 'axios'
import { useMessage } from 'naive-ui'
const messageAu = useMessage()

const service = axios.create({
  baseURL: import.meta.env.VITE_GLOB_API_URL,
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token)
      config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => {
    return Promise.reject(error.response)
  },
)

service.interceptors.response.use((response: any) => {
  if (response.status === 200)
    return response.data

  // throw new Error(response.status.toString())
},
(error) => {
  // messageAu.warning(error.response.data.message)
  if (error.response && error.response.data)
    alert(`${error.response.data.message}`)

  return Promise.reject(error.response)
},
)

export default service
