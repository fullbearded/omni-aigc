<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import type { FormInst } from 'naive-ui'
import { NButton, NForm, NFormItemGi, NGrid, NInput, NModal, NSpace, useMessage } from 'naive-ui'
import { api } from '@/api/index'
import request from '@/utils/request/index'
import { useAuthStore, useUserStore } from '@/store'
const props = defineProps<Props>()
const emit = defineEmits(['loginHander'])
const authStore = useAuthStore()
const visibleSate = ref(false)
const loginStatu = ref(true)
const messageAu = useMessage()
const userStore = useUserStore()
const formRef = ref<FormInst | null>(null)
const figistForm = ref<FormInst | null>(null)
// let props = defineProps(['visibleLoginProps'])
interface Props {
  visibleLogin: boolean
}
let formLogin = reactive({
  account: '',
  password: '',
})

let formRegist = reactive({
  account: '',
  password: '',
  passwordCheck: '',
  mobile: '',
})
const rules = {
  account: {
    required: true,
    message: '请输入姓名',
    trigger: 'blur',
  },
  password: {
    required: true,
    message: '请输入密码',
    trigger: 'blur',
  },

}
const message = '不能为空'
const ruleAccount0 = {
  required: true,
  trigger: ['blur'],
  validator() {
    if (!formLogin.account)
      return new Error(message)
  },
}
const rulePassword0 = {
  required: true,
  trigger: ['blur'],
  validator() {
    if (!formLogin.password)
      return new Error(message)
  },
}
const ruleAccount1 = {
  required: true,
  trigger: ['blur'],
  validator() {
    if (!formRegist.account)
      return new Error(message)
  },
}
const rulePassword1 = {
  required: true,
  trigger: ['blur'],
  validator() {
    if (!formRegist.password)
      return new Error(message)
  },
}
const rulePasswordCheck = {
  required: true,
  trigger: ['blur'],
  validator(rule: any, value: any) {
    const { password, passwordCheck } = formRegist
    if (!passwordCheck)
      return new Error('密码不能为空')
    else if (password !== passwordCheck)
      return new Error('两次输入的密码不一致')

    return true
  },
}
const ruleMobile1 = {
  required: true,
  trigger: ['blur'],
  validator(rule: any, value: any) {
    const mobileTest = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
    const { mobile } = formRegist
    if (!mobile)
      return new Error('手机号不能为空')
    else if (!mobileTest.test(mobile))
      return new Error('请输入正确的手机号码')

    return true
  },
}

watch(() => props.visibleLogin, (newValue) => {
  console.log('newValue', newValue)
  visibleSate.value = newValue
})
function closeMOdal() {
  loginStatu.value = true
  formLogin = reactive({
    account: '',
    password: '',
  })
  formRegist = reactive({
    account: '',
    password: '',
    passwordCheck: '',
    mobile: '',
  })
  emit('loginHander', 'islogin')
}
function login() {
  formRef.value?.validate((error) => {
    if (!error) {
      const { password, account } = formLogin
      request({
        url: api.login,
        data: {
          username: account,
          password,
        },
      }).then((res) => {
        console.log(res)
        if (res.status === 200) {
          messageAu.success('登陆成功！')
          localStorage.setItem('token', res.data.token)
          userStore.getUserInfor()
          authStore.$patch({
            isLogin: true,
          })
          closeMOdal()
        }
        else {
          messageAu.warning(res.message)
        }
      }).catch((error) => {
        console.log(error.response)
      })
    }
  })
}

// 用户注册
function regist() {
  figistForm.value?.validate((error) => {
    if (!error) {
      const { mobile, password, account } = formRegist
      request({
        url: api.rigist,
        data: {
          username: account,
          mobile,
          password,
        },
      }).then((res) => {
        messageAu.success('注册成功！')
        // visibleSate.value = false
      }).catch((error) => {
        messageAu.warning(error.message)
      })
    }
  })
}
</script>

<template>
  <NModal
    v-model:show="visibleSate" style="width: 40%; max-width: 440px" preset="card" :on-close="closeMOdal"
    :mask-closable="false"
  >
    <div v-if="loginStatu">
      <NForm ref="formRef" inline :label-width="60" :model="formLogin" label-placement="left" :rules="rules">
        <NGrid cols="12" :y-gap="6" :x-gap="12" responsive="self" :item-responsive="true">
          <NFormItemGi :span="12" label="账号:" path="formLogin.account" :rule="ruleAccount0">
            <NInput v-model:value="formLogin.account" placeholder="请输入账号" />
          </NFormItemGi>
          <NFormItemGi :span="12" label="密码:" path="formLogin.password" :rule="rulePassword0">
            <NInput v-model:value="formLogin.password" placeholder="请输入密码" />
          </NFormItemGi>
        </NGrid>
      </NForm>
      <NSpace vertical>
        <NButton type="primary" :block="true" @click="login">
          登录
        </NButton>
        <NButton type="tertiary" :block="true" @click="loginStatu = false">
          还没有账号立即注册
        </NButton>
      </NSpace>
    </div>
    <div v-else>
      <NForm ref="figistForm" inline :label-width="90" :model="formRegist" label-placement="left">
        <NGrid cols="12" :y-gap="6" :x-gap="12" responsive="self" :item-responsive="true">
          <NFormItemGi :span="12" label="账号:" path="formRegist.account" :rule="ruleAccount1">
            <NInput v-model:value="formRegist.account" placeholder="请输入账号" />
          </NFormItemGi>
          <NFormItemGi :span="12" label="手机号:" path="formRegist.mobile" :rule="ruleMobile1">
            <NInput v-model:value="formRegist.mobile" placeholder="请输入手机号码" />
          </NFormItemGi>
          <NFormItemGi :span="12" label="密码:" path="formRegist.password" :rule="rulePassword1">
            <NInput v-model:value="formRegist.password" placeholder="请输入密码" />
          </NFormItemGi>
          <NFormItemGi :span="12" label="确认密码:" path="formRegist.passwordCheck" :rule="rulePasswordCheck">
            <NInput v-model:value="formRegist.passwordCheck" placeholder="请再次输入密码" />
          </NFormItemGi>
        </NGrid>
      </NForm>
      <NSpace vertical>
        <NButton type="primary" :block="true" @click="regist">
          注册
        </NButton>
        <NButton type="tertiary" :block="true" @click="loginStatu = true">
          返回登陆
        </NButton>
      </NSpace>
    </div>
  </NModal>
</template>

<style scoped></style>
