package com.hansn.serviceimpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicProxyHandler implements InvocationHandler {
    private Object object;

    public DynamicProxyHandler(final Object object){
        this.object=object;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("买房前的准备");
        Object result = method.invoke(object,args);
        System.out.println("买房后装修");
        return result;
    }
}
