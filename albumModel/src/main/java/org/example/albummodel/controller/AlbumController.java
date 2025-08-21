package org.example.albummodel.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.albummodel.mapper.AlbumMapper;
import org.example.common.pojo.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class AlbumController {

    @Autowired
    private AlbumMapper albumMapper;
    @GetMapping("/getById")
    public List<Album> getAlbumById(@RequestParam("id") int id){
        QueryWrapper<Album> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id",id);
        return albumMapper.selectList(queryWrapper);
    }
}
