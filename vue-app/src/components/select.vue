<template>
  <div class="search-container">
    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-header">
        <h2 class="search-title">
          <i class="el-icon-search title-icon"></i>
          搜索音乐
        </h2>
        <p class="search-subtitle">输入歌曲名称，发现您喜爱的音乐</p>
      </div>
      
      <div class="search-box">
        <div class="search-input-wrapper">
          <el-input
            v-model="searchQuery"
            placeholder="输入歌曲名称..."
            clearable
            class="search-input"
            @keyup.enter="searchSongs"
          >
            <template #prefix>
              <i class="el-icon-search search-icon"></i>
            </template>
          </el-input>
          <el-button 
            type="primary" 
            @click="searchSongs"
            class="search-btn"
            :disabled="!searchQuery.trim()"
          >
            <i class="el-icon-search"></i>
            搜索
          </el-button>
        </div>
      </div>
    </div>

    <!-- 搜索结果区域 -->
    <div class="results-section" v-if="musics.length > 0">
      <div class="results-header">
        <h3 class="results-title">
          <i class="el-icon-music"></i>
          搜索结果 ({{ musics.length }})
        </h3>
      </div>
      
      <div class="music-table-container">
        <el-table 
          :data="musics"
          :show-header="false"
          highlight-current-row
          @current-change="selectSong"
          class="music-table" 
          stripe
        >
          <!-- 封面列 -->
          <el-table-column label="封面" width="80">
            <template #default="{ row }">
              <div class="song-cover-wrapper">
                <img :src="imgUrl(row.image)" alt="封面" class="song-cover" />
                <div class="cover-overlay">
                  <i class="el-icon-video-play play-icon"></i>
                </div>
              </div>
            </template>
          </el-table-column>

          <!-- 歌曲信息列 -->
          <el-table-column label="歌曲名称" prop="name" min-width="200">
            <template #default="{ row }">
              <div class="song-info">
                <div class="song-name">{{ row.name }}</div>
                <div class="song-artist">{{ row.singerName || '未知歌手' }}</div>
                <div class="song-album">{{ row.albumName || '未知专辑' }}</div>
              </div>
            </template>
          </el-table-column>

          <!-- 专辑列 -->
          <el-table-column label="专辑" prop="albumName" width="150">
            <template #default="{ row }">
              <div class="album-info">
                <span class="album-name">{{ row.albumName || '未知专辑' }}</span>
              </div>
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  @click.stop="addSong(row)" 
                  type="primary" 
                  size="small"
                  class="action-btn add-btn"
                >
                  <el-icon><VideoPlay /></el-icon>
                  添加
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="searchQuery && musics.length === 0" class="empty-state">
      <div class="empty-icon">
        <i class="el-icon-search"></i>
      </div>
      <h3>未找到相关歌曲</h3>
      <p>试试其他关键词吧</p>
    </div>

    <!-- 初始状态 -->
    <div v-else class="initial-state">
      <div class="initial-icon">
        <i class="el-icon-music"></i>
      </div>
      <h3>开始搜索</h3>
      <p>在搜索框中输入歌曲名称开始探索音乐</p>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import axios from "axios";
import { store } from '../store.js';
import { ElMessage } from 'element-plus';
import { VideoPlay } from '@element-plus/icons-vue';
import config from '../config'
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}

