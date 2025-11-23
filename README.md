# 悦享音乐系统 (DreamerMusic)

一个基于微服务架构的在线音乐平台，提供音乐播放、歌单管理、用户管理、评论互动等功能。

## 📋 项目概述

悦享音乐系统是一个前后端分离的音乐流媒体平台，采用Spring Cloud微服务架构和Vue 3前端框架构建。系统支持用户注册登录、音乐播放、歌单创建、歌手管理、专辑浏览、评论互动等核心功能。

## 🛠️ 技术栈

### 后端技术栈
- **框架**: Spring Boot 2.2.2.RELEASE
- **微服务**: Spring Cloud Hoxton.SR1
- **服务发现**: Spring Cloud Alibaba Nacos 2.2.5.RELEASE
- **网关**: Spring Cloud Gateway
- **ORM**: MyBatis Plus
- **数据库**: MySQL
- **缓存**: Redis (StringRedisTemplate)
- **邮件服务**: JavaMailSender (QQ邮箱)
- **认证**: JWT (JSON Web Token)
- **文件上传**: Spring MultipartFile
- **构建工具**: Maven

### 前端技术栈
- **框架**: Vue 3.5.18
- **构建工具**: Vite 7.1.2
- **UI组件库**: Element Plus 2.10.7
- **路由**: Vue Router 4.5.1
- **状态管理**: Pinia 3.0.3
- **HTTP客户端**: Axios 1.11.0
- **图表库**: ECharts 6.0.0
- **图标**: Element Plus Icons 2.3.2

## 📁 项目结构

```
dreamerMusic/
├── albumModel/          # 专辑服务模块
├── commentModel/        # 评论服务模块
├── common/              # 公共模块（实体类、工具类）
├── gateway/            # API网关服务
├── musicModel/          # 音乐服务模块
├── singerModel/         # 歌手服务模块
├── userModel/           # 用户服务模块
├── temp/                # 临时模块
├── vue-app/             # 前端Vue应用
├── upload/              # 文件上传目录
└── pom.xml              # Maven父POM配置
```

## 🔧 后端模块说明

### 1. userModel (用户服务) - 端口: 8850
**功能**:
- 用户注册、登录、认证
- 用户信息管理（增删改查）
- 用户类型管理
- 邮箱验证码发送
- 密码重置
- 用户状态管理（冻结/解冻）
- 文件上传（头像、图片）

**主要接口**:
- `POST /login` - 用户登录
- `POST /register` - 用户注册
- `POST /sendVerificationCode` - 发送验证码
- `GET /selectByPage` - 分页查询用户
- `PUT /update` - 更新用户信息
- `PUT /freeze` - 冻结用户
- `PUT /unfreeze` - 解冻用户
- `POST /upload` - 文件上传

### 2. musicModel (音乐服务) - 端口: 8088
**功能**:
- 歌曲管理（增删改查）
- 歌曲类型管理
- 音乐文件上传（音频、封面图片）
- 歌单管理（创建、查询、统计）
- 收藏管理
- 播放记录管理
- 评论管理
- 热门歌曲推荐
- 排行榜统计

**主要接口**:
- `GET /page` - 分页查询歌曲
- `GET /getAll` - 获取所有歌曲
- `GET /audio` - 获取音频文件
- `POST /upload/audio` - 上传音频文件
- `POST /upload/image` - 上传图片文件
- `POST /addSong` - 添加歌曲
- `PUT /updateSong` - 更新歌曲
- `PUT /freezeSong` - 冻结歌曲
- `POST /createPlaylist` - 创建歌单
- `GET /getHot` - 获取热门歌曲
- `GET /countByType` - 按类型统计歌曲数量

### 3. singerModel (歌手服务) - 端口: 8090
**功能**:
- 歌手信息管理（增删改查）
- 歌手类型管理
- 歌手分页查询（支持按名称、性别、地区、类型筛选）

**主要接口**:
- `GET /page` - 分页查询歌手
- `GET /getAll` - 获取所有歌手
- `GET /getById` - 根据ID获取歌手信息

### 4. albumModel (专辑服务) - 端口: 8810
**功能**:
- 专辑信息管理
- 专辑查询

**主要接口**:
- `GET /getById` - 根据ID获取专辑信息

### 5. commentModel (评论服务) - 端口: 8820
**功能**:
- 评论管理（增删改查）
- 评论分页查询

### 6. gateway (API网关) - 端口: 8888
**功能**:
- 统一API入口
- 路由转发
- 负载均衡
- 跨域处理

**路由配置**:
- `/users/**` → userModel服务
- `/singer/**` → singerModel服务
- `/music/**` → musicModel服务
- `/album/**` → albumModel服务

### 7. common (公共模块)
**功能**:
- 公共实体类（User, Song, Singer, Album, Comment等）
- 工具类（JWTUtil, HashUtil, UploadUtil等）
- 统一响应结果（Result）
- 认证过滤器（AuthFilter）

