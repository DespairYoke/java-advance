package com.zwd.mybatis.generator;

import com.zwd.mybatis.generator.domain.User;
import com.zwd.mybatis.generator.domain.UserExample;
import com.zwd.mybatis.generator.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Application {

    @Autowired
    private UserMapper userMapper;
    public static void main(String[] args) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        List<User> users = userMapper.selectByExample(userExample);

    }
}
