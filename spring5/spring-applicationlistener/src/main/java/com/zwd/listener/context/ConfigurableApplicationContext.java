package com.zwd.listener.context;

import com.zwd.listener.ApplicationListener;

public interface ConfigurableApplicationContext {

    void addApplicationListener(ApplicationListener<?> listener);
}
