import type { AxiosProgressEvent, GenericAbortSignal } from 'axios'
import { post } from '@/utils/request'
import { useSettingStore } from '@/store'

export function fetchChatAPI<T = any>(
  prompt: string,
  options?: { conversationId?: string; parentMessageId?: string },
  signal?: GenericAbortSignal,
) {
  return post<T>({
    url: '/chat',
    data: { prompt, options },
    signal,
  })
}

export function fetchChatConfig<T = any>() {
  return post<T>({
    url: '/config',
  })
}

export function fetchChatAPIProcess<T = any>(
  params: {
    messages: [
      {
        "role": "user",
        "content": "你在哪里？"
      }
    ],
    token: 'sasas'
  }
) {
  const settingStore = useSettingStore()

  return post<T>({
    url: '/api/chat/stream',
    data: params,
    responseType: 'stream',
    onDownloadProgress: params.onDownloadProgress,
  })
}

export function fetchSession<T>() {
  return post<T>({
    url: '/session',
  })
}

export function fetchVerify<T>(token: string) {
  return post<T>({
    url: '/verify',
    data: { token },
  })
}
let root = 'https://chat.opaigc.com'
export let api = {
  rigist: '/api/auth/registration',
  login: '/api/auth/login',
  getUserInfo: '/api/user/info',
  convertCharge: '/api/promotion/charge',
  chatStream: '/api/chat/stream',
}