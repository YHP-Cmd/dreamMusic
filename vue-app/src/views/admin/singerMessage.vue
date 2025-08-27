<template>
  <div class="singer-page">
    <el-card class="toolbar" shadow="never">
      <el-form :inline="true" :model="query" label-width="80px">
        <el-form-item label="歌手名">
          <el-input v-model="query.name" placeholder="输入歌手名" clearable />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="query.gender" placeholder="选择性别" clearable style="width: 120px">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="query.regionId" placeholder="选择地区" clearable style="width: 160px">
            <el-option v-for="r in regions" :key="r.regionId" :label="r.name" :value="r.regionId" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.typeId" placeholder="选择类型" clearable style="width: 160px">
            <el-option v-for="t in singerTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
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
        <el-button type="success" @click="openAdd">新增</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="singerId" label="ID" width="80" />
        <el-table-column prop="name" label="歌手名" min-width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="region" label="地区" min-width="100" />
        <el-table-column prop="type" label="类型" min-width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" text @click="deleteSinger(row)">删除</el-button>
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

    <el-dialog v-model="addVisible" title="新增歌手" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="歌手名">
          <el-input v-model="form.name" placeholder="请输入歌手名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="form.regionId" placeholder="选择地区">
            <el-option v-for="r in regions" :key="r.regionId" :label="r.name" :value="r.regionId" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.typeId" placeholder="选择类型">
            <el-option v-for="t in singerTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑歌手" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="歌手ID">
          <el-input v-model="editForm.singerId" disabled />
        </el-form-item>
        <el-form-item label="歌手名">
          <el-input v-model="editForm.name" placeholder="请输入歌手名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="地区">
          <el-select v-model="editForm.regionId" placeholder="选择地区">
            <el-option v-for="r in regions" :key="r.regionId" :label="r.name" :value="r.regionId" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.typeId" placeholder="选择类型">
            <el-option v-for="t in singerTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
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
const query = reactive({ name: '', gender: '', regionId: null, typeId: null })

// 列表分页数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 下拉：地区和歌手类型
const regions = ref([])
const singerTypes = ref([])

// 弹窗新增
const addVisible = ref(false)
const form = reactive({ name: '', gender: '', regionId: null, typeId: null })

// 弹窗编辑
const editVisible = ref(false)
const editForm = reactive({ singerId: null, name: '', gender: '', regionId: null, typeId: null })

// 加载地区数据
async function loadRegions() {
  await axios.get(`${config.api}/singer/regions`).then(response=>{
    if (response.data){
      regions.value=response.data
    }
  })
}

// 加载歌手类型数据
async function loadSingerTypes() {
  await axios.get(`${config.api}/singer/singerTypes`).then(response=>{
    if (response.data){
      singerTypes.value=response.data
    }
  })
}

// 加载表格分页
async function loadData() {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (query.name) params.name = query.name
  if (query.gender) params.gender = query.gender
  if (query.regionId) params.regionId = query.regionId
  if (query.typeId) params.typeId = query.typeId
  await axios.get(`${config.api}/singer/page`,{params}).then(response=>{
    if (response.data){
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
    }
  })

}

function resetQuery() {
  query.name = ''
  query.gender = ''
  query.regionId = null
  query.typeId = null
  pageNum.value = 1
  loadData()
}

function handlePageChange(p) {
  pageNum.value = p
  loadData()
}

function handleSizeChange(s) {
  pageSize.value = s
  pageNum.value = 1
  loadData()
}

function openAdd() {
  Object.assign(form, { name: '', gender: '', regionId: null, typeId: null })
  addVisible.value = true
}

function openEdit(row) {
  Object.assign(editForm, {
    singerId: row.singerId,
    name: row.name,
    gender: row.gender,
    regionId: row.regionId || null,
    typeId: row.typeId || null
  })
  editVisible.value = true
}

async function submitAdd() {
  if (!form.name || !form.gender || !form.regionId || !form.typeId) {
    ElMessage.error('请填写必填项')
    return
  }
  axios.put(`${config.api}/singer/add`,form).then(response=>{
    if (response.data){
      ElMessage.success('新增成功')
      addVisible.value = false
      loadData()
    }
  })
}

async function submitEdit() {
  if (!editForm.singerId) {
    ElMessage.error('缺少歌手ID')
    return
  }
  const payload = { ...editForm }
  console.log(payload)
  axios.put(`${config.api}/singer/update`,payload).then(response=>{
    if (response.data){
      ElMessage.success('保存成功')
      editVisible.value = false
      loadData()
    }
  })
}
async function deleteSinger(row) {
  await ElMessageBox.confirm(`确定删除歌手「${row.name}」吗？`, '提示')
  await axios.delete(`${config.api}/singer/delete?id=${row.singerId}`).then(response=>{
    if (response.data){
      ElMessage.success('删除成功')
      loadData()
    }
  })
}

onMounted(async () => {
  await loadRegions()
  await loadSingerTypes()
  await loadData()
})
</script>

<style scoped>
.singer-page { padding: 12px; }
.toolbar { margin-bottom: 12px; }
.table-actions { margin-bottom: 8px; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>