package com.zwd.mybatis.transaction;

import com.zwd.mybatis.transaction.pojo.User;
import com.zwd.mybatis.transaction.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartTest {


    @Test
    public void test1() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        User user = new User();
        user.setId(1);
        user.setName("zwd");
        sqlSession.insert("com.zwd.mybatis.transaction.mapper.UserMapper.insertUser",user);

        User user1 = sqlSession.selectOne("com.zwd.mybatis.transaction.mapper.UserMapper.selectUserById", 1);
        System.out.println(user1);

    }

    @Test
    public void test2() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setId(1);
        user.setName("zwd");
        userMapper.insertUser(user);
        User user1 = userMapper.selectUserById(1);
        System.out.println(user1);
    }

    @Test
    public void test3() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        User user = new User();
        user.setId(1);
        user.setName("zwd");
        sqlSession.insert("com.zwd.mybatis.transaction.mapper.UserMapper.insertUser",user);
        sqlSession.close();

        SqlSession sqlSession1 = (SqlSession) ctx.getBean("sqlSession");
        User user1 = sqlSession1.selectOne("com.zwd.mybatis.transaction.mapper.UserMapper.selectUserById", 1);

        System.out.println(user1);
    }



}

