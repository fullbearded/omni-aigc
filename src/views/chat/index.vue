<script setup lang='ts'>
import { reactive, Ref } from 'vue'
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { NAutoComplete, NButton, NInput, useDialog, useMessage, NForm, NGrid, NFormItemGi, NSelect } from 'naive-ui'
// import { NForm, NInput, NSelect, useMessage, NGrid, NFormItemGi, NPagination } from 'naive-ui'
import html2canvas from 'html2canvas'
import { Message } from './components'
import { useScroll } from './hooks/useScroll'
import { useChat } from './hooks/useChat'
import { useCopyCode } from './hooks/useCopyCode'
import { useUsingContext } from './hooks/useUsingContext'
import HeaderComponent from './components/Header/index.vue'
import commonPage from '@/components/common/commonPage/index.vue'

import { HoverButton, SvgIcon } from '@/components/common'
import { useBasicLayout } from '@/hooks/useBasicLayout'
import { useChatStore, usePromptStore } from '@/store'
import { transformData } from '@/utils/functions'
import { fetchChatAPIProcess } from '@/api'
import { t } from '@/locales'

let controller = new AbortController()

const openLongReply = import.meta.env.VITE_GLOB_OPEN_LONG_REPLY === 'true'

const route = useRoute()
const dialog = useDialog()
const ms = useMessage()

const chatStore = useChatStore()

useCopyCode()

const { isMobile } = useBasicLayout()
const { addChat, updateChat, updateChatSome, getChatByUuidAndIndex } = useChat()
const { scrollRef, scrollToBottom, scrollToBottomIfAtBottom } = useScroll()
const { usingContext, toggleUsingContext } = useUsingContext()

const { uuid } = route.params as { uuid: string }

const dataSources = computed(() => chatStore.getChatByUuid(+uuid))
const currentPrompt = computed(() => chatStore.prompt)

const conversationList = computed(() => dataSources.value.filter(item => (!item.inversion && !item.error)))

const prompt = ref<string>('')
const completPrompt = ref<string>('')
const promptPlaceholder = ref<string>('')
const loading = ref<boolean>(false)
const inputRef = ref<Ref | null>(null)
let precessForm = ref({
  languge: 'In Chinese'
})
const langugeList = [{
  value: 'In Chinese',
  label: '中文'
}, {
  value: 'In English',
  label: 'English'
}]
const toneList = [{
  value: '权威',
  label: '权威'
}, {
  value: '冷淡',
  label: '冷淡'
}, {
  value: '冷漠',
  label: '冷漠'
}, {
  value: '自信',
  label: '自信'
}, {
  value: '愤世嫉俗',
  label: '愤世嫉俗'
}, {
  value: '感染力',
  label: '感染力'
}, {
  value: '共情',
  label: '共情'
}, {
  value: '庄重',
  label: '庄重'
}, {
  value: '友好',
  label: '友好'
}, {
  value: '幽默',
  label: '幽默'
}, {
  value: '随便',
  label: '随便'
}, {
  value: '挖苦',
  label: '挖苦'
}, {
  value: '乐观',
  label: '乐观'
}, {
  value: '悲观',
  label: '悲观'
}, {
  value: '有趣',
  label: '有趣'
}]
const textTyleList = [{
  value: '学术',
  label: '学术'
}, {
  value: '善于分析',
  label: '善于分析'
}, {
  value: '爱辩论',
  label: '爱辩论'
}, {
  value: '非正式',
  label: '非正式'
}, {
  value: '有创造力',
  label: '有创造力'
}, {
  value: '批评',
  label: '批评'
}, {
  value: '说明',
  label: '说明'
}, {
  value: '禁句式',
  label: '禁句式'
}, {
  value: '书信',
  label: '书信'
}, {
  value: '解释',
  label: '解释'
}, {
  value: '更多信息',
  label: '更多信息'
}, {
  value: '富有教益',
  label: '富有教益'
}, {
  value: '新闻业',
  label: '新闻业'
}, {
  value: '隐喻',
  label: '隐喻'
}, {
  value: '叙述',
  label: '叙述'
}, {
  value: '有说服力',
  label: '有说服力'
}, {
  value: '诗歌',
  label: '诗歌'
}, {
  value: '讽刺',
  label: '讽刺'
}]

// 添加PromptStore
const promptStore = usePromptStore()

// 使用storeToRefs，保证store修改后，联想部分能够重新渲染
const { promptList: promptTemplate } = storeToRefs<any>(promptStore)

// 未知原因刷新页面，loading 状态不会重置，手动重置
dataSources.value.forEach((item, index) => {
  if (item.loading)
    updateChatSome(+uuid, index, { loading: false })
})

