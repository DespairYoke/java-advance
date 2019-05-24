package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public interface MyHandlerAdapter {

    boolean supports(Object handler);

    @Nullable
    MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

}
