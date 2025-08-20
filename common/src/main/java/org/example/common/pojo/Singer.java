package org.example.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Singer {
    private int singerId;
    private String name;
    private int regionId;
    @TableField(exist = false)
    private  String type;
    private  String type_id;
    @TableField(exist = false)
    private String region;
    private String gender;
    private String image;
}
