package org.example.musicmodel.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.pojo.*;
import org.example.musicmodel.mapper.MusicMapper;
import org.example.musicmodel.mapper.SongTypeMapper;
import org.example.musicmodel.server.Impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@CrossOrigin

public class MusicController {
    @Autowired
    SongTypeServiceImpl  songTypeService;

    @Autowired
    private MusicServerImpl musicServer;
    @Autowired
    private PlaylistStatServiceImpl playlistStatService;
    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private MyfaovritesServiceImpl myfaovritesService;

    @Autowired
    private PlaylistServiceImpl playlistService;

    @Autowired
    private ListenServiceImpl listenServiceImpl;

    private static final String AUDIO_DIR = "D:/下载/music";

    private static final String IMG_DIR = "D:/下载/image";
    @Autowired
    private SongTypeMapper songTypeMapper;

    @GetMapping("/page")
    public Page<Song> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) Integer singerId,
            @RequestParam(required = false) String status
    ) {
        Page<Song> page = new Page<>(pageNum, pageSize);
        List<Song> records = musicMapper.selectSongPage(page, name, typeId, singerId, status);
        page.setRecords(records);
        return page;
    }

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
    @GetMapping("/image/{filename}")
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
    public List<Song> selectByNum(@RequestParam int userId){
        return musicMapper.getRankByNum(userId);
    }
    @GetMapping("/selectByStat")
    public List<Song> selectByStat(@RequestParam int userId){
        return musicMapper.getRankByStat(userId);
    }
    @GetMapping("/getMyStat")
    public List<Song> getMyStat(@RequestParam Integer userId){
        return musicMapper.getByStat(userId);
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
    public boolean songIsPlaylist(int songId,int playlistId){
        return musicMapper.songIsPlaylist(playlistId, songId) != null;
    }
    @GetMapping("/getPlaylistByUserId")
    public List<Playlist> getPlaylistByUserId(@RequestParam Integer userId){
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_id",userId);
        return playlistService.list(queryWrapper);
    }
    @GetMapping("/getPlaylistByStat")
    public List<Playlist> getPlaylistByStat(@RequestParam Integer userId){
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        List<Integer> list=musicMapper.getAllStatList(userId);
        queryWrapper.in("id",list);
        return playlistService.list(queryWrapper);
    }
    @GetMapping("/getByPlaylistId")
    public List<Song> getByPlaylistId(@RequestParam Integer playlistId){
        return musicMapper.getByPlaylistId(playlistId);
    }
    @PutMapping("/addToPlaylist")
    public int addToPlaylist(@RequestParam Integer playlistId,@RequestParam Integer songId){
        if (songIsPlaylist(songId,playlistId)) {
            return 0;
        }
        return musicMapper.addToPlaylist(playlistId,songId);
    }
    @PutMapping("/addStat")
    public boolean addStat(@RequestBody Myfaovrites myfaovrites){
        return myfaovritesService.save(myfaovrites);
    }
    @PutMapping("/addListStat")
    public boolean addListStat(@RequestBody PlaylistStat myfaovrites){
        return playlistStatService.save(myfaovrites);
    }
    @PutMapping("/deleteStat")
    public boolean deleteStat(@RequestBody Myfaovrites myfaovrites){
        QueryWrapper<Myfaovrites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id",myfaovrites.getSongId());
        queryWrapper.eq("user_id",myfaovrites.getUserId());
        return myfaovritesService.remove(queryWrapper);
    }
    @PutMapping("/deleteListStat")
    public boolean deleteListStat(@RequestBody PlaylistStat myfaovrites){
        QueryWrapper<PlaylistStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id",myfaovrites.getPlaylistId());
        queryWrapper.eq("user_id",myfaovrites.getUserId());
        return playlistStatService.remove(queryWrapper);
    }
    @GetMapping("/isStat")
    public boolean isStat(@RequestParam int songId,@RequestParam int userId){
        QueryWrapper<Myfaovrites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song_id",songId);
        queryWrapper.eq("user_id",userId);
        return myfaovritesService.count(queryWrapper) > 0;
    }
    @GetMapping("/listIsStat")
    public boolean listIsStat(@RequestParam int listId,@RequestParam int userId){
        QueryWrapper<PlaylistStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("playlist_id",listId);
        queryWrapper.eq("user_id",userId);
        return playlistStatService.count(queryWrapper) > 0;
    }
    @GetMapping("/selectCommentBySongId")
    public List<Comment> selectCommentBySongId(@RequestParam Integer songId) {
        List<Comment> comments = commentService.selectBySongId(songId);
        // 遍历每个评论，设置子评论
        for (Comment comment : comments) {
            setComment(comment);
        }
        // 返回处理过的评论列表
        return comments;
    }
    public void setComment(Comment comment){
        // 根据评论的 songId 获取子评论
        List<Comment> comments = commentService.selectByParentId(comment.getCommentId()); // 这里改为根据 commentId 查找子评论
        if (!comments.isEmpty()) {
            comment.setChildComment(comments);
        }
    }
    @GetMapping("/selectCommentByParentId")
    public List<Comment> selectCommentByParentId(@RequestParam Integer parentId) {
        return commentService.selectByParentId(parentId);
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

    @GetMapping("/songCount")
    public int songCount(){
        return musicServer.count(null);
    }
    @GetMapping("/listenCount")
    public int listenCount(){
        return listenServiceImpl.count(null);
    }
    @GetMapping("/songTypeCount")
    public int songTypeCount(){
        return songTypeService.count(null);
    }
    @GetMapping("/getHot")
    public List<Song> getHot(){
        return musicMapper.getHot();
    }
    @GetMapping("/countByType")
    public List<TypeCountDTO> getCountByType(){
        return musicMapper.getCountByType();
    }
    @GetMapping("/getAllType")
    public List<SongType> getAllType(){
        return songTypeService.list(null);
    }
    // 分页查询歌曲类型
    @GetMapping("/pageType")
    public IPage page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name
    ) {
        Page<SongType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SongType> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name",name);
        }
        return songTypeService.page(page,queryWrapper);
    }
    @PutMapping("/addType")
    public boolean addType(@RequestBody SongType songType){
        return songTypeService.save(songType);
    }
    @PutMapping("/updateType")
    public boolean updateType(@RequestBody SongType songType){
        QueryWrapper<SongType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",songType.getTypeId());
        return songTypeService.update(songType,queryWrapper);
    }
    @DeleteMapping("/deleteType")
    public boolean deleteType(@RequestParam Integer id){
        QueryWrapper<SongType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",id);
        return songTypeService.remove(queryWrapper);
    }
    
    @PostMapping("/createPlaylist")
    public boolean createPlaylist(@RequestBody Playlist playlist){
        return playlistService.save(playlist);
    }
    
    @PostMapping(value = "/upload/audio", consumes = "multipart/form-data")
    public Map<String, Object> uploadAudio(@RequestParam("file") MultipartFile file){
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "文件为空");
            return response;
        }
        
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            response.put("code", 400);
            response.put("message", "文件名不能为空");
            return response;
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.equals(".mp3") && !extension.equals(".wav") && !extension.equals(".flac") && !extension.equals(".m4a")) {
            response.put("code", 400);
            response.put("message", "只支持 MP3、WAV、FLAC、M4A 格式的音频文件");
            return response;
        }
        
        // 检查文件大小 (50MB)
        if (file.getSize() > 100 * 1024 * 1024) {
            response.put("code", 400);
            response.put("message", "文件大小不能超过 100MB");
            return response;
        }
        
        try {
            // 生成UUID作为文件名
            String newFilename = UUID.randomUUID() + extension;
            // 确保目录存在
            File uploadDir = new File(AUDIO_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 保存文件
            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(destFile);
            // 返回成功响应
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", newFilename);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "文件上传失败：" + e.getMessage());
        }
        return response;
    }
    
    @PostMapping(value = "/upload/image", consumes = "multipart/form-data")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file){
        Map<String, Object> response = new HashMap<>();
        if (file.isEmpty()) {
            response.put("code", 400);
            response.put("message", "文件为空");
            return response;
        }
        
        // 检查文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            response.put("code", 400);
            response.put("message", "文件名不能为空");
            return response;
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png")) {
            response.put("code", 400);
            response.put("message", "只支持 JPG 和 PNG 格式的图片");
            return response;
        }
        
        // 检查文件大小 (2MB)
        if (file.getSize() > 2 * 1024 * 1024) {
            response.put("code", 400);
            response.put("message", "文件大小不能超过 2MB");
            return response;
        }
        
        try {
            // 生成UUID作为文件名
            String newFilename = UUID.randomUUID() + extension;
            // 确保目录存在
            File uploadDir = new File(IMG_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 保存文件
            File destFile = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(destFile);
            // 返回成功响应
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", newFilename);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "文件上传失败：" + e.getMessage());
        }
        return response;
    }
    
    @PostMapping("/addSong")
    public boolean addSong(@RequestBody Song song){
        return musicServer.save(song);
    }
    
    @PutMapping("/updateSong")
    public boolean updateSong(@RequestBody Song song){
        return musicServer.updateById(song);
    }
    
    @PutMapping("/freezeSong")
    public boolean freezeSong(@RequestParam Integer songId){
        Song song = musicServer.getById(songId);
        if (song != null) {
            song.setStatus("已冻结");
            return musicServer.updateById(song);
        }
        return false;
    }
    
    @PutMapping("/unfreezeSong")
    public boolean unfreezeSong(@RequestParam Integer songId){
        Song song = musicServer.getById(songId);
        if (song != null) {
            song.setStatus("正常");
            return musicServer.updateById(song);
        }
        return false;
    }
}

