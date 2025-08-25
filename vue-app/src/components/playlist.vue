<template>
  <div class="playlist-container">
    <h2 class="title">推荐歌单</h2>
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
</template>

<script setup>
import {onMounted, ref} from 'vue'
import config from '../config.js'
import axios from "axios";
import {useRouter} from "vue-router";
const router=useRouter()
const imgUrl=(filename)=>{
  return `${config.api}/music/`+filename
}
const playlists = ref([])

const getData=()=>{
  axios.get(`${config.api}/music/getAllPlaylist`).then(response=>{
    if (response.data){
      playlists.value=response.data
    }
  })
}
onMounted(()=>{
  getData()
})

// 跳转到歌单详情页面的函数
const goToPlaylist = (id) => {
  console.log(`跳转到歌单ID为${id}的详情页`)
  // 可以在此处加入路由跳转功能，例如
  router.push({
    path:'/playlistInfo',
    query:{playlistId:id}
  })
}
</script>

<style scoped>
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

.cover {
  width: 100%;
  border-radius: 8px;
}

.details {
  padding: 10px 0;
}

.name {
  font-size: 18px;
  font-weight: bold;
}

.description {
  font-size: 14px;
  color: #666;
}
</style>
