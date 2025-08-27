<template>
  <div class="user-page">
    <el-card class="toolbar" shadow="never">
      <el-form :inline="true" :model="query" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="输入用户名" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.typeId" placeholder="选择类型" clearable style="width: 160px">
            <el-option v-for="t in userTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="选择状态" clearable style="width: 160px">
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
        <el-button type="success" @click="openAdd">新增</el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="100" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column prop="typeId" label="类型" min-width="120" :formatter="formatTypeId"/>
        <el-table-column prop="phone" label="电话" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" text @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === '正常'" size="small" type="danger" text @click="freeze(row)">冻结</el-button>
            <el-button v-else size="small" type="success" text @click="unfreeze(row)">解冻</el-button>
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

    <el-dialog v-model="addVisible" title="新增用户" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.typeId" placeholder="选择类型">
            <el-option v-for="t in userTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户ID">
          <el-input v-model="editForm.id" disabled />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" placeholder="选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editForm.typeId" placeholder="选择类型">
            <el-option v-for="t in userTypes" :key="t.typeId" :label="t.name" :value="t.typeId" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="editForm.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="已冻结" value="已冻结" />
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
import config  from "../../config.js";
const formatTypeId=(row, column, index)=> {
  // 根据 typeId 返回对应的文本
  switch (row.typeId) {
    case '1':
      return '管理员';
    case '2':
      return '普通用户';
    case '3':
      return '歌手';
    case '4':
      return '超级管理员';
    default:
      return '未知类型';
  }
  }
// 查询条件
const query = reactive({ username: '', typeId: null, status: '' })

// 列表分页数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 下拉：用户类型
const userTypes = ref([])

// 弹窗新增
const addVisible = ref(false)
const form = reactive({ username: '', password: '', gender: '', typeId: null, phone: '', email: '' })

// 弹窗编辑
const editVisible = ref(false)
const editForm = reactive({ id: null, username: '', gender: '', typeId: null, phone: '', email: '', status: '' })

// 加载用户类型
async function loadUserTypes() {
  axios.get(`${config.api}/users/getAllUserType`).then(response=>{
    if (response.data){
      userTypes.value=response.data
    }
  })
}

// 加载表格分页
async function loadData() {
  const params = { pageNum: pageNum.value, pageSize: pageSize.value }
  if (query.username) params.username = query.username
  if (query.typeId) params.typeId = query.typeId
  if (query.status) params.status = query.status
  axios.get(`${config.api}/users/selectByPage`,{params}).then(response=>{
    if (response.data){
      const page = response.data
      tableData.value = page.records || []
      total.value = page.total || 0
    }
  })

}

function resetQuery() {
  query.username = ''
  query.typeId = null
  query.status = ''
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
  Object.assign(form, { username: '', password: '', gender: '', typeId: null, phone: '', email: '' })
  addVisible.value = true
}

function openEdit(row) {
  Object.assign(editForm, {
    id: row.id,
    username: row.username,
    gender: row.gender,
    typeId: row.typeId || null,
    phone: row.phone,
    email: row.email,
    status: row.status
  })
  editVisible.value = true
}

async function submitAdd() {
  if (!form.username || !form.password || !form.typeId) {
    ElMessage.error('请填写必填项')
    return
  }
  axios.post(`${config.api}/users/add`,form).then(response=>{
    if (response.data){
      ElMessage.success('新增成功')
      addVisible.value = false
      loadData()
    }
  })
}

async function submitEdit() {
  if (!editForm.id) {
    ElMessage.error('缺少用户ID')
    return
  }
  const user = { ...editForm }
  axios.put(`${config.api}/users/update`,user).then(response=>{
    if (response.data){
      console.log('fasong')
      ElMessage.success('保存成功')

      editVisible.value = false

      loadData()
    }
  })
}

async function freeze(row) {
  await ElMessageBox.confirm(`确定冻结用户「${row.username}」吗？`, '提示')
  await axios.put(`${config.api}/users/freeze?id=${row.id}`).then(response=>{
    if (response.data){
      ElMessage.success('已冻结')
      loadData()
    }
  })
}
async function unfreeze(row) {
  await axios.put(`${config.api}/users/unfreeze?id=${row.id}`).then(response=>{
    if (response.data){
      ElMessage.success('已解冻')
      loadData()
    }
  })
}
onMounted(async () => {
  await loadUserTypes()
  await loadData()
})
</script>

<style scoped>
.user-page { padding: 12px; }
.toolbar { margin-bottom: 12px; }
.table-actions { margin-bottom: 8px; }
.pager { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>