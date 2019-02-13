package com.hansn.service;

public interface Observable {
    //注册观察者
    void registerObserver(Observer observer);
    //取消观察者
    void removeObserver(Observer observer);
    //通知所有观察者更新消息
    void notifyObserver();
}