## 🎨 前端模块说明

### 主要页面和组件

#### 认证模块 (`src/auth/`, `src/views/auth/`)
- **登录页面** (`login.vue`) - 用户登录
- **注册页面** (`register.vue`) - 用户注册
- **忘记密码** (`forgotPassword.vue`) - 密码重置

#### 首页模块 (`src/views/home/`)
- **首页** (`home.vue`) - 主页面
- **用户首页** (`userHome.vue`) - 用户个人主页

#### 组件模块 (`src/components/`)
- **推荐** (`recommend.vue`) - 音乐推荐
- **排行榜** (`ranking.vue`) - 音乐排行榜
- **歌单** (`playlist.vue`) - 歌单列表
- **歌手** (`singer.vue`) - 歌手列表
- **搜索** (`select.vue`) - 音乐搜索
- **我的** (`my.vue`) - 个人中心
- **评论** (`comment.vue`) - 评论组件

#### 信息详情模块 (`src/info/`)
- **歌曲详情** (`songInfo.vue`) - 歌曲信息页面
- **歌手详情** (`singerInfo.vue`) - 歌手信息页面
- **专辑详情** (`albumInfo.vue`) - 专辑信息页面
- **歌单详情** (`playlistInfo.vue`) - 歌单详情页面
- **播放记录** (`record.vue`) - 播放历史
- **我的收藏** (`myfaovrites.vue`) - 收藏列表
- **排行榜详情** (`sumRank.vue`, `statRank.vue`) - 排行榜详情

#### 管理后台模块 (`src/views/admin/`)
- **用户管理** (`userMessage.vue`) - 用户信息管理
- **歌曲管理** (`songMessage.vue`) - 歌曲信息管理
- **歌手管理** (`singerMessage.vue`) - 歌手信息管理
- **用户类型管理** (`userTypeMessage.vue`) - 用户类型配置
- **歌曲类型管理** (`songTypeMessage.vue`) - 歌曲类型配置
- **歌手类型管理** (`singerTypeMessage.vue`) - 歌手类型配置
- **权限管理** (`permissionMessage.vue`) - 权限配置
- **个人设置** (`profileSettings.vue`) - 个人资料设置
- **安全设置** (`securitySettings.vue`) - 安全设置

#### 布局组件 (`src/layouts/`)
- **主布局** (`Layout.vue`) - 管理后台布局
- **首页布局** (`home.vue`) - 用户端布局
- **用户布局** (`userLayout.vue`) - 用户个人页面布局

### 状态管理 (`src/stores/`)
- **用户状态** (`user.js`) - 用户信息、登录状态管理

### 配置文件
- **路由配置** (`router.js`) - Vue Router路由配置
- **API配置** (`config.js`) - 后端API地址配置
- **主入口** (`main.js`) - Vue应用入口文件

## 🚀 环境要求

### 后端环境
- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis (可选，用于缓存)
- Nacos Server 2.x (服务注册与发现)

### 前端环境
- Node.js 16+
- pnpm 或 npm

## 📦 安装和运行

### 1. 数据库配置
创建MySQL数据库 `music_system`，并配置各模块的 `application.properties` 文件中的数据库连接信息。

### 2. 启动Nacos
```bash
# 下载并启动Nacos Server
# 默认地址: http://localhost:8848/nacos
```

### 3. 启动后端服务
```bash
# 在项目根目录下
mvn clean install

# 依次启动各个服务模块
# 1. 启动 common 模块（如果独立运行）
# 2. 启动 userModel (端口: 8850)
# 3. 启动 musicModel (端口: 8088)
# 4. 启动 singerModel (端口: 8090)
# 5. 启动 albumModel (端口: 8810)
# 6. 启动 commentModel (端口: 8820)
# 7. 启动 gateway (端口: 8888)
```

### 4. 启动前端服务
```bash
cd vue-app
pnpm install  # 或 npm install
pnpm dev      # 或 npm run dev
```

前端默认运行在 `http://localhost:5173` (Vite默认端口)

## ⚙️ 配置说明

### 后端配置

#### 数据库配置
各模块的 `application.properties` 中配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/music_system?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

#### Nacos配置
```properties
spring.cloud.nacos.discovery.server-addr=localhost:8848
```

