<template>
  <div class="ranking-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-trophy title-icon"></i>
          音乐排行榜
        </h2>
        <p class="page-subtitle">发现最受欢迎的音乐，聆听热门歌曲</p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 标签页区域 -->
    <div class="tabs-section">
      <div class="tabs-container">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === '1' }"
          @click="handleSelect('1')"
        >
          <i class="el-icon-video-play tab-icon"></i>
          <span>播放最多</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === '2' }"
          @click="handleSelect('2')"
        >
          <i class="el-icon-star-on tab-icon"></i>
          <span>收藏最多</span>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <router-view/>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRoute } from "vue-router";
import config from "../config.js";
import router from "../router.js";
import sumRank from "../info/sumRank.vue";

const route = useRoute();
const activeTab = ref('1');

const handleSelect = (key) => {
  activeTab.value = key;
  if (key === '2') {
    router.push('/statRank');
  } else {
    router.push('/sumRank1');
  }
};

const imgUrl = (filename) => {
  return `${config.api}/music/image/` + filename;
};

onMounted(() => {
  router.push('/sumRank1');
});
</script>

<style scoped>
.ranking-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(255, 154, 158, 0.3);
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

/* 标签页区域 */
.tabs-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  overflow: hidden;
}

.tabs-container {
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 5px;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 15px 20px;
  cursor: pointer;
  border-radius: 15px;
  transition: all 0.3s ease;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  font-size: 16px;
  position: relative;
  overflow: hidden;
}

.tab-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.tab-item:hover::before {
  opacity: 1;
}

.tab-item.active {
  background: rgba(255, 255, 255, 0.95);
  color: #667eea;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.tab-item.active::before {
  display: none;
}

.tab-icon {
  font-size: 18px;
}

/* 内容区域 */
.content-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 400px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .tabs-container {
    flex-direction: column;
  }
  
  .tab-item {
    padding: 12px 15px;
    font-size: 14px;
  }
}
</style>
