package com.example.zwd.hystrix.controller;

import com.example.zwd.hystrix.service.HelloRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwd
 * @date 2018/10/2 15:18
 * @Email stephen.zwd@gmail.com
 */
@RestController
public class ConsumerController {
    @Autowired
    HelloRemote helloRemote;
    @RequestMapping("/hello")
    public String index(String name) {
        System.out.println(name);
        return helloRemote.hello(name);
    }
}
