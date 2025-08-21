<script setup lang="ts">
import {onMounted, ref} from "vue";
import axios from "axios";
import config from "../config"
import {useRouter} from "vue-router";

const router = useRouter()

const imgUrl=(filename)=>{
  return `${config.api}/music/`+filename
}
const singers=ref([])
const getData=()=>{
  axios.get(`${config.api}/singer/getAll`).then(response=>{
    if (response.data){
      singers.value=response.data
    }
  })
}
onMounted(()=>{
  getData()
})
const handleCurrentChange=(val)=>{
  router.push({
    path:'/singerInfo',
    query: {singerId:val.singerId}
  })
}
</script>

<template>
  <div class="recommend-container">
  <el-table
      class="music-table"
      :show-header="false"
      :data="singers"
      stripe
      highlight-current-row
      @current-change="handleCurrentChange"
  >
    <!-- Song image column -->
    <el-table-column label="封面" width="80">
      <template #default="{ row }">
        <img :src="imgUrl(row.image)" alt="封面" class="song-cover" />
      </template>
    </el-table-column>

    <!-- Song name column -->
    <el-table-column label="歌曲名称" prop="name" min-width="200">
      <template #default="{ row }">
        <div class="song-info">
          <div class="song-name">{{ row.name }}</div>
          <div class="song-intro">{{ row.region || '未知歌手' }}</div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="类型" prop="type">
    </el-table-column>
  </el-table>
  </div>
</template>

<style scoped>
.music-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.recommend-container {
  padding: 20px;
}
.song-cover {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>