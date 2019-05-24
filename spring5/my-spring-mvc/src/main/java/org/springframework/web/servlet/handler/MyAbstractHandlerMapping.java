package org.springframework.web.servlet.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.MyHandlerExecutionChain;
import org.springframework.web.servlet.MyHandlerInterceptor;
import org.springframework.web.servlet.MyHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
public abstract class MyAbstractHandlerMapping extends WebApplicationObjectSupport implements MyHandlerMapping {


    @Nullable
    private Object defaultHandler;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private final List<MyHandlerInterceptor> adaptedInterceptors = new ArrayList<>();

    @Nullable
    protected abstract Object getHandlerInternal(HttpServletRequest request) throws Exception;

    @Override
    public MyHandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Object handler = getHandlerInternal(request);

        if (handler == null) {
            getDefaultHandler();
        }
        MyHandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
//        if (CorsUtils.isCorsRequest(request)) {
//            CorsConfiguration globalConfig = this.globalCorsConfigSource.getCorsConfiguration(request);
//            CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
//            CorsConfiguration config = (globalConfig != null ? globalConfig.combine(handlerConfig) : handlerConfig);
//            executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
//        }
        return executionChain;
    }

    protected MyHandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
        MyHandlerExecutionChain chain = (handler instanceof MyHandlerExecutionChain ?
                (MyHandlerExecutionChain) handler : new MyHandlerExecutionChain(handler));

        String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
        for (MyHandlerInterceptor interceptor : this.adaptedInterceptors) {
            if (interceptor instanceof MyMappedInterceptor) {
                MyMappedInterceptor mappedInterceptor = (MyMappedInterceptor) interceptor;
                if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
                    chain.addInterceptor(mappedInterceptor.getInterceptor());
                }
            }
            else {
                chain.addInterceptor(interceptor);
            }
        }
        return chain;

    }

    public PathMatcher getPathMatcher() {
        return this.pathMatcher;
    }
    public void setDefaultHandler(@Nullable Object defaultHandler) {
        this.defaultHandler = defaultHandler;
    }

    @Nullable
    public Object getDefaultHandler() {
        return this.defaultHandler;
    }
}
