<template>
  <div class="permission-container">
    <el-card class="permission-card">
<!--      <template #header>-->
<!--        <div class="card-header">-->
<!--          <el-icon class="header-icon"><Lock /></el-icon>-->
<!--          <span class="header-title">权限管理</span>-->
<!--        </div>-->
<!--      </template>-->
      
      <!-- 用户类型选择区域 -->
      <div class="type-selector-section">
        <div class="selector-wrapper">
          <el-form :inline="true" class="permission-form">
            <el-form-item label="选择用户类型：" class="type-form-item">
              <el-select 
                v-model="selectedTypeId" 
                placeholder="请选择用户类型"
                @change="handleTypeChange"
                class="type-select"
                clearable
              >
                <el-option
                  v-for="type in userTypes"
                  :key="type.typeId"
                  :label="type.typeName"
                  :value="type.typeId"
                >
                  <span class="option-content">
                    <el-icon><User /></el-icon>
                    {{ type.typeName }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item class="action-buttons">
              <el-button 
                type="primary" 
                @click="savePermissions" 
                :disabled="!selectedTypeId"
                :loading="saving"
              >
                <el-icon><Check /></el-icon>
                保存权限
              </el-button>
              <el-button 
                @click="clearAllPermissions" 
                :disabled="!selectedTypeId"
                :loading="clearing"
              >
                <el-icon><Delete /></el-icon>
                清除所有权限
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <!-- 权限树形结构 -->
      <div class="permission-tree-section" v-if="selectedTypeId">
        <div class="tree-header">
          <el-divider content-position="left">
            <span class="divider-text">
              <el-icon><Menu /></el-icon>
              模块权限配置
            </span>
          </el-divider>
          <div class="tree-actions">
            <el-button size="small" @click="expandAll" text>
              <el-icon><Plus /></el-icon>
              展开全部
            </el-button>
            <el-button size="small" @click="collapseAll" text>
              <el-icon><Minus /></el-icon>
              收起全部
            </el-button>
            <el-button size="small" @click="checkAll" text>
              <el-icon><Select /></el-icon>
              全选
            </el-button>
            <el-button size="small" @click="uncheckAll" text>
              <el-icon><CloseBold /></el-icon>
              取消全选
            </el-button>
          </div>
        </div>
        
        <div class="tree-container">
          <el-tree
            ref="permissionTree"
            :data="treeData"
            :props="treeProps"
            show-checkbox
            node-key="moduleId"
            :default-expanded-keys="defaultExpandedKeys"
            :default-checked-keys="selectedModules"
            @check="handleTreeCheck"
            class="permission-tree"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <div class="node-content">
                  <el-icon class="node-icon" :class="getIconClass(data)">
                    <component :is="getIconComponent(data.icon)" />
                  </el-icon>
                  <span class="node-label">{{ data.moduleName }}</span>
                  <el-tag size="small" class="node-route" v-if="data.route">
                    {{ data.route }}
                  </el-tag>
                </div>
              </div>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 空状态提示 -->
      <div class="empty-state" v-if="!selectedTypeId">
        <el-empty 
          description="请先选择用户类型来配置权限"
          :image-size="120"
        >
          <template #image>
            <el-icon class="empty-icon"><UserFilled /></el-icon>
          </template>
          <el-button type="primary" @click="focusTypeSelect">
            选择用户类型
          </el-button>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Lock, User, Check, Delete, Menu, Plus, Minus, Select, CloseBold, UserFilled,
  HomeFilled, Setting, Tools, Headset, Microphone, CollectionTag, Stamp, 
  VideoPlay, Avatar
} from '@element-plus/icons-vue'
import axios from "axios";
import config from "../../config.js";

// 响应式数据
const userTypes = ref([]) // 用户类型列表
const modules = ref([]) // 模块列表
const selectedTypeId = ref(null) // 选中的用户类型ID
const selectedModules = ref([]) // 选中的模块ID列表
const saving = ref(false) // 保存状态
const clearing = ref(false) // 清除状态
const permissionTree = ref(null) // 树组件引用

// 树形组件配置
const treeProps = {
  children: 'children',
  label: 'moduleName'
}

// 默认展开的节点
const defaultExpandedKeys = ref([])

