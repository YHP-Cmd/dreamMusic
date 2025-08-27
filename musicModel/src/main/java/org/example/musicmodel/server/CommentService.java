package org.example.musicmodel.server;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.pojo.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    List<Comment> selectBySongId(Integer SongId);
    List<Comment> selectByParentId(Integer ParentId);
}
