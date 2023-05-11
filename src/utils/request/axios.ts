<<<<<<< HEAD
import axios from 'axios'
import { useMessage } from 'naive-ui'
const messageAu = useMessage()
=======
import axios, {type AxiosResponse} from 'axios'
import {useMessage} from 'naive-ui'

let messageAu = useMessage()
>>>>>>> 69bf2f4e94546b07459aa1c6a6dc46dd6e50d58f

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
<<<<<<< HEAD
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
=======
		console.log('response', response.status)
		if (response.status === 200) {
			return response.data
		}
		// throw new Error(response.status.toString())
	},
	(error) => {
		if (error.response.status === 500) {
			alert('服务器错误')
		} else {
			console.log('error.response', error.response, messageAu)
			// messageAu.warning(error.response.data.message)
			if (error.response && error.response.data) {
				let data = {}
				try {
					data = JSON.parse(error.response.data)
				} catch (error) {
					console.log(error)
					data = {}
				}
				alert(data.message ? data.message : `${error.response.data.message}`)
			}
		}


		return Promise.reject(error.response)
	},
>>>>>>> 69bf2f4e94546b07459aa1c6a6dc46dd6e50d58f
)

export default service
