package org.example.musicmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.common.pojo.Playlist;

@Mapper
public interface PlaylistMapper extends BaseMapper<Playlist> {
}
