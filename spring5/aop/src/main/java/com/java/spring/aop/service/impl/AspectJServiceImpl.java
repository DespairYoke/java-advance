package com.java.spring.aop.service.impl;


import com.java.spring.aop.service.AspectJService;

/**
 * @author zwd
 * @date 2018/10/5 13:35
 * @Email stephen.zwd@gmail.com
 */
//实现类
public class AspectJServiceImpl implements AspectJService {

    @Override
    public void beforeAdvice() {
        System.out.println("测试前置通知，我是第一个Service。。。。。。");
    }

    /**
     * 测试后置通知
     */
    @Override
    public void afterAdvice() {
        System.out.println("测试AspectJ后置通知。。。。");
    }
}