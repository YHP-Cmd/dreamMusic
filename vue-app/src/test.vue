<template>
  <div class="music-player">
    <div v-if="currentSong">
      <h2>{{ currentSong.title }}</h2>
      <p>{{ currentSong.artist }}</p>

      <!-- 音乐控制 -->
      <div class="controls">
        <button @click="playPause">
          {{ isPlaying ? 'Pause' : 'Play' }}
        </button>
        <input type="range" v-model="progress" max="100" @input="seek" />
        <p>{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</p>
      </div>

      <!-- 音乐播放器 -->
      <audio ref="audio" :src="currentSong.url" @timeupdate="updateProgress" @ended="onSongEnd" />
    </div>

    <div v-else>
      <p>请选择歌曲</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentSong: null, // 当前播放的歌曲
      isPlaying: false,  // 播放状态
      currentTime: 0,    // 当前播放进度
      duration: 0,       // 歌曲总时长
      progress: 0,       // 播放进度（控制条）
    };
  },
  methods: {
    // 播放或暂停音乐
    playPause() {
      const audio = this.$refs.audio;
      if (this.isPlaying) {
        audio.pause();
      } else {
        audio.play();
      }
      this.isPlaying = !this.isPlaying;
    },

    // 更新时间进度
    updateProgress() {
      const audio = this.$refs.audio;
      this.currentTime = audio.currentTime;
      this.progress = (this.currentTime / this.duration) * 100;
    },

    // 切换歌曲播放进度
    seek() {
      const audio = this.$refs.audio;
      audio.currentTime = (this.progress / 100) * this.duration;
    },

    // 处理歌曲结束
    onSongEnd() {
      this.isPlaying = false;
      this.currentTime = 0;
      this.progress = 0;
    },

    // 格式化时间
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60);
      const secs = Math.floor(seconds % 60);
      return `${minutes}:${secs < 10 ? '0' : ''}${secs}`;
    },

    // 设置新的歌曲
    setSong(song) {
      this.currentSong = song;
      this.isPlaying = false;
      this.currentTime = 0;
      this.progress = 0;
      const audio = this.$refs.audio;
      audio.load();
      audio.play();
      this.duration = audio.duration;
    }
  },
  watch: {
    // 监听当前歌曲变化
    currentSong(newSong) {
      if (newSong) {
        this.setSong(newSong);
      }
    }
  }
};
</script>

<style scoped>
.music-player {
  text-align: center;
}

.controls {
  margin-top: 20px;
}

input[type="range"] {
  width: 80%;
}
</style>
