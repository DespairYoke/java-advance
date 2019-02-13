package com.hansn.serviceImpl;

import com.hansn.service.Observer;

public class User implements Observer {
    private String name;
    private String message;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        this.message = message;
        read();
    }

    public void read(){
        System.out.println(name + "接收到推送消息" + message);
    }
}
