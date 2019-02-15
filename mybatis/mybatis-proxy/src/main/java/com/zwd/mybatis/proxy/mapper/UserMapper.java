package com.zwd.mybatis.proxy.mapper;

import com.zwd.mybatis.proxy.pojo.User;

public interface UserMapper {

    User selectUserById(int id);

    void insertUser(User user);
}
