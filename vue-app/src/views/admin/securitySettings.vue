<template>
  <div class="security-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>修改密码</span>
        </div>
      </template>
      
      <el-form :model="passwordForm" label-width="120px" style="max-width: 600px">
        <el-form-item label="邮箱验证码">
          <div class="verification-input">
            <el-input 
              v-model="passwordForm.verificationCode" 
              placeholder="请输入邮箱验证码" 
              style="flex: 1; margin-right: 10px;"
            />
            <el-button 
              @click="sendVerificationCode" 
              :loading="sendingCode"
              :disabled="countdown > 0"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="新密码">
          <el-input 
            v-model="passwordForm.password" 
            type="password" 
            placeholder="请输入新密码" 
            show-password 
          />
        </el-form-item>
        
        <el-form-item label="确认密码">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password 
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="changePassword" :loading="changingPassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user.js'
import axios from "axios";
import config from "../../config.js";
import {useRouter} from "vue-router";
const router=useRouter()
// 用户状态管理
const userStore = useUserStore()

// 密码修改表单
const passwordForm = reactive({
  verificationCode: '',
  password: '',
  confirmPassword: ''
})

// 状态变量
const sendingCode = ref(false)
const changingPassword = ref(false)
const countdown = ref(0)
const userEmail = ref('')

// 获取用户邮箱
async function getUserEmail() {
  try {
    const username = userStore.username
    await axios.get(`${config.api}/users/getEmail?username=${username}`).then(response=>{
      if (response.data){
        userEmail.value=response.data || ''
        console.log('email++++'+userEmail.value)
        if (!userEmail.value) {
          ElMessage.warning('您还未绑定邮箱，请先在个人设置中绑定邮箱')
        }
      }else {
        ElMessage.error('获取用户信息失败')
      }
    })
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

// 发送验证码
async function sendVerificationCode() {
  if (!userEmail.value) {
    ElMessage.error('请先绑定邮箱')
    return
  }
  
  sendingCode.value = true
  try {
    await axios.get(`${config.api}/users/getEmailYzm/${userEmail.value}`).then(response=>{
      if (response.data){
        ElMessage.success('验证码已发送到您的邮箱')
        // 开始倒计时
        countdown.value = 60
        const timer = setInterval(() => {
          countdown.value--
          if (countdown.value <= 0) {
            clearInterval(timer)
          }
        }, 1000)
      }else {
        ElMessage.error('验证码发送失败')
      }
    })

  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    sendingCode.value = false
  }
}

// 修改密码
async function changePassword() {
  if (!passwordForm.verificationCode) {
    ElMessage.error('请输入验证码')
    return
  }
  if (!passwordForm.password) {
    ElMessage.error('请输入新密码')
    return
  }
  if (passwordForm.password !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  if (passwordForm.password.length < 6) {
    ElMessage.error('密码长度不能少于6位')
    return
  }
  
  changingPassword.value = true
  try {
        const username = userStore.username
        const user = {
          id: userStore.uid,
          username: username,
          password: passwordForm.password,
        }
        console.log('user+++'+user.username)
        console.log('user+++'+user.uid)
        console.log('user+++'+user.password)
        await axios.put(`${config.api}/users/setPassword/${userEmail.value}/${passwordForm.verificationCode}`, user).then(response => {
          if (response.data) {
            ElMessage.success( '密码修改成功')
            ElMessage.error( '登录状态失效请重新登录！')
            router.push('/')
          } else {
            ElMessage.error( '密码修改失败')
          }
        })
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  getUserEmail()
})
</script>

<style scoped>
.security-page {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.verification-input {
  display: flex;
  align-items: center;
  width: 100%;
}
</style>
