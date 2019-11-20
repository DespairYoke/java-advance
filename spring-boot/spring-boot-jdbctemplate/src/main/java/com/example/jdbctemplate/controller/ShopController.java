package com.example.jdbctemplate.controller;

import com.example.jdbctemplate.entity.Shop;
import com.example.jdbctemplate.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: 青阳
 * @create: 2019-11-19 09:45
 */
@RestController
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping(value = "decrease")
    public void decrease(Integer id) throws InterruptedException {
        shopService.decrease(id);
    }
}
