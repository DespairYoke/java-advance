package com.zwd.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EventPulish {

    @Autowired
    private ApplicationContext applicationContext;


    public void publish(String message) {
        applicationContext.publishEvent(new MyEvent(this,message));
    }
}
