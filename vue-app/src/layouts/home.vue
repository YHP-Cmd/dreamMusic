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
         <div class="footer-inner">
           <!-- 左侧：歌曲信息 -->
           <div class="song-info">
             <img
                 :src="imgUrl(store.currentSong?.image)"
                 style="width: 70px;height: 70px;border-radius: 50%; object-fit: cover; animation: rotate 5s linear infinite;"/>
             <div v-if="store.currentSong" style="margin-left: 15px;">
               <div style="font-size: 18px; color: #fff; font-weight: bold; margin-bottom: 5px;">{{ store.currentSong.name }}</div>
               <div style="font-size: 14px; color: #fff; opacity: 0.8;">{{ store.currentSong.singerName || '暂无介绍' }}</div>
             </div>
           </div>
           <div>
<!--             加入收藏-->
             <el-button><el-icon><Star/></el-icon></el-button>
<!--             评论-->
             <el-button><el-icon><ChatDotRound /></el-icon></el-button>
           </div>
           
           <!-- 中间：播放控制和进度条 -->
           <div class="center-section">
             <div class="control-buttons">
               <el-button @click="previousSong" type="primary" circle>
                 <el-icon><ArrowLeft /></el-icon>
               </el-button>
               <el-button @click="togglePlayPause" type="primary" circle size="large">
                 <el-icon v-if="!store.isPlaying"><VideoPlay /></el-icon>
                 <el-icon v-else><VideoPause /></el-icon>
               </el-button>
               <el-button @click="nextSong" type="primary" circle>
                 <el-icon><ArrowRight /></el-icon>
               </el-button>
             </div>
             <div class="progress-section">
               <div class="time-display">
                 <span>{{ formatTime(currentTime) }}</span>
                 <span>{{ formatTime(duration) }}</span>
               </div>
               <el-slider 
                 v-model="progress" 
                 :max="100" 
                 @change="seekTo"
                 class="progress-slider"
                 :show-tooltip="false"
               />
             </div>
           </div>


           
           <!-- 右侧：音量控制和播放队列 -->
           <div class="right-section">
             <div class="volume-section">
               <el-icon><Microphone /></el-icon>
               <el-slider 
                 v-model="volume" 
                 :max="100" 
                 @change="setVolume"
                 class="volume-slider"
                 :show-tooltip="false"
               />
             </div>
             <div class="queue-control">
               <el-button @click="showQueue = !showQueue" type="primary" circle>
                 <el-icon><List /></el-icon>
               </el-button>
               <span style="margin-left: 10px; color: #fff; font-size: 14px;"></span>
             </div>
           </div>
         </div>
       </el-footer>
    </el-container>
    
    <!-- 播放队列弹窗 -->
    <el-dialog 
      v-model="showQueue" 
      title="播放队列" 
      width="500px"
      :before-close="handleCloseQueue"
    >
      <div class="queue-content">
        <div v-if="playlist.length === 0" class="empty-queue">
          <el-empty description="播放队列为空" />
        </div>
        <div v-else class="queue-list">
          <div 
            v-for="(song, index) in playlist" 
            :key="song.songId"
            class="queue-item"
            :class="{ 'current-song': index === currentIndex }"
            @click="playSong(index)"
          >
            <img :src="imgUrl(song.image)" class="song-thumbnail" />
            <div class="song-details">
              <div class="song-name">{{ song.name }}</div>
              <div class="song-intro">{{ song.introduction || '暂无介绍' }}</div>
            </div>
            <div class="song-actions">
              <el-button 
                v-if="index === currentIndex" 
                type="primary" 
                size="small" 
                circle
              >
                <el-icon><VideoPlay /></el-icon>
              </el-button>
              <el-button 
                @click.stop="removeFromQueue(index)" 
                type="danger" 
                size="small" 
                circle
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 隐藏的音频元素 -->
    <audio 
      ref="audio" 
      :src="audioUrl" 
      @timeupdate="updateProgress" 
      @ended="onSongEnd"
      @loadedmetadata="updateProgress"
      style="display: none;"
    >
      您的浏览器不支持音频播放。
    </audio>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { Search, ArrowLeft, ArrowRight, VideoPlay, VideoPause, Microphone, List, Delete } from '@element-plus/icons-vue'
import {onMounted, ref, watch, computed, nextTick} from "vue";
import axios from "axios";
import { store } from '../store.js';
import config from '../config'

const imgUrl=(filename)=>{
  return `${config.api}/music/`+filename
}

