package com.zwd.mybatis.spring;

import com.zwd.mybatis.spring.pojo.User;
import com.zwd.mybatis.spring.mapper.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartTest {

    ClassPathXmlApplicationContext ctx = null;


    @Before
    public void initContext() {

        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
    @Test
    public void testSelectUser() {

        UserMapper userMapper = (UserMapper) ctx.getBean("userMapper");

        User user = userMapper.selectUserById(1);

        System.out.println(user);
    }
}

