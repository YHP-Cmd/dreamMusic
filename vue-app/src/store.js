import { reactive } from 'vue'

// 创建全局状态
export const store = reactive({
  currentSong: null,
  isPlaying: false,
  playlist: [],
  currentIndex: -1,
  
  // 设置当前播放的歌曲
  setCurrentSong(song) {
    this.currentSong = song
  },
  
  // 设置播放列表
  setPlaylist(songs) {
    this.playlist = songs
  },
  
  // 设置当前索引
  setCurrentIndex(index) {
    this.currentIndex = index
  },
  
  // 设置播放状态
  setPlaying(playing) {
    this.isPlaying = playing
  },
  
  // 播放/暂停切换
  togglePlay() {
    this.isPlaying = !this.isPlaying
  },
  
  // 停止播放
  stopPlay() {
    this.isPlaying = false
  }
})
