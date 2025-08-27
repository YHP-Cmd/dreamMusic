<template>
  <div class="song-page">
    <el-card class="toolbar" shadow="never">
      <el-form :inline="true" :model="query" label-width="80px">
        <el-form-item label="歌曲名">
          <el-input v-model="query.name" placeholder="输入歌曲名" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.typeId" placeholder="选择类型" clearable style="  width: 120px">
            <el-option v-for="t in songTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="歌手">
          <el-select v-model="query.singerId" placeholder="选择歌手" clearable style="width: 120px">
            <el-option v-for="s in singers" :key="s.singerId" :label="s.name" :value="s.singerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="正常" value="正常" />
            <el-option label="已冻结" value="已冻结" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-actions">
        <el-button type="success" @click="openAdd">发布歌曲</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="songId" label="ID" width="80" />
        <el-table-column prop="name" label="歌曲名" min-width="140" />
        <el-table-column prop="type" label="类型" min-width="120" />
        <el-table-column prop="singerName" label="歌手" min-width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === '正常'" size="small" type="danger" text @click="() => freeze(row)">冻结</el-button>
            <el-button v-else size="small" type="success" text @click="() => unfreeze(row)">解冻</el-button>
            <el-button size="small" type="info" text @click="preview(row)">预览</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="pageNum"
          :page-size="pageSize"
          :page-sizes="[5,10,20,50]"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 预览音频对话框，使用原生audio标签确保兼容性 -->
    <el-dialog v-model="previewVisible" title="试听" width="480px">
      <img v-if="coverImg" :src="coverImg" alt="暂无图片" style="width:100%"/>
      <audio v-if="previewVisible" :src="previewSrc" controls style="width:100%"></audio>
      <template #footer>
        <el-button @click="exitListen">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 发布/编辑弹窗：与用户一致，确认后再入库 -->
    <el-dialog v-model="addVisible" :title="isEdit ? '编辑歌曲' : '发布歌曲'" width="700px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="歌曲名">
          <el-input v-model="form.name" placeholder="请输入歌曲名" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.typeId" placeholder="选择类型" style="width: 200px">
            <el-option v-for="t in songTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="歌手">
          <el-select v-model="form.singerId" placeholder="选择歌手" style="width: 200px">
            <el-option v-for="s in singers" :key="s.singerId" :label="s.name" :value="s.singerId" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="image/*"
            :on-change="onImageChange"
            :before-upload="beforeImageUpload"
          >
            <el-button type="primary">选择图片</el-button>
          </el-upload>
          <span class="path">{{ pendingImageFile ? pendingImageFile.name : form.image }}</span>
          <div v-if="form.image" class="image-preview">
            <img :src="`${config.api}/music/image/${form.image}`" alt="封面预览" class="preview-img" />
          </div>
        </el-form-item>
        <el-form-item label="音乐文件">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            accept="audio/*"
            :on-change="onMusicChange"
            :before-upload="beforeAudioUpload"
          >
            <el-button type="primary">选择音乐</el-button>
          </el-upload>
          <span class="path">{{ pendingMusicFile ? pendingMusicFile.name : form.path }}</span>
          <div v-if="form.path" class="audio-preview">
            <audio :src="`${config.api}/music/audio/?user_id=${useUserStore().uid}&filename=${form.path}&song_id=0`" controls style="width: 100%; margin-top: 10px;"></audio>
          </div>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="3" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 200px">
            <el-option label="正常" value="正常" />
            <el-option label="已冻结" value="已冻结" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from "axios";
import config from "../../config.js";
import { useUserStore } from "../../stores/user.js"; // 修正拼写
// 查询条件
const query = reactive({ name: '', typeId: null, singerId: null, status: '' })
// 列表分页数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 下拉：歌曲类型、歌手
const songTypes = ref([])
const singers = ref([])

// 弹窗
const addVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ songId: null, name: '', typeId: null, singerId: null,
  path: '', image: '', introduction: '', status: '正常' })
const previewVisible = ref(false)
const previewSrc = ref('')
const coverImg = ref('')
// 待确认再上传的文件占位
const pendingImageFile = ref(null)
const pendingMusicFile = ref(null)
const loading = ref(false)

// 载入类型（示例：后端可提供列表接口，此处简单用数据库表名字段）
async function loadSongTypes() {
  // 若后端暂无接口，可由前端静态维护；这里尝试从权限模块获取或自行兜底
  try {
    // 这里假设与 userType 一样提供了列表接口 /songType/list（若无将忽略错误）
    axios.get(`${config.api}/music/getAllType`).then(response=>{
      if (response.data){
        const arr = response.data
        if (Array.isArray(arr)) songTypes.value = arr
      }
    })

  } catch (e) {
    // 简单兜底空数组
    songTypes.value = []
  }
}
const exitListen=()=>{

  previewSrc.value=''
  previewVisible.value=false

}
async function loadSingers() {
  try {
    await axios.get(`${config.api}/singer/getAll`).then(response=>{
      if (response.data){
        const arr =response.data
        singers.value = Array.isArray(arr) ? arr : []
      }
    })

  } catch (e) {
    singers.value = []
  }
}

