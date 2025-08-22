<script setup lang="ts">
import {useRoute,useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import axios from "axios";
import config from '../config'
const router=useRouter()


const imgUrl=(filename)=>{
  return `${config.api}/music/`+filename
}
const route = useRoute()
const singerId=route.query.singerId
const albums=ref([])
const loading = ref(true)

const getData=()=>{
  loading.value = true
  axios.get(`${config.api}/album/getById`,{
    params:{id:singerId}
  }).then(response=>{
    if (response.data){
      albums.value=response.data
    }
  }).finally(() => {
    loading.value = false
  })
}

const handleAlbumClick = (album) => {
  // 这里可以添加点击专辑后的逻辑，比如跳转到专辑详情页
  console.log('点击专辑:', album)

  router.push({ path: '/albumInfo', query: { albumId: album.albumId } })
}

onMounted(()=>{
  getData()
})
</script>

<template>
  <div class="album-container">


    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
      <el-skeleton :rows="3" animated />
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
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && albums.length === 0" class="empty-state">
      <i class="el-icon-folder-opened empty-icon"></i>
      <p class="empty-text">暂无专辑信息</p>
    </div>
  </div>
</template>

<style scoped>
.album-container {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.page-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.page-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
}

.loading-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px;
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  padding: 20px;
}

.album-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.album-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 35px rgba(0,0,0,0.2);
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
  background: rgba(0,0,0,0.5);
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
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #667eea;
  transition: all 0.3s ease;
}

.play-button:hover {
  background: white;
  transform: scale(1.1);
}

.album-info {
  padding: 20px;
}

.album-name {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #333;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.album-artist {
  font-size: 0.95rem;
  color: #666;
  margin: 0 0 5px 0;
  font-weight: 500;
}

.album-year {
  font-size: 0.85rem;
  color: #999;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: white;
}

.empty-icon {
  font-size: 4rem;
  opacity: 0.6;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 1.2rem;
  opacity: 0.8;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .album-container {
    padding: 15px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .album-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 20px;
    padding: 15px;
  }
  
  .album-info {
    padding: 15px;
  }
}

@media (max-width: 480px) {
  .album-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .page-title {
    font-size: 1.8rem;
  }
}
</style>