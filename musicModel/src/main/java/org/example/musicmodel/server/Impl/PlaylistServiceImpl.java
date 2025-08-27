package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.Playlist;
import org.example.musicmodel.mapper.PlaylistMapper;
import org.example.musicmodel.server.PlatlistService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements PlatlistService {
}
