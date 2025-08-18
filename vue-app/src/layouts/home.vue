<template>
  <div class="common-layout">
    <el-container style="height: 100%; width: 100%">
      <!-- Header Section -->
      <el-header class="header-content">
        <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
        >
          <el-menu-item index="recommend">推荐</el-menu-item>
          <span style="width: 10px"/>
          <el-menu-item index="singer">歌手</el-menu-item>
          <span style="width: 10px"/>
          <el-menu-item index="ranking">排行</el-menu-item>
          <span style="width: 10px"/>
          <el-menu-item index="playlist">歌单</el-menu-item>
          <span style="width: 10px"/>
          <el-menu-item index="my">我的</el-menu-item>
          <span style="width: 10px"/>
          <el-menu-item index="select">
            <el-icon><Search /></el-icon>
          </el-menu-item>
        </el-menu>
      </el-header>

      <!-- Main Section -->
      <el-main class="main-content">
        <router-view/>
      </el-main>

      <el-footer class="footer-content">
        <el-row type="flex" justify="space-between" align="middle">
          <el-col :span="4" class="song-info">
            <span>歌曲名称 - 歌手名</span>
          </el-col>
          <el-col :span="12" class="player-controls" style="text-align: center;">
            <el-button icon="el-icon-play" @click="togglePlay">播放</el-button>
            <el-button icon="el-icon-pause" @click="togglePause">暂停</el-button>
            <el-button icon="el-icon-stop" @click="stopPlayer">停止</el-button>
          </el-col>
          <el-col :span="4" class="volume-control">
            <el-slider v-model="volume" :min="0" :max="100" show-input></el-slider>
          </el-col>
        </el-row>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const activeIndex = ref('1')
const volume = ref(50)  // Volume level, can range from 0 to 100

const handleSelect = (key: string) => {
  router.push("/" + key)
}

// Control functions for the player
const togglePlay = () => {
  console.log("Playing music...")
}

const togglePause = () => {
  console.log("Pausing music...")
}

const stopPlayer = () => {
  console.log("Stopping music...")
}
</script>

<style scoped>
.common-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.el-header {
  background-color: #fff;
  padding: 0 20px;
  border-bottom: 1px solid #e4e7ed;
}

.el-footer {
  background-color: #5b9cdc;
  color: #fff;
  padding: 10px;
  display: flex;
  justify-content: center;
}

.song-info {
  font-size: 16px;
  color: #fff;
}

.player-controls {
  display: flex;
  justify-content: center;
  align-items: center;
}

.volume-control {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.el-row {
  width: 100%;
}

.el-col {
  display: flex;
  align-items: center;
}

.el-button {
  margin: 0 10px;
}
</style>