function handleSubmit() {
  console.log(conversationList)
  let reqList = []
  loading.value = false
  conversationList.value.forEach(item => {
    reqList.push({
      role: "user",
      content: item.requestOptions.prompt
    })
    reqList.push({
      role: "assistant",
      content: item.text
    })
  }
  )
  console.log(reqList)
  onConversation(reqList)
}

function handleReSubmit() {
  console.log(conversationList)
  loading.value = true
  let reqList = []
  conversationList.value.forEach(item => {
    reqList.push({
      role: "user",
      content: item.requestOptions.prompt
    })
    reqList.push({
      role: "assistant",
      content: item.text
    })
  }
  )
  console.log(reqList)
  try {
    let lastText = ''
    const fetchChatAPIOnce = () => {
      fetchChatAPIProcess<Chat.ConversationResponse>({
        messages: [
          ...reqList
        ],
        token: localStorage.getItem('token') || '',
        onDownloadProgress: ({ event }) => {
          const xhr = event.target
          const { responseText } = xhr
          console.log('responseText', responseText)
          if(responseText.status !== 200){
            loading.value = false
          }
          // Always process the final line
          const lastIndex = responseText.lastIndexOf('\n', responseText.length - 2)
          let chunk = responseText
          console.log('lastIndex', lastIndex - responseText.length, lastIndex)

          if (lastIndex !== -1)
            chunk = responseText.substring(0, lastIndex)
          try {
            let chunk1 = chunk.split('\n').filter(item => item)
            let chunk2 = chunk1.map(item => JSON.parse(item.split('data:')[1]).message).join('')

            const data = chunk2
            updateChat(
              +uuid,
              dataSources.value.length - 1,
              {
                dateTime: new Date().toLocaleString(),
                text: lastText + chunk2 ?? '',
                inversion: false,
                error: false,
                loading: true,
                conversationOptions: { conversationId: data.conversationId, parentMessageId: data.id },
                requestOptions: { prompt: message, options: { ...options } },
              },
            )

            if (openLongReply && data.detail.choices[0].finish_reason === 'length') {
              options.parentMessageId = data.id
              lastText = data.text
              message = ''
              return fetchChatAPIOnce()
            }

            scrollToBottomIfAtBottom()
          }
          catch (error) {
            //
          }
        },
      })
    }

    fetchChatAPIOnce()
  }
  catch (error: any) {
    console.log(error)
    const errorMessage = error?.message ?? t('common.wrong')

    if (error.message === 'canceled') {
      updateChatSome(
        +uuid,
        dataSources.value.length - 1,
        {
          loading: false,
        },
      )
      scrollToBottomIfAtBottom()
      return
    }

    const currentChat = getChatByUuidAndIndex(+uuid, dataSources.value.length - 1)

    if (currentChat?.text && currentChat.text !== '') {
      updateChatSome(
        +uuid,
        dataSources.value.length - 1,
        {
          text: `${currentChat.text}\n[${errorMessage}]`,
          error: false,
          loading: false,
        },
      )
      return
    }

    updateChat(
      +uuid,
      dataSources.value.length - 1,
      {
        dateTime: new Date().toLocaleString(),
        text: errorMessage,
        inversion: false,
        error: true,
        loading: false,
        conversationOptions: null,
        requestOptions: { prompt: message, options: { ...options } },
      },
    )
    scrollToBottomIfAtBottom()
  }
  finally {
    loading.value = true
  }
}


