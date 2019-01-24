package com.hansn.proxy;

import com.hansn.service.BuyHouse;

public class BuyHouseProxy implements BuyHouse {
    private BuyHouse buyHouse;

    public BuyHouseProxy(final BuyHouse buyHouse){
        this.buyHouse = buyHouse;
    }
    public void buyHouse() {
        System.out.println("买房前准备");
        buyHouse.buyHouse();
        System.out.println("买房后装修");
    }
}
