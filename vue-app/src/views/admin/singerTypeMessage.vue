<template>
  <div class="singer-type-page">
    <el-card class="toolbar" shadow="never">
      <el-form :inline="true" :model="query" label-width="80px">
        <el-form-item label="类型名称">
          <el-input v-model="query.name" placeholder="输入类型名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div class="table-actions">
        <el-button type="success" @click="openAdd">新增</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="typeId" label="类型ID" width="100" />
        <el-table-column prop="name" label="类型名称" min-width="200" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" text @click="deleteSingerType(row)">删除</el-button>
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

    <!-- 新增弹窗 -->
    <el-dialog v-model="addVisible" title="新增歌手类型" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型名称" required>
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑歌手类型" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="类型ID">
          <el-input v-model="editForm.typeId" disabled />
        </el-form-item>
        <el-form-item label="类型名称" required>
          <el-input v-model="editForm.name" placeholder="请输入类型名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from "axios";
import config from "../../config.js";

// 查询条件
const query = reactive({ name: '' })

// 列表分页数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 弹窗新增
const addVisible = ref(false)
const form = reactive({ name: '' })

// 弹窗编辑
const editVisible = ref(false)
const editForm = reactive({ typeId: null, name: '' })

// 加载表格分页数据
async function loadData() {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (query.name) params.name = query.name
  try {
    await axios.get(`${config.api}/singer/pageType`,{params}).then(response=>{
      if(response.data){
        const page = response.data
        tableData.value = page.records || []
        total.value = page.total || 0
      }
    })

  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

// 重置查询条件
function resetQuery() {
  query.name = ''
  pageNum.value = 1
  loadData()
}

// 分页变化处理
function handlePageChange(p) {
  pageNum.value = p
  loadData()
}

function handleSizeChange(s) {
  pageSize.value = s
  pageNum.value = 1
  loadData()
}

// 打开新增弹窗
function openAdd() {
  form.name = ''
  addVisible.value = true
}

// 打开编辑弹窗
function openEdit(row) {
  editForm.typeId = row.typeId
  editForm.name = row.name
  editVisible.value = true
}

// 提交新增
async function submitAdd() {
  if (!form.name || !form.name.trim()) {
    ElMessage.error('请输入类型名称')
    return
  }
  
  try {
    await axios.put(`${config.api}/singer/addType`,form).then(response=>{
      if(response.data){
        ElMessage.success('新增成功')
        addVisible.value = false
        loadData()
      }
    })

  } catch (error) {
    ElMessage.error('新增失败')
  }
}

// 提交编辑
async function submitEdit() {
  if (!editForm.name || !editForm.name.trim()) {
    ElMessage.error('请输入类型名称')
    return
  }
  
  try {
    await axios.put(`${config.api}/singer/updateType`,editForm).then(response=>{
      if(response.data){
        ElMessage.success('保存成功')
        editVisible.value = false
        loadData()
      }
    })

  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除歌手类型
async function deleteSingerType(row) {
  try {
    await ElMessageBox.confirm(`确定删除歌手类型「${row.name}」吗？`, '提示', {
      type: 'warning'
    })
    await axios.delete(`${config.api}/singer/deleteType?id=${row.typeId}`).then(response=>{
      if(response.data){
        ElMessage.success('删除成功')
        loadData()
      }
    })
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.singer-type-page { padding: 12px; }
.toolbar { margin-bottom: 12px; }
.table-actions { margin-bottom: 8px; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>