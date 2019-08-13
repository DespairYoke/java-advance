package com.zwd.event;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EventBootstrap {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext act = new AnnotationConfigApplicationContext(Configration.class);

        EventPulish eventPulish = act.getBean(EventPulish.class);
        eventPulish.publish("我叫张三！");
    }
}
