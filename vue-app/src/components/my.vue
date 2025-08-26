<template>
  <div class="my-component">
    <div class="cards-row">
      <el-card class="card" @click="goToPlaybackHistory">
        <div class="card-content">
          <h3>播放历史</h3>
        </div>
      </el-card>
      <el-card class="card" @click="goToMyFavorites">
        <div class="card-content">
          <h3>我的收藏</h3>
        </div>
      </el-card>
    </div>
    <div class="tabs-row">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="我的歌单" @click="showMyPlaylist" name="myPlaylists">
          <!-- 这里可以添加我的歌单的内容 -->
        </el-tab-pane>
        <el-tab-pane label="收藏歌单" @click="showMyStatPlaylist" name="myFavorites">
          <!-- 这里可以添加收藏的歌单的内容 -->
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="playlist-container">
      <div class="playlist-list">
        <div
            v-for="(playlist, index) in playlists"
            :key="index"
            class="playlist-item"
            @click="goToPlaylist(playlist.id)"
        >
          <img :src="imgUrl(playlist.image)" alt="Playlist Cover" class="cover" />
          <div class="details">
            <h3 class="name">{{ playlist.name }}</h3>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import { ElMessage } from 'element-plus';
import {useRouter} from "vue-router";
import axios from "axios";
const router=useRouter()
import config from '../config'

const activeTab = ref('myPlaylists');
const imgUrl = (filename) => {
  return `${config.api}/music/` + filename
}

const goToPlaybackHistory = () => {
  // 这里可以加入路由跳转逻辑
  router.push('/record')
};

const goToMyFavorites = () => {
  // 这里可以加入路由跳转逻辑
  router.push('/myfaovrites')
};

const showMyPlaylist=()=>{
  axios.get(``)
}
const showMyStatPlaylist=()=>{

}
const playlists = ref([])

const getData = () => {
  axios.get(`${config.api}/music/getAllPlaylist`).then(response => {
    if (response.data) {
      playlists.value = response.data
    }
  })
}
onMounted(() => {
  getData()
})

// 跳转到歌单详情页面的函数
const goToPlaylist = (id) => {
  router.push({
    path: '/playlistInfo',
    query: { playlistId: id }
  })
}
</script>

<style scoped>
.my-component {
  padding: 20px;
}

.cards-row {
  display: flex;
  gap: 20px;
}

.card {
  width: 100%;
  cursor: pointer;
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 120px;
}

.tabs-row {
  margin-top: 20px;
}

.playlists-row {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.playlist-card {
  width: 200px;
  cursor: pointer;
}
.playlist-container {
  padding: 20px;
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.playlist-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.playlist-item {
  width: 200px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.playlist-item:hover {
  transform: scale(1.05);
}

/* 修改这里 */
.cover {
  width: 100%;
  height: 300px; /* 可以根据需要调整高度 */
  border-radius: 8px;
  object-fit: cover; /* 使图像填充容器并保持比例 */
}

.details {
  padding: 10px 0;
}

.name {
  font-size: 18px;
  font-weight: bold;
}

</style>
