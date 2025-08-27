package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.SongType;
import org.example.musicmodel.mapper.SongTypeMapper;
import org.example.musicmodel.server.SongTypeService;
import org.springframework.stereotype.Service;

@Service
public class SongTypeServiceImpl extends ServiceImpl<SongTypeMapper, SongType> implements SongTypeService {
}
