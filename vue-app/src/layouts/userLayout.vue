<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-content">
        <!-- Logo区域 -->
        <div class="logo-section">
          <img src="../assets/image/logo.png" alt="悦享音乐" class="logo" />
          <span class="logo-text">悦享音乐</span>
        </div>

        <!-- 搜索区域 -->
        <div class="search-section">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索音乐、歌手、专辑"
            class="search-input"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>

        <!-- 用户区域 -->
        <div class="user-section">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.username }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="main-container">
      <!-- 侧边导航栏 -->
      <aside class="sidebar">
        <nav class="nav-menu">
          <div class="menu-item" 
               v-for="item in menuItems" 
               :key="item.path"
               :class="{ active: $route.path === item.path }"
               @click="navigateTo(item.path)">
            <el-icon class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            <span class="menu-text">{{ item.name }}</span>
          </div>
        </nav>

        <!-- 播放控制区域 -->
        <div class="player-mini" v-if="currentSong">
          <div class="song-info">
            <img :src="currentSong.cover" alt="" class="song-cover" />
            <div class="song-details">
              <div class="song-name">{{ currentSong.name }}</div>
              <div class="song-artist">{{ currentSong.artist }}</div>
            </div>
          </div>
          <div class="player-controls">
            <el-icon class="control-btn" @click="togglePlay">
              <component :is="isPlaying ? 'VideoPause' : 'VideoPlay'" />
            </el-icon>
          </div>
        </div>
      </aside>

      <!-- 内容区域 -->
      <main class="content">
        <router-view />
      </main>
    </div>

    <!-- 底部播放器 -->
    <footer class="player-bar" v-if="currentSong">
      <div class="player-content">
        <!-- 歌曲信息 -->
        <div class="song-section">
          <img :src="currentSong.cover" alt="" class="current-cover" />
          <div class="current-info">
            <div class="current-name">{{ currentSong.name }}</div>
            <div class="current-artist">{{ currentSong.artist }}</div>
          </div>
          <el-icon class="like-btn" :class="{ liked: currentSong.liked }">
            <component :is="currentSong.liked ? 'Star' : 'StarFilled'" />
          </el-icon>
        </div>

        <!-- 播放控制 -->
        <div class="control-section">
          <div class="control-buttons">
            <el-icon class="control-btn"><RefreshLeft /></el-icon>
            <el-icon class="control-btn"><DArrowLeft /></el-icon>
            <el-icon class="play-btn" @click="togglePlay">
              <component :is="isPlaying ? 'VideoPause' : 'VideoPlay'" />
            </el-icon>
            <el-icon class="control-btn"><DArrowRight /></el-icon>
            <el-icon class="control-btn"><RefreshRight /></el-icon>
          </div>
          <div class="progress-section">
            <span class="time">{{ formatTime(currentTime) }}</span>
            <el-slider 
              v-model="progress" 
              class="progress-slider"
              :show-tooltip="false"
              @change="onProgressChange"
            />
            <span class="time">{{ formatTime(duration) }}</span>
          </div>
        </div>

        <!-- 音量和其他控制 -->
        <div class="extra-section">
          <el-icon class="extra-btn"><VideoPlay /></el-icon>
          <el-icon class="extra-btn"><List /></el-icon>
          <div class="volume-control">
            <el-icon class="volume-btn">
              <component :is="volume === 0 ? 'Mute' : 'VideoPlay'" />
            </el-icon>
            <el-slider 
              v-model="volume" 
              class="volume-slider"
              :max="100"
              :show-tooltip="false"
            />
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user.js'
import { 
  Search, User, ArrowDown, House, Microphone, TrendCharts, 
  List, UserFilled, VideoPause, VideoPlay, Star, StarFilled,
  RefreshLeft, RefreshRight, DArrowLeft, DArrowRight,
  Mute
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

// 搜索关键词
const searchKeyword = ref('')

// 用户头像
const userAvatar = ref('')

// 菜单项
const menuItems = reactive([
  { name: '主页', path: '/user/home', icon: 'House' },
  { name: '歌手', path: '/user/singers', icon: 'Microphone' },
  { name: '排行', path: '/user/ranking', icon: 'TrendCharts' },
  { name: '歌单', path: '/user/playlists', icon: 'List' },
  { name: '我的', path: '/user/profile', icon: 'UserFilled' }
])

// 播放器状态
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(240)
const progress = ref(30)
const volume = ref(80)

// 当前播放歌曲（示例数据）
const currentSong = ref({
  name: '青花瓷',
  artist: '周杰伦',
  cover: '/src/assets/image/pic1.png',
  liked: false
})

// 导航到指定路径
const navigateTo = (path) => {
  router.push(path)
}

// 处理用户下拉菜单命令
const handleUserCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'settings':
      ElMessage.info('设置功能开发中...')
      break
    case 'logout':
      userStore.clearToken()
      router.push('/')
      ElMessage.success('已退出登录')
      break
  }
}

