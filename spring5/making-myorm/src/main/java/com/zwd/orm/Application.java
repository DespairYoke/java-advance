package com.zwd.orm;

import com.zwd.orm.configuration.MySqlsession;
import com.zwd.orm.domain.User;
import com.zwd.orm.mapper.UserMapper;

public class Application {

    public static void main(String[] args) {
        MySqlsession sqlsession=new MySqlsession();
        UserMapper mapper = sqlsession.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user);
    }
}