// 创建 audio 元素的 ref 引用
const audio = ref<HTMLAudioElement | null>(null);
const audioUrl = ref(``)
const isSongEnded = ref(false);

// 播放进度和音量控制
const currentTime = ref(0);
const duration = ref(0);
const progress = ref(0);
const volume = ref(50);
const showQueue = ref(false);

// 使用store中的播放列表
const playlist = computed(() => store.playlist);
const currentIndex = computed(() => store.currentIndex);
onMounted(()=>{
  store.currentSong=null
  // getData()
  // 初始化音量
  if (audio.value) {
    audio.value.volume = volume.value / 100;
  }
  
  // 添加音频事件监听器
  nextTick(() => {
    if (audio.value) {
      audio.value.addEventListener('play', () => {
        store.setPlaying(true);
      });
      audio.value.addEventListener('pause', () => {
        store.setPlaying(false);
      });
      audio.value.addEventListener('ended', () => {
        store.setPlaying(false);
        // 播放完毕后自动播放下一首
        nextSong();
      });
    }
  });
})

// 监听歌曲变化
watch(() => store.currentSong, (newSong) => {
  if (newSong) {
    console.log('收到新的歌曲:', newSong);
    // 更新音频源
    audioUrl.value = `${config.api}/music/audio/?filename=${newSong.path}`;
    
    // 自动播放新歌曲
    setTimeout(() => {
      if (audio.value) {
        audio.value.play().then(() => {
          store.setPlaying(true);
        }).catch(err => {
          console.log('自动播放失败:', err);
        });
      }
    }, 100);
  }
}, { immediate: true });

// 格式化时间
const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 更新播放进度
const updateProgress = () => {
  if (audio.value) {
    currentTime.value = audio.value.currentTime;
    duration.value = audio.value.duration || 0;
    
    if (duration.value > 0) {
      progress.value = (currentTime.value / duration.value) * 100;
    }
  }
};

// 跳转到指定时间
const seekTo = (value: number) => {
  if (audio.value && duration.value > 0) {
    const newTime = (value / 100) * duration.value;
    audio.value.currentTime = newTime;
  }
};

// 设置音量
const setVolume = (value: number) => {
  if (audio.value) {
    audio.value.volume = value / 100;
  }
};

// 歌曲播放完毕的处理
const onSongEnd = () => {
  isSongEnded.value = true;
  console.log("歌曲播放完毕！");
};
const router = useRouter()
const activeIndex = ref('1')

const handleSelect = (key: string) => {
  router.push("/" + key)
}

// 播放/暂停切换
const togglePlayPause = () => {
  if (audio.value) {
    if (store.isPlaying) {
      audio.value.pause();
      store.setPlaying(false);
    } else {
      audio.value.play().then(() => {
        store.setPlaying(true);
      }).catch(err => {
        console.log('播放失败:', err);
      });
    }
  }
}

// 上一首
const previousSong = () => {
  if (playlist.value.length > 0) {
    let newIndex;
    if (currentIndex.value > 0) {
      newIndex = currentIndex.value - 1;
    } else {
      newIndex = playlist.value.length - 1; // 循环到最后一首
    }
    const song = playlist.value[newIndex];
    store.setCurrentIndex(newIndex);
    store.setCurrentSong(song);
    audioUrl.value = `${config.api}/music/audio/?filename=${song.path}`;
    // 自动播放
    setTimeout(() => {
      if (audio.value) {
        audio.value.play().then(() => {
          store.setPlaying(true);
        }).catch(err => {
          console.log('播放失败:', err);
        });
      }
    }, 100);
  }
}

// 下一首
const nextSong = () => {
  if (playlist.value.length > 0) {
    let newIndex;
    if (currentIndex.value < playlist.value.length - 1) {
      newIndex = currentIndex.value + 1;
    } else {
      newIndex = 0; // 循环到第一首
    }
    const song = playlist.value[newIndex];
    store.setCurrentIndex(newIndex);
    store.setCurrentSong(song);
    audioUrl.value = `${config.api}/music/audio/?filename=${song.path}`;
    // 自动播放
    setTimeout(() => {
      if (audio.value) {
        audio.value.play().then(() => {
          store.setPlaying(true);
        }).catch(err => {
          console.log('播放失败:', err);
        });
      }
    }, 100);
  }
}

// 播放队列相关函数
const handleCloseQueue = () => {
  showQueue.value = false;
};