async function onConversation(reqList) {
  let message = prompt.value

  if (loading.value)
    return

  if (!message || message.trim() === '')
    return

  controller = new AbortController()
  addChat(
    +uuid,
    {
      dateTime: new Date().toLocaleString(),
      text: message,
      inversion: true,
      error: false,
      conversationOptions: null,
      requestOptions: { prompt: transformData(completPrompt.value, 'prompt', message), options: null },
    },
  )
  scrollToBottom()

  loading.value = true
  prompt.value = ''

  let options: Chat.ConversationRequest = {}
  const lastContext = conversationList.value[conversationList.value.length - 1]?.conversationOptions

  if (lastContext && usingContext.value)
    options = { ...lastContext }

  addChat(
    +uuid,
    {
      dateTime: new Date().toLocaleString(),
      text: '',
      loading: true,
      inversion: false,
      error: false,
      conversationOptions: null,
      requestOptions: { prompt: message, options: { ...options } },
    },
  )
  scrollToBottom()
  console.log('promptv', prompt, completPrompt, message)
  try {
    let lastText = ''
    const fetchChatAPIOnce = () => {
      fetchChatAPIProcess<Chat.ConversationResponse>({
        messages: [
          ...reqList,
          {
            role: "user",
            content: transformData(completPrompt.value, 'prompt', message, precessForm.value.languge) || message
          }
        ],
        token: localStorage.getItem('token') || '',
        onDownloadProgress: ({ event }) => {
          const xhr = event.target
          const { responseText } = xhr
          console.log('responseText', responseText)
          loading.value = false
          // Always process the final line
          const lastIndex = responseText.lastIndexOf('\n', responseText.length - 2)
          let chunk = responseText
          console.log('lastIndex', lastIndex - responseText.length, lastIndex)

          if (lastIndex !== -1)
            chunk = responseText.substring(0, lastIndex)
          try {
            let chunk1 = chunk.split('\n').filter(item => item)
            let chunk2 = chunk1.map(item => JSON.parse(item.split('data:')[1]).message).join('')

            const data = chunk2
            updateChat(
              +uuid,
              dataSources.value.length - 1,
              {
                dateTime: new Date().toLocaleString(),
                text: lastText + chunk2 ?? '',
                inversion: false,
                error: false,
                loading: false,
                conversationOptions: { conversationId: data.conversationId, parentMessageId: data.id },
                requestOptions: { prompt: message, options: { ...options } },
              },
            )

            if (openLongReply && data.detail.choices[0].finish_reason === 'length') {
              options.parentMessageId = data.id
              lastText = data.text
              message = ''
              return fetchChatAPIOnce()
            }

            scrollToBottomIfAtBottom()
          }
          catch (error) {
            //
          }
        },
      })
    }

    fetchChatAPIOnce()
  }
  catch (error: any) {
    const errorMessage = error?.message ?? t('common.wrong')

    if (error.message === 'canceled') {
      updateChatSome(
        +uuid,
        dataSources.value.length - 1,
        {
          loading: false,
        },
      )
      scrollToBottomIfAtBottom()
      return
    }

    const currentChat = getChatByUuidAndIndex(+uuid, dataSources.value.length - 1)

    if (currentChat?.text && currentChat.text !== '') {
      updateChatSome(
        +uuid,
        dataSources.value.length - 1,
        {
          text: `${currentChat.text}\n[${errorMessage}]`,
          error: false,
          loading: false,
        },
      )
      return
    }

    updateChat(
      +uuid,
      dataSources.value.length - 1,
      {
        dateTime: new Date().toLocaleString(),
        text: errorMessage,
        inversion: false,
        error: true,
        loading: false,
        conversationOptions: null,
        requestOptions: { prompt: message, options: { ...options } },
      },
    )
    scrollToBottomIfAtBottom()
  }
  finally {
    // loading.value = false
  }
}

async function onRegenerate(index: number) {
  if (loading.value)
    return

  controller = new AbortController()

  const { requestOptions } = dataSources.value[index]

  let message = requestOptions?.prompt ?? ''

  let options: Chat.ConversationRequest = {}

  if (requestOptions.options)
    options = { ...requestOptions.options }

  loading.value = true

  updateChat(
    +uuid,
    index,
    {
      dateTime: new Date().toLocaleString(),
      text: '',
      inversion: false,
      error: false,
      loading: true,
      conversationOptions: null,
      requestOptions: { prompt: message, ...options },
    },
  )

  try {
    let lastText = ''
    const fetchChatAPIOnce = async () => {
      await fetchChatAPIProcess<Chat.ConversationResponse>({
        prompt: transformData(completPrompt.value, 'prompt', message, precessForm.value.languge),
        options,
        signal: controller.signal,
        onDownloadProgress: ({ event }) => {
          const xhr = event.target
          const { responseText } = xhr
          // Always process the final line
          const lastIndex = responseText.lastIndexOf('\n', responseText.length - 2)
          let chunk = responseText
          if (lastIndex !== -1)
            chunk = responseText.substring(lastIndex)
          try {
            const data = JSON.parse(chunk)
            updateChat(
              +uuid,
              index,
              {
                dateTime: new Date().toLocaleString(),
                text: lastText + data.text ?? '',
                inversion: false,
                error: false,
                loading: false,
                conversationOptions: { conversationId: data.conversationId, parentMessageId: data.id },
                requestOptions: { prompt: message, ...options },
              },
            )

            if (openLongReply && data.detail.choices[0].finish_reason === 'length') {
              options.parentMessageId = data.id
              lastText = data.text
              message = ''
              return fetchChatAPIOnce()
            }
          }
          catch (error) {
            //
          }
        },
      })
    }
    await fetchChatAPIOnce()
  }
  catch (error: any) {
    if (error.message === 'canceled') {
      updateChatSome(
        +uuid,
        index,
        {
          loading: false,
        },
      )
      return
    }

    const errorMessage = error?.message ?? t('common.wrong')

    updateChat(
      +uuid,
      index,
      {
        dateTime: new Date().toLocaleString(),
        text: errorMessage,
        inversion: false,
        error: true,
        loading: false,
        conversationOptions: null,
        requestOptions: { prompt: message, ...options },
      },
    )
  }
  finally {
    loading.value = false
  }
}

