package com.zwd.caffine.service;

import com.zwd.caffine.entity.User;
import com.zwd.caffine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "user",key = "#id")
    public User getUser(Long id) {
        return userRepository.getOne(id);
    }

    @CachePut(value = "user",key = "#user.id")
    public User update(User user) {
        user = userRepository.save(user);
        return user;
    }
    @CacheEvict(value = "user",key = "#id")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    @PostConstruct
    public void init() {
        System.out.println("zzzz");
    }



}
