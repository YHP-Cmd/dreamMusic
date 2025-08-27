<template>
  <div class="profile-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      
      <el-form :model="profileForm" label-width="100px" style="max-width: 600px">
        <el-form-item label="用户ID">
          <el-input v-model="profileForm.userId" disabled />
        </el-form-item>
        
        <el-form-item label="用户名">
          <el-input v-model="profileForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="性别">
          <el-select v-model="profileForm.gender" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="电话">
          <el-input v-model="profileForm.phone" placeholder="请输入电话号码" />
        </el-form-item>
        
        <el-form-item label="邮箱">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱地址" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="updateProfile" :loading="updating">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../stores/user.js'

// 用户状态管理
const userStore = useUserStore()

// 个人信息表单
const profileForm = reactive({
  userId: '',
  username: '',
  gender: '',
  phone: '',
  email: ''
})

// 加载状态
const updating = ref(false)

// 加载用户信息
async function loadProfile() {
  try {
    const username = userStore.username
    if (!username) {
      ElMessage.error('未获取到用户名')
      return
    }
    
    const { data } = await request.get(`/user/profile/${username}`)
    if (data.code === 200 && data.data) {
      const userInfo = data.data
      Object.assign(profileForm, {
        userId: userInfo.userId || '',
        username: userInfo.username || '',
        gender: userInfo.gender || '',
        phone: userInfo.phone || '',
        email: userInfo.email || ''
      })
    } else {
      ElMessage.error(data.message || '获取用户信息失败')
    }
  } catch (error) {
    // 错误已在拦截器中处理
  }
}

// 更新个人信息
async function updateProfile() {
  if (!profileForm.gender && !profileForm.phone && !profileForm.email) {
    ElMessage.warning('请至少填写一项信息')
    return
  }
  
  updating.value = true
  try {
    const { data } = await request.put('/user', {
      userId: profileForm.userId,
      gender: profileForm.gender,
      phone: profileForm.phone,
      email: profileForm.email
    })
    if (data.code === 200) {
      ElMessage.success(data.message || '个人信息更新成功')
    } else {
      ElMessage.error(data.message || '更新失败')
    }
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  padding: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
