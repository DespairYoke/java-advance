package com.zwd.mybatis.linkquery;

import com.zwd.mybatis.linkquery.domain.Course;
import com.zwd.mybatis.linkquery.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestUserMapper {


    @Autowired
    private UserMapper userMapper;

    @Test
    public void test() {

        Course course = userMapper.selectByusername("zzz");

        System.out.println(course);
    }
}
