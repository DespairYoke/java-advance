package com.example.zwd.eurekaclient.contoller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zwd
 * @date 2018/10/2 15:15
 * @Email stephen.zwd@gmail.com
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String index(String name) {
        return "hello "+ name+", fegin is success";
    }
}
