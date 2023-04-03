<script setup lang='ts'>
import { computed, defineEmits, inject, ref} from 'vue'
import { NAvatar } from 'naive-ui'
import { useUserStore } from '@/store'
import defaultAvatar from '@/assets/avatar.jpg'
import { isString } from '@/utils/is'
import login from '@/components/common/login/index.vue'

// const emit = defineEmits(['showUpgrade'])
const showUpgrade = inject('showUpgrade', Function, true)
const userStore = useUserStore()
let visibleLogin = ref(false)
const userInfo = computed(() => userStore.userInfo)
function upgradeHandler(){
  showUpgrade()
}
function loginHander(){
  visibleLogin.value = !visibleLogin.value
}
</script>

<template>
  <div class="flex items-center overflow-hidden">
    <div class="w-10 h-10 overflow-hidden rounded-full shrink-0">
      <template v-if="isString(userInfo.avatar) && userInfo.avatar.length > 0">
        <NAvatar
          size="large"
          round
          :src="userInfo.avatar"
          :fallback-src="defaultAvatar"
        />
      </template>
      <template v-else>
        <NAvatar size="large" round :src="defaultAvatar" />
      </template>
    </div>
    <div class="flex-1 min-w-0 ml-2">
      <h2 class="overflow-hidden font-bold text-md text-ellipsis whitespace-nowrap">
        {{ userInfo.name || 'ChenZhaoYu' }}
      </h2>
      <p class="overflow-hidden font-bold text-md text-ellipsis whitespace-nowrap">
        每日剩余对话次数0次
      </p>
      <p class="overflow-hidden text-s text-gray-500 text-ellipsis whitespace-nowrap">
        <div class="upgrade-but" @click="upgradeHandler">
          用户升级计划
        </div>
        
      </p>
      <h3 class="log-out  text-gray-500" @click="loginHander">
        退出登陆
      </h3>
    </div>
  </div>
  <login :visibleLogin="visibleLogin" @loginHander="loginHander"></login>
</template>
<style scoped>
 .upgrade-but{
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
