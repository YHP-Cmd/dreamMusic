package org.example.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Myfaovrites {
    private int id;
    private int userId;
    private String songId;
    private Date createTime;
}
