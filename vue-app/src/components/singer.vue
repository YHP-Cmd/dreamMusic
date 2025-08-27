<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import config from "../config";
import { useRouter } from "vue-router";

const router = useRouter();

const imgUrl = (filename) => {
  return `${config.api}/music/image/` + filename;
};

const singers = ref([]);

const getData = () => {
  axios.get(`${config.api}/singer/getAll`).then(response => {
    if (response.data) {
      singers.value = response.data;
    }
  });
};

onMounted(() => {
  getData();
});

const handleCurrentChange = (val) => {
  router.push({
    path: '/singerInfo',
    query: { singerId: val.singerId }
  });
};
</script>

<template>
  <div class="singer-container">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <i class="el-icon-user title-icon"></i>
          歌手列表
        </h2>
        <p class="page-subtitle">发现优秀歌手，聆听动人旋律</p>
      </div>
      <div class="header-decoration">
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
        <div class="decoration-circle"></div>
      </div>
    </div>

    <!-- 歌手列表区域 -->
    <div class="singer-section">
      <div class="singer-table-container">
        <el-table
          class="singer-table"
          :show-header="false"
          :data="singers"
          stripe
          highlight-current-row
          @current-change="handleCurrentChange"
        >
          <!-- 歌手头像列 -->
          <el-table-column label="头像" width="100">
            <template #default="{ row }">
              <div class="singer-avatar-wrapper">
                <img :src="imgUrl(row.image)" alt="歌手头像" class="singer-avatar" />
                <div class="avatar-overlay">
                  <i class="el-icon-user"></i>
                </div>
              </div>
            </template>
          </el-table-column>

          <!-- 歌手信息列 -->
          <el-table-column label="歌手信息" prop="name" min-width="250">
            <template #default="{ row }">
              <div class="singer-info">
                <div class="singer-name">{{ row.name }}</div>
                <div class="singer-region">{{ row.region || '未知地区' }}</div>
                <div class="singer-type">{{ row.type || '未知类型' }}</div>
              </div>
            </template>
          </el-table-column>

          <!-- 类型列 -->
          <el-table-column label="类型" prop="type" width="120">
            <template #default="{ row }">
              <div class="type-badge">
                <span>{{ row.type || '未知' }}</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 空状态 -->
      <div v-if="singers.length === 0" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-user"></i>
        </div>
        <h3>暂无歌手信息</h3>
        <p>稍后再来看看吧</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.singer-container {
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
  min-height: 100vh;
  padding: 20px;
}

/* 页面标题区域 */
.page-header {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(168, 237, 234, 0.3);
}

.header-content {
  position: relative;
  z-index: 2;
}

.page-title {
  color: #fff;
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.title-icon {
  font-size: 36px;
  color: #ffd700;
}

.page-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin: 0;
}

.header-decoration {
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.decoration-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 3s ease-in-out infinite;
}

.decoration-circle:nth-child(2) {
  animation-delay: 1s;
}

.decoration-circle:nth-child(3) {
  animation-delay: 2s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

/* 歌手列表区域 */
.singer-section {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.singer-table-container {
  padding: 20px;
}

.singer-table {
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

/* 歌手头像样式 */
.singer-avatar-wrapper {
  position: relative;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease;
}

.singer-avatar-wrapper:hover {
  transform: scale(1.05);
}

.singer-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
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

.singer-avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  color: #fff;
  font-size: 24px;
}

/* 歌手信息样式 */
.singer-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.singer-name {
  font-weight: bold;
  color: #2c3e50;
  font-size: 18px;
  line-height: 1.2;
}

.singer-region {
  color: #7f8c8d;
  font-size: 14px;
  font-weight: 500;
}

.singer-type {
  color: #95a5a6;
  font-size: 12px;
}

/* 类型标签样式 */
.type-badge {
  display: flex;
  align-items: center;
}

.type-badge span {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #2c3e50;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(168, 237, 234, 0.3);
}

/* 表格行悬停效果 */
:deep(.el-table__row:hover) {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%) !important;
  transform: scale(1.01);
  transition: all 0.3s ease;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
}

/* 空状态样式 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  color: #bdc3c7;
}

.empty-state h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.empty-state p {
  font-size: 16px;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .singer-avatar-wrapper {
    width: 60px;
    height: 60px;
  }
  
  .singer-name {
    font-size: 16px;
  }
}
</style>