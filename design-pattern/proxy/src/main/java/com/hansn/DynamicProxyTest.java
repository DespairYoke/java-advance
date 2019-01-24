package com.hansn;

import com.hansn.service.BuyHouse;
import com.hansn.serviceimpl.BuyHouseImpl;
import com.hansn.serviceimpl.DynamicProxyHandler;

import java.lang.reflect.Proxy;

public class DynamicProxyTest {
    //动态代理
    public static void main(String[] args) {
        BuyHouse buyHouse = new BuyHouseImpl();
        BuyHouse proxyBuyHouse = (BuyHouse) Proxy.newProxyInstance(BuyHouse.class.getClassLoader(), new
                Class[]{BuyHouse.class}, new DynamicProxyHandler(buyHouse));
        proxyBuyHouse.buyHouse();
    }
}
