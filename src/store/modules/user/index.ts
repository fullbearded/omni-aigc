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
        console.log('catch', res)
        
      })
    },
    
    resetUserInfo() {
      this.userInfo = { ...defaultSetting().userInfo }
      this.recordState()
    },

    recordState() {
      setLocalState(this.$state)
    },
  },
})
