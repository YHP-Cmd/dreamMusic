import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const uid = ref(localStorage.getItem('uid') || '')

  // 设置token和用户名
  const setToken = (newToken, newUsername) => {
    token.value = newToken
    username.value = newUsername
    localStorage.setItem('token', newToken)
    localStorage.setItem('username', newUsername)
  }

  // 设置userId并更新反应式状态
  const setUserId = (newUserId) => {
    uid.value = newUserId; // 更新userId的反应式值
    localStorage.setItem('uid', newUserId)
  }

  // 清除token和用户名
  const clearToken = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('uid') // 如果需要，也可以清除 userId
    uid.value = ''; // 清空userId的反应式值
  }

  // 检查是否已登录
  const isLoggedIn = () => {
    return token.value !== ''
  }

  return {
    token,
    username,
    uid, // 需要确保将 userId 返回
    setToken,
    clearToken,
    isLoggedIn,
    setUserId
  }
})