// 构建树形数据
const treeData = computed(() => {
  if (!modules.value.length) return []
  
  // 构建树形结构
  const buildTree = (parentId = 0) => {
    return modules.value
      .filter(module => module.parentId === parentId)
      .map(module => ({
        ...module,
        children: buildTree(module.moduleId)
      }))
      .sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  }
  
  return buildTree()
})

// 获取图标组件
const getIconComponent = (iconName) => {
  const iconMap = {
    'HomeFilled': HomeFilled,
    'Menu': Menu,
    'Setting': Setting,
    'User': User,
    'Headset': Headset,
    'UserFilled': UserFilled,
    'Tools': Tools,
    'Microphone': Microphone,
    'CollectionTag': CollectionTag,
    'Stamp': Stamp,
    'VideoPlay': VideoPlay,
    'Avatar': Avatar,
    'Lock': Lock
  }
  return iconMap[iconName] || Menu
}

// 获取图标样式类
const getIconClass = (data) => {
  return {
    'parent-icon': data.parentId === 0,
    'child-icon': data.parentId !== 0
  }
}

// 获取用户类型列表
const getUserTypes = async () => {
  try {
    await axios.get(`${config.api}/users/getAllUserType`).then(response=>{
      if (response.data){
        userTypes.value=response
      } else {
        ElMessage.error('获取用户类型失败')
      }
    })
  } catch (error) {
    ElMessage.error('获取用户类型失败')
  }
}

// 获取所有模块列表
const getModules = async () => {
  try {
    const response = await request.get('/permission/modules')
    if (response.data.code === 200) {
      modules.value = response.data.data
      // 设置默认展开的根节点
      defaultExpandedKeys.value = modules.value
        .filter(m => m.parentId === 0)
        .map(m => m.moduleId)
    } else {
      ElMessage.error('获取模块列表失败')
    }
  } catch (error) {
    ElMessage.error('获取模块列表失败')
  }
}

// 获取已分配的权限
const getAssignedPermissions = async (typeId) => {
  try {
    const response = await request.get(`/permission/assigned/${typeId}`)
    if (response.data.code === 200) {
      selectedModules.value = response.data.data
      // 更新树的选中状态
      await nextTick()
      if (permissionTree.value) {
        permissionTree.value.setCheckedKeys(selectedModules.value)
      }
    } else {
      ElMessage.error('获取权限信息失败')
    }
  } catch (error) {
    ElMessage.error('获取权限信息失败')
  }
}

// 用户类型改变事件
const handleTypeChange = (typeId) => {
  if (typeId) {
    getAssignedPermissions(typeId)
  } else {
    selectedModules.value = []
    if (permissionTree.value) {
      permissionTree.value.setCheckedKeys([])
    }
  }
}

// 树节点选中事件
const handleTreeCheck = (data, checked) => {
  if (permissionTree.value) {
    // 获取完全选中的节点
    const checkedKeys = permissionTree.value.getCheckedKeys()
    // 获取半选中的节点（父节点部分子节点被选中时的状态）
    const halfCheckedKeys = permissionTree.value.getHalfCheckedKeys()
    
    // 只保存完全选中的节点，半选中的父节点不需要保存到权限中
    // 因为权限是基于具体的功能模块，父节点只是分类
    selectedModules.value = checkedKeys
  }
}

// 展开全部
const expandAll = () => {
  if (permissionTree.value) {
    // 获取所有节点的key
    const allKeys = modules.value.map(m => m.moduleId)
    // 使用Element Plus树组件的API展开所有节点
    for (const key of allKeys) {
      permissionTree.value.store.nodesMap[key] && (permissionTree.value.store.nodesMap[key].expanded = true)
    }
  }
}

// 收起全部
const collapseAll = () => {
  if (permissionTree.value) {
    // 获取所有节点的key
    const allKeys = modules.value.map(m => m.moduleId)
    // 使用Element Plus树组件的API收起所有节点
    for (const key of allKeys) {
      permissionTree.value.store.nodesMap[key] && (permissionTree.value.store.nodesMap[key].expanded = false)
    }
  }
}

// 全选
const checkAll = () => {
  if (permissionTree.value) {
    const allKeys = modules.value.map(m => m.moduleId)
    permissionTree.value.setCheckedKeys(allKeys)
    selectedModules.value = allKeys
  }
}

