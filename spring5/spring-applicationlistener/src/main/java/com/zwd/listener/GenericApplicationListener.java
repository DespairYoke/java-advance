package com.zwd.listener;

import com.sun.istack.internal.Nullable;
import com.zwd.listener.event.ApplicationEvent;

public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>{


    /**
     * Determine whether this listener actually supports the given source type.
     */
    boolean supportsSourceType(@Nullable Class<?> sourceType);
}
