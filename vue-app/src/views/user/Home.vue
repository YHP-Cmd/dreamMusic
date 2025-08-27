<template>
  <div class="user-home">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <el-carousel height="300px" indicator-position="outside">
        <el-carousel-item v-for="(item, index) in banners" :key="index">
          <div class="banner-item" :style="{ backgroundImage: `url(${item.image})` }">
            <div class="banner-content">
              <h2 class="banner-title">{{ item.title }}</h2>
              <p class="banner-desc">{{ item.description }}</p>
              <el-button type="primary" size="large" round>立即收听</el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <!-- 推荐内容 -->
    <div class="content-sections">
      <!-- 推荐歌单 -->
      <section class="section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><List /></el-icon>
            推荐歌单
          </h3>
          <el-button text type="primary">查看更多 ></el-button>
        </div>
        <div class="playlist-grid">
          <div v-for="playlist in recommendPlaylists" :key="playlist.id" class="playlist-card">
            <div class="playlist-cover">
              <img :src="playlist.cover" :alt="playlist.name" />
              <div class="play-overlay">
                <el-icon class="play-icon"><VideoPlay /></el-icon>
              </div>
            </div>
            <div class="playlist-info">
              <h4 class="playlist-name">{{ playlist.name }}</h4>
              <p class="playlist-desc">{{ playlist.description }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 热门歌曲 -->
      <section class="section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><TrendCharts /></el-icon>
            热门歌曲
          </h3>
          <el-button text type="primary">查看更多 ></el-button>
        </div>
        <div class="song-list">
          <div v-for="(song, index) in hotSongs" :key="song.id" class="song-item">
            <div class="song-rank">{{ index + 1 }}</div>
            <img :src="song.cover" :alt="song.name" class="song-cover" />
            <div class="song-info">
              <div class="song-name">{{ song.name }}</div>
              <div class="song-artist">{{ song.artist }}</div>
            </div>
            <div class="song-actions">
              <el-icon class="action-btn"><VideoPlay /></el-icon>
              <el-icon class="action-btn"><Plus /></el-icon>
              <el-icon class="action-btn"><Star /></el-icon>
            </div>
          </div>
        </div>
      </section>

      <!-- 推荐歌手 -->
      <section class="section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><Microphone /></el-icon>
            推荐歌手
          </h3>
          <el-button text type="primary">查看更多 ></el-button>
        </div>
        <div class="singer-grid">
          <div v-for="singer in recommendSingers" :key="singer.id" class="singer-card">
            <div class="singer-avatar">
              <img :src="singer.avatar" :alt="singer.name" />
            </div>
            <h4 class="singer-name">{{ singer.name }}</h4>
            <p class="singer-desc">{{ singer.description }}</p>
            <el-button size="small" round>关注</el-button>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { List, VideoPlay, TrendCharts, Microphone, Plus, Star } from '@element-plus/icons-vue'

// 轮播图数据
const banners = ref([
  {
    title: '发现好音乐',
    description: '精选全球优质音乐，为你推荐最适合的歌曲',
    image: '/src/assets/image/swiper1.png'
  },
  {
    title: '热门排行榜',
    description: '实时更新的音乐排行榜，掌握最新音乐趋势',
    image: '/src/assets/image/swiper2.png'
  },
  {
    title: '个性化推荐',
    description: '基于你的喜好，智能推荐专属音乐',
    image: '/src/assets/image/swiper3.png'
  }
])

// 推荐歌单
const recommendPlaylists = ref([
  { id: 1, name: '华语流行精选', description: '最新最热的华语流行歌曲', cover: '/src/assets/image/pic1.png' },
  { id: 2, name: '欧美金曲', description: '经典欧美流行音乐合集', cover: '/src/assets/image/pic2.png' },
  { id: 3, name: '日韩热歌', description: '日韩最新流行音乐', cover: '/src/assets/image/pic3.png' },
  { id: 4, name: '怀旧经典', description: '那些年我们一起听过的歌', cover: '/src/assets/image/pic1.png' },
  { id: 5, name: '民谣清新', description: '治愈系民谣音乐', cover: '/src/assets/image/pic2.png' },
  { id: 6, name: '摇滚力量', description: '燃烧激情的摇滚乐', cover: '/src/assets/image/pic3.png' }
])

// 热门歌曲
const hotSongs = ref([
  { id: 1, name: '青花瓷', artist: '周杰伦', cover: '/src/assets/image/pic1.png' },
  { id: 2, name: '稻香', artist: '周杰伦', cover: '/src/assets/image/pic2.png' },
  { id: 3, name: '告白气球', artist: '周杰伦', cover: '/src/assets/image/pic3.png' },
  { id: 4, name: '夜曲', artist: '周杰伦', cover: '/src/assets/image/pic1.png' },
  { id: 5, name: '七里香', artist: '周杰伦', cover: '/src/assets/image/pic2.png' }
])

// 推荐歌手
const recommendSingers = ref([
  { id: 1, name: '周杰伦', description: '华语流行天王', avatar: '/src/assets/image/pic1.png' },
  { id: 2, name: '邓紫棋', description: '实力派女歌手', avatar: '/src/assets/image/pic2.png' },
  { id: 3, name: '林俊杰', description: '创作才子', avatar: '/src/assets/image/pic3.png' },
  { id: 4, name: '陈奕迅', description: '歌神', avatar: '/src/assets/image/pic1.png' }
])
</script>

<style scoped>
.user-home {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 轮播图区域 */
.banner-section {
  margin-bottom: 40px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.banner-item {
  height: 300px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  position: relative;
}

.banner-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.3));
}

