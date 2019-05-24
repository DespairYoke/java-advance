package org.springframework.web.servlet.mvc.method;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.MyHandlerAdapter;
import org.springframework.web.servlet.MyModelAndView;
import org.springframework.web.servlet.support.MyWebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public abstract class MyAbstractHandlerMethodAdapter extends MyWebContentGenerator implements MyHandlerAdapter, Ordered {

    @Override
    @Nullable
    public final MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        return handleInternal(request, response, (HandlerMethod) handler);
    }

    @Nullable
    protected abstract MyModelAndView handleInternal(HttpServletRequest request,
                                                   HttpServletResponse response, HandlerMethod handlerMethod) throws Exception;
}