// 切换播放状态
const togglePlay = () => {
  isPlaying.value = !isPlaying.value
}

// 进度条改变
const onProgressChange = (value) => {
  currentTime.value = (duration.value * value) / 100
}

// 格式化时间
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.user-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 顶部导航栏 */
.header {
  height: 70px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.search-section {
  flex: 1;
  max-width: 500px;
  margin: 0 40px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 25px;
  background: rgba(0, 0, 0, 0.05);
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-section {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 20px;
  background: rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.1);
}

.username {
  font-weight: 500;
  color: #333;
}

.dropdown-icon {
  font-size: 12px;
  color: #666;
}

/* 主体容器 */
.main-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 240px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  padding: 20px 0;
}

.nav-menu {
  flex: 1;
  padding: 0 20px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.menu-item:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.menu-item.active {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.menu-icon {
  font-size: 20px;
}

.menu-text {
  font-weight: 500;
}

/* 迷你播放器 */
.player-mini {
  padding: 16px 20px;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  background: rgba(0, 0, 0, 0.02);
}

.song-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.song-cover {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.song-details {
  flex: 1;
  min-width: 0;
}

.song-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-artist {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.player-controls {
  display: flex;
  justify-content: center;
}

.control-btn {
  font-size: 18px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
}

.control-btn:hover {
  color: #764ba2;
  transform: scale(1.1);
}

/* 内容区域 */
.content {
  flex: 1;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

/* 底部播放器 */
.player-bar {
  height: 80px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(0, 0, 0, 0.1);
  position: sticky;
  bottom: 0;
  z-index: 1000;
}

.player-content {
  height: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.song-section {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 300px;
}

.current-cover {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.current-info {
  flex: 1;
  min-width: 0;
}

.current-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.current-artist {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.like-btn {
  font-size: 18px;
  color: #ccc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.like-btn:hover,
.like-btn.liked {
  color: #ff6b6b;
}

.control-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.control-buttons {
  display: flex;
  align-items: center;
  gap: 16px;
}

.play-btn {
  font-size: 32px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
}

.play-btn:hover {
  color: #764ba2;
  transform: scale(1.1);
}

.progress-section {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  max-width: 500px;
}

.time {
  font-size: 12px;
  color: #666;
  min-width: 35px;
  text-align: center;
}

.progress-slider {
  flex: 1;
}

.progress-slider :deep(.el-slider__runway) {
  background: rgba(0, 0, 0, 0.1);
}

.progress-slider :deep(.el-slider__bar) {
  background: linear-gradient(45deg, #667eea, #764ba2);
}

.extra-section {
  display: flex;
  align-items: center;
  gap: 16px;
  width: 300px;
  justify-content: flex-end;
}

.extra-btn {
  font-size: 18px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.extra-btn:hover {
  color: #667eea;
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.volume-btn {
  font-size: 16px;
  color: #666;
  cursor: pointer;
}

.volume-slider {
  width: 80px;
}

.volume-slider :deep(.el-slider__runway) {
  background: rgba(0, 0, 0, 0.1);
}

.volume-slider :deep(.el-slider__bar) {
  background: linear-gradient(45deg, #667eea, #764ba2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px;
  }
  
  .header-content {
    padding: 0 16px;
  }
  
  .search-section {
    margin: 0 20px;
  }
  
  .player-content {
    padding: 0 16px;
  }
  
  .song-section,
  .extra-section {
    width: auto;
    min-width: 200px;
  }
}
</style>