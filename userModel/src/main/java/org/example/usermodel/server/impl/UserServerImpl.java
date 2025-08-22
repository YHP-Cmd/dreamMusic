package org.example.usermodel.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.User;
import org.example.usermodel.mapper.UserMapper;
import org.example.usermodel.server.UserServer;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServerImpl extends ServiceImpl<UserMapper,User> implements UserServer  {
    @Override
    public User login(String username, String password) {
        // 对输入的密码进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        // 使用条件查询，限制1条，内部实现不影响控制器的简洁性
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username).eq("password", encryptedPassword).last("limit 1");
        return this.getOne(queryWrapper);
    }
    @Override
    public boolean register(User user) {
        // 对密码进行MD5加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encryptedPassword);
        // 保存用户信息
        return this.save(user);
    }

    @Override
    public boolean isUsernameExists(String username) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        return this.count(qw) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("email", email);
        return this.count(qw) > 0;
    }

}
