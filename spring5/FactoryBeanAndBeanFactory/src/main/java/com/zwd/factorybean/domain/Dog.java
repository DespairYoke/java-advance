package com.zwd.factorybean.domain;

public class Dog {
    private String msg;

    public Dog(String msg){
        this.msg=msg;
    }
    public void run(){
        System.out.println(msg);
    }
}