package org.example.musicmodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.musicmodel.mapper")
public class MusicModelApplication {
    public static void main(String[] args) {
        SpringApplication.run(MusicModelApplication.class, args);
    }

}
