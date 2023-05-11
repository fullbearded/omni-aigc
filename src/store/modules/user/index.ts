import { defineStore } from 'pinia'
import type { UserInfo, UserState } from './helper'
import { defaultSetting, getLocalState, setLocalState } from './helper'
import { api } from '@/api/index'
import request from '@/utils/request/index'

export const useUserStore = defineStore('user-store', {
  state: (): UserState => getLocalState(),
  actions: {
    updateUserInfo(userInfo: Partial<UserInfo>) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      this.recordState()
    },
    // 获取用户信息
    getUserInfor() {
      request({
        url: api.getUserInfo
      }).then(res => {
        console.log(res)
        localStorage.setItem('user', JSON.stringify(res.data))
        this.userInfo = res.data
      }).catch(res => {
      	if(res.status === 403) {
      		alert("权限错误，请重新登陆")
				}
        console.log('catch', res)
      })
    },
<<<<<<< HEAD
    
=======

>>>>>>> 69bf2f4e94546b07459aa1c6a6dc46dd6e50d58f
    resetUserInfo() {
      this.userInfo = { ...defaultSetting().userInfo }
      this.recordState()
    },

    recordState() {
      setLocalState(this.$state)
    },
  },
})