// 播放指定索引的歌曲
const playSong = (index: number) => {
  if (playlist.value[index]) {
    const song = playlist.value[index];
    store.setCurrentIndex(index);
    store.setCurrentSong(song);
    audioUrl.value = `${config.api}/music/audio/?filename=${song.path}`;
    // 自动播放
    setTimeout(() => {
      if (audio.value) {
        audio.value.play().then(() => {
          store.setPlaying(true);
        }).catch(err => {
          console.log('播放失败:', err);
        });
      }
    }, 100);
  }
};

// 从队列中移除歌曲
const removeFromQueue = (index: number) => {
  if (playlist.value.length > 1) {
    const newPlaylist = [...playlist.value];
    newPlaylist.splice(index, 1);
    store.setPlaylist(newPlaylist);
    
    // 如果删除的是当前播放的歌曲，自动播放下一首
    if (index === currentIndex.value) {
      if (index >= newPlaylist.length) {
        // 如果删除的是最后一首，播放第一首
        playSong(0);
      } else {
        // 播放当前索引的歌曲（删除后索引不变，但歌曲变了）
        playSong(index);
      }
    } else if (index < currentIndex.value) {
      // 如果删除的歌曲在当前歌曲之前，需要调整当前索引
      store.setCurrentIndex(currentIndex.value - 1);
    }
  }
};
</script>

<style scoped>
@keyframes rotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
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
  background: linear-gradient(to right, #5bdc93, #3aa6d5);  color: #fff;
  padding: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 110px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.footer-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  max-width: 1400px;
  gap: 30px;
}

/* 左侧：歌曲信息 */
.song-info {
  font-size: 16px;
  color: #fff;
  display: flex;
  align-items: center;
  flex: 0 0 300px;
  min-width: 0;
}

/* 中间：播放控制和进度条 */
.center-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex: 1;
  max-width: 600px;
}

.control-buttons {
  display: flex;
  align-items: center;
  gap: 20px;
}

.progress-section {
  width: 100%;
  max-width: 500px;
}

.time-display {
  display: flex;
  justify-content: space-between;
  color: #fff;
  font-size: 12px;
  margin-bottom: 8px;
}

.progress-slider {
  width: 100%;
}

/* 自定义进度条样式 */
:deep(.progress-slider .el-slider__runway) {
  height: 4px;
  background-color: rgba(255, 255, 255, 0.3);
}

:deep(.progress-slider .el-slider__bar) {
  height: 4px;
  background: linear-gradient(to right, #409eff, #67c23a);
}

:deep(.progress-slider .el-slider__button) {
  width: 12px;
  height: 12px;
  border: 2px solid #fff;
  background-color: #409eff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

:deep(.progress-slider .el-slider__button:hover) {
  transform: scale(1.2);
}

/* 自定义音量滑块样式 */
:deep(.volume-slider .el-slider__runway) {
  height: 3px;
  background-color: rgba(255, 255, 255, 0.3);
}

:deep(.volume-slider .el-slider__bar) {
  height: 3px;
  background-color: #fff;
}

:deep(.volume-slider .el-slider__button) {
  width: 10px;
  height: 10px;
  border: 2px solid #fff;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

/* 右侧：音量控制和播放队列 */
.right-section {
  display: flex;
  align-items: center;
  gap: 20px;
  flex: 0 0 250px;
}

.volume-section {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
}

.volume-slider {
  width: 80px;
}

.queue-control {
  display: flex;
  justify-content: center;
  align-items: center;
}

.el-button {
  margin: 0 10px;
}

/* 自定义按钮样式 */
:deep(.control-buttons .el-button) {
  border: none;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

:deep(.control-buttons .el-button:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

:deep(.control-buttons .el-button--large) {
  width: 50px;
  height: 50px;
}

:deep(.queue-control .el-button) {
  border: none;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

:deep(.queue-control .el-button:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.05);
}

/* 播放队列样式 */
.queue-content {
  max-height: 400px;
  overflow-y: auto;
}

.queue-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.queue-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
  border: 1px solid #e4e7ed;
}

.queue-item:hover {
  background-color: #f5f7fa;
}

.queue-item.current-song {
  background-color: #ecf5ff;
  border-color: #409eff;
}

.song-thumbnail {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.song-details {
  flex: 1;
  min-width: 0;
}

.song-name {
  font-weight: bold;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-intro {
  font-size: 12px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.song-actions {
  display: flex;
  gap: 5px;
}

.empty-queue {
  text-align: center;
  padding: 40px 0;
}
</style>
