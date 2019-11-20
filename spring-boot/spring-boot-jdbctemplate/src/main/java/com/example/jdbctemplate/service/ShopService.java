package com.example.jdbctemplate.service;

import com.example.jdbctemplate.config.MyLock;
import com.example.jdbctemplate.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @description:
 * @author: 青阳
 * @create: 2019-11-19 09:46
 */
@Service
public class ShopService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MyLock lock = new MyLock();

    private int stack;

    @GetMapping
    public void decrease(Integer id) throws InterruptedException {
        lock.lock();
        Shop query = jdbcTemplate.queryForObject("select * from shop where id = ?", new Object[]{id}, new BeanPropertyRowMapper<>(Shop.class));
        if (query == null) {
            System.out.println("没有查询到结果");
        } else {

            stack = query.getNumber();
            stack--;
            Thread.sleep(1000);
            jdbcTemplate.update("update shop set number = " + stack);
            System.out.println("余额" + stack + Thread.currentThread());
            lock.unlock();
        }
    }
}
