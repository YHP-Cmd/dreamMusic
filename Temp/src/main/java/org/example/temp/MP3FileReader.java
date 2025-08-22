package org.example.temp;

import org.example.common.pojo.Song;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MP3FileReader {

    public static List<Song> getMP3FilesInfo(String folderPath) {
        List<Song> mp3Files = new ArrayList<>();
        File folder = new File(folderPath);

        // 检查文件夹是否存在且是一个目录
        if (folder.exists() && folder.isDirectory()) {
            // 获取文件夹中的所有MP3文件
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

            // 遍历所有MP3文件
            if (files != null) {
                for (File file : files) {
                    try {
                        // 读取MP3文件
                        MP3File mp3File = new MP3File(file);

                        // 获取MP3文件的ID3标签信息
                        String title = mp3File.getTag().getFirst(FieldKey.TITLE) != null ? mp3File.getTag().getFirst(FieldKey.TITLE) : "未知标题";
                        String artist = mp3File.getTag().getFirst(FieldKey.ARTIST) != null ? mp3File.getTag().getFirst(FieldKey.ARTIST) : "未知艺术家";
                        String album = mp3File.getTag().getFirst(FieldKey.ALBUM) != null ? mp3File.getTag().getFirst(FieldKey.ALBUM) : "未知专辑";
                        // 获取文件名
                        String fileName = file.getName();

                        Song song=new Song();
                        song.setName(title);
                        song.setPath(fileName);
                        song.setSingerName(artist);
                        song.setAlbumName(album);
                        // 保存文件信息到集合中
                        mp3Files.add(song);
                    } catch (Exception e) {
                        System.err.println("无法读取文件: " + file.getName());
                    }
                }
            }
        }
        return mp3Files;
    }
}



