<template>
  <div class="playlist-info-container" v-if="playlist">
    <!-- 歌单头部信息 -->
    <div class="playlist-header">
      <div class="header-background">
        <div class="bg-overlay"></div>
      </div>
      <div class="header-content">
        <div class="playlist-cover">
          <img :src="imgUrl(playlist.image)" alt="Playlist Cover" class="cover-image" />
          <div class="cover-overlay">
            <div class="play-button">
              <i class="el-icon-video-play"></i>
            </div>
          </div>
        </div>
        <div class="playlist-info">
          <h1 class="playlist-name">{{ playlist.name }}</h1>
          <p class="playlist-description">{{ playlist.introduction }}</p>
          <div class="playlist-stats">
            <span class="stat-item">
              <i class="el-icon-music"></i>
              {{ musics.length }} 首歌曲
            </span>
            <span class="stat-item">
              <i class="el-icon-time"></i>
              创建时间: {{ formatTime(playlist.createTime) }}
            </span>
          </div>
          <div class="playlist-actions">
            <el-button @click="playPlaylist" type="primary" class="play-btn">
              <el-icon><CaretRight /></el-icon>
              播放歌单
            </el-button>
            <el-button 
              @click="cancelStat" 
              v-if="store.listStat" 
              class="favorite-btn active"
            >
              <i class="el-icon-star-on"></i>
              已收藏
            </el-button>
            <el-button 
              @click="addStat" 
              v-if="!store.listStat" 
              class="favorite-btn"
            >
              <i class="el-icon-star-off"></i>
              收藏
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 歌曲列表区域 -->
    <div class="songs-section">
      <div class="section-header">
        <h2 class="section-title">
          <i class="el-icon-music"></i>
          歌曲列表
        </h2>
        <span class="song-count">{{ musics.length }} 首歌曲</span>
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

      <!-- 空状态 -->
      <div v-if="musics.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-music"></i>
        </div>
        <h3>暂无歌曲</h3>
        <p>这个歌单还没有歌曲，快去添加一些吧！</p>
      </div>
    </div>
  </div>
  
  <!-- 加载状态 -->
  <div v-else class="loading-state">
    <div class="loading-spinner">
      <i class="el-icon-loading"></i>
    </div>
    <p>正在加载歌单信息...</p>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import config from '../config.js'
import {useRoute} from "vue-router";
import axios from "axios";
import {VideoPlay} from "@element-plus/icons-vue";
import {store} from "../store.js";
import {useUserStore} from "../stores/user.js";
import {ElMessage} from "element-plus";
const route=useRoute()
const useStore=useUserStore()
const playlistId=route.query.playlistId
const musics=ref([])
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
const isStat=()=>{
  axios.get(`${config.api}/music/listIsStat`,{
    params:{listId:playlistId,
      userId:useStore.uid}
  }).then(response=>{
    if (response.data){
      store.listStat=response.data
    }
  })
}
const playlist=ref({})
const getData=()=>{
  console.log('id::'+playlistId)
  axios.get(`${config.api}/music/getPlaylistById`,{
    params:{id:playlistId}
  }).then(response=>{
    if (response.data){
      console.log('res::'+response.data)
      playlist.value=response.data
      console.log('play:::'+playlist.value)
    }
  })
  axios.get(`${config.api}/music/getByPlaylistId`,{
    params:{playlistId:playlistId}
  }).then(response=>{
    if (response.data){
      musics.value=response.data
    }
  })
  isStat()
}
onMounted(()=>{
  getData()
})
// 播放歌单函数
const playPlaylist = () => {

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
const addStat=()=>{//加入收藏
  const myfaovrites={
    userId:useStore.uid,
    playlistId:playlistId
  }
  axios.put(`${config.api}/music/addListStat`,myfaovrites).then(response=>{
    store.listStat=response.data
  })
}
const cancelStat=(songId)=>{//取消收藏
  const myfaovrites={
    userId:useStore.uid,
    playlistId:playlistId
  }
  axios.put(`${config.api}/music/deleteListStat`,myfaovrites).then(response=>{
    store.listStat=!response.data
  })

}

// 格式化时间函数
const formatTime = (time) => {
  if (!time) return '未知时间';
  const date = new Date(time);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};
</script>

<style scoped>
.playlist-info-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 0;
}

/* 歌单头部 */
.playlist-header {
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 30px;
  color: #fff;
  overflow: hidden;
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 1;
}

.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.header-content {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 30px;
  align-items: center;
}

.playlist-cover {
  position: relative;
  width: 200px;
  height: 200px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.cover-image {
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

.playlist-cover:hover .cover-overlay {
  opacity: 1;
}

.play-button {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  transition: all 0.3s ease;
}

.play-button i {
  color: #667eea;
  font-size: 24px;
}

.playlist-info {
  flex-grow: 1;
}

.playlist-name {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 15px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.playlist-description {
  font-size: 18px;
  margin-bottom: 20px;
  opacity: 0.9;
  line-height: 1.5;
}

.playlist-stats {
  display: flex;
  gap: 30px;
  margin-bottom: 25px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  opacity: 0.8;
}

.stat-item i {
  font-size: 18px;
}

.playlist-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.play-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  border: none;
  border-radius: 12px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
  transition: all 0.3s ease;
}

.play-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.favorite-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 16px;
  font-weight: 500;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.favorite-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

.favorite-btn.active {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  border-color: #ffd700;
  color: #333;
}

.favorite-btn.active:hover {
  background: linear-gradient(135deg, #ffed4e 0%, #ffd700 100%);
}

/* 歌曲列表区域 */
.songs-section {
  background: #fff;
  margin: 30px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.section-header {
  padding: 25px 30px;
  border-bottom: 2px solid #f8f9fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #2c3e50;
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.section-title i {
  color: #667eea;
  font-size: 26px;
}

.song-count {
  color: #7f8c8d;
  font-size: 16px;
  font-weight: 500;
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

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  color: #bdc3c7;
}

.empty-state h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.empty-state p {
  font-size: 16px;
  color: #7f8c8d;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  color: #7f8c8d;
}

.loading-spinner {
  font-size: 60px;
  margin-bottom: 20px;
  color: #667eea;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-state p {
  font-size: 18px;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .playlist-cover {
    width: 150px;
    height: 150px;
  }
  
  .playlist-name {
    font-size: 28px;
  }
  
  .playlist-stats {
    flex-direction: column;
    gap: 15px;
  }
  
  .playlist-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  .songs-section {
    margin: 15px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
