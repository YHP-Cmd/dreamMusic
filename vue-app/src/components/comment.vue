<template>
  <div class="comment-section">
    <div v-if="song" class="singer-info">
      <div class="avatar">
        <img :src="imgUrl(song.image)" alt="Singer Avatar" />
      </div>
      <div class="info">
        <h2>{{ song.name }}</h2>
        <p class="bio">{{ song.singerName}}</p>
      </div>
    </div>
    <div class="comment-input">
      <textarea v-model="newComment" placeholder="写下你的评论..." rows="4"></textarea>
      <button @click="submitComment" :disabled="!newComment.trim()">发表评论</button>
    </div>
    <!-- 评论列表 -->
    <div v-if="comments.length" class="comment-list">
      <div v-for="comment in comments" :key="comment.commnet_id" class="comment-item">
        <div class="comment-author">{{ comment.commenterName }}</div>
        <div class="comment-content">{{ comment.content}}</div>
        <div class="comment-time">{{ formatTime(comment.time) }}</div>
        <div class="queue-control"><el-button><el-icon><ChatSquare /></el-icon></el-button></div>
      </div>
    </div>
    <div v-else>
      <p>暂无评论</p>
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
  return `${config.api}/music/`+filename
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
  font-family: Arial, sans-serif;
  margin: 20px;
}
.queue-control {
  justify-content: center;
  align-items: center;
}

.comment-list {
  margin-bottom: 20px;
}

.comment-item {
  border-bottom: 1px solid #f0f0f0;
  padding: 10px 0;
}

.comment-author {
  font-weight: bold;
  color: #1a73e8;
}

.comment-content {
  margin-top: 5px;
}

.comment-time {
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}

.comment-input {
  margin-top: 20px;
}

.comment-input textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 14px;
  resize: none;
}

.comment-input button {
  margin-top: 10px;
  padding: 8px 20px;
  background-color: #1a73e8;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.comment-input button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
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
