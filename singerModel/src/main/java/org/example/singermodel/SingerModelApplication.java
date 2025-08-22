package org.example.singermodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@MapperScan("org.example.singermodel.mapper")
@SpringBootApplication
@ServletComponentScan("org.example.usermodel.util")
public class SingerModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SingerModelApplication.class, args);
    }

}
