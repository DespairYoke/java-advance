package com.zwd.mybatis.analysis;

import com.zwd.mybatis.analysis.pojo.User;
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
        User user = new User();
        try {
            user.setName("abc");
            sqlSession.insert("com.zwd.mybatis.analysis.mapper.UserMapper.insertUser",user);
        }finally {
            sqlSession.commit();
            sqlSession.close();
        }
        System.out.println(user);

    }


}

