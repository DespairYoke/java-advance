package com.hansn;

import com.hansn.service.Observer;
import com.hansn.serviceImpl.User;
import com.hansn.serviceImpl.WeChatServer;

public class ObserverTest {
    public static void main(String[] args) {
        WeChatServer server = new WeChatServer();

        Observer userZhang = new User("ZhangSan");
        Observer userLi = new User("LiSi");
        Observer userWang = new User("WangWu");

        server.registerObserver(userZhang);
        server.registerObserver(userLi);
        server.registerObserver(userWang);
        server.setInfomation("PHP是世界上最好用的语言！");

        System.out.println("----------------------------------------------");
        server.removeObserver(userLi);
        server.setInfomation("JAVA是世界上最好用的语言！");
    }
}
