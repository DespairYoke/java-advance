package com.zwd.aspectj.controller;

import org.aspectj.lang.annotation.Around;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping(value = "/")
    @Around(value = "excudeController()")
    public void index() {
        System.out.println("切面测试");
    }
}
