package org.example.musicmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Song;

@Mapper
public interface MusicMapper extends BaseMapper<Song> {
}
