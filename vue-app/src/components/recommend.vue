<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import { store } from '../store.js';
import {useUserStore} from '../stores/user'
import { ElMessage } from 'element-plus';
import { VideoPlay } from '@element-plus/icons-vue';
import config from '../config'
const userStore=useUserStore()
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
// Ref to store the fetched music data
const musics = ref<any[]>([]);
const dialogVisible = ref(false); // 弹窗的显示状态
const selectedPlaylist = ref(null); // 存储选择的歌单
const playlists = ref([]); // 存储歌单数据
const songId=ref(null)
// Function to fetch data from the server
const getData = () => {
  axios.get(`${config.api}/music/recommend`).then((response) => {
    if (response.data) {
      musics.value = response.data; // Storing fetched data into musics
    }
  });
};
// Fetch the data when the component is mounted
onMounted(() => {
  getData();
  getPlaylists()
});
const getPlaylists = () => {
  axios.get(`${config.api}/music/getPlaylistByUserId?userId=${userStore.uid}`).then((response) => {
    if (response.data) {
      playlists.value = response.data;
    }
  });
};
const addSong = (song) => {

  // 将歌曲添加到播放队列的最后面
  const currentPlaylist = [...store.playlist];
  // 检查歌曲是否已经在队列中
  const existingIndex = currentPlaylist.findIndex(item => item.songId === song.songId);
  if (existingIndex === -1) {
    // 如果歌曲不在队列中，将其添加到队列最后面
    currentPlaylist.push(song);
  }
  store.setPlaylist(currentPlaylist);
  ElMessage.success(`已添加歌曲到播放队列: ${song.name}`);
};
// 选择歌曲的函数
const selectSong = (song) => {
  console.log('选择的歌曲:', song);
  const currentPlaylist = [...store.playlist];
  const existingIndex = currentPlaylist.findIndex(item => item.songId === song.songId);
  if (existingIndex !== -1) {
    currentPlaylist.splice(existingIndex, 1);
  }
  currentPlaylist.unshift(song);
  store.setPlaylist(currentPlaylist);
  store.setCurrentIndex(0);
  store.setCurrentSong(song);
  ElMessage.success(`已添加歌曲到播放队列: ${song.name}`);
};
const addList = (row) => {

  console.log('点击了按钮')
  songId.value=row.songId
  console.log('sid'+songId.value)
  dialogVisible.value = true;
};
const saveToPlaylist = () => {
  if (!selectedPlaylist.value) {
    ElMessage.error("请选择一个歌单");
    return;
  }
  // 假设我们有一个API接口将歌曲保存到歌单中
  axios.put(`${config.api}/music/addToPlaylist`,{
    params:{playlistId:selectedPlaylist.value,
    songId:songId}
  }).then((response) => {
    if (response.data.success) {
      ElMessage.success("已成功保存到歌单");
      dialogVisible.value = false;
    } else {
      ElMessage.error("保存失败");
    }
  });
};
</script>

<template>
  <div class="recommend-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-star-on title-icon"></i>
          推荐歌曲
        </h2>
        <p class="page-subtitle">发现更多好音乐，让音乐陪伴您的每一天</p>
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
          <el-table-column label="操作" width="150" fixed="right">
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
                <el-button 
                  @click.stop="addList(row)" 
                  type="success" 
                  size="small"
                  class="action-btn playlist-btn"
                >
                  <el-icon><Plus /></el-icon>
                  收藏
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
        <h3>暂无推荐歌曲</h3>
        <p>稍后再来看看有什么新歌吧</p>
      </div>
    </div>

    <!-- 歌单选择弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="选择歌单"
      width="400px"
      class="playlist-dialog"
    >
      <div class="dialog-content">
        <div class="dialog-icon">
          <i class="el-icon-folder-add"></i>
        </div>
        <p class="dialog-text">选择要添加到哪个歌单</p>
        <el-select 
          v-model="selectedPlaylist" 
          placeholder="选择一个歌单"
          class="playlist-select"
        >
          <el-option 
            v-for="playlist in playlists" 
            :key="playlist.id" 
            :label="playlist.name" 
            :value="playlist.id" 
          />
        </el-select>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="saveToPlaylist" class="confirm-btn">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<style scoped>
.recommend-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);
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

.add-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.playlist-btn {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.playlist-btn:hover {
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

/* 弹窗样式 */
.playlist-dialog :deep(.el-dialog) {
  border-radius: 20px;
  overflow: hidden;
}

.playlist-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 20px 30px;
}

.playlist-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 20px;
  font-weight: bold;
}

.playlist-dialog :deep(.el-dialog__body) {
  padding: 30px;
}

.dialog-content {
  text-align: center;
}

.dialog-icon {
  font-size: 60px;
  color: #667eea;
  margin-bottom: 20px;
}

.dialog-text {
  font-size: 16px;
  color: #2c3e50;
  margin-bottom: 25px;
}

.playlist-select {
  width: 100%;
}

.playlist-select :deep(.el-input__inner) {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  padding: 12px 15px;
  font-size: 14px;
}

.playlist-select :deep(.el-input__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding-top: 20px;
}

.cancel-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  color: #6c757d;
  border-radius: 8px;
  padding: 10px 20px;
}

.cancel-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.confirm-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  border-radius: 8px;
  padding: 10px 20px;
}

.confirm-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
