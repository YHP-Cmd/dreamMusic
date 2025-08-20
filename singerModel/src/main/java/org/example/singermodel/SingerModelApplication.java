package org.example.singermodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.example.singermodel.mapper")
@SpringBootApplication
public class SingerModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingerModelApplication.class, args);
    }

}