function handleExport() {
  if (loading.value)
    return

  const d = dialog.warning({
    title: t('chat.exportImage'),
    content: t('chat.exportImageConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: async () => {
      try {
        d.loading = true
        const ele = document.getElementById('image-wrapper')
        const canvas = await html2canvas(ele as HTMLDivElement, {
          useCORS: true,
        })
        const imgUrl = canvas.toDataURL('image/png')
        const tempLink = document.createElement('a')
        tempLink.style.display = 'none'
        tempLink.href = imgUrl
        tempLink.setAttribute('download', 'chat-shot.png')
        if (typeof tempLink.download === 'undefined')
          tempLink.setAttribute('target', '_blank')

        document.body.appendChild(tempLink)
        tempLink.click()
        document.body.removeChild(tempLink)
        window.URL.revokeObjectURL(imgUrl)
        d.loading = false
        ms.success(t('chat.exportSuccess'))
        Promise.resolve()
      }
      catch (error: any) {
        ms.error(t('chat.exportFailed'))
      }
      finally {
        d.loading = false
      }
    },
  })
}

function handleDelete(index: number) {
  if (loading.value)
    return

  dialog.warning({
    title: t('chat.deleteMessage'),
    content: t('chat.deleteMessageConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: () => {
      chatStore.deleteChatByUuid(+uuid, index)
    },
  })
}

function handleClear() {
  if (loading.value)
    return

  dialog.warning({
    title: t('chat.clearChat'),
    content: t('chat.clearChatConfirm'),
    positiveText: t('common.yes'),
    negativeText: t('common.no'),
    onPositiveClick: () => {
      chatStore.clearChatByUuid(+uuid)
    },
  })
}

function handleEnter(event: KeyboardEvent) {
  if (!isMobile.value) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault()
      handleSubmit()
    }
  }
  else {
    if (event.key === 'Enter' && event.ctrlKey) {
      event.preventDefault()
      handleSubmit()
    }
  }
}

function handleStop() {
  if (loading.value) {
    controller.abort()
    loading.value = false
  }
}

// 可优化部分
// 搜索选项计算，这里使用value作为索引项，所以当出现重复value时渲染异常(多项同时出现选中效果)
// 理想状态下其实应该是key作为索引项,但官方的renderOption会出现问题，所以就需要value反renderLabel实现
const searchOptions = computed(() => {
  if (prompt.value.startsWith('/')) {
    return promptTemplate.value.filter((item: { key: string }) => item.key.toLowerCase().includes(prompt.value.substring(1).toLowerCase())).map((obj: { value: any }) => {
      return {
        label: obj.value,
        value: obj.value,
      }
    })
  }
  else {
    return []
  }
})

// value反渲染key
const renderOption = (option: { label: string }) => {
  for (const i of promptTemplate.value) {
    if (i.value === option.label)
      return [i.key]
  }
  return []
}

const placeholder = computed(() => {
  if (isMobile.value)
    return t('chat.placeholderMobile')
  return t('chat.placeholder')
})

const buttonDisabled = computed(() => {
  return loading.value || !prompt.value || prompt.value.trim() === ''
})

const footerClass = computed(() => {
  let classes = ['p-4']
  if (isMobile.value)
    classes = ['sticky', 'left-0', 'bottom-0', 'right-0', 'p-2', 'pr-3', 'overflow-hidden']
  return classes
})

onMounted(() => {
  scrollToBottom()
  prompt.value = currentPrompt.value
  if (inputRef.value && !isMobile.value)
    inputRef.value?.focus()
})
watch(currentPrompt, (newValue) => {
  promptPlaceholder.value = transformData(newValue.promptHint)
  completPrompt.value = newValue.prompt
})
onUnmounted(() => {
  if (loading.value)
    controller.abort()
})
</script>

