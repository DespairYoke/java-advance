package com.zwd.mybatis.cache;

import com.zwd.mybatis.cache.domain.User;
import com.zwd.mybatis.cache.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestUserMapper {


    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {

        long start = System.currentTimeMillis();

        List<User> user = userMapper.selectByusername("张三");

        long end = System.currentTimeMillis();

        long count =end-start;

        System.out.println(count);



    }
}
