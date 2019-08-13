package com.zwd.boot.configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfigration {

    @Bean
    String name(){
        return "我是name";
    }
}
