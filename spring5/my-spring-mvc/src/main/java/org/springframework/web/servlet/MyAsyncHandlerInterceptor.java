package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public interface MyAsyncHandlerInterceptor extends MyHandlerInterceptor{

    default void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
                                                Object handler) throws Exception {
    }
}
