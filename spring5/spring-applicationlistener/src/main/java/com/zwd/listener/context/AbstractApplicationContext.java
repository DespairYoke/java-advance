package com.zwd.listener.context;

import com.zwd.listener.ApplicationListener;
import com.zwd.listener.GenericApplicationListener;
import com.zwd.listener.event.ContextRefreshedEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AbstractApplicationContext implements ConfigurableApplicationContext {

    private List<ApplicationListener> listeners = new CopyOnWriteArrayList<>();
    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {

        listeners.add(listener);
    }

   public void onRefresh() {
        for (ApplicationListener listener : listeners) {
           if (((GenericApplicationListener)listener).supportsSourceType(getClass())){
               listener.onApplicationEvent(new ContextRefreshedEvent());
            }

        }
    }
}
