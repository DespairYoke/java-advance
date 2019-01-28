package com.hansn;

import com.hansn.proxy.BuyHouseProxy;
import com.hansn.service.BuyHouse;
import com.hansn.serviceimpl.BuyHouseImpl;

public class Hansn {
    public static void main(String[] args) {
        BuyHouse buyHouse = new BuyHouseImpl();
        buyHouse.buyHouse();
        System.out.println("-----------");
        BuyHouseProxy buyHouseProxy = new BuyHouseProxy(buyHouse);
        buyHouseProxy.buyHouse();
    }
}
