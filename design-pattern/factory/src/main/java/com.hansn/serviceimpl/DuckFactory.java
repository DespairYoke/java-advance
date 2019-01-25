package com.hansn.serviceimpl;

import com.hansn.service.CookFactory;
import com.hansn.service.Resaurant;

public class DuckFactory extends CookFactory {
    @Override
    public Resaurant createRestaurant() {
        return new Duck();
    }
}
