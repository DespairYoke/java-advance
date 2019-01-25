package com.hansn;

import com.hansn.service.Resaurant;
import com.hansn.serviceimpl.DuckFactory;
import com.hansn.serviceimpl.FishFactory;

public class Cook {
    //工厂方法模式
    public static void main(String[] args) {
        Resaurant duck = new DuckFactory().createRestaurant();
        duck.cook();
        Resaurant fish = new FishFactory().createRestaurant();
        fish.cook();
    }
}