<template>
  <div class="flex flex-col w-full h-full">
    <HeaderComponent v-if="isMobile" :using-context="usingContext" @export="handleExport"
      @toggle-using-context="toggleUsingContext" />
    <main class="flex-1 overflow-hidden">
      <div id="scrollRef" ref="scrollRef" class="h-full overflow-hidden overflow-y-auto">
        <div id="image-wrapper" class="w-full max-w-screen-xl m-auto dark:bg-[#101014]"
          :class="[isMobile ? 'p-2' : 'p-4']">
          <template v-if="!dataSources.length">
            <div class="flex items-center justify-center mt-4 text-center text-neutral-300">
              <commonPage></commonPage>
            </div>
          </template>
          <template v-else>
            <div>
              <Message v-for="(item, index) of dataSources" :key="index" :date-time="item.dateTime" :text="item.text"
                :inversion="item.inversion" :error="item.error" :loading="item.loading" @regenerate="onRegenerate(index)"
                @delete="handleDelete(index)" />
              <div v-if="loading" type="warning" @click="handleStop" class="stop-but">
                停止生成
              </div>
              <div v-if="!loading" type="warning" @click="handleReSubmit" class="start-but">
                继续生成
              </div>
            </div>
          </template>
        </div>
      </div>
    </main>
    <div class="precess">
      <n-form inline :label-width="80" :model="precessForm">
        <n-grid cols="10 400:12 600:24" :x-gap="12" responsive="self" :itemResponsive="true">
          <n-form-item-gi :span="5" offset="5" label="输出语言" path="precessForm.languge">
            <n-select v-model:value="precessForm.languge" placeholder="请选择" :options="langugeList" value-field="value" />
          </n-form-item-gi>
          <n-form-item-gi :span="5" label="语气" path="precessForm.tone">
            <n-select v-model:value="precessForm.tone" placeholder="请选择" :options="toneList" value-field="value" />
          </n-form-item-gi>
          <n-form-item-gi :span="5" label="文字风格" path="precessForm.textTyle">
            <n-select v-model:value="precessForm.textTyle" placeholder="请选择" :options="textTyleList"
              value-field="value" />
          </n-form-item-gi>
        </n-grid>
      </n-form>
    </div>
    <footer :class="footerClass">
      <div class="w-full max-w-screen-xl m-auto">
        <div class="flex items-center justify-between space-x-2">
          <HoverButton @click="handleClear">
            <span class="text-xl text-[#4f555e] dark:text-white">
              <SvgIcon icon="ri:delete-bin-line" />
            </span>
          </HoverButton>
          <HoverButton v-if="!isMobile" @click="handleExport">
            <span class="text-xl text-[#4f555e] dark:text-white">
              <SvgIcon icon="ri:download-2-line" />
            </span>
          </HoverButton>
          <HoverButton v-if="!isMobile" @click="toggleUsingContext">
            <span class="text-xl" :class="{ 'text-[#4b9e5f]': usingContext, 'text-[#a8071a]': !usingContext }">
              <SvgIcon icon="ri:chat-history-line" />
            </span>
          </HoverButton>
          <NAutoComplete v-model:value="prompt" :options="searchOptions" :render-label="renderOption">
            <template #default="{ handleInput, handleBlur, handleFocus }">
              <NInput ref="inputRef" v-model:value="prompt" type="textarea" :placeholder="promptPlaceholder"
                :autosize="{ minRows: 1, maxRows: isMobile ? 4 : 8 }" @input="handleInput" @focus="handleFocus"
                @blur="handleBlur" @keypress="handleEnter" />

            </template>
          </NAutoComplete>
          <NButton type="primary" :disabled="buttonDisabled" @click="handleSubmit">
            <template #icon>
              <span class="dark:text-black">
                <SvgIcon icon="ri:send-plane-fill" />
              </span>
            </template>
          </NButton>
        </div>
      </div>
    </footer>
    <div class="blow"> 本站点基于外部API二次开发，仅供学习 AI 使用，使用前请知晓<a href="hhttp:www.baidu.com">免责申明</a></div>
  </div>
</template>
<style scoped>
.blow {
  text-align: center;
  font-size: 12px;
}

.precess {

  border-top: 1px solid #c0ccdc;
  padding: 20px 20px 0px;
}

.stop-but {
  width: 60px;
  margin: 0 auto;
  padding: 4px 6px;
  border: 1px solid #c0ccdc;
  width: 82px;
  text-align: center;
  border-radius: 4px;
  cursor: pointer;
}
.start-but{
  width: 60px;
  position: absolute;
  bottom: 70px;
  right: 10px;
  margin: 0 auto;
  padding: 4px 6px;
  border: 1px solid #c0ccdc;
  width: 82px;
  text-align: center;
  border-radius: 4px;
  cursor: pointer;
}
</style>
