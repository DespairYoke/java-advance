package com.zwd.listener;

import com.zwd.listener.event.ApplicationEvent;

import java.util.EventListener;

public interface ApplicationListener <E extends ApplicationEvent> extends EventListener {

    void onApplicationEvent(E event);

}
