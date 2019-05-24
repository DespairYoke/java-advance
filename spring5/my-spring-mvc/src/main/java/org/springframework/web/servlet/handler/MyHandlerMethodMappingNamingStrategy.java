package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
@FunctionalInterface
public interface MyHandlerMethodMappingNamingStrategy<T> {

    String getName(HandlerMethod handlerMethod, T mapping);
}
