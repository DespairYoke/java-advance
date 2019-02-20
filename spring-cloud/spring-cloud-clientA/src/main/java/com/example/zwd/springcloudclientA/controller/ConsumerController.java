package com.example.zwd.springcloudclientA.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {

    @GetMapping(value = "hello/{name}")
    public String hello(@PathVariable String name) {

        System.out.println( "hello "+name+"!");

        return "success";
    }
}
