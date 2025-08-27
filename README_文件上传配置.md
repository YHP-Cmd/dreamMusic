# 文件上传配置说明

## 问题描述
在发布音乐功能中，当上传大文件时出现以下错误：
```
org.apache.tomcat.util.http.fileupload.FileUploadBase$SizeLimitExceededException: 
the request was rejected because its size (61850617) exceeds the configured maximum (10485760)
```

这个错误表明上传的文件大小（约61MB）超过了Spring Boot默认的文件上传大小限制（10MB）。

## 解决方案
在所有Spring Boot模块的`application.properties`文件中添加文件上传大小限制配置。

## 配置说明

### 配置项
```properties
# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

### 参数解释
- **`spring.servlet.multipart.max-file-size`**: 单个文件的最大大小限制
- **`spring.servlet.multipart.max-request-size`**: 整个请求的最大大小限制（包含所有文件）

### 已配置的模块
1. **musicModel** (端口: 8088) - 音乐模块，处理音频文件上传
2. **userModel** (端口: 8850) - 用户模块，处理图片文件上传
3. **gateway** (端口: 8888) - 网关模块，需要支持大文件传输
4. **common** - 公共模块
5. **albumModel** (端口: 8810) - 专辑模块
6. **singerModel** (端口: 8090) - 歌手模块
7. **commentModel** (端口: 8820) - 评论模块
8. **Temp** (端口: 8000) - 临时模块

## 配置生效
修改配置文件后，需要重启相应的Spring Boot服务才能生效。

## 注意事项

### 1. 文件大小限制
- 当前配置支持最大100MB的文件上传
- 如果需要支持更大的文件，可以调整配置值
- 建议根据实际需求设置合理的大小限制

### 2. 服务器资源
- 大文件上传会消耗更多服务器内存和带宽
- 建议监控服务器资源使用情况
- 可以考虑使用分片上传等技术优化大文件传输

### 3. 超时设置
- 大文件上传可能需要较长时间
- 如果遇到超时问题，可能需要调整以下配置：
  ```properties
  # 连接超时时间
  server.tomcat.connection-timeout=60000
  # 读取超时时间
  server.tomcat.read-timeout=60000
  ```

### 4. 前端配置
- 前端也需要相应调整文件大小限制
- 确保前端验证与后端配置一致

## 验证配置
可以通过以下方式验证配置是否生效：

1. **查看启动日志**: 启动服务时查看是否有相关配置信息
2. **测试上传**: 尝试上传接近100MB的文件
3. **监控错误**: 观察是否还有文件大小超限的错误

## 相关技术文档
- [Spring Boot File Upload](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-uploading-files)
- [MultipartFile Configuration](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-multipart)
