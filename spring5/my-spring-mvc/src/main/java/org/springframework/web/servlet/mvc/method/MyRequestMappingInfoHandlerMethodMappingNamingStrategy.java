package org.springframework.web.servlet.mvc.method;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.MyHandlerMethodMappingNamingStrategy;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-24
 **/
public class MyRequestMappingInfoHandlerMethodMappingNamingStrategy
        implements MyHandlerMethodMappingNamingStrategy<MyRequestMappingInfo> {

    public static final String SEPARATOR = "#";

    @Override
    public String getName(HandlerMethod handlerMethod, MyRequestMappingInfo mapping) {
        if (mapping.getName() != null) {
            return mapping.getName();
        }
        StringBuilder sb = new StringBuilder();
        String simpleTypeName = handlerMethod.getBeanType().getSimpleName();
        for (int i = 0 ; i < simpleTypeName.length(); i++) {
            if (Character.isUpperCase(simpleTypeName.charAt(i))) {
                sb.append(simpleTypeName.charAt(i));
            }
        }
        sb.append(SEPARATOR).append(handlerMethod.getMethod().getName());
        return sb.toString();
    }
}
