<template>
  <div class="comment-section">
    <!-- 歌曲信息卡片 -->
    <div v-if="song" class="song-info-card">
      <div class="song-cover">
        <img :src="imgUrl(song.image)" alt="Song Cover" />
        <div class="cover-overlay">
          <i class="el-icon-video-play"></i>
        </div>
      </div>
      <div class="song-details">
        <h2 class="song-title">{{ song.name }}</h2>
        <p class="song-artist">{{ song.singerName }}</p>
        <div class="song-stats">
          <span class="stat-item">
            <i class="el-icon-chat-dot-round"></i>
            {{ comments.length }} 条评论
          </span>
        </div>
      </div>
    </div>

    <!-- 评论输入区域 -->
    <div class="comment-input-section">
      <div class="input-header">
        <i class="el-icon-edit-outline"></i>
        <span>发表评论</span>
      </div>
      <div class="input-container">
        <textarea 
          v-model="newComment" 
          placeholder="写下你的评论..." 
          rows="4"
          class="comment-textarea"
        ></textarea>
        <div class="input-actions">
          <button 
            @click="submitComment" 
            :disabled="!newComment.trim()"
            class="submit-btn"
          >
            <i class="el-icon-s-promotion"></i>
            发表评论
          </button>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comments-section">
      <div class="comments-header">
        <h3 class="comments-title">
          <i class="el-icon-chat-line-round"></i>
          评论 ({{ comments.length }})
        </h3>
      </div>
      
      <div v-if="comments.length" class="comments-list">
        <div 
          v-for="comment in comments" 
          :key="comment.commnet_id" 
          class="comment-card"
        >
          <div class="comment-avatar">
            <div class="avatar-placeholder">
              {{ comment.commenterName ? comment.commenterName.charAt(0).toUpperCase() : 'U' }}
            </div>
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.commenterName }}</span>
              <span class="comment-time">{{ formatTime(comment.time) }}</span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-actions">
              <button class="action-btn">
                <i class="el-icon-chat-square"></i>
                回复
              </button>
              <button class="action-btn">
                <i class="el-icon-star-off"></i>
                点赞
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else class="empty-comments">
        <div class="empty-icon">
          <i class="el-icon-chat-dot-round"></i>
        </div>
        <h3>暂无评论</h3>
        <p>成为第一个评论的人吧！</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import {onMounted, ref} from 'vue';
import {useRoute,useRouter} from "vue-router";
import axios from "axios";
import config from '../config.js'
import {useUserStore} from "../stores/user.js";
import {ElMessage} from "element-plus";
const userStore = useUserStore();
const router=useRouter()
const route=useRoute()
const imgUrl=(filename)=>{
  return `${config.api}/music/image/`+filename
}
const songId=route.query.songId
const song=ref();
const getData=()=>{
  axios.get(`${config.api}/music/selectById`,{
    params:{id:songId}
  }).then(response=>{
    if (response.data){
      song.value=response.data
    }
  })
  axios.get(`${config.api}/music/selectCommentBySongId`,{
    params:{songId:songId}
  }).then(response=>{
    if (response.data){
      comments.value=response.data
    }
  })
}
onMounted(()=>{
  getData()
})
const comments = ref([]);

    // 新评论
    const newComment = ref('');

    // 提交评论
    const submitComment = () => {
      if (newComment.value.trim()) {
        const comment = {
          commenterId:Number(userStore.uid),
          content: newComment.value,
          songId:Number(songId)
        };
        axios.put(`${config.api}/music/addComment`,comment).then(response=>{
          if (response.data){
            ElMessage('评论成功')
            getData()
          }
        })
        console.log('comment:::'+comment)
        newComment.value = ''; // 清空输入框
      }
    };

    // 格式化时间
    const formatTime = (time) => {
      const date = new Date(time);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

</script>

<style scoped>
.comment-section {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 歌曲信息卡片 */
.song-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 25px;
  box-shadow: 0 15px 35px rgba(102, 126, 234, 0.3);
  position: relative;
  overflow: hidden;
}

.song-info-card::before {
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

.song-cover {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  flex-shrink: 0;
}

.song-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
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

.song-cover:hover .cover-overlay {
  opacity: 1;
}

.cover-overlay i {
  color: #fff;
  font-size: 30px;
}

.song-details {
  flex-grow: 1;
  color: #fff;
}

.song-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.song-artist {
  font-size: 18px;
  margin-bottom: 15px;
  opacity: 0.9;
}

.song-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.8;
}

.stat-item i {
  font-size: 16px;
}

/* 评论输入区域 */
.comment-input-section {
  background: #fff;
  border-radius: 20px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.input-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 18px;
  font-weight: bold;
}

.input-header i {
  color: #667eea;
  font-size: 20px;
}

.input-container {
  position: relative;
}

.comment-textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e9ecef;
  border-radius: 15px;
  font-size: 16px;
  resize: none;
  transition: all 0.3s ease;
  font-family: inherit;
  line-height: 1.5;
}

.comment-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.comment-textarea::placeholder {
  color: #95a5a6;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 评论列表区域 */
.comments-section {
  background: #fff;
  border-radius: 20px;
  padding: 25px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.comments-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f8f9fa;
}

.comments-title {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #2c3e50;
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.comments-title i {
  color: #667eea;
  font-size: 22px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-card {
  display: flex;
  gap: 15px;
  padding: 20px;
  border-radius: 15px;
  background: #f8f9fa;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.comment-card:hover {
  background: #fff;
  border-color: #e9ecef;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.comment-avatar {
  flex-shrink: 0;
}

.avatar-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
}

.comment-content {
  flex-grow: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: bold;
  color: #2c3e50;
  font-size: 16px;
}

.comment-time {
  font-size: 12px;
  color: #7f8c8d;
  background: #ecf0f1;
  padding: 4px 8px;
  border-radius: 8px;
}

.comment-text {
  color: #34495e;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 15px;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  background: none;
  border: none;
  color: #7f8c8d;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-btn:hover {
  background: #ecf0f1;
  color: #667eea;
}

.action-btn i {
  font-size: 16px;
}

/* 空状态 */
.empty-comments {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  color: #bdc3c7;
}

.empty-comments h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.empty-comments p {
  font-size: 16px;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .song-info-card {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .song-cover {
    width: 100px;
    height: 100px;
  }
  
  .song-title {
    font-size: 24px;
  }
  
  .comment-card {
    flex-direction: column;
    gap: 10px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>