.banner-content {
  position: relative;
  z-index: 1;
  color: white;
  padding: 0 60px;
}

.banner-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 16px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.banner-desc {
  font-size: 18px;
  margin-bottom: 24px;
  opacity: 0.9;
}

/* 内容区域 */
.content-sections {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.section {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.section-title .el-icon {
  color: #667eea;
}

/* 歌单网格 */
.playlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 20px;
}

.playlist-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.playlist-card:hover {
  transform: translateY(-4px);
}

.playlist-cover {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
  aspect-ratio: 1;
}

.playlist-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.play-overlay {
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
  transition: all 0.3s ease;
}

.playlist-card:hover .play-overlay {
  opacity: 1;
}

.play-icon {
  font-size: 32px;
  color: white;
}

.playlist-info {
  text-align: center;
}

.playlist-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.playlist-desc {
  font-size: 12px;
  color: #666;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 歌曲列表 */
.song-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.song-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  cursor: pointer;
}

.song-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(4px);
}

.song-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: #667eea;
  font-size: 14px;
}

.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
}

.song-info {
  flex: 1;
  min-width: 0;
}

.song-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
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

.song-actions {
  display: flex;
  gap: 12px;
  opacity: 0;
  transition: all 0.3s ease;
}

.song-item:hover .song-actions {
  opacity: 1;
}

.action-btn {
  font-size: 16px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  color: #667eea;
  transform: scale(1.1);
}

/* 歌手网格 */
.singer-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 20px;
}

.singer-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.singer-card:hover {
  transform: translateY(-4px);
}

.singer-avatar {
  width: 100px;
  height: 100px;
  margin: 0 auto 12px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.singer-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.singer-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
}

.singer-desc {
  font-size: 12px;
  color: #666;
  margin: 0 0 12px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-home {
    padding: 16px;
  }
  
  .banner-content {
    padding: 0 30px;
  }
  
  .banner-title {
    font-size: 28px;
  }
  
  .banner-desc {
    font-size: 16px;
  }
  
  .playlist-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 16px;
  }
  
  .singer-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 16px;
  }
  
  .singer-avatar {
    width: 80px;
    height: 80px;
  }
  
  .section {
    padding: 20px;
  }
  
  .content-sections {
    gap: 30px;
  }
}
</style>