// 加载表格分页
async function loadData() {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (query.name) params.name = query.name
  if (query.typeId) params.typeId = query.typeId
  if (query.singerId) params.singerId = query.singerId
  if (query.status) params.status = query.status
  await axios.get(`${config.api}/music/page`, {params}).then(response=>{
    if (response.data){
      const page = response.data
      tableData.value = page.records || []
      total.value = page.total || 0
    }
  })

}

function resetQuery() {
  Object.assign(query, { name: '', typeId: null, singerId: null, status: '' })
  pageNum.value = 1
  loadData()
}

function handlePageChange(p) { pageNum.value = p; loadData() }
function handleSizeChange(s) { pageSize.value = s; pageNum.value = 1; loadData() }

function openAdd() {
  isEdit.value = false
  Object.assign(form, { songId: null, name: '', typeId: null, singerId: null, path: '', image: '', introduction: '', status: '正常' })
  addVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row })
  addVisible.value = true
}

async function submitForm() {
  if (!form.name || !form.typeId || !form.singerId) {
    ElMessage.error('请填写必填项')
    return
  }
  
  loading.value = true
  
  try {
    // 若选择了新文件，在此时统一上传并回填路径
    if (pendingImageFile.value) {
      const fd = new FormData()
      fd.append('file', pendingImageFile.value)
      const response = await axios.post(`${config.api}/music/upload/image`, fd, { 
        headers: { 'Content-Type': 'multipart/form-data' } 
      })
      if (response.data.code === 200) {
        form.image = response.data.data
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(response.data.message || '图片上传失败')
        return
      }
    }
    
    if (pendingMusicFile.value) {
      const fd = new FormData()
      fd.append('file', pendingMusicFile.value)
      const response = await axios.post(`${config.api}/music/upload/audio`, fd, { 
        headers: { 'Content-Type': 'multipart/form-data' } 
      })
      if (response.data.code === 200) {
        form.path = response.data.data
        ElMessage.success('音频上传成功')
      } else {
        ElMessage.error(response.data.message || '音频上传失败')
        return
      }
    }
    
    const payload = { ...form }
    let success = false
    
    if (isEdit.value) {
      success = await axios.put(`${config.api}/music/updateSong`, payload).then(res => res.data)
    } else {
      success = await axios.post(`${config.api}/music/addSong`, payload).then(res => res.data)
    }
    
    if (success) {
      ElMessage.success('保存成功')
      addVisible.value = false
      pendingImageFile.value = null
      pendingMusicFile.value = null
      loadData()
    } else {
      ElMessage.error('保存失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    loading.value = false
  }
}

// 文件验证函数
function beforeImageUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

function beforeAudioUpload(file) {
  const isAudio = file.type.startsWith('audio/')
  const isLt50M = file.size / 1024 / 1024 < 50
  
  if (!isAudio) {
    ElMessage.error('只能上传音频文件!')
    return false
  }
  if (!isLt50M) {
    ElMessage.error('音频文件大小不能超过 50MB!')
    return false
  }
  return true
}

// 自定义上传：仅上传文件，返回路径，待确认后再保存到表单
function onImageChange(uploadFile) {
  pendingImageFile.value = uploadFile.raw
}

function onMusicChange(uploadFile) {
  pendingMusicFile.value = uploadFile.raw
}

function preview(row) {
  if (!row.path) {
    ElMessage.info('暂无音频');
    return
  }
  previewSrc.value =`${config.api}/music/audio/?user_id=${useUserStore().uid}&filename=${row.path}&song_id=${row.songId}`
  coverImg.value = `${config.api}/music/image/${row.image}`
  previewVisible.value = true
}

// 冻结/解冻
async function freeze(row) {
  try {
    await ElMessageBox.confirm(`确定冻结歌曲「${row.name}」吗？`, '提示')
    const success = await axios.put(`${config.api}/music/freezeSong`, null, {
      params: { songId: row.songId }
    }).then(res => res.data)
    
    if (success) {
      ElMessage.success('已冻结')
      loadData()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

async function unfreeze(row) {
  try {
    await ElMessageBox.confirm(`确定解冻歌曲「${row.name}」吗？`, '提示')
    const success = await axios.put(`${config.api}/music/unfreezeSong`, null, {
      params: { songId: row.songId }
    }).then(res => res.data)
    
    if (success) {
      ElMessage.success('已解冻')
      loadData()
    } else {
      ElMessage.error('操作失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(async () => {
  await Promise.all([loadSongTypes(), loadSingers()])
  await loadData()
})
</script>

<style scoped>
.song-page { padding: 12px; }
.toolbar { margin-bottom: 12px; }
.table-actions { margin-bottom: 8px; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
.path { margin-left: 10px; color: #666; }

.image-preview {
  margin-top: 10px;
  text-align: center;
}

.preview-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
}

.audio-preview {
  margin-top: 10px;
}

.audio-preview audio {
  border-radius: 8px;
}
</style>