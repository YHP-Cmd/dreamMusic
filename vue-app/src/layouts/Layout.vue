<template>
  <!-- 整体布局：左侧菜单 + 右侧顶部栏 + 内容区 -->
  <el-container class="layout">
    <el-aside :width="isCollapsed ? '64px' : '220px'" class="aside">
      <!-- 系统 Logo 与标题（点击回到首页） -->
      <div class="logo" @click="goHome">
        <img src="../assets/image/logo.png" alt="logo" />
        <span v-if="!isCollapsed">悦享音乐·后台</span>
      </div>
      <!-- 左侧菜单：根据后端模块数据渲染，支持折叠与路由联动 -->
      <el-menu
        class="menu"
        :default-active="activePath"
        :collapse="isCollapsed"
        router
        unique-opened
        background-color="#0f172a"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
      >
        <el-menu-item index="home1" @click="handleMenuClick('/home1')">
          <el-icon><HomeFilled /></el-icon>
          首页</el-menu-item>
        <!--            <el-menu-item-group>-->
        <el-menu-item index="admin" @click="handleMenuClick('/admin/user')">
          <el-icon><Calendar /></el-icon>
          用户管理</el-menu-item>
        <el-menu-item index="SaleManage" @click="handleMenuClick('/admin/song')">
          <el-icon><School /></el-icon>
          歌曲管理</el-menu-item>
        <el-menu-item index="CityManage" @click="handleMenuClick('/admin/singer')">
          <el-icon><School /></el-icon>
          歌手管理</el-menu-item>
        <el-menu-item index="MedicalPolicy" @click="handleMenuClick('/settings/security')">
          <el-icon><School /></el-icon>
          安全设置</el-menu-item>
        <el-menu-item index="CompanyPolicy" @click="handleMenuClick('/type/userType')">
          <el-icon><School /></el-icon>
          用户类型管理</el-menu-item>
        <el-menu-item index="DoctorManage" @click="handleMenuClick('/type/songType')">
          <el-icon><School /></el-icon>
          歌曲类型管理</el-menu-item>
        <el-menu-item index="MaterialManage" @click="handleMenuClick('/type/singerType')">
          <el-icon><School /></el-icon>
          歌手类型管理</el-menu-item>
        <el-menu-item  index="account" @click="handleMenuClick('/permission')">
          <el-icon><User /></el-icon>
          权限管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="left">
          <el-button text @click="isCollapsed = !isCollapsed">
            <el-icon>
              <component :is="isCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
          <span class="title">悦享音乐后台管理</span>
        </div>
        <div class="right">
          <el-dropdown>
            <span class="el-dropdown-link">
              <el-avatar size="small">{{ usernameFirst }}</el-avatar>
              <span class="username">{{ userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goProfile">个人设置</el-dropdown-item>
                <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <!-- 面包屑：显示"设置中心 / 个人设置"等路径提示 -->
        <div class="breadcrumb-container" v-if="breadcrumbs.length">
          <div class="breadcrumb-wrapper">
            <el-icon class="breadcrumb-icon">
              <Location />
            </el-icon>
            <el-breadcrumb separator="/" class="custom-breadcrumb">
              <el-breadcrumb-item 
                v-for="(bc, idx) in breadcrumbs" 
                :key="idx"
                :class="{ 'is-last': idx === breadcrumbs.length - 1 }"
              >
                {{ bc }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user.js'
function handleMenuClick(path) {
  router.push(path)
}
// 路由与用户状态
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠与当前激活路径
const isCollapsed = ref(false)
const activePath = ref(route.path)

// 原始菜单数据与树结构
const rawMenu = ref([])
const menuTree = computed(() => buildMenuTree(rawMenu.value))

// 用户名首字母头像
const usernameFirst = computed(() => (userStore.username || 'U').slice(0, 1).toUpperCase())

// 面包屑数据来源于路由 meta
const breadcrumbs = computed(() => {
  const bc = route.meta?.breadcrumb
  return Array.isArray(bc) ? bc : []
})

watch(
  () => route.path,
  (newPath) => {
    activePath.value = newPath
  }
)

onMounted(async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/')
    return
  }
  await loadMenu()
  if (route.path === '/layout') {
    router.replace('/home1')
  }
})

// 加载后端菜单：优先使用数组，否则兼容 Result<T>
async function loadMenu() {
  try {
    const username = userStore.username
    const { data } = await request.get(`/menu/${encodeURIComponent(username)}`)
    // 后端直接返回数组
    rawMenu.value = Array.isArray(data) ? data : (data?.data || [])
  } catch (e) {
    rawMenu.value = []
  }
}

// 将扁平菜单构建为树结构
function buildMenuTree(list) {
  if (!Array.isArray(list)) return []
  const sorted = [...list].sort((a, b) => (a?.sortOrder ?? 0) - (b?.sortOrder ?? 0))
  const idToNode = {}
  sorted.forEach((item) => {
    idToNode[item.moduleId] = { ...item, children: [] }
  })
  const roots = []
  Object.values(idToNode).forEach((node) => {
    const parentId = node.parentId
    if (parentId && idToNode[parentId]) {
      idToNode[parentId].children.push(node)
    } else {
      roots.push(node)
    }
  })
  const sortChildren = (nodes) => {
    nodes.sort((a, b) => (a?.sortOrder ?? 0) - (b?.sortOrder ?? 0))
    nodes.forEach((n) => n.children && n.children.length && sortChildren(n.children))
  }
  sortChildren(roots)
  return roots
}

// 图标解析：将数据库中的图标名称映射到 Element Plus 图标组件名
function resolveIcon(iconName) {
  if (!iconName) return 'Menu'
  
  // 直接使用新的图标名称映射
  const map = {
    'HomeFilled': 'HomeFilled',
    'Menu': 'Menu',
    'Setting': 'Setting',
    'User': 'User',
    'Headset': 'Headset',
    'UserFilled': 'UserFilled',
    'Tools': 'Tools',
    'Microphone': 'Microphone',
    'CollectionTag': 'CollectionTag',
    'Stamp': 'Stamp',
    'VideoPlay': 'VideoPlay',
    'Avatar': 'Avatar',
  }
  
  // 如果在映射表中找到对应的图标，则返回映射值
  if (map[iconName]) return map[iconName]
  
  // 兼容旧格式：如果传入的是 el-icon- 格式，尝试转换
  const oldMap = {
    'el-icon-s-home': 'HomeFilled',
    'el-icon-s-manager': 'Menu',
    'el-icon-setting': 'Setting',
    'el-icon-user': 'User',
    'el-icon-music': 'Headset',
    'el-icon-user-solid': 'UserFilled',
    'el-icon-lock': 'Tools'
  }
  
  if (String(iconName).startsWith('el-icon-') && oldMap[iconName]) {
    return oldMap[iconName]
  }
  
  // 如果都不匹配，返回默认图标
  return 'Menu'
}

// 便捷导航
function goHome() {
  router.push('/home')
}

function goProfile() {
  router.push('/settings/profile')
}

function logout() {
  userStore.clearToken()
  router.push('/')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.aside {
  background: #0f172a;
  color: #cbd5e1;
  border-right: 1px solid #0b1220;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 56px;
  padding: 0 12px;
  cursor: pointer;
  color: #e2e8f0;
}
.logo img {
  width: 24px;
  height: 24px;
}
.logo span {
  font-weight: 600;
  letter-spacing: 1px;
}
.menu {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
}
.left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.title {
  font-size: 16px;
  color: #0f172a;
}
.right .el-dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  color: #334155;
}
.main {
  padding: 0;
  background: #f8fafc;
}

/* 面包屑样式 */
.breadcrumb-container {
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  padding: 12px 24px;
  margin-bottom: 0;
}

.breadcrumb-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.breadcrumb-icon {
  color: #64748b;
  font-size: 16px;
}

.custom-breadcrumb {
  font-size: 14px;
}

.custom-breadcrumb :deep(.el-breadcrumb__item) {
  color: #64748b;
  font-weight: 400;
}

.custom-breadcrumb :deep(.el-breadcrumb__item:not(.is-last)) {
  color: #94a3b8;
}

.custom-breadcrumb :deep(.el-breadcrumb__item.is-last) {
  color: #1e293b;
  font-weight: 500;
}

.custom-breadcrumb :deep(.el-breadcrumb__separator) {
  color: #cbd5e1;
  margin: 0 8px;
}

.custom-breadcrumb :deep(.el-breadcrumb__inner) {
  transition: color 0.2s ease;
}

.custom-breadcrumb :deep(.el-breadcrumb__item:not(.is-last) .el-breadcrumb__inner:hover) {
  color: #3b82f6;
  cursor: pointer;
}
</style>