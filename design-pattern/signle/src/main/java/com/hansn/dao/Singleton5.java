package com.hansn.dao;

public class Singleton5 {
    //静态内部类
    private static class SingletonHolder {
        private static final Singleton5 INSTANCE = new Singleton5();
    }
    private Singleton5 (){}

    public static final Singleton5 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
