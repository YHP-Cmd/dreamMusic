package org.example.singermodel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.common.pojo.Singer;
import org.example.singermodel.mapper.SingerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/getById")
    public Singer getById(@RequestParam Integer id){
        return singerMapper.getById(id);
    }

    @GetMapping("/test")
    public void test(){
        List<Singer> singers = singerMapper.selectList(null);
        for (Singer singer : singers) {
            if(singer.getImage()==null|| singer.getImage().isEmpty()){
                singer.setImage(singer.getName()+".jpg");
                QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("singer_id",singer.getSingerId());
                singerMapper.update(singer,queryWrapper);
            }
        }
    }

}
