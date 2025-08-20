package org.example.common.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    private int albumId;
    private String albumName;
    private int singerId;
}
