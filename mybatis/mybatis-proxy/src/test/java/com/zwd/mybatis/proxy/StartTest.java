package com.zwd.mybatis.proxy;

import com.zwd.mybatis.proxy.mapper.UserMapper;
import com.zwd.mybatis.proxy.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class StartTest {

    @Test
    public void test1() throws IOException {

        String resource = "mybatis/mybatis-config.xml";

        InputStream inputStream = Resources.getResourceAsStream(resource);

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = null;
        try {
          user = userMapper.selectUserById(1);
        }finally {
            sqlSession.close();
        }
        System.out.println(user);

    }


}

