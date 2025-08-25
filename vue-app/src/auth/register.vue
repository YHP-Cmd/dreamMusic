<template>
  <div class="register-page">
    <div class="card">
      <h2 class="title">用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model.trim="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="password">
          <el-input v-model="passwordAgain" type="password" show-password placeholder="请确认密码" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model.trim="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model.trim="form.email" placeholder="请输入邮箱用于接收验证码">
            <template #append>
              <el-button :disabled="sendBtnDisabled" @click="sendCode">{{ sendBtnText }}</el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="verificationCode">
          <el-input v-model.trim="form.verificationCode" placeholder="请输入邮箱验证码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="onSubmit">注 册</el-button>
          <el-button link type="primary" @click="toLogin">已有账号？去登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from "axios";
import config from "../config.js";

const router = useRouter()

const formRef = ref()
const submitting = ref(false)
const passwordAgain=ref('')
const form = reactive({
  username: '',
  password: '',
  gender: '',
  phone: '',
  email: '',
  verificationCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^\d{5,15}$/, message: '电话格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur','change'] }
  ],
  verificationCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const sendBtnText = ref('发送验证码')
const sendBtnDisabled = ref(false)
let countdownTimer = null

const startCountdown = (seconds = 60) => {
  let remaining = seconds
  sendBtnDisabled.value = true
  sendBtnText.value = `${remaining}s`
  clearInterval(countdownTimer)
  countdownTimer = setInterval(() => {
    remaining -= 1
    if (remaining <= 0) {
      clearInterval(countdownTimer)
      sendBtnDisabled.value = false
      sendBtnText.value = '发送验证码'
    } else {
      sendBtnText.value = `${remaining}s`
    }
  }, 1000)
}

const sendCode = async () => {
  // 简要校验邮箱
  if (!form.email) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  console.log(form.email)
  try {
    await axios.get(`${config.api}/users/getEmailYzm/${form.email}`).then(response=>{
      if (response.data){
        ElMessage.success('验证码发送成功，请查收邮箱')
        startCountdown(60)
      }else {
        ElMessage.error(data.message || '验证码发送失败')
      }
    })
  } catch (e) {
  }
}

const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    submitting.value = true
    try {
      await axios.post(`${config.api}/users/register`, {...form}).then(response=>{
        if (response.data){
          ElMessage.success('注册成功，请登录')
          router.push('/')
        }else {
          ElMessage.error(data.message || '注册失败')
        }
      })
    } catch (e) {
      // 错误已在拦截器处理
    } finally {
      submitting.value = false
    }
  })
}

const toLogin = () => {
  router.push('/')
}

onMounted(() => {

})
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('../assets/image/login.png') center/cover no-repeat;
}

.card {
  width: 520px;
  padding: 28px 24px 12px;
  background-color: rgba(255, 255, 255, 0.94);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.18);
}

.title {
  text-align: center;
  margin-bottom: 22px;
  font-size: 22px;
  font-weight: bold;
  color: #222222; /* 文字和背景区分 */
  text-shadow: 0 1px 2px rgba(0,0,0,0.4);
}

/* 表单文字颜色 */
:deep(.el-form-item__label) {
  color: #222222;
  font-weight: 500;
}

/* 输入框更清爽 */
:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.85);
  border-radius: 8px;
}

:deep(.el-form-item__content) {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 0 !important;
  /* 根据实际需求调整值，!important 用来强制覆盖，
     也可以尝试不用 !important，靠选择器优先级覆盖 */
}

/* 登录按钮优化 */
:deep(.el-button--primary) {
  width: 50%;
  border-radius: 8px;
  font-size: 16px;
}

:deep(.el-button.is-link) {
  margin-top: 10px;
  margin-left: 8px;
}
</style>