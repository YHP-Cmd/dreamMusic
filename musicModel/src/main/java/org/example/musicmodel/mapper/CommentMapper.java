package org.example.musicmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.common.pojo.Comment;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Select("select c.comment_id,c.commenter_id,c.content,c.time,c.song_id,c.parent_id,u.nickname as commenterName " +
            "from comment c " +
            "left join user u on c.commenter_id=u.id " +
            "where song_id=#{songId}")
    List<Comment> selectBySongId(Integer songId);
    @Select("select c.comment_id,c.commenter_id,c.content,c.time,c.song_id,c.parent_id,u.nickname as commenterName " +
            "from comment c " +
            "left join user u on c.commenter_id=u.id " +
            "where parent_id=#{songId}")
    List<Comment> selectByParentId(Integer parentId);
}
