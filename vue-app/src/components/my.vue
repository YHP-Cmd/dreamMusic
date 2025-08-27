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
      <el-header class="header-content">
        <el-menu
            :default-active="'1'"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
        >
          <el-menu-item index="1">我的歌单</el-menu-item>
          <el-menu-item index="2">收藏歌单</el-menu-item>
          <el-menu-item index="3">新建歌单</el-menu-item>
        </el-menu>
      </el-header>
    </div>

    <div class="playlist-container" >
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

  <el-dialog
      title="新建歌单"
      v-model="dialogVisible"
      width="40%"
      @close="resetForm"
  >
    <el-form :model="newPlaylistForm" ref="newPlaylistFormRef" label-width="80px">
      <el-form-item label="歌单名称" prop="name" :rules="[{ required: true, message: '请输入歌单名称', trigger: 'blur' }]">
        <el-input v-model="newPlaylistForm.name" placeholder="请输入歌单名称"></el-input>
      </el-form-item>

      <el-form-item label="简介" prop="description" :rules="[{ required: true, message: '请输入简介', trigger: 'blur' }]">
        <el-input type="textarea" v-model="newPlaylistForm.description" placeholder="请输入歌单简介"></el-input>
      </el-form-item>

      <el-form-item label="上传封面">
        <el-upload
            class="upload-demo"
            drag
            :action="`${config.api}/users/upload`"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
            accept=".jpg,.jpeg,.png"
        >
        <i class="el-icon-plus"></i>
        </el-upload>
        <div v-if="newPlaylistForm.image" class="uploaded-image-preview">
          <img :src="imgUrl(newPlaylistForm.image)" alt="封面" class="preview-img" />
        </div>
      </el-form-item>

      <el-form-item>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">创建歌单</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import { ElMessage } from 'element-plus';
import {useRouter} from "vue-router";
import axios from "axios";
const router=useRouter()
import config from '../config'
import {useUserStore} from '../stores/user'
const userStore=useUserStore()
const tag=ref(false)
const activeTab = ref('myPlaylists');
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
const dialogVisible = ref(false);
const loading = ref(false);
const newPlaylistForm = ref({
  name: '',
  description: '',
  image: '' // 上传的封面图片URL
});

const newPlaylistFormRef = ref();

// 打开新建歌单弹窗
const openNewPlaylistDialog = () => {
  dialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  newPlaylistForm.value = {
    name: '',
    description: '',
    image: ''
  };
};

// 处理上传图片成功
const handleUploadSuccess = (response: any, file: any) => {
  if (response.code === 200) {
    newPlaylistForm.value.image = response.data; // 返回的是文件名
    ElMessage.success('图片上传成功!');
  } else {
    ElMessage.error('图片上传失败!');
  }
};

// 限制上传图片大小和格式
const beforeUpload = (file: any) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;
  
  if (!isJPG && !isPNG) {
    ElMessage.error('上传图片只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!');
    return false;
  }
  return true;
};

// 处理上传错误
const handleUploadError = (error: any) => {
  console.error('上传失败:', error);
  ElMessage.error('图片上传失败，请重试');
};

// 提交新建歌单表单
const submitForm = () => {
  newPlaylistFormRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true;
      const playlistData = {
        name: newPlaylistForm.value.name,
        introduction: newPlaylistForm.value.description,
        image: newPlaylistForm.value.image || 'default_playlist.jpg',
        createId: userStore.uid
      };
      
      axios.post(`${config.api}/music/createPlaylist`, playlistData)
        .then(response => {
          if (response.data) {
            ElMessage.success('歌单创建成功!');
            dialogVisible.value = false;
            resetForm(); // 重置表单
            showMyPlaylist(); // 刷新歌单列表
          } else {
            ElMessage.error('创建失败，请稍后再试');
          }
        })
        .catch(error => {
          console.error('创建歌单失败:', error);
          ElMessage.error('创建失败，请稍后再试');
        })
        .finally(() => {
          loading.value = false;
        });
    } else {
      ElMessage.error('请填写完整信息');
      return false;
    }
  });
};
onMounted(() => {
  showMyPlaylist();  // 初始化时加载“我的歌单”
});
const handleSelect = (key) => {
  if(key==='1'){
    showMyPlaylist()
  }else if(key==='2'){
    showMyStatPlaylist()
  }else if(key==='3'){
    dialogVisible.value = true
  }
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
  axios.get(`${config.api}/music/getPlaylistByUserId?userId=${userStore.uid}`).then(response => {
    if (response.data) {
      playlists.value = response.data
    }
  })
}
const showMyStatPlaylist=()=>{
  axios.get(`${config.api}/music/getPlaylistByStat?userId=${userStore.uid}`).then(response => {
    if (response.data) {
      playlists.value = response.data
    }
  })
}
const playlists = ref([])


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
.upload-demo i {
  font-size: 28px;
}

.upload-demo .el-upload__text {
  font-size: 14px;
  color: #999;
}

.upload-demo .el-upload__dragger {
  height: 120px;
}

.upload-demo .el-upload__list {
  margin-top: 10px;
}

.uploaded-image-preview {
  margin-top: 10px;
  text-align: center;
}

.preview-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
}

.upload-demo .el-upload--picture-card {
  width: 120px;
  height: 120px;
  line-height: 120px;
}

.upload-demo .el-upload--picture-card i {
  font-size: 28px;
  color: #8c939d;
}
</style>
