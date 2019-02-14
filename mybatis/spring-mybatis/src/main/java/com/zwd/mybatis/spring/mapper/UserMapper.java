package com.zwd.mybatis.spring.mapper;

import com.zwd.mybatis.spring.pojo.User;

public interface UserMapper {

    User selectUserById(int id);
}
