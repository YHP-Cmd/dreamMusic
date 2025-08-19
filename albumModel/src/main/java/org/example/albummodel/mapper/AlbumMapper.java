package org.example.albummodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.Album;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
}
