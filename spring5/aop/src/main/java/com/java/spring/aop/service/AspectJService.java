package com.java.spring.aop.service;

/**
 * @author zwd
 * @date 2018/10/5 13:34
 * @Email stephen.zwd@gmail.com
 */
public interface AspectJService {


    /**
     * 测试前置通知
     */
    void beforeAdvice();

    /**
     * 测试后置通知
     */
    void afterAdvice();
}
