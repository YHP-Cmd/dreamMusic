package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.Myfaovrites;
import org.example.musicmodel.mapper.MyfaovritesMapper;
import org.example.musicmodel.server.MyfaovritesService;
import org.springframework.stereotype.Service;

@Service
public class MyfaovritesServiceImpl extends ServiceImpl<MyfaovritesMapper, Myfaovrites> implements MyfaovritesService {
}
