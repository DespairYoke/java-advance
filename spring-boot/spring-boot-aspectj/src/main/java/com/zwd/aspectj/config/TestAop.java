package com.zwd.aspectj.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


//描述切面类
@Aspect
@Component
public class TestAop {

    @PostConstruct
    public void init() {
        System.out.println("-----init-----");
    }

    // 用@Pointcut来注解一个切入方法
    @Pointcut("execution(* com.zwd.aspectj.controller..*.*(..))")
    public void excudeController() {
    }


    @Around(value = "excudeController()")
    public Object twiceAsOld(ProceedingJoinPoint thisJoinPoint) throws Throwable {

        System.out.println("----before------");

        thisJoinPoint.proceed();

        System.out.println("------after-----");
        return null;
    }
}