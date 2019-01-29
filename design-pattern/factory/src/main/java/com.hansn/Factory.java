package com.hansn;

import com.hansn.service.Resaurant;
import com.hansn.serviceimpl.Fish;
import com.hansn.serviceimpl.Meet;

public class Factory {
    public static void main(String[] args) {
        //简单工厂模式
        Resaurant resaurant = Wait.getMean(Wait.MEAN_DUCK);
        resaurant.cook();
    }
}
