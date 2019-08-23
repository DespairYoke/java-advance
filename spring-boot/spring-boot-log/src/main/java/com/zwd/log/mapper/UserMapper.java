package com.zwd.log.mapper;

import com.zwd.log.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    User selectUser(Integer id);
}
