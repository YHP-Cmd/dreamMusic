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
    <h2 class="page-title">推荐歌曲</h2>
    <el-table :data="musics"
              :show-header="false"
              highlight-current-row
              @current-change="selectSong"
              class="music-table" stripe>
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img :src="imgUrl(row.image)" alt="封面" class="song-cover" />
        </template>
      </el-table-column>

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
          <el-button @click.stop="addSong(row)" type="primary" size="small" >
            <el-icon><VideoPlay /></el-icon>
          </el-button>
          <el-button @click.stop="addList(row)" type="primary" size="small" >
            <el-icon><Plus/></el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 歌单选择弹窗 -->
    <el-dialog v-model="dialogVisible" title="选择歌单">
      <el-select v-model="selectedPlaylist" placeholder="选择一个歌单">
        <el-option v-for="playlist in playlists" :key="playlist.id" :label="playlist.name" :value="playlist.id" />
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveToPlaylist">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>


<style scoped>
/* 设置表格背景色 */
.music-table {
  background-color: #00fdd5;  /* 可以设置为任何颜色 */
}

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
.icon-button {
  border: none;           /* 移除边框 */
  background: transparent; /* 移除背景 */
  padding: 0;            /* 移除内边距 */
  cursor: pointer;       /* 鼠标悬停时变为手型 */
  font-size: 24px;       /* 定义图标大小 */
}
</style>
