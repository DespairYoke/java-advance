package org.springframework.web.servlet;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-23
 **/
public class MyHandlerExecutionChain {

    private final Object handler;

    @Nullable
    private MyHandlerInterceptor[] interceptors;

    @Nullable
    private List<MyHandlerInterceptor> interceptorList;

    public MyHandlerExecutionChain(Object handler) {
        this(handler, (MyHandlerInterceptor[]) null);
    }

    /**
     * Create a new HandlerExecutionChain.
     * @param handler the handler object to execute
     * @param interceptors the array of interceptors to apply
     * (in the given order) before the handler itself executes
     */
    public MyHandlerExecutionChain(Object handler, @Nullable MyHandlerInterceptor... interceptors) {
        if (handler instanceof MyHandlerExecutionChain) {
            MyHandlerExecutionChain originalChain = (MyHandlerExecutionChain) handler;
            this.handler = originalChain.getHandler();
            this.interceptorList = new ArrayList<>();
            CollectionUtils.mergeArrayIntoCollection(originalChain.getInterceptors(), this.interceptorList);
            CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
        }
        else {
            this.handler = handler;
            this.interceptors = interceptors;
        }
    }

    public Object getHandler() {
        return this.handler;
    }

    @Nullable
    public MyHandlerInterceptor[] getInterceptors() {
        if (this.interceptors == null && this.interceptorList != null) {
            this.interceptors = this.interceptorList.toArray(new MyHandlerInterceptor[0]);
        }
        return this.interceptors;
    }

    public void addInterceptor(MyHandlerInterceptor interceptor) {
        initInterceptorList().add(interceptor);
    }

    private List<MyHandlerInterceptor> initInterceptorList() {
        if (this.interceptorList == null) {
            this.interceptorList = new ArrayList<>();
            if (this.interceptors != null) {
                // An interceptor array specified through the constructor
                CollectionUtils.mergeArrayIntoCollection(this.interceptors, this.interceptorList);
            }
        }
        this.interceptors = null;
        return this.interceptorList;
    }

}
