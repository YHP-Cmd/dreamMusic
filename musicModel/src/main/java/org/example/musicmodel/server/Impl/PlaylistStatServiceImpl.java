package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.PlaylistStat;
import org.example.musicmodel.mapper.PlaylistStatMapper;
import org.example.musicmodel.server.PlaylistStatService;
import org.springframework.stereotype.Service;

@Service
public class PlaylistStatServiceImpl extends ServiceImpl<PlaylistStatMapper,PlaylistStat> implements PlaylistStatService {

}
