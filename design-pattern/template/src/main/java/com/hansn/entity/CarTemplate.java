package com.hansn.entity;

public abstract class CarTemplate {
    protected String name;

    protected CarTemplate(String name){
        this.name = name;
    }

    protected abstract void buildWheel();

    protected abstract void buildEngine();

    protected abstract void buildCarbody();

    protected abstract void buildCarlight();

    public final void buildCar(){
        buildWheel();
        buildEngine();
        buildCarbody();
        buildCarlight();
    }
}
