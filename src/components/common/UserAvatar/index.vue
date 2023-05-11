<script setup lang='ts'>
import { computed, inject, ref, watch } from 'vue'
import { NAvatar } from 'naive-ui'
import { useAuthStore, useUserStore } from '@/store'
import defaultAvatar from '@/assets/avatar.jpg'
import { isString } from '@/utils/is'
import login from '@/components/common/login/index.vue'

// const emit = defineEmits(['showUpgrade'])
const showUpgrade = inject('showUpgrade', Function, true)
const userStore = useUserStore()
const authStore = useAuthStore()
const visibleLogin = ref(false)

const userInfo = computed(() => userStore.userInfo)
const isLogin = computed(() => authStore.isLogin)
watch(isLogin, (newVla) => {
  console.log('newVla', newVla)
})
function upgradeHandler() {
  showUpgrade()
}
function loginHander(type: any) {
  console.log('type', type)
  if (type === 'logout') {
    visibleLogin.value = false
    localStorage.removeItem('token')
    authStore.$patch({
      isLogin: false,
    })
    userStore.resetUserInfo()
  }
  else if (type === 'islogin') {
    visibleLogin.value = false
  }
  else {
    visibleLogin.value = true
  }
}
</script>

<template>
  <div class="flex items-center overflow-hidden">
    <div class="w-10 h-10 overflow-hidden rounded-full shrink-0">
      <template v-if="isString(userInfo.avatar) && userInfo.avatar.length > 0">
        <NAvatar size="large" round :src="userInfo.avatar" :fallback-src="defaultAvatar" />
      </template>
      <template v-else>
        <NAvatar size="large" round :src="defaultAvatar" />
      </template>
    </div>
    <div class="flex-1 min-w-0 ml-2">
      <h2 class="overflow-hidden font-bold text-md text-ellipsis whitespace-nowrap">
        {{ userInfo.username || '' }}
      </h2>
      <p class="overflow-hidden font-bold text-md text-ellipsis whitespace-nowrap">
        每日剩余对话次数{{ userInfo.freeUser ? (userInfo.dailyLimit - userInfo.todayUsedQuota) : ((userInfo.totalQuota - userInfo.usedQuota) || 0) }}次
      </p>
      <p class="overflow-hidden text-gray-500 text-s text-ellipsis whitespace-nowrap" />
      <div class="upgrade-but" @click="upgradeHandler">
        用户升级计划
      </div>

      <h3 v-if="isLogin" class="text-gray-500 log-out" @click="() => loginHander('logout')">
        退出登陆
      </h3>
      <h3 v-else class="text-gray-500 log-out" @click="() => loginHander('loghin')">
        登陆账户
      </h3>
    </div>
  </div>
  <login :visible-login="visibleLogin" @loginHander="loginHander" />
</template>

<style scoped>
.upgrade-but {
  width: fit-content;
  border-radius: 2px;
  border: 1px solid #4b9e5f;
  padding: 2px 4px;
  cursor: pointer;
}

.log-out {
  margin-top: 10px;
  cursor: pointer;
  text-decoration: underline;
}
</style>
