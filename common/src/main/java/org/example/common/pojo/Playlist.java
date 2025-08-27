package org.example.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    public int id;
    public String name;
    public String introduction;
    private int createId;
    private String image;
}
