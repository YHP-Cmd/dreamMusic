package org.example.temp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Album;

import java.util.List;

@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
    @Select("select * from album where singer_id=#{id}")
    List<Album> getById(@Param("id") int id);
}
