package org.example.usermodel.server;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.pojo.User;

public interface UserServer extends IService<User> {
    User login(String username, String password);

    // 用户注册
    boolean register(User user);

    // 检查用户名是否已存在
    boolean isUsernameExists(String username);

    // 检查邮箱是否已存在
    boolean isEmailExists(String email);
}
