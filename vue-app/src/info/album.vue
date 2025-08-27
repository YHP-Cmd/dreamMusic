<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { onMounted, ref } from "vue";
import axios from "axios";
import config from '../config';

const router = useRouter();

const imgUrl = (filename) => {
  return `${config.api}/music/image/` + filename;
};

const route = useRoute();
const singerId = route.query.singerId;
const albums = ref([]);
const loading = ref(true);

const getData = () => {
  loading.value = true;
  axios.get(`${config.api}/album/getById`, {
    params: { id: singerId }
  }).then(response => {
    if (response.data) {
      albums.value = response.data;
    }
  }).finally(() => {
    loading.value = false;
  });
};

const handleAlbumClick = (album) => {
  console.log('点击专辑:', album);
  router.push({ path: '/albumInfo', query: { albumId: album.albumId } });
};

onMounted(() => {
  getData();
});
</script>

<template>
  <div class="album-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-folder-opened title-icon"></i>
          专辑列表
        </h2>
        <p class="page-subtitle">发现精彩专辑，聆听完整音乐故事</p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 专辑列表区域 -->
    <div class="album-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-skeleton" v-for="i in 6" :key="i">
          <div class="skeleton-cover"></div>
          <div class="skeleton-info">
            <div class="skeleton-title"></div>
            <div class="skeleton-subtitle"></div>
          </div>
        </div>
      </div>

      <!-- 专辑网格 -->
      <div v-else class="album-grid">
        <div 
          v-for="(album, index) in albums" 
          :key="index" 
          class="album-card"
          @click="handleAlbumClick(album)"
        >
          <div class="album-cover">
            <img :src="imgUrl(album.image)" :alt="album.albumName" class="album-image">
            <div class="album-overlay">
              <div class="play-button">
                <i class="el-icon-video-play"></i>
              </div>
            </div>
          </div>
          <div class="album-info">
            <h3 class="album-name">{{ album.albumName }}</h3>
            <p class="album-artist">{{ album.singerName || '未知歌手' }}</p>
            <p class="album-year">{{ album.releaseYear || '未知年份' }}</p>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && albums.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-folder-opened"></i>
        </div>
        <h3>暂无专辑信息</h3>
        <p>该歌手暂时没有专辑</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.album-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(252, 182, 159, 0.3);
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

/* 专辑列表区域 */
.album-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  overflow: hidden;
}

/* 加载状态 */
.loading-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.loading-skeleton {
  background: #f8f9fa;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}

.skeleton-cover {
  width: 100%;
  height: 280px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

.skeleton-info {
  padding: 20px;
}

.skeleton-title {
  height: 20px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 10px;
}

.skeleton-subtitle {
  height: 16px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
  border-radius: 4px;
  width: 60%;
}

@keyframes loading {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* 专辑网格 */
.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.album-card {
  background: #fff;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.album-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.album-cover {
  position: relative;
  overflow: hidden;
  aspect-ratio: 1;
}

.album-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.album-card:hover .album-image {
  transform: scale(1.05);
}

.album-overlay {
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

.album-card:hover .album-overlay {
  opacity: 1;
}

.play-button {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fcb69f;
  transition: all 0.3s ease;
  border: 2px solid #fff;
}

.play-button:hover {
  background: #fff;
  transform: scale(1.1);
}

.album-info {
  padding: 20px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.album-name {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #2c3e50;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.album-artist {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0 0 5px 0;
  font-weight: 500;
}

.album-year {
  font-size: 12px;
  color: #95a5a6;
  margin: 0;
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
  
  .album-section {
    padding: 20px;
  }
  
  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
  }
  
  .album-info {
    padding: 15px;
  }
  
  .album-name {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .album-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .page-title {
    font-size: 20px;
  }
}
</style>