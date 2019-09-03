package com.java.spring.aop.service.impl;


import com.java.spring.aop.service.TicketService;

/**
 * @author zwd
 * @date 2018/10/5 13:35
 * @Email stephen.zwd@gmail.com
 */
//实现类
public class RailwayStation implements TicketService {


    public void sellTicket(){
        System.out.println("售票............");
    }

    public void inquire() {
        System.out.println("问询.............");
    }

    public void withdraw() {
        System.out.println("退票.............");
    }

}