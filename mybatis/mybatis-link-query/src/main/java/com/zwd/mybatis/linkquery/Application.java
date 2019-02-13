package com.zwd.mybatis.linkquery;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan(value = {"com.zwd.mybatis.linkquery.mapper"})
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class);

    }
}
