<template>
  <div class="singer-info-container">
    <!-- 歌手信息卡片 -->
    <div v-if="singer" class="singer-info-card">
      <div class="singer-avatar">
        <img :src="imgUrl(singer.image)" alt="Singer Avatar" class="avatar-image" />
        <div class="avatar-overlay">
          <i class="el-icon-video-play"></i>
        </div>
      </div>
      <div class="singer-details">
        <h2 class="singer-name">{{ singer.name }}</h2>
        <div class="singer-type">
          <span class="type-badge">{{ singer.type }}</span>
        </div>
        <div class="singer-stats">
          <div class="stat-item">
            <i class="el-icon-music"></i>
            <span>单曲</span>
          </div>
          <div class="stat-item">
            <i class="el-icon-folder"></i>
            <span>专辑</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 导航标签页 -->
    <div class="navigation-section">
      <div class="tabs-container">
        <div 
          class="tab-item" 
          :class="{ active: activeIndex === 'songInfo' }"
          @click="handleSelect('songInfo')"
        >
          <i class="el-icon-music"></i>
          <span>单曲</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeIndex === 'album' }"
          @click="handleSelect('album')"
        >
          <i class="el-icon-folder"></i>
          <span>专辑</span>
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
import  {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";
import config from "../config.js";
import axios from "axios";
import router from "../router.js";
const route = useRoute()
const singerId=route.query.singerId
const singer = ref(null)
const activeIndex=ref("songInfo")
const handleSelect = (key) => {
  console.log(key)
  activeIndex.value = key
  router.push({
    path:"/" + key,
    query: {singerId:singerId}
  })
}
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
const  getData=()=>{
  axios.get(`${config.api}/singer/getById`,{
    params:{id:singerId}
  }).then(response=>{
    if (response.data){
      singer.value=response.data
    }
  })
}
onMounted(()=>{
  getData()
  router.push({
    path:"/songInfo" ,
    query: {singerId:singerId}
  })
})
</script>

<style scoped>
.singer-info-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 歌手信息卡片 */
.singer-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 25px;
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.singer-info-card::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50% 0 0 50%;
  transform: translateX(100px);
}

.singer-avatar {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
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

.singer-avatar:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  color: #fff;
  font-size: 30px;
}

.singer-details {
  flex-grow: 1;
  color: #fff;
}

.singer-name {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.singer-type {
  margin-bottom: 15px;
}

.type-badge {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.singer-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

.stat-item i {
  font-size: 16px;
}

/* 导航标签页 */
.navigation-section {
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  padding: 10px 20px;
}

.tabs-container {
  display: flex;
  gap: 20px;
  justify-content: center;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 5px;
}

.tab-item {
  cursor: pointer;
  padding: 12px 24px;
  border-radius: 10px;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #606266;
  border: 1px solid transparent;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab-item:hover {
  color: #409eff;
  border-color: #dcdfe6;
  background-color: #f5f7fa;
}

.tab-item.active {
  color: #409eff;
  border-color: #409eff;
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.tab-item i {
  font-size: 16px;
}

/* 内容区域 */
.content-section {
  flex-grow: 1;
  background: #fff;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .singer-info-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .singer-avatar {
    width: 100px;
    height: 100px;
  }
  
  .singer-name {
    font-size: 24px;
  }
  
  .singer-stats {
    justify-content: center;
  }
  
  .tabs-container {
    flex-direction: column;
    gap: 10px;
  }
  
  .tab-item {
    justify-content: center;
  }
}
</style>
