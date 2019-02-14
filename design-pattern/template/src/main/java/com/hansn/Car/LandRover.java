package com.hansn.Car;

import com.hansn.entity.CarTemplate;

public class LandRover extends CarTemplate{

    public LandRover(String name) {
        super(name);
    }

    @Override
    protected void buildWheel() {
        System.out.println(name + "越野车轮");

    }

    @Override
    protected void buildEngine() {
        System.out.println(name + "柴油发动机");
    }

    @Override
    protected void buildCarbody() {
        System.out.println(name + "SUV越野车型");
    }

    @Override
    protected void buildCarlight() {
        System.out.println(name + "普通车灯");
    }
}
