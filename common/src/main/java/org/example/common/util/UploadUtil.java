package org.example.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class UploadUtil {

    // 保存图片，返回相对访问路径 /image/xxx（仅在确认入库时调用该方法）
    public String saveImage(MultipartFile file) throws IOException {
        String baseDir = System.getProperty("user.dir") + "/uploads/images/";
        return save(file, baseDir, "/image/");
    }

    // 保存音乐，返回相对访问路径 /music/xxx（仅在确认入库时调用该方法）
    public String saveMusic(MultipartFile file) throws IOException {
        String baseDir = System.getProperty("user.dir") + "/uploads/music/";
        return save(file, baseDir, "/music/");
    }

    // 内部保存方法
    private String save(MultipartFile file, String baseDir, String urlPrefix) throws IOException {
        String dateDir = LocalDate.now().toString();
        Path dir = Paths.get(baseDir, dateDir);
        Files.createDirectories(dir);
        String original = file.getOriginalFilename();
        String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = dir.resolve(fileName);
        file.transferTo(target.toFile());
        return urlPrefix + dateDir + "/" + fileName;
    }
}


