<template>
  <div v-if="singer" class="singer-info">
    <div class="avatar">
      <img :src="imgUrl(singer.image)" alt="Singer Avatar" />
    </div>
    <div class="info">
      <h2>{{ singer.name }}</h2>
      <p class="bio">{{ singer.type }}</p>
    </div>
  </div>
  <el-container>


  <el-header class="header-content">
    <el-menu
        :default-active="activeIndex"
        class="el-menu-demo"
        mode="horizontal"
        @select="handleSelect"
    >
      <el-menu-item index="songInfo">单曲</el-menu-item>
      <el-menu-item index="album">专辑</el-menu-item>
    </el-menu>
  </el-header>
    <el-main>
      <router-view/>
    </el-main>
  </el-container>
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
.singer-info {
  display: flex;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.avatar img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
}

.info h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.bio {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}

.songs h3 {
  margin: 10px 0;
  font-size: 18px;
  color: #333;
}

.songs ul {
  list-style-type: none;
  padding: 0;
}

.songs li {
  font-size: 16px;
  color: #333;
}
</style>
