package com.zwd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-03-21
 **/
@Controller
public class HelloController {

    @GetMapping(value = "")
    public String index() {

        return "hello";
    }
}
