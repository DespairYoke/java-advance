package com.example.zwd.springcloudhystrix.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;


    @GetMapping(value = "ribbon")
    @HystrixCommand(fallbackMethod = "addFallback")
    public String add() {

        return restTemplate.getForEntity("http://eureka-client-a/hello/zwd",String.class).getBody();
    }

    public String addFallback() {
        return "error";
    }
}
