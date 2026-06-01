package com.heima.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.heima.ai.mapper")
@SpringBootApplication
public class HeimaAi2Application {

    public static void main(String[] args) {
        SpringApplication.run(HeimaAi2Application.class, args);
    }

}
