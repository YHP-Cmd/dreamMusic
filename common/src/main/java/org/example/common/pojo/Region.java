package org.example.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("region")
public class Region {
    // 地区ID，主键
    @TableId(value = "region_id", type = IdType.AUTO)
    private Integer regionId;

    // 地区名称，如中国、美国、韩国等
    private String name;
}