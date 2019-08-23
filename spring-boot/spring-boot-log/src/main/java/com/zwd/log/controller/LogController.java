package com.zwd.log.controller;

import com.zwd.log.entity.User;
import com.zwd.log.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping(value = "/")
    public String select(Integer id) {
        User user = userMapper.selectUser(id);

        System.out.println(user.getName());
        return user.getName();
    }
}
