package com.zwd.log;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(value = "com.zwd.log.mapper")
public class Bootstrap {

    public static void main(String[] args) {

        SpringApplication.run(Bootstrap.class);
    }
}
