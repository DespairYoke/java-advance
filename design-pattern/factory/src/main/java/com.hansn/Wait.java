package com.hansn;

import com.hansn.service.Resaurant;
import com.hansn.serviceimpl.Duck;
import com.hansn.serviceimpl.Fish;
import com.hansn.serviceimpl.Meet;

public class Wait {
    public static final int MEAN_MEET = 1;
    public static final int MEAN_FISH = 2;
    public static final int MEAN_DUCK = 3;

    public static Resaurant getMean(int meantype){
        switch (meantype){
            case MEAN_MEET :
                return new Meet();
            case MEAN_FISH :
                return new Fish();
            case MEAN_DUCK :
                return new Duck();
            default:
                return null;
        }
    }
}
