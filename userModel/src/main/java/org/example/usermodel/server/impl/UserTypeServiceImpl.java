package org.example.usermodel.server.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.UserType;
import org.example.usermodel.mapper.UserTypeMapper;
import org.example.usermodel.server.UserTypeService;
import org.springframework.stereotype.Service;

@Service
public class UserTypeServiceImpl extends ServiceImpl<UserTypeMapper, UserType> implements UserTypeService {
}