#### 文件上传配置
```properties
# 文件上传大小限制
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

#### 邮件服务配置 (userModel)
```properties
spring.mail.username=your_email@qq.com
spring.mail.password=your_email_password
spring.mail.host=smtp.qq.com
spring.mail.port=465
spring.mail.properties.mail.smtp.ssl.enable=true
```

### 前端配置

#### API地址配置 (`vue-app/src/config.js`)
```javascript
const config = {
    api: 'http://localhost:8888',  // 网关地址
}
```

## 🎯 核心功能

### 用户功能
- ✅ 用户注册、登录、登出
- ✅ 邮箱验证码注册
- ✅ 密码重置
- ✅ 个人资料管理
- ✅ 头像上传

### 音乐功能
- ✅ 音乐播放
- ✅ 音乐搜索
- ✅ 音乐分类浏览
- ✅ 热门歌曲推荐
- ✅ 排行榜（总榜、统计榜）
- ✅ 播放记录
- ✅ 音乐收藏

### 歌单功能
- ✅ 创建歌单
- ✅ 歌单管理
- ✅ 歌单详情
- ✅ 歌单统计

### 歌手功能
- ✅ 歌手列表浏览
- ✅ 歌手详情查看
- ✅ 歌手分类筛选
- ✅ 歌手歌曲列表

### 专辑功能
- ✅ 专辑浏览
- ✅ 专辑详情
- ✅ 专辑歌曲列表

### 评论功能
- ✅ 歌曲评论
- ✅ 评论查看
- ✅ 评论管理

### 管理功能
- ✅ 用户管理（增删改查、冻结/解冻）
- ✅ 歌曲管理（发布、编辑、冻结）
- ✅ 歌手管理
- ✅ 类型管理（用户类型、歌曲类型、歌手类型）
- ✅ 权限管理

## 📝 API接口文档

### 用户服务 (userModel)
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/users/login` | 用户登录 |
| POST | `/users/register` | 用户注册 |
| POST | `/users/sendVerificationCode` | 发送验证码 |
| GET | `/users/selectByPage` | 分页查询用户 |
| PUT | `/users/update` | 更新用户信息 |
| PUT | `/users/freeze` | 冻结用户 |
| PUT | `/users/unfreeze` | 解冻用户 |
| POST | `/users/upload` | 文件上传 |

### 音乐服务 (musicModel)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/music/page` | 分页查询歌曲 |
| GET | `/music/getAll` | 获取所有歌曲 |
| GET | `/music/audio` | 获取音频文件 |
| POST | `/music/upload/audio` | 上传音频文件 |
| POST | `/music/upload/image` | 上传图片文件 |
| POST | `/music/addSong` | 添加歌曲 |
| PUT | `/music/updateSong` | 更新歌曲 |
| POST | `/music/createPlaylist` | 创建歌单 |
| GET | `/music/getHot` | 获取热门歌曲 |

### 歌手服务 (singerModel)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/singer/page` | 分页查询歌手 |
| GET | `/singer/getAll` | 获取所有歌手 |
| GET | `/singer/getById` | 根据ID获取歌手 |

### 专辑服务 (albumModel)
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/album/getById` | 根据ID获取专辑 |

## 📂 文件存储

### 存储路径
- **音频文件**: `D:/下载/music/`
- **图片文件**: `D:/下载/image/`

### 支持的文件格式
- **音频**: MP3, WAV, FLAC, M4A (最大50MB)
- **图片**: JPG, JPEG, PNG (最大2MB)

> ⚠️ **注意**: 请根据实际部署环境修改文件存储路径

## 🔐 认证机制

系统使用JWT (JSON Web Token) 进行用户认证：
- 登录成功后返回token
- 前端将token存储在localStorage或sessionStorage
- 后续请求在请求头中携带token
- 后端通过AuthFilter验证token有效性

## 📊 数据库设计

主要数据表：
- `user` - 用户表
- `user_type` - 用户类型表
- `song` - 歌曲表
- `song_type` - 歌曲类型表
- `singer` - 歌手表
- `singer_type` - 歌手类型表
- `album` - 专辑表
- `playlist` - 歌单表
- `comment` - 评论表
- `listen` - 播放记录表
- `myfaovrites` - 收藏表

## 🐛 常见问题

### 1. 文件上传失败
- 检查文件大小是否超过限制（100MB）
- 检查文件存储路径是否存在且有写权限
- 检查application.properties中的文件上传配置

### 2. 服务无法注册到Nacos
- 确认Nacos Server已启动
- 检查Nacos地址配置是否正确
- 检查网络连接

### 3. 跨域问题
- 后端已配置`@CrossOrigin`注解
- 前端请求地址需与后端网关地址一致

### 4. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库连接配置（用户名、密码、数据库名）
- 确认数据库已创建

## 📄 相关文档

- [发布音乐功能说明](./README_发布音乐功能.md)
- [新建歌单功能说明](./README_新建歌单功能.md)
- [文件上传配置说明](./README_文件上传配置.md)

## 👥 开发团队

- 项目名称: 悦享音乐系统
- 开发组: 蒋云飞组
- 开发者: 阳辉鹏

## 📜 许可证

本项目仅供学习和研究使用。

## 🔄 更新日志

### v1.0.0
- ✅ 完成用户注册登录功能
- ✅ 完成音乐播放功能
- ✅ 完成歌单管理功能
- ✅ 完成歌手管理功能
- ✅ 完成专辑管理功能
- ✅ 完成评论功能
- ✅ 完成管理后台功能
- ✅ 完成文件上传功能

---

**注意**: 本项目为学习项目，生产环境使用请进行安全加固和性能优化。

