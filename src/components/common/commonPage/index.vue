<template>
    <div class="common-page">
        <p class="doc">
        <div>AI交流群</div> OR <div>使用文档</div>
        </p>
        <p class="header">AIPRM - 提示模板</p>
        <div class="select-form">
            <n-form inline :label-width="80" :model="formValue">
                <n-grid cols="10 400:12 600:24" :x-gap="12" responsive="self" :itemResponsive="true">
                    <n-form-item-gi :span="5" label="主题" path="formValue.subject">
                        <n-select v-model:value="formValue.subject" placeholder="请选择" :options="subjectList"
                            value-field="id" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="5" label="活动" path="formValue.activity">
                        <n-select v-model:value="formValue.activity" placeholder="请选择" :options="activityList"
                            value-field="id" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="5" label="排序" path="formValue.order">
                        <n-select v-model:value="formValue.order" placeholder="请选择" :options="orderList" />
                    </n-form-item-gi>
                    <n-form-item-gi :span="5" label="搜索" path="formValue.keyword">
                        <n-input v-model:value="formValue.keyword" placeholder="请输入关键字" />
                    </n-form-item-gi>
                </n-grid>
            </n-form>
        </div>
        <div class="artic-content">
            <div class="artics">
                <div class="artic" v-for="(item, index) in currentArtics" :key="index" @click="selectedPrompt(item)">
                    <p class="title-2">{{ transformData(item.title) }}</p>
                    <p class="title-4">{{ transformData(item.community, 'topic') }} / {{ transformData(item.category) }}</p>
                    <p class="title-4">{{ item.authorName }} · {{ transformData(item.revisionTime, 'time') }}</p>
                    <p class="title-3">{{ transformData(item.teaser) }}</p>
                    <p class="title-4 use-info">
                        <span>查看 {{ item.views / 1000 }} k</span>
                        <span>使用 {{ item.usages / 1000 }} k</span>
                        <span>评论 {{ item.votes / 1000 }} k</span>
                    </p>
                    <p class="title-4 copy-link">复制链接</p>
                </div>
            </div>
            <div class="pages">
                <n-pagination v-model:page="page" :page-count="pageCount" size="large" :on-update:page="changePage" />
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, onMounted, watchEffect, watch } from 'vue'
import moment from 'moment'
import DSDATA from '@/assets/ds.json'
import { NForm, NInput, NSelect, useMessage, NGrid, NFormItemGi, NPagination } from 'naive-ui'
import { useChatStore } from '@/store';
const subjectList = DSDATA.topics
const transformDataBasic = DSDATA.transform
let chat = useChatStore()
let activityList = ref(DSDATA.activities)

let artics = DSDATA.prompts

const orderList = [{
    "value": "1",
    "label": "最高查看"
}, {
    "value": "2",
    "label": "最高使用"
}, {
    "value": "3",
    "label": "最高评论"
}]
let formValue = ref({
    subject: null,
    activity: null,
    order: '1',
    keyword: ''
})
let serch: any
let currentArtics = ref([])
let page = ref(1)
let pageCount = ref(1)
onMounted(() => {
    getCurrentArticsPages()
    activityList.value = activityList.value.map(item => {
        return {
            ...item,
            label: transformData(item.label)
        }

    })
    getCurrentArtics()
})
watch(formValue.value, (newValue, oldValue) => {
    let { subject, activity, order, keyword } = newValue
    serch && clearTimeout(serch)
    serch = setTimeout(() => {
        console.log('sssdasd')
    
        let prompts = DSDATA.prompts
        if (subject) {
            prompts = prompts.filter(item => (item.community == subject))
        }
        if (activity) {
            prompts = prompts.filter(item => (item.category == activity))
        }
        if (keyword) {
            prompts = prompts.map(item => ({
                ...item,
                CNteaser: transformData(item.teaser)
            }))
            prompts = prompts.filter(item => (item.CNteaser.indexOf(keyword) > 0))
        }
        if (order == 1) {
            prompts = prompts.sort((prev, next) => (next.views - prev.views))
        } else if (order == 2) {
            prompts = prompts.sort((prev, next) => (next.usages - prev.usages))
        } else {
            prompts = prompts.sort((prev, next) => (next.votes - prev.votes))
        }
        artics = prompts
        changePage(1)
        getCurrentArticsPages()
    }, 500)
})
function transformData(keyword, type) {
    if (type && type == 'topic') {
        return subjectList.filter(item => item.id == keyword)[0].label
    } else if (type && type == 'time') {
        return moment(keyword).format('YYYY-MM-DD HH:mm:ss')
    } else if (type && type == 'prompt') {
        keyword = keyword.replaceAll('[TARGETLANGUAGE]', 'In Chinese')
        return keyword.replaceAll('"[PROMPT]"', '.')
    }
    return transformDataBasic.filter(item => item.k.toUpperCase() == keyword.toUpperCase())[0].v
}
function changePage(value) {
    page.value = value
    getCurrentArtics()
}
function getCurrentArtics() {
    currentArtics.value = artics.slice((page.value - 1) * 4, (page.value - 1) * 4 + 4)
}
function getCurrentArticsPages() {
    pageCount.value = Math.ceil(artics.length / 4)
}
function selectedPrompt(prompt) {
    console.log(prompt)
    chat.getCurrentPrompt(prompt)
}
</script>
<style scoped>
.common-page {
    color: black;
    width: 100%;
}

.doc {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-bottom: 12px;
}

.doc div {
    padding: 0 6px;
    border: 1px solid #18a058;
    border-radius: 4px;
    margin: 0 6px;
    color: #18a058;
    cursor: pointer;
}

.header {
    font-size: 1.8rem;
    text-align: center;
    padding-bottom: 2rem;
}

.artics {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-gap: 1.5rem;
}

.artic {
    background-color: rgb(250, 250, 252);
    padding: 1.5rem;
    cursor: pointer;
}
.artic:hover {
    box-shadow: 1px 1px 20px #c0ccdc;
}
.artic p {
    text-align: left;
    padding: 6px 0;
}

.title-2 {
    font-size: 1.8rem;
    font-weight: 500;
}

.title-4 {
    font-size: 0.75rem;
    color: rgb(118, 124, 130);
    line-height: 2rem;
}

.use-info {
    display: flex;
    justify-content: space-between;
}

.title-4.copy-link {
    text-align: right;
}

.pages {
    padding: 15px;
    display: flex;
    justify-content: flex-end;
}</style>