package org.example.usermodel.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.pojo.User;
import org.example.usermodel.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/selectByPage")//分页查询，后续可添加条件
    public IPage selectByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gender", "男");
        System.out.println("Page Number: " + pageNum + ", Page Size: " + pageSize);
        return userMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
    }
    @GetMapping("/selectById")
    public User selectById(@RequestParam Integer id){
        return userMapper.selectById(id);
    }

    @PutMapping("/update")
    public int update(@RequestBody User user){
        return userMapper.updateById(user);
    }

    @PostMapping("/add")
    public int add(@RequestBody User user){
        return userMapper.insert(user);
    }

    @DeleteMapping("/delete")
    public int delete(@RequestParam Integer id){
        return userMapper.deleteById(id);
    }
}
