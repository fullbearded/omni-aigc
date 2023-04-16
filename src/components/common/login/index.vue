<script lang="ts" setup>
import { ref, watch, reactive, onMounted, computed} from 'vue'
import { FormInst, NForm, NModal, NInput, NGrid, NFormItemGi, NButton, NSpace, useMessage } from 'naive-ui'
import { api } from '@/api/index'
import request from '@/utils/request/index'
import { useAuthStore, useUserStore } from '@/store';
let authStore = useAuthStore()
let visibleSate = ref(false)
let loginStatu = ref(true)
let messageAu = useMessage()
const userStore = useUserStore()
let formRef = ref<FormInst | null>(null)
let figistForm = ref<FormInst | null>(null)
// let props = defineProps(['visibleLoginProps'])
interface Props {
    visibleLogin: boolean
}
let props = defineProps<Props>()
let emit = defineEmits(['loginHander'])
let formLogin = reactive({
    account: '',
    password: ''
})


let formRegist = reactive({
    account: '',
    password: '',
    passwordCheck: '',
    mobile: ''
})
let rules = {
    account: {
        required: true,
        message: '请输入姓名',
        trigger: 'blur'
    },
    password: {
        required: true,
        message: '请输入密码',
        trigger: 'blur'
    }

}
const message = '不能为空'
let ruleAccount0 = {
    required: true,
    trigger: ['blur'],
    validator() {
        if (!formLogin.account) {
            return new Error(message)
        }
    }
}
let rulePassword0 = {
    required: true,
    trigger: ['blur'],
    validator() {
        if (!formLogin.password) {
            return new Error(message)
        }
    }
}
let ruleAccount1 = {
    required: true,
    trigger: ['blur'],
    validator() {
        if (!formRegist.account) {
            return new Error(message)
        }
    }
}
let rulePassword1 = {
    required: true,
    trigger: ['blur'],
    validator() {
        if (!formRegist.password) {
            return new Error(message)
        }
    }
}
let rulePasswordCheck = {
    required: true,
    trigger: ['blur'],
    validator(rule: any, value: any) {
        let { password, passwordCheck } = formRegist
        if (!passwordCheck) {
            return new Error('密码不能为空')
        } else if (password !== passwordCheck) {
            return new Error('两次输入的密码不一致')
        }
        return true
    }
}
let ruleMobile1 = {
    required: true,
    trigger: ['blur'],
    validator(rule: any, value: any) {
        let mobileTest = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/
        let { mobile } = formRegist
        if (!mobile) {
            return new Error('手机号不能为空')
        } else if (!mobileTest.test(mobile)) {
            return new Error('请输入正确的手机号码')
        }
        return true
    }
}


watch(() => props.visibleLogin, (newValue) => {
    console.log('newValue', newValue)
    visibleSate.value = newValue
})
function closeMOdal() {
    loginStatu.value = true
    formLogin = reactive({
        account: '',
        password: ''
    })
    formRegist = reactive({
        account: '',
        password: '',
        passwordCheck: '',
        mobile: ''
    })
    emit('loginHander', 'islogin')
}
function login() {
    formRef.value?.validate((error) => {
        if (!error) {
            let { password, account } = formLogin
            request({
                url: api.login,
                data: {
                    username: account,
                    password
                }
            }).then(res => {
                console.log(res)
                if (res.status === 200) {
                    messageAu.success('登陆成功！')
                    localStorage.setItem('token', res.data.token)
                    userStore.getUserInfor()
                    authStore.$patch({
                        isLogin: true
                    })
                    closeMOdal()
                } else {
                    messageAu.warning(res.message)
                }
            }).catch(error => {
                console.log(error.response)
              

            })
        }
    })
}

// 用户注册
function regist() {
    figistForm.value?.validate((error) => {
        if (!error) {
            let { mobile, password, account } = formRegist
            request({
                url: api.rigist,
                data: {
                    username: account,
                    mobile,
                    password
                }
            }).then(res => {
                console.log(res)
                messageAu.success('注册成功！')
                visibleSate.value = false
            }).catch(error => {
                messageAu.warning(error.message)
            })
        }
    })
}
</script>
<template>
    <NModal v-model:show="visibleSate" style="width: 40%; max-width: 440px" preset="card" :onClose="closeMOdal"
        :mask-closable="false">
        <div v-if="loginStatu">
            <n-form ref="formRef" inline :label-width="60" :model="formLogin" label-placement="left" :rules="rules">
                <n-grid cols="12" :y-gap="6" :x-gap="12" responsive="self" :itemResponsive="true">
                    <n-form-item-gi :span="12" label="账号:" path="formLogin.account" :rule="ruleAccount0">
                        <n-input v-model:value="formLogin.account" placeholder="请输入账号" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="12" label="密码:" path="formLogin.password" :rule="rulePassword0">
                        <n-input v-model:value="formLogin.password" placeholder="请输入密码" />
                    </n-form-item-gi>
                </n-grid>
            </n-form>
            <n-space vertical>
                <n-button type="primary" :block="true" @click="login">
                    登录
                </n-button>
                <n-button type="tertiary" :block="true" @click="loginStatu = false">
                    还没有账号立即注册
                </n-button>
            </n-space>
        </div>
        <div v-else>
            <n-form ref="figistForm" inline :label-width="90" :model="formRegist" label-placement="left">
                <n-grid cols="12" :y-gap="6" :x-gap="12" responsive="self" :itemResponsive="true">
                    <n-form-item-gi :span="12" label="账号:" path="formRegist.account" :rule="ruleAccount1">
                        <n-input v-model:value="formRegist.account" placeholder="请输入账号" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="12" label="手机号:" path="formRegist.mobile" :rule="ruleMobile1">
                        <n-input v-model:value="formRegist.mobile" placeholder="请输入手机号码" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="12" label="密码:" path="formRegist.password" :rule="rulePassword1">
                        <n-input v-model:value="formRegist.password" placeholder="请输入密码" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="12" label="确认密码:" path="formRegist.passwordCheck" :rule="rulePasswordCheck">
                        <n-input v-model:value="formRegist.passwordCheck" placeholder="请再次输入密码" />
                    </n-form-item-gi>
                </n-grid>
            </n-form>
            <n-space vertical>
                <n-button type="primary" :block="true" @click="regist">
                    注册
                </n-button>
                <n-button type="tertiary" :block="true" @click="loginStatu = true">
                    返回登陆
                </n-button>
            </n-space>
        </div>
    </NModal>
</template>

<style scoped></style>