package com.zwd.jdk;

import com.zwd.jdk.handler.MyInvocationHandler;
import com.zwd.jdk.service.UserService;
import com.zwd.jdk.service.impl.UserServiceImpl;

import java.lang.reflect.Proxy;

public class BootStrap {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        MyInvocationHandler myInvocationHandler = new MyInvocationHandler(userService);

        UserService proxyInstance = (UserService)myInvocationHandler.getProxy();

        proxyInstance.add();
    }

}
