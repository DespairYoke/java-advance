package com.hansn.Car;

import com.hansn.entity.CarTemplate;

public class Audi extends CarTemplate {
    public Audi(String name) {
        super(name);
    }

    @Override
    protected void buildWheel() {
        System.out.println(name + "的普通轿车车轮");
    }

    @Override
    protected void buildEngine() {
        System.out.println(name + "的汽油发动机");
    }

    @Override
    protected void buildCarbody() {
        System.out.println(name + "的豪华舒适性车身");
    }

    @Override
    protected void buildCarlight() {
        System.out.println(name + "的独特魔力车灯");
    }
}
