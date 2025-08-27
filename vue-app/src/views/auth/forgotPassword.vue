<template>
  <div class="forgot-password-page">
    <div class="card">
      <h2 class="title">找回密码</h2>
      
      <!-- 步骤1: 输入用户名获取邮箱信息 -->
      <div v-if="step === 1">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model.trim="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="getEmailInfo">下一步</el-button>
            <el-button link type="primary" @click="toLogin">返回登录</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤2: 验证邮箱信息 -->
      <div v-if="step === 2">
        <div class="email-info">
          <p>您的邮箱是：<span class="masked-email">{{ maskedEmail }}</span></p>
          <p>请输入完整的邮箱地址进行验证</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="邮箱地址" prop="email">
            <el-input v-model.trim="form.email" placeholder="请输入完整的邮箱地址" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="verifyEmail">验证邮箱</el-button>
            <el-button @click="step = 1">上一步</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 步骤3: 获取验证码并重置密码 -->
      <div v-if="step === 3">
        <div class="success-info">
          <p>邮箱验证成功！现在可以重置密码</p>
        </div>
        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="验证码" prop="verificationCode">
            <div class="verification-code-container">
              <el-input v-model.trim="form.verificationCode" placeholder="请输入验证码" />
              <el-button 
                type="primary" 
                :disabled="cooldown > 0" 
                @click="sendVerificationCode"
              >
                {{ cooldown > 0 ? `${cooldown}秒后重试` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="新密码" prop="password">
            <el-input 
              v-model.trim="form.password" 
              type="password" 
              show-password 
              placeholder="请输入新密码" 
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="resetPassword">重置密码</el-button>
            <el-button @click="step = 2">上一步</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const cooldown = ref(0)
const step = ref(1) // 当前步骤：1-输入用户名，2-验证邮箱，3-重置密码
const maskedEmail = ref('') // 隐藏的邮箱信息

// 表单数据
const form = reactive({
  username: '',
  email: '',
  verificationCode: '',
  password: ''
})

// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  verificationCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}

// 步骤1: 获取邮箱信息
const getEmailInfo = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    
    loading.value = true
    try {
      const { data } = await request.post('/auth/getEmailByUsername', {
        username: form.username
      })
      
      if (data.code === 200) {
        maskedEmail.value = data.data.maskedEmail
        step.value = 2
        ElMessage.success('获取邮箱信息成功')
      } else {
        ElMessage.error(data.message || '获取邮箱信息失败')
      }
    } catch (e) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}

// 步骤2: 验证邮箱
const verifyEmail = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    
    loading.value = true
    try {
      const { data } = await request.post('/auth/verifyEmail', {
        username: form.username,
        email: form.email
      })
      
      if (data.code === 200) {
        step.value = 3
        ElMessage.success('邮箱验证成功')
      } else {
        ElMessage.error(data.message || '邮箱验证失败')
      }
    } catch (e) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}

// 步骤3: 发送验证码
const sendVerificationCode = async () => {
  try {
    const { data } = await request.post('/auth/sendVerificationCode', {
      username: form.username,
      email: form.email
    })
    
    if (data.code === 200) {
      ElMessage.success('验证码已发送到您的邮箱')
      // 设置冷却时间
      cooldown.value = 60
      const timer = setInterval(() => {
        cooldown.value--
        if (cooldown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      ElMessage.error(data.message || '验证码发送失败')
    }
  } catch (e) {
    // 错误已在拦截器中处理
  }
}

// 步骤3: 重置密码
const resetPassword = () => {
  formRef.value.validate(async valid => {
    if (!valid) return
    
    loading.value = true
    try {
      const { data } = await request.post('/auth/changePassword', {
        username: form.username,
        password: form.password,
        verificationCode: form.verificationCode
      })
      
      if (data.code === 200) {
        ElMessage.success('密码重置成功')
        router.push('/')
      } else {
        ElMessage.error(data.message || '密码重置失败')
      }
    } catch (e) {
      // 错误已在拦截器中处理
    } finally {
      loading.value = false
    }
  })
}

// 返回登录页
const toLogin = () => {
  router.push('/')
}
</script>

<style scoped>
.forgot-password-page {
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
  color: #222222;
  text-shadow: 0 1px 2px rgba(0,0,0,0.4);
}

.email-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 8px;
  border-left: 4px solid #1890ff;
}

.email-info p {
  margin: 5px 0;
  color: #333;
  font-size: 14px;
}

.masked-email {
  font-weight: bold;
  color: #1890ff;
}

.success-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: rgba(82, 196, 26, 0.1);
  border-radius: 8px;
  border-left: 4px solid #52c41a;
}

.success-info p {
  margin: 0;
  color: #333;
  font-size: 14px;
}

.verification-code-container {
  display: flex;
  gap: 10px;
}

.verification-code-container .el-button {
  flex-shrink: 0;
  white-space: nowrap;
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
}

/* 按钮优化 */
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