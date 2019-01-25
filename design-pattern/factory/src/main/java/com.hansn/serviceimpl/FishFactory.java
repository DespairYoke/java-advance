package com.hansn.serviceimpl;

import com.hansn.service.CookFactory;
import com.hansn.service.Resaurant;

public class FishFactory extends CookFactory {
    public Resaurant createRestaurant() {
        return new Fish();
    }
}
