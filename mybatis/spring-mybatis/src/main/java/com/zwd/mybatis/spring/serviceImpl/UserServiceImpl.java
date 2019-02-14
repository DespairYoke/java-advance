//package com.zwd.mybatis.spring.serviceImpl;
//
//import com.zwd.mybatis.spring.pojo.User;
//import com.zwd.mybatis.spring.mapper.UserMapper;
//import com.zwd.mybatis.spring.service.UserService;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class UserServiceImpl implements UserService {
//
//    private SqlSessionTemplate sqlSessionTemplate;
//
//    @Autowired
//    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
//        this.sqlSessionTemplate = sqlSessionTemplate;
//    }
//
//    @Override
//    public User selctUserById(int id) {
//        UserMapper userMapper = sqlSessionTemplate.getMapper(UserMapper.class);
//
//        return userMapper.selectUserById(id);
//    }
//}
