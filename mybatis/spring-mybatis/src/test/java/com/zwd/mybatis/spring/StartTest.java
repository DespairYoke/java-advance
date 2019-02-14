package com.zwd.mybatis.spring;

import com.zwd.mybatis.spring.pojo.User;
import com.zwd.mybatis.spring.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartTest {

    @Test
    public void test1() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        User user=sqlSession.selectOne("com.zwd.mybatis.spring.mapper.UserMapper.selectUserById",1);


        System.out.println(user);
    }

    @Test
    public void test2() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");


        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        User user = userMapper.selectUserById(1);

        System.out.println(user);
    }

}

