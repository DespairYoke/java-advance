package com.zwd.listener;

import com.sun.istack.internal.Nullable;
import com.zwd.listener.event.ApplicationEvent;

public class SourceFilteringListener implements GenericApplicationListener, SmartApplicationListener {


    private final Object source;
    @Nullable
    private GenericApplicationListener delegate;


    public SourceFilteringListener(Object source,ApplicationListener<?> delegate) {
        this.source = source;
        this.delegate = new GenericApplicationListenerAdapter(delegate);
    }

    @Override
    public boolean supportsSourceType(@Nullable Class<?> sourceType) {
        return (sourceType != null && sourceType.isInstance(this.source));
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        delegate.onApplicationEvent(event);
    }
}
