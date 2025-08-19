<template>

  <router-view/>
</template>

<script setup>
import {onMounted, ref} from "vue";
import axios from "axios";

// 创建 audio 元素的 ref 引用
const audio = ref(null);
const audioUrl = ref('http://localhost:8088/audio/?filename=失控.wav')
const isSongEnded = ref(false);

onMounted(()=>{
  // getData()
})

// 更新播放进度
const updateProgress = () => {
  // 使用 audio 引用来获取播放进度
  if (audio.value) {
    console.log(`当前播放时间: ${audio.value.currentTime}秒`);
  }
};

// 歌曲播放完毕的处理
const onSongEnd = () => {
  isSongEnded.value = true;
  console.log("歌曲播放完毕！");
};
</script>

<style scoped>
.music-player {
  text-align: center;
  margin-top: 20px;
}
</style>
