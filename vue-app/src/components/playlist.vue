<template>
  <div class="playlist-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-folder title-icon"></i>
          歌单广场
        </h2>
        <p class="page-subtitle">发现精选歌单，享受音乐盛宴</p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 歌单列表区域 -->
    <div class="playlist-section">
      <div class="playlist-grid">
        <div
          v-for="(playlist, index) in playlists"
          :key="index"
          class="playlist-card"
          @click="goToPlaylist(playlist.id)"
        >
          <div class="playlist-cover">
            <img :src="imgUrl(playlist.image)" alt="Playlist Cover" class="cover-image" />
            <div class="cover-overlay">
              <div class="play-button">
                <i class="el-icon-video-play"></i>
              </div>
            </div>
          </div>
          <div class="playlist-info">
            <h3 class="playlist-name">{{ playlist.name }}</h3>
            <p class="playlist-count">{{ playlist.songCount || 0 }} 首歌曲</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="playlists.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-folder"></i>
        </div>
        <h3>暂无歌单</h3>
        <p>稍后再来看看有什么新歌单吧</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import config from '../config.js';
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

const imgUrl = (filename) => {
  return `${config.api}/music/image/` + filename;
};

const playlists = ref([]);

const getData = () => {
  axios.get(`${config.api}/music/getAllPlaylist`).then(response => {
    if (response.data) {
      playlists.value = response.data;
    }
  });
};

onMounted(() => {
  getData();
});

// 跳转到歌单详情页面的函数
const goToPlaylist = (id) => {
  console.log(`跳转到歌单ID为${id}的详情页`);
  router.push({
    path: '/playlistInfo',
    query: { playlistId: id }
  });
};
</script>

<style scoped>
.playlist-container {
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

/* 歌单列表区域 */
.playlist-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  overflow: hidden;
}

.playlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 25px;
}

.playlist-card {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  background: #fff;
}

.playlist-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.playlist-cover {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.playlist-card:hover .cover-image {
  transform: scale(1.05);
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

.playlist-card:hover .cover-overlay {
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

.playlist-card:hover .play-button {
  background: #fff;
  transform: scale(1.1);
}

.playlist-info {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.playlist-name {
  font-size: 18px;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.playlist-count {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0;
  font-weight: 500;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .playlist-section {
    padding: 20px;
  }
  
  .playlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
  
  .playlist-cover {
    height: 200px;
  }
  
  .playlist-info {
    padding: 15px;
  }
  
  .playlist-name {
    font-size: 16px;
  }
}
</style>
