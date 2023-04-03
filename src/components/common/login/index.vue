<script lang="ts" setup>
import { ref, watch, reactive } from 'vue'
import { NForm, NModal, NInput, NGrid, NFormItemGi, NButton, NSpace, FormInst } from 'naive-ui'
let visibleSate = ref(false)
let loginStatu = ref(true)
let formRef = ref<FormInst | null>(null)
let props = defineProps(['visibleLogin'])
let emit = defineEmits(['loginHander'])
let formLogin = reactive({
    account: '',
    password: ''
})


let formRegist = reactive({
    account: '',
    password: '',
    passwordCheck: ''
})
let rules = {
    account: {
        required: true,
        message: '请输入姓名',
        trigger: 'blur'
    },
    password: {
        required: true,
    },
    passwordCheck: {
        required: true,
    },
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
    validator() {
        if (!formRegist.passwordCheck) {
            return new Error(message)
        }
    }
}
watch(() => props.visibleLogin, (newValue) => {
    visibleSate.value = newValue
})
function closeMOdal() {
    formLogin = reactive({
    account: '',
    password: ''
})
    formRegist = reactive({
    account: '',
    password: '',
    passwordCheck: ''
})
    emit('loginHander')
}
function login(){
    console.log(formLogin)
}
function regist(){
    console.log(formRegist)
}
</script>
<template>
    <NModal v-model:show="visibleSate" style="width: 40%; max-width: 440px" preset="card" :onClose="closeMOdal">
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
            <n-form inline :label-width="90" :model="formRegist" label-placement="left">
                <n-grid cols="12" :y-gap="6" :x-gap="12" responsive="self" :itemResponsive="true">
                    <n-form-item-gi :span="12" label="账号:" path="formRegist.account" :rule="ruleAccount1">
                        <n-input v-model:value="formRegist.account" placeholder="请输入账号" />
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