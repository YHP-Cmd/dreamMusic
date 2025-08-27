<template>
  <el-container>
    <el-header class="header-content">
      <el-menu
          :default-active="sumRank"
          class="el-menu-demo"
          mode="horizontal"
          @select="handleSelect"
      >
        <el-menu-item index="sumRank">播放最多</el-menu-item>
        <el-menu-item index="statRank">收藏最多</el-menu-item>
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
import router from "../router.js";
const route = useRoute()
const handleSelect = (key) => {
  console.log(key)
  router.push(`/${key}`)
}
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
onMounted(()=>{
  router.push('/sumRank')
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
