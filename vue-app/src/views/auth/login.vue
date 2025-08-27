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
        <el-form-item label="验证码" prop="captchaCode">
          <div class="captcha-container">
            <el-input 
              v-model.trim="form.captchaCode" 
              placeholder="请输入验证码" 
              style="flex: 1; margin-right: 10px;"
            />
            <div class="captcha-image" @click="refreshCaptcha">
              <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
              <div v-else class="captcha-loading">加载中...</div>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="onSubmit">登 录</el-button>
        </el-form-item>
        <el-form-item class="links-form-item">
          <div class="links-container">
            <el-button link type="primary" @click="toRegister">注册</el-button>
            <el-button link type="primary" @click="toForgotPassword">忘记密码</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>

</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request.js'
import { useUserStore } from '../../stores/user.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const form = reactive({
  username: '',
  password: '',
  captchaCode: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

// 获取验证码
const getCaptcha = async () => {
  try {
    const { data } = await request.get('/auth/captcha')
    if (data.code === 200) {
      captchaImage.value = data.data.captchaImage
      captchaKey.value = data.data.captchaKey
    }
  } catch (e) {
    ElMessage.error('获取验证码失败')
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  form.captchaCode = ''
  getCaptcha()
}

const onSubmit = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    try {
      const { data } = await request.post('/auth/login', {
        username: form.username,
        password: form.password,
        captchaKey: captchaKey.value,
        captchaCode: form.captchaCode
      })
      if (data.code === 200 && data.data && data.data.token) {
        userStore.setToken(data.data.token, form.username)
        ElMessage.success('登录成功')

        // 使用 await 等待 getUserType 的结果
        const userType = await getUserType(form.username);
        if (userType === '用户') {
          router.push('/user/home');
        } else {
          router.push('/home');
        }

      } else {
        ElMessage.error(data.message || '登录失败')
        refreshCaptcha() // 登录失败后刷新验证码
      }
    } catch (e) {
      refreshCaptcha() // 请求失败后刷新验证码
    } finally {
      loading.value = false
    }
  })
}

// 获取用户类型
const getUserType =async (username) => {
  try {
    const {data} =await request.get(`user/profile/`+username)
    if (data.code === 200) {
      return data.data.typeName
    } else {
      ElMessage.error(data.message || '获取用户类型失败')
      return ''
    }
  } catch (e) {
    ElMessage.error(e)
  }
}

const toRegister = () => {
  router.push('/register')
}

// 跳转到找回密码页面
const toForgotPassword = () => {
  router.push('/forgot-password')
}

// 页面加载时获取验证码
onMounted(() => {
  getCaptcha()
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url('../../assets/image/login.png') center/cover no-repeat;
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

.links-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 50%;
}

.links-form-item {
  margin-top: -10px;
}

:deep(.el-button.is-link) {
  margin: 5px 0;
}

/* 验证码容器样式 */
.captcha-container {
  display: flex;
  align-items: center;
  width: 100%;
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  transition: border-color 0.3s;
}

.captcha-image:hover {
  border-color: #409eff;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  border-radius: 4px;
}

.captcha-loading {
  font-size: 12px;
  color: #909399;
}
</style>
