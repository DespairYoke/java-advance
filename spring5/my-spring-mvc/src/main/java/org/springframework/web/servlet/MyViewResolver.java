package org.springframework.web.servlet;

import org.springframework.lang.Nullable;

import java.util.Locale;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-11
 **/
public interface MyViewResolver {

    @Nullable
    MyView resolveViewName(String viewName, Locale locale) throws Exception;
}
