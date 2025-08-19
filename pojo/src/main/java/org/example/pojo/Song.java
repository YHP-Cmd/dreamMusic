package org.example.pojo;


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
    private String path;
    private String image;
    private String introduction;
    private int singerId;
    private int albumId;
}
