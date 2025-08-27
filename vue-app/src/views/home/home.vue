<template>
  <div class="home-container">
    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card user-card">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ userCount }}</div>
            <div class="stat-label">用户数量</div>
          </div>
        </div>
        
        <div class="stat-card song-card">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ songCount }}</div>
            <div class="stat-label">歌曲数量</div>
          </div>
        </div>
        
        <div class="stat-card play-card">
          <div class="stat-icon">
            <el-icon><VideoPlay /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ playCount }}</div>
            <div class="stat-label">总播放量</div>
          </div>
        </div>
        
        <div class="stat-card singer-card">
          <div class="stat-icon">
            <el-icon><Microphone /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ singerCount }}</div>
            <div class="stat-label">歌手数量</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 歌曲类型统计图 -->
        <div class="chart-container">
          <div class="chart-header">
            <h3>歌曲类型分布</h3>
          </div>
          <div ref="songTypeChart" class="chart"></div>
        </div>
        
        <!-- 歌手类型统计图 -->
        <div class="chart-container">
          <div class="chart-header">
            <h3>歌手类型分布</h3>
          </div>
          <div ref="singerTypeChart" class="chart"></div>
        </div>
      </div>
    </div>

    <!-- 热门歌曲区域 -->
    <div class="hot-songs-section">
      <div class="section-header">
        <h3>本月前10首热门歌曲</h3>
        <el-icon class="music-note"><Star /></el-icon>
      </div>
      <div class="songs-list">
        <div
            v-for="(song, index) in hotSongs"
            :key="song.songId"
            class="song-item"
            :class="{ 'top-three': index < 3 }"
        >
          <div class="song-rank">{{ index + 1 }}</div>
          <div class="song-info">
            <div class="song-name">{{ song.name }}</div>
            <div class="song-singer">{{ song.singerName }}</div>
          </div>
<!--          <div class="song-plays">{{ formatNumber(song.playCount) }} 播放</div>-->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, VideoPlay, Microphone, Star } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import axios from "axios";
import config from '../../config.js'

// 响应式数据
const userCount = ref(0)
const songCount = ref(0)
const playCount = ref(0)
const singerCount = ref(0)
const hotSongs = ref([])
const songTypeChart = ref()
const singerTypeChart = ref()

// 格式化数字显示
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }
  return num.toString()
}

// 获取统计数据
const getStatistics = async () => {
  try {
    // 获取用户数量
    await axios.get(`${config.api}/users/count`).then(response=>{
      if (response.data){
        userCount.value=response.data
      }
    })

    // 获取歌曲数量
    await axios.get(`${config.api}/music/songCount`).then(response=>{
      if (response.data){
        songCount.value=response.data
      }
    })
    // 获取总播放量
    await axios.get(`${config.api}/music/listenCount`).then(response=>{
      if (response.data){
        playCount.value=response.data
      }
    })
    // 获取歌手数量
    await axios.get(`${config.api}/singer/count`).then(response=>{
      if (response.data){
        singerCount.value=response.data
      }
    })
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 获取热门歌曲
const getHotSongs = async () => {
  try {
    axios.get(`${config.api}/music/getHot`).then(response=>{
      if (response.data){
        hotSongs.value=response.data
      }
    })
  } catch (error) {
    ElMessage.error('获取热门歌曲失败')
  }
}

// 初始化歌曲类型图表
const initSongTypeChart = async () => {

  try {
    axios.get(`${config.api}/music/countByType`).then(response=>{
      if (response.data) {
        const chartData = response.data.map(item => ({
          name: item.typeName,
          value: item.count
        }))
        const chart = echarts.init(songTypeChart.value)
        const option = {
          title: {
            text: '歌曲类型统计',
            left: 'center',
            textStyle: {
              color: '#fff',
              fontSize: 16
            }
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            textStyle: {
              color: '#fff'
            }
          },
          series: [
            {
              name: '歌曲类型',
              type: 'pie',
              radius: '50%',
              data: chartData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              itemStyle: {
                borderRadius: 8,
                borderColor: '#fff',
                borderWidth: 2
              }
            }
          ],
          color: ['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57', '#ff9ff3', '#54a0ff']
        }
        chart.setOption(option)

        // 响应式调整
        window.addEventListener('resize', () => {
          chart.resize()
        })
      }
    })

  } catch (error) {
    ElMessage.error('获取歌曲类型数据失败')
  }
}

// 初始化歌手类型图表
const initSingerTypeChart = async () => {
  try {
    axios.get(`${config.api}/singer/typeCount`).then(response=>{
      if (response.data) {
        const chartData = response.data.map(item => ({
          name: item.typeName,
          value: item.count
        }))

        const chart = echarts.init(singerTypeChart.value)
        const option = {
          title: {
            text: '歌手类型统计',
            left: 'center',
            textStyle: {
              color: '#fff',
              fontSize: 16
            }
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            textStyle: {
              color: '#fff'
            }
          },
          series: [
            {
              name: '歌手类型',
              type: 'pie',
              radius: '50%',
              data: chartData,
              emphasis: {
                itemStyle: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
              },
              itemStyle: {
                borderRadius: 8,
                borderColor: '#fff',
                borderWidth: 2
              }
            }
          ],
          color: ['#a8e6cf', '#dcedc1', '#ffd3a5', '#fd9853', '#ff8a80', '#ea80fc', '#8c9eff']
        }
        chart.setOption(option)

        // 响应式调整
        window.addEventListener('resize', () => {
          chart.resize()
        })
      }
    })

  } catch (error) {
    ElMessage.error('获取歌手类型数据失败')
  }
}

// 页面加载时初始化数据
onMounted(async () => {
  await getStatistics()
  await getHotSongs()
  
  // 等待DOM更新后初始化图表
  await nextTick()
  initSongTypeChart()
  initSingerTypeChart()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  background: linear-gradient(145deg, #2d2013 0%, #5c3d2e 50%, #b85c38 100%);
  min-height: 100vh;
}

/* 统计卡片样式 */
.stats-section {
  margin-bottom: 30px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
}

.user-card .stat-icon {
  background: linear-gradient(45deg, #ff6b6b, #ee5a52);
  color: white;
}

.song-card .stat-icon {
  background: linear-gradient(45deg, #4ecdc4, #44a08d);
  color: white;
}

.play-card .stat-icon {
  background: linear-gradient(45deg, #45b7d1, #96c93d);
  color: white;
}

.singer-card .stat-icon {
  background: linear-gradient(45deg, #f093fb, #f5576c);
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: white;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 热门歌曲样式 */
.hot-songs-section {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 30px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  color: white;
  margin: 0;
  margin-right: 10px;
  font-size: 20px;
}

.music-note {
  color: #ffd700;
  font-size: 20px;
}

.songs-list {
  display: grid;
  gap: 12px;
}

.song-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
}

.song-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

.song-item.top-three {
  background: linear-gradient(45deg, rgba(255, 215, 0, 0.2), rgba(255, 165, 0, 0.2));
}

.song-rank {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: linear-gradient(45deg, #ff6b6b, #ee5a52);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-right: 16px;
}

.song-info {
  flex: 1;
}

.song-name {
  color: white;
  font-weight: 500;
  margin-bottom: 4px;
}

.song-singer {
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}

.song-plays {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

/* 图表样式 */
.charts-section {
  margin-bottom: 30px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-container {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.chart-header h3 {
  color: white;
  margin: 0 0 16px 0;
  text-align: center;
  font-size: 18px;
}

.chart {
  width: 100%;
  height: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .chart {
    height: 250px;
  }
}
</style>