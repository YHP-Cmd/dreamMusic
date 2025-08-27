# 发布音乐功能说明

## 功能概述
本功能允许管理员发布新的音乐，包括歌曲信息、封面图片和音频文件。

## 功能特性

### 1. 歌曲信息输入
- **歌曲名称**: 必填项，不能为空
- **歌曲类型**: 必填项，从下拉列表选择
- **歌手**: 必填项，从下拉列表选择
- **歌曲简介**: 可选，描述歌曲信息
- **状态**: 默认为"正常"，可选择"已冻结"

### 2. 文件上传功能
- **封面图片**: 支持JPG、JPEG、PNG格式，最大2MB
- **音频文件**: 支持MP3、WAV、FLAC、M4A格式，最大50MB
- **上传路径**: 
  - 图片：D:/下载/image
  - 音频：D:/下载/music

### 3. 文件预览功能
- **图片预览**: 上传后立即显示封面预览
- **音频预览**: 上传后可直接播放试听

### 4. 管理功能
- **编辑歌曲**: 修改已发布的歌曲信息
- **冻结/解冻**: 控制歌曲的可用状态
- **试听功能**: 预览音频播放效果

## 使用方法

### 1. 访问管理页面
在管理员界面点击"歌曲管理"菜单

### 2. 发布新歌曲
1. 点击"发布歌曲"按钮
2. 在弹出的对话框中填写歌曲信息：
   - 输入歌曲名称
   - 选择歌曲类型
   - 选择歌手
   - 上传封面图片（可选）
   - 上传音频文件
   - 输入歌曲简介（可选）
   - 选择状态
3. 点击"确认"按钮完成发布

### 3. 管理已发布的歌曲
- **编辑**: 点击"编辑"按钮修改歌曲信息
- **冻结/解冻**: 点击对应按钮控制歌曲状态
- **试听**: 点击"预览"按钮试听音频

## 技术实现

### 前端 (Vue 3 + Element Plus)
- 使用Element Plus的Dialog、Form、Upload、Table组件
- 支持文件拖拽上传
- 实时文件预览
- 表单验证和错误处理
- 分页显示歌曲列表

### 后端 (Spring Boot)
- 音频上传API: `/music/upload/audio`
- 图片上传API: `/music/upload/image`
- 添加歌曲API: `/music/addSong`
- 更新歌曲API: `/music/updateSong`
- 冻结歌曲API: `/music/freezeSong`
- 解冻歌曲API: `/music/unfreezeSong`
- 文件格式和大小验证
- 自动生成唯一文件名

### 数据库
- 歌曲信息存储在song表中
- 字段包括：songId、name、typeId、path、image、introduction、singerId、albumId、status

## 支持的文件格式

### 音频文件
- **MP3**: 最常用的音频格式
- **WAV**: 无损音频格式
- **FLAC**: 无损压缩音频格式
- **M4A**: Apple音频格式

### 图片文件
- **JPG/JPEG**: 常用图片格式
- **PNG**: 支持透明背景的图片格式

## 文件大小限制

- **音频文件**: 最大50MB
- **图片文件**: 最大2MB

## 注意事项

1. **文件格式**: 只支持指定的音频和图片格式
2. **文件大小**: 注意文件大小限制
3. **网络连接**: 上传大文件需要稳定的网络连接
4. **存储空间**: 确保D:/下载目录有足够的存储空间
5. **权限**: 只有管理员可以发布和管理歌曲

## 错误处理

- **文件格式错误**: 提示"只支持指定格式的文件"
- **文件过大**: 提示"文件大小超过限制"
- **网络错误**: 提示"上传失败，请重试"
- **表单验证**: 提示"请填写必填项"
- **操作失败**: 提示"操作失败，请重试"

## 开发环境

- 前端: Vue 3 + TypeScript + Element Plus
- 后端: Spring Boot + MyBatis Plus
- 数据库: MySQL
- 文件存储: 本地文件系统 (D:/下载/)

## API接口说明

### 上传音频文件
```
POST /music/upload/audio
Content-Type: multipart/form-data
参数: file (音频文件)
返回: { code: 200, message: "上传成功", data: "文件名" }
```

### 上传图片文件
```
POST /music/upload/image
Content-Type: multipart/form-data
参数: file (图片文件)
返回: { code: 200, message: "上传成功", data: "文件名" }
```

### 添加歌曲
```
POST /music/addSong
Content-Type: application/json
参数: Song对象
返回: boolean (成功/失败)
```

### 更新歌曲
```
PUT /music/updateSong
Content-Type: application/json
参数: Song对象
返回: boolean (成功/失败)
```

### 冻结歌曲
```
PUT /music/freezeSong?songId={id}
返回: boolean (成功/失败)
```

### 解冻歌曲
```
PUT /music/unfreezeSong?songId={id}
返回: boolean (成功/失败)
```
