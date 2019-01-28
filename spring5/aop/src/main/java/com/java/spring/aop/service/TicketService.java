package com.java.spring.aop.service;

/**
 * @author zwd
 * @date 2018/10/5 13:34
 * @Email stephen.zwd@gmail.com
 */
public interface TicketService {


    //售票
    public void sellTicket();

    //问询
    public void inquire();

    //退票
    public void withdraw();
}
