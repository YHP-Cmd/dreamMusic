package org.example.musicmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Song;
import org.example.common.pojo.TypeCountDTO;

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
            "WHERE user_id = #{userId} " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY temp DESC")
    List<Song> getRankByNum(int userId);
    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS type, s.path, s.image, s.singer_id, si.name AS singerName, " +
            "a.album_name AS albumName, " +
            "COUNT(l.id) AS temp " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN listen l ON s.song_id = l.song_id " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY temp DESC " +
            "LIMIT 10")
    List<Song> getHot();
    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS type, s.path, s.image, s.singer_id, si.name AS singerName, " +
            "a.album_name AS albumName, " +
            "COUNT(l.id) AS temp " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN myfaovrites l ON s.song_id = l.song_id " +
            "WHERE user_id = #{userId} " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY temp DESC")
    List<Song> getRankByStat(int userId);

    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "where s.song_id = #{id}")
    Song getById(Integer id);

    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS type, s.path, s.image, s.singer_id, si.name AS singerName, " +
            "a.album_name AS albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "WHERE s.name LIKE CONCAT('%', #{name}, '%') OR si.name LIKE CONCAT('%', #{name}, '%')")
    List<Song> getByLike(String name);
    @Select("SELECT s.song_id, s.name, s.type_id, t.name AS `type`, s.path, s.image, s.singer_id, " +
            "si.name AS singerName, a.album_name AS albumName, MAX(l.create_time) AS create_time " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN listen l ON l.song_id = s.song_id " +
            "WHERE l.user_id = #{userId} " +
            "GROUP BY s.song_id, s.name, s.type_id, t.name, s.path, s.image, s.singer_id, si.name, a.album_name " +
            "ORDER BY  create_time DESC")
    List<Song> getByListen(int userId);

    @Select("SELECT s.song_id, s.name,s.type_id,t.name as type,s.path,s.image,s.singer_id, si.name as singerName , " +
            "a.album_name as albumName " +
            "FROM song s " +
            "LEFT JOIN singer si ON s.singer_id = si.singer_id " +
            "LEFT JOIN song_type t ON s.type_id = t.type_id " +
            "LEFT JOIN album a ON s.album_id = a.album_id " +
            "LEFT JOIN myfaovrites m ON m.song_id=s.song_id " +
            "where m.user_id = #{userId}")
    List<Song> getByStat(Integer userId);
    // 按歌曲类型统计数量
    @Select("select st.name as typeName, count(*) as count " +
            "from song s " +
            "left join song_type st on s.type_id = st.type_id " +
            "group by s.type_id, st.name")
    List<TypeCountDTO> getCountByType();
    List<Song> selectSongPage(
            Page<Song> page,
            @Param("name") String name,
            @Param("typeId") Integer typeId,
            @Param("singerId") Integer singerId,
            @Param("status") String status
    );

}
