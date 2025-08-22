package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.Listen;
import org.example.musicmodel.mapper.ListenMapper;
import org.example.musicmodel.server.ListenService;
import org.springframework.stereotype.Service;

@Service
public class ListenServiceImpl extends ServiceImpl<ListenMapper,Listen> implements ListenService {
}
