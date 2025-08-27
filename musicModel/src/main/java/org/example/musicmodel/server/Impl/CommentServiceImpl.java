package org.example.musicmodel.server.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.pojo.Comment;
import org.example.musicmodel.mapper.CommentMapper;
import org.example.musicmodel.server.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Override
    public List<Comment> selectBySongId(Integer SongId) {
        return commentMapper.selectBySongId(SongId);
    }
    @Override
    public List<Comment> selectByParentId(Integer parentId) {
        return commentMapper.selectByParentId(parentId);
    }
}
