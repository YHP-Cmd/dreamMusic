<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import { store } from '../store.js';
import { ElMessage } from 'element-plus';
import { VideoPlay } from '@element-plus/icons-vue';
import config from '../config'
const imgUrl=(filename)=>{
  return `${config.api}/music/`+filename
}
// Ref to store the fetched music data
const musics = ref<any[]>([]);

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
  <div class="recommend-container">
    <h2 class="page-title">推荐歌曲</h2>
    <el-table :data="musics" :show-header="false" class="music-table" stripe>
      <!-- Song image column -->
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img :src="imgUrl(row.image)" alt="封面" class="song-cover" />
        </template>
      </el-table-column>

      <!-- Song name column -->
      <el-table-column label="歌曲名称" prop="name" min-width="200">
        <template #default="{ row }">
          <div class="song-info">
            <div class="song-name">{{ row.name }}</div>
            <div class="song-intro">{{ row.singerName || '未知歌手' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="专辑" prop="albumName" width="100"></el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button @click="selectSong(row)" type="primary" size="small">
            <el-icon><VideoPlay /></el-icon>
            播放
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.recommend-container {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
  font-weight: bold;
}

.music-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.song-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.song-name {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.song-intro {
  color: #666;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

/* 按钮样式 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
