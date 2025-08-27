package org.example.singermodel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.common.pojo.*;
import org.example.singermodel.mapper.SingerMapper;
import org.example.singermodel.service.impl.SingerTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SingerController {
    @Autowired
    private SingerTypeServiceImpl  singerTypeService;

    @Autowired
    SingerMapper singerMapper;

    @GetMapping("/page")
    public Page<Singer> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) Integer regionId,
            @RequestParam(required = false) Integer typeId
    ) {
        Page<Singer> page = new Page<>(pageNum, pageSize);
        List<Singer> records = singerMapper.selectSingerPage(page, name, gender, regionId, typeId);
        page.setRecords(records);
        return page;
    }
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
    @GetMapping("/count")
    public int count(){
        return singerMapper.selectCount(null);
    }
    @GetMapping("/typeCount")
    public List<TypeCountDTO> typeCount(){
        return singerMapper.getCountByType();
    }
    // 获取所有歌手类型（用于下拉选择）
    @GetMapping("/singerTypes")
    public List<SingerType> getSingerTypes() {
        return singerMapper.selectAllSingerTypes();
    }
    // 获取所有地区（用于下拉选择）
    @GetMapping("/regions")
    public List<Region> getRegions() {
        return singerMapper.selectAllRegions();
    }
    @PutMapping("/add")
    public int add(@RequestBody Singer singer){
        return singerMapper.insert(singer);
    }
    @PutMapping("/update")
    public int update(@RequestBody Singer singer){
        System.out.println("singer:"+singer);
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id",singer.getSingerId());
        return singerMapper.update(singer,queryWrapper);
    }
    @DeleteMapping("/delete")
    public int delete(@RequestParam Integer id){
        QueryWrapper<Singer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("singer_id",id);
        return singerMapper.delete(queryWrapper);
    }
    @GetMapping("/pageType")
    public IPage page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String name
    ) {
        Page<SingerType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SingerType> queryWrapper = new QueryWrapper<>();
        if (name != null) {
            queryWrapper.like("name",name);
        }
        return singerTypeService.page(page,queryWrapper);
    }

    @PutMapping("/addType")
    public boolean addType(@RequestBody SingerType singer){
        return singerTypeService.save(singer);
    }
    @PutMapping("/updateType")
    public boolean updateType(@RequestBody SingerType singer){
        QueryWrapper<SingerType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",singer.getTypeId());
        return singerTypeService.update(singer,queryWrapper);
    }
    @DeleteMapping("/deleteType")
    public boolean deleteType(@RequestParam Integer id){
        QueryWrapper<SingerType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id",id);
        return singerTypeService.remove(queryWrapper);
    }

}
