package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.Song;
import org.example.musicmodel.mapper.MusicMapper;
import org.example.musicmodel.server.MusicServer;
import org.springframework.stereotype.Service;

@Service
public class MusicServerImpl extends ServiceImpl<MusicMapper,Song> implements MusicServer {

}
