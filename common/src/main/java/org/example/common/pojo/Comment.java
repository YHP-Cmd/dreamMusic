package org.example.common.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int commentId;
    private String content;
    private Date time;
    private int commenterId;
    private int songId;
    private int parentId;
    @TableField(exist = false)
    private String commenterName;
    @TableField(exist = false)
    private List<Comment> childComment;
}
