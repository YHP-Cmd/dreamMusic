package org.example.musicmodel.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.pojo.*;
import org.example.musicmodel.mapper.MusicMapper;
import org.example.musicmodel.server.Impl.CommentServiceImpl;
import org.example.musicmodel.server.Impl.ListenServiceImpl;
import org.example.musicmodel.server.Impl.MyfaovritesServiceImpl;
import org.example.musicmodel.server.Impl.PlaylistServiceImpl;
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
import java.util.*;

@RestController
@CrossOrigin

public class MusicController {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private MyfaovritesServiceImpl myfaovritesService;

    @Autowired
    private PlaylistServiceImpl playlistService;

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private ListenServiceImpl listenServiceImpl;

    private static final String AUDIO_DIR = "D:/下载/music";

    private static final String IMG_DIR = "D:/下载/image";

    @GetMapping("/getAll")
    public List<Song> getAll(){
        return musicMapper.selectList(null);
    }
    @GetMapping("/audio")
    public ResponseEntity<Resource> getAudio(@RequestParam String filename,
                                             @RequestParam int song_id,
                                             @RequestParam int user_id
    ) {
        // 安全检查：文件名不能包含非法字符
        if (filename.contains("..")) {
            return ResponseEntity.badRequest().body(null);
        }
        System.out.println(filename+"----"+song_id+"----"+user_id);
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

            Listen listen = new Listen();
            listen.setUserId(user_id);
            listen.setSongId(song_id);
            listen.setCreateTime(new Date());
            listenServiceImpl.save(listen);
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
        List<Song> songs = musicMapper.getAll();

        //随机打乱集合
        Collections.shuffle(songs, new Random());

        // 返回前10个不重复的歌曲
        return songs.subList(0, Math.min(10, songs.size()));
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
    @GetMapping("/selectAll")
    public List<Song> getAllSong() {
        return musicMapper.getAll();
    }
    @GetMapping("/selectBySingerId")
    public List<Song> selectBySingerId(@RequestParam Integer singerId){
        return musicMapper.getBySingerId(singerId);
    }
    @GetMapping("/selectByAlbumId")
    public List<Song> selectByAlbumId(@RequestParam Integer albumId){
        return musicMapper.getByAlbumId(albumId);
    }
    @GetMapping("/selectById")
    public Song selectById(@RequestParam Integer id){
        return musicMapper.getById(id);
    }
    @GetMapping("/selectByNum")
    public List<Song> selectByNum(){
        return musicMapper.getByNum();
    }
    @GetMapping("/selectByStat")
    public List<Song> selectByStat(){
        return musicMapper.getByStat();
    }
    @GetMapping("/test")
    public void test(){
        List<Song> songs = musicMapper.selectList(null);
        for (Song song : songs) {
            if(song.getImage()==null|| song.getImage().isEmpty()){
                song.setImage(song.getName()+".png");
                QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("song_id",song.getSongId());
                musicMapper.update(song,queryWrapper);
            }
        }
    }

    @GetMapping("/getAllPlaylist")
    public List<Playlist> getAllPlaylist(){
        return playlistService.list(null);
    }
    @GetMapping("/getPlaylistById")
    public Playlist getPlaylistById(@RequestParam Integer id){
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        return playlistService.getOne(queryWrapper);
    }
    @GetMapping("/getByPlaylistId")
    public List<Song> getByPlaylistId(@RequestParam Integer playlistId){
        return musicMapper.getByPlaylistId(playlistId);
    }
    @PutMapping("/addStat")
    public boolean addStat(@RequestBody Myfaovrites myfaovrites){
        return myfaovritesService.save(myfaovrites);
    }
    @PutMapping("/deleteStat")
    public boolean deleteStat(@RequestBody Myfaovrites myfaovrites){
        QueryWrapper<Myfaovrites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id",myfaovrites.getSongId());
        queryWrapper.eq("user_id",myfaovrites.getUserId());
        return myfaovritesService.remove(queryWrapper);
    }
    @GetMapping("/isStat")
    public boolean isStat(@RequestParam int songId,@RequestParam int userId){
        QueryWrapper<Myfaovrites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id",songId);
        queryWrapper.eq("user_id",userId);
        return myfaovritesService.count(queryWrapper) > 0;
    }
    @GetMapping("/selectCommentBySongId")
    public List<Comment> selectCommentBySongId(@RequestParam Integer songId) {
        return commentService.selectBySongId(songId);
    }
    @PutMapping("/addComment")
    public boolean addComment(@RequestBody Comment comment){
        System.out.println(comment);
        return commentService.save(comment);
    }

    @GetMapping("/selectByLike")
    public List<Song> selectByLike(@RequestParam String name){
        return musicMapper.getByLike(name);
    }

    @GetMapping("/selectByListen")
    public List<Song> selectByListen(@RequestParam int userId){
        return musicMapper.getByListen(userId);
    }



}

