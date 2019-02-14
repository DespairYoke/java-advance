package com.hansn;

import com.hansn.Car.Audi;
import com.hansn.Car.LandRover;
import com.hansn.entity.CarTemplate;

public class TemplateTest {
    public static void main(String[] args) {
        CarTemplate car1 = new LandRover("路虎");
        CarTemplate car2 = new Audi("奥迪");
        car1.buildCar();
        car2.buildCar();
    }
}
