package org.example.musicmodel.controller;

import org.example.musicmodel.mapper.MusicMapper;
import org.example.pojo.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin
public class MusicController {

    @Autowired
    private MusicMapper musicMapper;

    private static final String AUDIO_DIR = "D:/下载/music";

    private static final String IMG_DIR = "D:/下载/image";

    @GetMapping("/getAll")
    public List<Song> getAll(){
        return musicMapper.selectList(null);
    }
    @GetMapping("/audio")
    public ResponseEntity<Resource> getAudio(@RequestParam String filename) {
        // 安全检查：文件名不能包含非法字符
        if (filename.contains("..")) {
            return ResponseEntity.badRequest().body(null);
        }

        // 根据传入的文件名构造路径
        Path filePath = Paths.get(AUDIO_DIR, filename).normalize();

        // 确保文件路径位于预期的目录中
        if (!filePath.startsWith(AUDIO_DIR)) {
            return ResponseEntity.badRequest().body(null); // 防止路径遍历攻击
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());

            // 如果文件不存在，返回404
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 设置 MIME 类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "audio/mpeg";  // 默认 MIME 类型
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null); // 文件读取异常
        }
    }
    @GetMapping("/recommend")
    public List<Song> getRecommend() {
        return musicMapper.selectList(null);
    }
    @GetMapping("/{filename}")
    public ResponseEntity<?> getImage(@PathVariable("filename") String filename) {
        try {
            // 文件的路径
            Path filePath = Paths.get(IMG_DIR, filename);
            // 读取文件
            if (Files.exists(filePath)) {
                byte[] imageBytes = Files.readAllBytes(filePath);
                return ResponseEntity.ok().body(imageBytes);
            } else {
                return ResponseEntity.status(404).body("图片未找到");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("获取图片失败: " + e.getMessage());
        }
    }
}

