<template>
  <div class="my-component">
    <!-- 顶部卡片区域 -->
    <div class="cards-row">
      <div class="card-wrapper" @click="goToPlaybackHistory">
        <div class="feature-card history-card">
          <div class="card-icon">
            <i class="el-icon-time"></i>
          </div>
          <div class="card-content">
            <h3>播放历史</h3>
            <p>查看您的听歌记录</p>
          </div>
          <div class="card-overlay"></div>
        </div>
      </div>
      
      <div class="card-wrapper" @click="goToMyFavorites">
        <div class="feature-card favorites-card">
          <div class="card-icon">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="card-content">
            <h3>我的收藏</h3>
            <p>管理您喜爱的音乐</p>
          </div>
          <div class="card-overlay"></div>
        </div>
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
          <span>我的歌单</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === '2' }"
          @click="handleSelect('2')"
        >
          <span>收藏歌单</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === '3' }"
          @click="handleSelect('3')"
        >
          <span>新建歌单</span>
        </div>
      </div>
    </div>

    <!-- 歌单列表区域 -->
    <div class="playlist-container">
      <div class="playlist-grid">
        <div
          v-for="(playlist, index) in playlists"
          :key="index"
          class="playlist-card"
          @click="goToPlaylist(playlist.id)"
        >
          <div class="playlist-cover">
            <img :src="imgUrl(playlist.image)" alt="Playlist Cover" class="cover-image" />
            <div class="playlist-overlay">
              <div class="play-button">
                <i class="el-icon-video-play"></i>
              </div>
            </div>
          </div>
          <div class="playlist-info">
            <h3 class="playlist-name">{{ playlist.name }}</h3>
            <p class="playlist-count">{{ playlist.songCount || 0 }} 首歌曲</p>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="playlists.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-folder"></i>
        </div>
        <h3>暂无歌单</h3>
        <p>创建您的第一个歌单开始收藏音乐吧</p>
        <el-button type="primary" @click="handleSelect('3')" class="create-btn">
          创建歌单
        </el-button>
      </div>
    </div>
  </div>

  <!-- 新建歌单弹窗 -->
  <el-dialog
    title="新建歌单"
    v-model="dialogVisible"
    width="500px"
    @close="resetForm"
    class="playlist-dialog"
  >
    <el-form :model="newPlaylistForm" ref="newPlaylistFormRef" label-width="80px">
      <el-form-item label="歌单名称" prop="name" :rules="[{ required: true, message: '请输入歌单名称', trigger: 'blur' }]">
        <el-input 
          v-model="newPlaylistForm.name" 
          placeholder="请输入歌单名称"
          class="form-input"
        ></el-input>
      </el-form-item>

      <el-form-item label="简介" prop="description" :rules="[{ required: true, message: '请输入简介', trigger: 'blur' }]">
        <el-input 
          type="textarea" 
          v-model="newPlaylistForm.description" 
          placeholder="请输入歌单简介"
          :rows="3"
          class="form-textarea"
        ></el-input>
      </el-form-item>

      <el-form-item label="上传封面">
        <div class="upload-section">
          <el-upload
            class="upload-area"
            drag
            :action="`${config.api}/users/upload`"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :show-file-list="false"
            accept=".jpg,.jpeg,.png"
          >
            <div class="upload-content">
              <i class="el-icon-plus upload-icon"></i>
              <div class="upload-text">点击或拖拽上传封面</div>
            </div>
          </el-upload>
          
          <div v-if="newPlaylistForm.image" class="uploaded-preview">
            <img :src="imgUrl(newPlaylistForm.image)" alt="封面" class="preview-image" />
            <div class="preview-overlay">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </div>
      </el-form-item>

      <el-form-item class="dialog-actions">
        <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading" class="submit-btn">
          创建歌单
        </el-button>
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
const activeTab = ref('1');
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
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cards-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.card-wrapper {
  position: relative;
  cursor: pointer;
  overflow: hidden;
  border-radius: 15px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-wrapper:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.15);
}

.feature-card {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  background-color: #fff;
  position: relative;
  overflow: hidden;
}

