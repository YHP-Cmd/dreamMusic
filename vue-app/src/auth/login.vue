<template>
  <div class="login-page">
    <div class="card">
      <h2 class="title">账号登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model.trim="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model.trim="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登 录</el-button>
          <el-button link type="primary" @click="toRegister">还没有账号？去注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>

</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user.js'
import { useRouter } from 'vue-router'
import axios from "axios";
import config from '../config.js'
const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const type=ref('')
const getUserType  = async (username)=>{
  await axios.get(`${config.api}/users/getUserType?username=${username}`).then(response=>{
    if (response.data){
      console.log(response.data)
      type.value= response.data
    }
  })
}
const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    try {
       await axios.post(`${config.api}/users/login`, {
        username: form.username,
        password: form.password
      }).then(async response => {
         if (response.data.code === 200 && response.data.data && response.data.data.token) {
           userStore.setToken(response.data.data.token, form.username)
           ElMessage.success('登录成功')
           await getUserType(form.username)
           console.log('utp' + type.value)
           if (type.value === '普通用户') {
             await router.push('/home')
           } else if (type.value === '管理员') {
             await router.push('/layout')
           }
         } else {
           ElMessage.error(response.data.message || '登录失败')
         }
       })
    } catch (e) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}

const toRegister = () => {
  router.push('/regis')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('../assets/image/login.png') center/cover no-repeat;
}

.card {
  width: 370px;
  padding: 28px 24px 12px;
  background-color: rgba(255, 255, 255, 0.92);
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