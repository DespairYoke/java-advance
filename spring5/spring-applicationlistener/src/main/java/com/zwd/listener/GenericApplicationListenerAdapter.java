package com.zwd.listener;


import com.zwd.listener.context.AbstractApplicationContext;
import com.zwd.listener.event.ApplicationEvent;

public class GenericApplicationListenerAdapter implements GenericApplicationListener, SmartApplicationListener {

    private final ApplicationListener<ApplicationEvent> delegate;

    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        this.delegate = (ApplicationListener<ApplicationEvent>)delegate;
    }



    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        delegate.onApplicationEvent(event);
    }

    @Override
    public boolean supportsSourceType( Class<?> sourceType) {
        return !(this.delegate instanceof SmartApplicationListener) ||
                ((SmartApplicationListener) this.delegate).supportsSourceType(sourceType);
    }
}
