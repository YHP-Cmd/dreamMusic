package org.example.musicmodel.mapper;

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
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "where s.singer_id = #{singerId}")
    List<Song> getBySingerId(Integer singerId);
    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "where s.song_id in (" +
            "select song_id from playlist_song where playlist_id=#{playlistId})")
            List<Song> getByPlaylistId(Integer playlistId);
    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id ")
    List<Song> getAll();
    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "where s.album_id = #{albumId}")
    List<Song> getByAlbumId(Integer albumId);
    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS type, s.path, s.image, s.singer_id, si.name AS singerName, " +
            "a.album_name AS albumName, " +
            "COUNT(l.id) AS temp " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN listen l ON s.song_id = l.song_id " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY temp DESC")
    List<Song> getByNum();
    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS type, s.path, s.image, s.singer_id, si.name AS singerName, " +
            "a.album_name AS albumName, " +
            "COUNT(l.id) AS temp " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN myfaovrites l ON s.song_id = l.song_id " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY temp DESC")
    List<Song> getByStat();

    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "where s.song_id = #{id}")
    Song getById(Integer id);

}
