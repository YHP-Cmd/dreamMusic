<script setup lang="ts">

import swiper1 from '../assets/image/swiper1.png'
import swiper2 from '../assets/image/swiper2.png'
import swiper3 from '../assets/image/swiper3.png'
import swiper4 from '../assets/image/swiper4.png'
import swiper5 from '../assets/image/swiper5.png'
import swiper6 from '../assets/image/swiper6.png'

const images = [swiper1, swiper2, swiper3, swiper4, swiper5,swiper6]

</script>
<template>
  <div style="width: 100%;">
    <div class="block text-center" style="width: 600px; height: 100px;">
      <el-carousel motion-blur style="height: 600px">
        <el-carousel-item v-for="item in images" :key="item" style="height: 600px">
          <el-image :src="item"/>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<style scoped>

</style>