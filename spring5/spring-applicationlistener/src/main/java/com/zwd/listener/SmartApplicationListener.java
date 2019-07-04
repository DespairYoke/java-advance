package com.zwd.listener;

import com.zwd.listener.event.ApplicationEvent;

public interface SmartApplicationListener extends ApplicationListener<ApplicationEvent> {

    boolean supportsSourceType( Class<?> sourceType);
}
