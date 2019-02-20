package com.example.zwd.springcloudfeign.controller;

import com.example.zwd.springcloudfeign.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    @GetMapping(value = "hello/{name}")
    public String hello(@PathVariable String name) {
        return consumerService.hello(name);
    }
}
