package org.example.temp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.pojo.Album;
import org.example.common.pojo.Singer;
import org.example.common.pojo.Song;
import org.example.temp.mapper.AlbumMapper;
import org.example.temp.mapper.MusicMapper;
import org.example.temp.mapper.SingerMapper;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class SingerController {

    @Autowired
    SingerMapper singerMapper;
    @Autowired
    AlbumMapper albumMapper;
    @Autowired
    MusicMapper musicMapper;
    public List<Song> getMP3FilesInfo(String folderPath) {
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


    @GetMapping("/getAll")
    public List<Singer> getAll(){
        return singerMapper.getAll();
    }
    @GetMapping("/test")
    public List<Song> test(){
        List<Song> songs=getMP3FilesInfo("D://下载/music");
        for (Song song : songs) {
            System.out.println(song);
            String name = song.getAlbumName();

            if (name!=null && !name.isEmpty()){
                Album album= albumMapper.selectOne(new QueryWrapper<Album>().eq("album_name",name));
                song.setAlbumId(album.getAlbumId());
                song.setSingerId(album.getSingerId());
            }else{
                Singer singer1=singerMapper.selectOne(new QueryWrapper<Singer>().eq("name",name));
                if(singer1!=null){
                    song.setSingerId(singer1.getSingerId());
                }else{
                    song.setSingerId(1);
                }

            }
        }
        for (Song song : songs) {
            musicMapper.insert(song);
        }


        return songs;
    }


}
