package com.hansn;

import com.hansn.service.Resaurant;
import com.hansn.serviceimpl.Fish;
import com.hansn.serviceimpl.Meet;

public class factory {
    public static void main(String[] args) {
        System.out.println("aaaaaaa");
        Resaurant resaurant = new Meet();
        resaurant.cook();
        Resaurant resaurant1 = new Fish();
        resaurant1.cook();
        System.out.println("-------------");

        //简单工厂模式
        Resaurant resaurant2 = Wait.getMean(Wait.MEAN_DUCK);
        resaurant2.cook();
    }
}
