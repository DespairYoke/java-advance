package com.example.zwd.springcloudribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;


    @GetMapping(value = "ribbon")
    public String add() {

        return restTemplate.getForEntity("http://eureka-client-a/hello/zwd",String.class).getBody();
    }
}
