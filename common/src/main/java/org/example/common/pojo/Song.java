package org.example.common.pojo;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    private int songId;
    private String name;
    private String typeId;
    @TableField(exist = false)
    private String type;
    private String path;
    private String image;
    private String introduction;
    private int singerId;
    @TableField(exist = false)
    private String singerName;
    private int albumId;
    @TableField(exist = false)
    private String albumName;
    @TableField(exist = false)
    private int temp;
    @TableField(exist = false)
    private String statNum;

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", name='" + name + '\'' +
                ", typeId='" + typeId + '\'' +
                ", type='" + type + '\'' +
                ", path='" + path + '\'' +
                ", image='" + image + '\'' +
                ", introduction='" + introduction + '\'' +
                ", singerId=" + singerId +
                ", singerName='" + singerName + '\'' +
                ", albumId=" + albumId +
                ", albumName='" + albumName + '\'' +
                '}';
    }
}
