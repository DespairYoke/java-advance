package org.springframework.web.servlet;

import org.springframework.context.i18n.LocaleContext;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public interface MyLocaleContextResolver extends MyLocaleResolver{

    LocaleContext resolveLocaleContext(HttpServletRequest request);
}
