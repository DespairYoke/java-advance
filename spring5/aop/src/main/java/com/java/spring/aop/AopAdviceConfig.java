package com.java.spring.aop;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * @author zwd
 * @date 2018/10/5 13:27
 * @Email stephen.zwd@gmail.com
 */
@Aspect
public class AopAdviceConfig {

    @Before("execution(* com.zwd.example.aspectJproxyfactory.demo.service..*.*(..))")
    public void beforeAdvice(JoinPoint joinPoint) {
        System.out.println(joinPoint.getThis());
        System.out.println("我是前置通知....");
    }}
