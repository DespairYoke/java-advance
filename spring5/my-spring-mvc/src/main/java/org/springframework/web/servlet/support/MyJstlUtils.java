package org.springframework.web.servlet.support;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public class MyJstlUtils {

    public static MessageSource getJstlAwareMessageSource(
            @Nullable ServletContext servletContext, MessageSource messageSource) {

        if (servletContext != null) {
            String jstlInitParam = servletContext.getInitParameter(Config.FMT_LOCALIZATION_CONTEXT);
            if (jstlInitParam != null) {
                // Create a ResourceBundleMessageSource for the specified resource bundle
                // basename in the JSTL context-param in web.xml, wiring it with the given
                // Spring-defined MessageSource as parent.
                ResourceBundleMessageSource jstlBundleWrapper = new ResourceBundleMessageSource();
                jstlBundleWrapper.setBasename(jstlInitParam);
                jstlBundleWrapper.setParentMessageSource(messageSource);
                return jstlBundleWrapper;
            }
        }
        return messageSource;
    }
    public static void exposeLocalizationContext(MyRequestContext requestContext) {
        Config.set(requestContext.getRequest(), Config.FMT_LOCALE, requestContext.getLocale());
        TimeZone timeZone = requestContext.getTimeZone();
        if (timeZone != null) {
            Config.set(requestContext.getRequest(), Config.FMT_TIME_ZONE, timeZone);
        }
        MessageSource messageSource = getJstlAwareMessageSource(
                requestContext.getServletContext(), requestContext.getMessageSource());
        LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, requestContext.getRequest());
        Config.set(requestContext.getRequest(), Config.FMT_LOCALIZATION_CONTEXT, jstlContext);
    }



    public static void exposeLocalizationContext(HttpServletRequest request, @Nullable MessageSource messageSource) {
        Locale jstlLocale = MyRequestContextUtils.getLocale(request);
        Config.set(request, Config.FMT_LOCALE, jstlLocale);
        TimeZone timeZone = MyRequestContextUtils.getTimeZone(request);
        if (timeZone != null) {
            Config.set(request, Config.FMT_TIME_ZONE, timeZone);
        }
        if (messageSource != null) {
            LocalizationContext jstlContext = new SpringLocalizationContext(messageSource, request);
            Config.set(request, Config.FMT_LOCALIZATION_CONTEXT, jstlContext);
        }
    }

    private static class SpringLocalizationContext extends LocalizationContext {

        private final MessageSource messageSource;

        private final HttpServletRequest request;

        public SpringLocalizationContext(MessageSource messageSource, HttpServletRequest request) {
            this.messageSource = messageSource;
            this.request = request;
        }

        @Override
        public ResourceBundle getResourceBundle() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object lcObject = Config.get(session, Config.FMT_LOCALIZATION_CONTEXT);
                if (lcObject instanceof LocalizationContext) {
                    ResourceBundle lcBundle = ((LocalizationContext) lcObject).getResourceBundle();
                    return new MessageSourceResourceBundle(this.messageSource, getLocale(), lcBundle);
                }
            }
            return new MessageSourceResourceBundle(this.messageSource, getLocale());
        }

        @Override
        public Locale getLocale() {
            HttpSession session = this.request.getSession(false);
            if (session != null) {
                Object localeObject = Config.get(session, Config.FMT_LOCALE);
                if (localeObject instanceof Locale) {
                    return (Locale) localeObject;
                }
            }
            return MyRequestContextUtils.getLocale(this.request);
        }
    }
}
