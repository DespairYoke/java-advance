package com.zwd.caffine;

import com.zwd.caffine.entity.User;
import com.zwd.caffine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 启动类
 * @author: zwd
 * @create: 2019-11-18 16:40
 */
@SpringBootApplication
@RestController
@EnableCaching
public class CaffineApplication {
    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(CaffineApplication.class, args);
    }

    @GetMapping(value = "/select")
    public User selectUser(Long id) {
        System.out.println("访问成功");
        User user = userService.getUser(id);
        return user;
    }

    @GetMapping(value = "/update")
    public User updateUser(String name) {
        User user = new User();
        user.setAge(11);
        user.setName(name);
        user.setId(1L);
        return userService.update(user);
    }

    @GetMapping(value = "/delete")
    public void deleteUser(Long id) {
        userService.deleteUser(id);
        System.out.println("删除成功");
    }

}
