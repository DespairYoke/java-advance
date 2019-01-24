package com.hansn;

import com.hansn.proxy.BuyHouseProxy;
import com.hansn.service.BuyHouse;
import com.hansn.serviceimpl.BuyHouseImpl;

public class Hansn {
    public static void main(String[] args) {
        System.out.println("aaaa");
        BuyHouse buyHouse = new BuyHouseImpl();
        buyHouse.buyHouse();
        BuyHouseProxy buyHouseProxy = new BuyHouseProxy(buyHouse);
        buyHouseProxy.buyHouse();
    }
}
