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
        <el-tab-pane label="我的歌单" name="myPlaylists">
          <!-- 这里可以添加我的歌单的内容 -->
        </el-tab-pane>
        <el-tab-pane label="收藏歌单" name="myFavorites">
          <!-- 这里可以添加收藏的歌单的内容 -->
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="playlists-row">
      <el-card
          v-for="(playlist, index) in createdPlaylists"
          :key="index"
          class="playlist-card"
          @click="goToPlaylist(playlist.id)"
      >
        <div class="playlist-content">
          <h3>{{ playlist.name }}</h3>
          <p>{{ playlist.description }}</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const activeTab = ref('myPlaylists');

// 示例：创建的歌单数据
const createdPlaylists = ref([
  { id: 1, name: '夏日热歌', description: '清凉一夏，尽在此歌单' },
  { id: 2, name: '心情慢歌', description: '悠扬动听，适合放松时聆听' },
]);

const goToPlaybackHistory = () => {
  ElMessage.info('跳转到播放历史');
  // 这里可以加入路由跳转逻辑
};

const goToMyFavorites = () => {
  ElMessage.info('跳转到我的收藏');
  // 这里可以加入路由跳转逻辑
};

const goToPlaylist = (id: number) => {
  ElMessage.info(`跳转到歌单ID为${id}的详情`);
  // 这里可以加入路由跳转逻辑
};
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
</style>
