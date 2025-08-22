package org.example.temp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Song;

import java.util.List;

@Mapper
public interface MusicMapper extends BaseMapper<Song> {
    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "JOIN singer si ON s.singer_id = si.singer_id " +
            "JOIN song_type t ON s.type_id = t.type_id " +
            "JOIN album a ON s.album_id = a.album_id " +
            "where s.singer_id = #{singerId}")
    List<Song> getBySingerId(Integer singerId);
    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "JOIN singer si ON s.singer_id = si.singer_id " +
            "JOIN song_type t ON s.type_id = t.type_id " +
            "JOIN album a ON s.album_id = a.album_id ")
    List<Song> getAll();
}