// 添加到播放列表的函数
const addSong = (song) => {
  // 将歌曲添加到播放队列的最后面
  const currentPlaylist = [...store.playlist];
  // 检查歌曲是否已经在队列中
  const existingIndex = currentPlaylist.findIndex(item => item.songId === song.songId);
  if (existingIndex === -1) {
    // 如果歌曲不在队列中，将其添加到队列最后面
    currentPlaylist.push(song);
  }
  // 更新播放列表，不改变当前歌曲和播放状态
  store.setPlaylist(currentPlaylist);
  // 可以添加一些用户反馈，比如消息提示
  ElMessage.success(`已添加歌曲到播放队列: ${song.name}`);
};
// 选择歌曲的函数
const selectSong = (song) => {
  console.log('选择的歌曲:', song);

  // 将歌曲添加到播放队列的最前面
  const currentPlaylist = [...store.playlist];

  // 检查歌曲是否已经在队列中
  const existingIndex = currentPlaylist.findIndex(item => item.songId === song.songId);

  if (existingIndex !== -1) {
    // 如果歌曲已存在，将其移到最前面
    currentPlaylist.splice(existingIndex, 1);
  }

  // 将歌曲添加到队列最前面
  currentPlaylist.unshift(song);

  // 更新播放列表
  store.setPlaylist(currentPlaylist);

  // 设置当前歌曲为第一首（新添加的歌曲）
  store.setCurrentIndex(0);
  store.setCurrentSong(song);

  // 可以添加一些用户反馈，比如消息提示
  ElMessage.success(`已添加歌曲到播放队列: ${song.name}`);
};
const searchQuery = ref('');
const musics=ref([])
const searchSongs = () => {
  if (searchQuery.value) {
    axios.get(`${config.api}/music/selectByLike`,{
      params:{name:searchQuery.value}
    }).then(response=>{
      if (response.data){
        musics.value=response.data
      }
    })
  } else {
    console.log('请输入歌曲名称');
  }
};
</script>

<style scoped>
.search-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 搜索区域 */
.search-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 25px;
  padding: 40px 30px;
  margin-bottom: 30px;
  text-align: center;
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.search-section::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50% 0 0 50%;
  transform: translateX(100px);
}

.search-header {
  position: relative;
  z-index: 2;
  margin-bottom: 30px;
}

.search-title {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  margin-bottom: 10px;
}

.title-icon {
  font-size: 36px;
  color: #ffd700;
}

.search-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.search-box {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: center;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50px;
  padding: 8px 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  width: 600px;
  max-width: 90%;
  backdrop-filter: blur(10px);
}

.search-input {
  flex-grow: 1;
  border: none;
  outline: none;
  font-size: 16px;
  padding: 12px 15px;
  background: transparent;
}

.search-input :deep(.el-input__inner) {
  border: none;
  background: transparent;
  font-size: 16px;
  color: #2c3e50;
}

.search-input :deep(.el-input__inner::placeholder) {
  color: #95a5a6;
}

.search-icon {
  color: #667eea;
  font-size: 20px;
  margin-right: 10px;
}

.search-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  min-width: 100px;
}

.search-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.search-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 搜索结果区域 */
.results-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.results-header {
  padding: 25px 30px;
  border-bottom: 2px solid #f8f9fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.results-title {
  color: #2c3e50;
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.results-title i {
  color: #667eea;
  font-size: 26px;
}

.music-table-container {
  padding: 20px;
}

.music-table {
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

/* 歌曲封面样式 */
.song-cover-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.song-cover-wrapper:hover {
  transform: scale(1.05);
}

.song-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.song-cover-wrapper:hover .cover-overlay {
  opacity: 1;
}

.play-icon {
  color: #fff;
  font-size: 20px;
}

/* 歌曲信息样式 */
.song-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.song-name {
  font-weight: bold;
  color: #2c3e50;
  font-size: 16px;
  line-height: 1.2;
}

.song-artist {
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

.song-album {
  color: #95a5a6;
  font-size: 12px;
}

.album-info {
  display: flex;
  align-items: center;
}

.album-name {
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.action-btn {
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%) !important;
  transform: scale(1.01);
  transition: all 0.3s ease;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

/* 空状态和初始状态 */
.empty-state, .initial-state {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
  padding: 80px 20px;
  color: #7f8c8d;
}

.empty-icon, .initial-icon {
  font-size: 80px;
  margin-bottom: 20px;
  color: #bdc3c7;
}

.empty-state h3, .initial-state h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.empty-state p, .initial-state p {
  font-size: 16px;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-section {
    padding: 30px 20px;
  }
  
  .search-title {
    font-size: 24px;
  }
  
  .search-input-wrapper {
    width: 100%;
    max-width: 400px;
  }
  
  .results-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .results-title {
    font-size: 20px;
  }
}
</style>
