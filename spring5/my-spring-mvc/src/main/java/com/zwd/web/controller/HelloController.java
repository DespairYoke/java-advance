package com.zwd.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.MyModelAndView;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
@Controller
public class HelloController {

    @GetMapping(value = "hello")
    public MyModelAndView index(String name, MyModelAndView modelAndView) {

        modelAndView.addObject("message",name);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @RequestMapping(value = "getname")
    public String getName() {

        return "index";
    }
}
