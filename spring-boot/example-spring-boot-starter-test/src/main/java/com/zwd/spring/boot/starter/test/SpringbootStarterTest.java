package com.zwd.spring.boot.starter.test;

import com.example.zwd.springbootstarter.config.ExampleAutoConfigure;
import com.example.zwd.springbootstarter.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import(ExampleAutoConfigure.class)
public class SpringbootStarterTest {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStarterTest.class, args);
    }

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/input")
    public String input(String word){
        return exampleService.wrap(word);
    }
}
