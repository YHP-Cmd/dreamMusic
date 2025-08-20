package org.example.singermodel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.albummodel.mapper.AlbumMapper;
import org.example.common.pojo.Album;
import org.example.common.pojo.Singer;
import org.example.common.pojo.Song;
import org.example.singermodel.mapper.SingerMapper;
import org.example.temp.MP3FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SingerController {

    @Autowired
    SingerMapper singerMapper;


    @GetMapping("/getAll")
    public List<Singer> getAll(){
        return singerMapper.getAll();
    }


}
