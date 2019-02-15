package com.zwd.mybatis.analysis.mapper;

import com.zwd.mybatis.analysis.pojo.User;

public interface UserMapper {

    User selectUserById(int id);

    void insertUser(User user);
}
