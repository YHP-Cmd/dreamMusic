package org.example.commentmodel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.common.pojo.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