// 取消全选
const uncheckAll = () => {
  if (permissionTree.value) {
    permissionTree.value.setCheckedKeys([])
    selectedModules.value = []
  }
}

// 聚焦到类型选择器
const focusTypeSelect = () => {
  const selectEl = document.querySelector('.type-select input')
  if (selectEl) {
    selectEl.focus()
  }
}

// 保存权限
const savePermissions = async () => {
  if (!selectedTypeId.value) {
    ElMessage.warning('请先选择用户类型')
    return
  }

  saving.value = true
  try {
    const response = await request.post('/permission/assign', selectedModules.value, {
      params: { typeId: selectedTypeId.value }
    })
    if (response.data.code === 200) {
      ElMessage.success('权限保存成功')
    } else {
      ElMessage.error(response.data.message || '权限保存失败')
    }
  } catch (error) {
    ElMessage.error('权限保存失败')
  } finally {
    saving.value = false
  }
}

// 清除所有权限
const clearAllPermissions = async () => {
  if (!selectedTypeId.value) {
    ElMessage.warning('请先选择用户类型')
    return
  }

  try {
    await ElMessageBox.confirm('确定要清除该用户类型的所有权限吗？', '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    clearing.value = true
    const response = await request.delete(`/permission/clear/${selectedTypeId.value}`)
    if (response.data.code === 200) {
      selectedModules.value = []
      if (permissionTree.value) {
        permissionTree.value.setCheckedKeys([])
      }
      ElMessage.success('权限清除成功')
    } else {
      ElMessage.error(response.data.message || '权限清除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('权限清除失败')
    }
  } finally {
    clearing.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  getUserTypes()
  getModules()
})
</script>

<style scoped>
.permission-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.permission-card {
  max-width: 1200px;
  margin: 0 auto;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.header-icon {
  font-size: 24px;
  color: #409eff;
}

.header-title {
  background: linear-gradient(135deg, #409eff, #67c23a);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.type-selector-section {
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #f8f9ff, #f0f9ff);
  border-radius: 12px;
  border: 1px solid #e1e8ff;
}

.selector-wrapper {
  display: flex;
  justify-content: center;
}

.permission-form {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.type-form-item {
  margin-bottom: 0;
}

.type-select {
  width: 240px;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-buttons {
  margin-bottom: 0;
}

.action-buttons .el-button {
  padding: 12px 24px;
  border-radius: 8px;
  font-weight: 500;
}

.permission-tree-section {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: #fafbfc;
  border-bottom: 1px solid #e4e7ed;
}

.divider-text {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.tree-actions {
  display: flex;
  gap: 8px;
}

.tree-actions .el-button {
  border: none;
  background: transparent;
  color: #606266;
  font-size: 13px;
}

.tree-actions .el-button:hover {
  color: #409eff;
  background: #ecf5ff;
}

.tree-container {
  padding: 24px;
  /* 移除最大高度限制和滚动条 */
  max-height: none;
  overflow-y: visible;
}

.permission-tree {
  background: transparent;
}

:deep(.el-tree-node__content) {
  height: 48px;
  padding: 8px 16px;
  border-radius: 8px;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

:deep(.el-tree-node__content:hover) {
  background: #f0f9ff;
  transform: translateX(4px);
}

:deep(.el-tree-node__expand-icon) {
  color: #409eff;
  font-size: 16px;
}

:deep(.el-checkbox) {
  margin-right: 12px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #409eff;
  border-color: #409eff;
}

.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.node-content {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.node-icon {
  font-size: 18px;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.parent-icon {
  color: #409eff;
  font-size: 20px;
}

.child-icon {
  color: #67c23a;
  font-size: 16px;
}

.node-label {
  font-weight: 500;
  color: #303133;
  font-size: 14px;
}

.node-route {
  background: #f0f2f5;
  color: #606266;
  border: none;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 120px;
  color: #c0c4cc;
}

:deep(.el-empty__description) {
  color: #909399;
  font-size: 16px;
  margin: 24px 0;
}

:deep(.el-divider__text) {
  background: #fafbfc;
  color: #303133;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .permission-container {
    padding: 16px;
  }
  
  .permission-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .type-select {
    width: 100%;
  }
  
  .tree-header {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .tree-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
}

/* 滚动条样式 */
.tree-container::-webkit-scrollbar {
  width: 6px;
}

.tree-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.tree-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>