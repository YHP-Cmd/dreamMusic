<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import { store } from '../store.js';
import { ElMessage } from 'element-plus';
import { VideoPlay } from '@element-plus/icons-vue';
import config from '../config'
import {useRoute} from "vue-router";

const route = useRoute()
const albumId=route.query.albumId

const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
// Ref to store the fetched music data
const musics = ref<any[]>([]);

// Function to fetch data from the server
const getData = () => {
  axios.get(`${config.api}/music/selectByAlbumId`,{
    params:{albumId:albumId}
  }).then((response) => {
    if (response.data) {
      musics.value = response.data; // Storing fetched data into musics
    }
  });
};

// Fetch the data when the component is mounted
onMounted(() => {
  getData();
  console.log('访问了这个组件')
});

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
</script>

<template>
  <div class="album-info-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-folder title-icon"></i>
          专辑详情
        </h2>
        <p class="page-subtitle">聆听专辑中的每一首精彩歌曲</p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 音乐列表区域 -->
    <div class="music-section">
      <div class="music-table-container">
        <el-table 
          :data="musics" 
          :show-header="false" 
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
                  @click="selectSong(row)" 
                  type="primary" 
                  size="small"
                  class="action-btn play-btn"
                >
                  <el-icon><VideoPlay /></el-icon>
                  播放
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态 -->
      <div v-if="musics.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-folder"></i>
        </div>
        <h3>暂无歌曲</h3>
        <p>该专辑暂时没有歌曲</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.album-info-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(17, 153, 142, 0.3);
}

.header-content {
  position: relative;
  z-index: 2;
}

.page-title {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.title-icon {
  font-size: 36px;
  color: #ffd700;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.header-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.decoration-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 3s ease-in-out infinite;
}

.decoration-circle:nth-child(2) {
  animation-delay: 1s;
}

.decoration-circle:nth-child(3) {
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 音乐列表区域 */
.music-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
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

.play-btn {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.play-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(17, 153, 142, 0.4);
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
</style>
