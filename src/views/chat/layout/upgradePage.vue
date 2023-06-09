<script setup lang='ts'>
import { computed, ref, watch } from 'vue'
import { NButton, NInput, NInputGroup, NModal, useMessage } from 'naive-ui'
import moment from 'moment'
import { api } from '@/api/index'
import request from '@/utils/request/index'
import { useUserStore } from '@/store'
interface Props {
  visible: boolean
}
const props = defineProps<Props>()
const emit = defineEmits(['showUpgrade'])
const visibleSate = ref(false)
const convertCode = ref('')
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)
const messageAu = useMessage()
const orderList = ref([{
  name: '￥8.99(次数体验版)',
  sigleTimes: 100,
  limitTime: 31,
  totalTimes: 100,
  orderKey: 1,
}, {
  name: '￥19.99(月度体验版)',
  sigleTimes: 100,
  limitTime: 31,
  totalTimes: 3100,
  orderKey: 2,
}, {
  name: '￥29.99(次数基础版)',
  sigleTimes: 600,
  limitTime: 31,
  totalTimes: 600,
  orderKey: 3,
}, {
  name: '￥49.99(月度基础版)',
  sigleTimes: 240,
  limitTime: 31,
  totalTimes: 7440,
  orderKey: 4,
}, {
  name: '￥99.99(月度畅聊版)',
  sigleTimes: 600,
  limitTime: 31,
  totalTimes: 18600,
  orderKey: 5,
}, {
  name: '￥159.99(月度专业版)',
  sigleTimes: 1200,
  limitTime: 31,
  totalTimes: 37200,
  orderKey: 6,
}])

watch(() => props.visible, (newValue) => {
  console.log('newValue', newValue)
  visibleSate.value = newValue
})
function closeMOdal() {
  emit('showUpgrade')
}
function convert() {
  const code = convertCode.value
  request({
    url: api.convertCharge,
    data: {
      code,
      userCode: userInfo.value.code || '',
      channel: 'web',
    },
  }).then((res) => {
    if (res.status === 200)
      messageAu.success('兑换成功')

    else
      messageAu.warning(data.message)
  }).catch((error) => {
    console.log('response', error)
    messageAu.warning(error.response.data.message)
  })
}
</script>

<template>
  <NModal
    v-model:show="visibleSate" style="width: 90%; max-width: 640px" preset="card" :on-close="closeMOdal"
    :mask-closable="false"
  >
    <div class="bg-white rounded dark:bg-slate-800">
      <div class="header">
        用户升级计划
        <div class="close-icon" />
      </div>
      <div class="contain">
        <div class="tips-box">
          <p>
            尊敬的 {{ userInfo.username }} 您好，您今日剩余可用AI问答次数为：<span class="title-3">{{ userInfo.freeUser ? userInfo.daliyLimit
              - userInfo.usedQuota : 0 }}</span> 次
          </p>
          <p>尊敬的 {{ userInfo.username }} 若您还需要更多的次数以满足您的使用需求，可以选择以下合适的套餐~ 次</p>
          <p>以下套餐若在过程中出现无结果或异常，将不会扣除您的次数，尽请放心。</p>
        </div>
        <div class="code-box">
          <NInputGroup>
            <NInput v-model:value="convertCode" :style="{ width: '50%' }" placeholder="请输入兑换码进行兑换" />
            <NButton type="primary" ghost @click="convert">
              兑换
            </NButton>
          </NInputGroup>
        </div>
        <div class="pay-box">
          <p class="title-1">
            已激活计划
          </p>
          <p>
            普通用户 - {{ moment(userInfo.expireDate).format('YYYY-MM-DD') }}到期 - 使用{{ userInfo.usedQuota }} / 总量{{
              userInfo.totalQuota }}
          </p>
          <div class="pay-box-items">
            <div v-for="(item, index) in orderList" :key="index" class="pay-item">
              <div class="title-2">
                {{ item.name }}
              </div>
              <div class="">
                单次：{{ item.sigleTimes }}<span class="title-3"> 次 </span>
              </div>
              <div class="">
                有效期：{{ item.limitTime }} <span class="title-3"> 天 </span>
              </div>
              <div class="">
                总次数：{{ item.totalTimes }} <span class="title-3"> 次 </span>
              </div>
              <div class="">
                问答：单次<span class="title-3">无限制</span>(4096)字符
              </div>
              <div class="">
                优化：若遇不可抗力的故障临时方案不扣次数
              </div>
              <div class="pay-but">
                立即购买
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="footer">
        以上套餐均可重复多次叠加购买
      </div>
    </div>
  </NModal>
</template>

<style scoped>
.header {
    padding: 0 6.0rem 1.5rem;
    line-height: 3rem;
    text-align: center;
    border-bottom: 1px solid #eee;
    position: relative;
    font-size: 18px;
    font-weight: 600;
}

.close-icon {
    position: absolute;
    right: 10px;
    top: 10px;
}

.contain {}

.tips-box {
    padding: 10px 0;
    border-bottom: 1px solid #eee;
}

.tips-box p {
    line-height: 3rem;
}

.code-box {
    padding: 1.5rem 0;
}

.title-1 {
    font-weight: 600;
    line-height: 3rem;
}

.title-2 {
    /* font-size: 18px; */
    font-weight: 600;
    padding-bottom: 2.0rem;
}

.title-3 {
    font-weight: 600;
}

.pay-box-items {
    padding: 0.3rem 0;
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-gap: 10px;
    border-top: 1px solid #eee;
}

.pay-item {
    padding: 15px;
    /* width: calc(30% - 5px); */
    background-color: rgb(250, 250, 252);
}

.pay-but {
    border-radius: 2px;
    border: 1px solid #4b9e5f;
    padding: 2px 4px;
    cursor: pointer;
    text-align: center;
    margin: 0.1rem 0;
    color: #4b9e5f;
}

.footer {
    text-align: center;
    margin: 0.1rem 0;
    color: #4b9e5f;
}
</style>
