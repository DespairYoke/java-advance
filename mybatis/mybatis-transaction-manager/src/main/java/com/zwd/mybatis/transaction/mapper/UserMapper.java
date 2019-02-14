package com.zwd.mybatis.transaction.mapper;

import com.zwd.mybatis.transaction.pojo.User;

public interface UserMapper {

    User selectUserById(int id);

    void insertUser(User user);
}
