import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  
  // 设置token和用户名
  const setToken = (newToken, newUsername) => {
    token.value = newToken
    username.value = newUsername
    localStorage.setItem('token', newToken)
    localStorage.setItem('username', newUsername)
  }
  
  // 清除token和用户名
  const clearToken = () => {
    token.value = ''
    username.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }
  
  // 检查是否已登录
  const isLoggedIn = () => {
    return token.value !== ''
  }
  
  return {
    token,
    username,
    setToken,
    clearToken,
    isLoggedIn
  }
}) 