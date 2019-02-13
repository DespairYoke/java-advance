package com.zwd.mybatis.cache;

import com.zwd.mybatis.cache.domain.User;
import com.zwd.mybatis.cache.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@SpringBootApplication
@MapperScan(value = {"com.zwd.mybatis.cache.mapper"})
public class Application {

    @Autowired
    private UserMapper userMapper;

    public static void main(String[] args) {

        SpringApplication.run(Application.class);

    }

    @GetMapping(value = "/")
    public Long selectUser(String username) {

        long start = System.currentTimeMillis();

        List<User> user = userMapper.selectByusername(username);

        long end = System.currentTimeMillis();

        long count =end-start;

        System.out.println(count);

        return count;
    }

    @GetMapping(value = "/insert")
    public void insertUser() {
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123456");
        userMapper.insertSelective(user);
        System.out.println("插入数据");
    }

    @GetMapping(value = "update")
    public void update() {

        User user = new User();
        user.setId(1);
        user.setUsername("李四");
        user.setPassword("123456");
        userMapper.updateByPrimaryKey(user);
        System.out.println("更新成功！");
    }
}