.history-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.favorites-card {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
}

.card-icon {
  font-size: 40px;
  color: #fff;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1;
}

.card-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 15px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  text-align: center;
  z-index: 2;
}

.card-content h3 {
  margin-bottom: 5px;
  font-size: 20px;
}

.card-content p {
  font-size: 14px;
  opacity: 0.8;
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%);
  z-index: 0;
}

.tabs-section {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  padding: 10px 20px;
  margin-bottom: 20px;
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
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: bold;
  color: #606266;
  border: 1px solid transparent;
}

.tab-item:hover {
  color: #409eff;
  border-color: #dcdfe6;
}

.tab-item.active {
  color: #409eff;
  border-color: #409eff;
  background-color: #ecf5ff;
}

.playlist-container {
  flex-grow: 1;
  background-color: #fff;
  border-radius: 15px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto; /* Enable scrolling for the container */
}

.playlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.playlist-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.playlist-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.playlist-cover {
  position: relative;
  width: 100%;
  height: 200px; /* Adjust height as needed */
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px 12px 0 0;
}

.playlist-overlay {
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

.playlist-card:hover .playlist-overlay {
  opacity: 1;
}

.play-button {
  font-size: 40px;
  color: #fff;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  transition: background-color 0.3s ease;
}

.playlist-card:hover .play-button {
  background-color: rgba(0, 0, 0, 0.7);
}

.playlist-info {
  padding: 15px;
  background-color: #f9f9f9;
  border-radius: 0 0 12px 12px;
}

.playlist-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.playlist-count {
  font-size: 14px;
  color: #666;
}

.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #909399;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 15px;
  color: #dcdfe6;
}

.empty-state h3 {
  font-size: 22px;
  margin-bottom: 10px;
}

.empty-state p {
  font-size: 16px;
  margin-bottom: 25px;
}

.create-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 25px;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  cursor: pointer;
  transition: background 0.3s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.create-btn:hover {
  background: linear-gradient(135deg, #35bdd0 0%, #18c2d6 100%);
}

.playlist-dialog .el-dialog__header {
  background-color: #409eff;
  color: #fff;
  border-bottom: 1px solid #409eff;
}

.playlist-dialog .el-dialog__title {
  color: #fff;
}

.playlist-dialog .el-dialog__body {
  padding: 30px 40px;
}

.playlist-dialog .el-form-item__label {
  color: #333;
}

.playlist-dialog .el-input__inner,
.playlist-dialog .el-textarea__inner {
  border-radius: 8px;
  border-color: #dcdfe6;
  padding: 10px 15px;
}

.playlist-dialog .el-input__inner:focus,
.playlist-dialog .el-textarea__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px #409eff;
}

.playlist-dialog .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}

.playlist-dialog .el-button {
  border-radius: 8px;
  padding: 10px 25px;
  font-size: 16px;
}

.playlist-dialog .el-button--primary {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
}

.playlist-dialog .el-button--primary:hover {
  background: linear-gradient(135deg, #35bdd0 0%, #18c2d6 100%);
}

.playlist-dialog .el-button--text {
  color: #606266;
}

.playlist-dialog .el-button--text:hover {
  color: #409eff;
}

.upload-section {
  position: relative;
  margin-bottom: 20px;
}

.upload-area {
  width: 100%;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.upload-area:hover {
  border-color: #409eff;
}

.upload-content {
  text-align: center;
  color: #909399;
}

.upload-icon {
  font-size: 28px;
  margin-bottom: 10px;
}

.upload-text {
  font-size: 14px;
}

.uploaded-preview {
  position: relative;
  margin-top: 10px;
  text-align: center;
}

.preview-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.uploaded-preview:hover .preview-overlay {
  opacity: 1;
}

.preview-overlay i {
  font-size: 24px;
  color: #fff;
}

.dialog-actions {
  text-align: right;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #f5f7fa;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.cancel-btn:hover {
  background-color: #ebeef5;
  border-color: #dcdfe6;
}

.submit-btn {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #35bdd0 0%, #18c2d6 100%);
}
</style>
