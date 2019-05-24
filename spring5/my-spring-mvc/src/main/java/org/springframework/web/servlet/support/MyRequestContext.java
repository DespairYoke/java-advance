package org.springframework.web.servlet.support;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.MyDispatcherServlet;
import org.springframework.web.servlet.MyLocaleContextResolver;
import org.springframework.web.servlet.MyLocaleResolver;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-05-13
 **/
public class MyRequestContext {


    @Nullable
    private Locale locale;

    @Nullable
    private TimeZone timeZone;

    private HttpServletRequest request;

    @Nullable
    private HttpServletResponse response;

    @Nullable
    private Map<String, Object> model;

    private WebApplicationContext webApplicationContext;

    @Nullable
    private Boolean responseEncodedHtmlEscape;

    private UrlPathHelper urlPathHelper;

    @Nullable
    private MyRequestDataValueProcessor requestDataValueProcessor;

    protected static final boolean jstlPresent = ClassUtils.isPresent(
            "javax.servlet.jsp.jstl.core.Config", MyRequestContext.class.getClassLoader());

    @Nullable
    private Boolean defaultHtmlEscape;

    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyRequestContext.class.getName() + ".CONTEXT";


    @Nullable
    protected final ServletContext getServletContext() {
        return this.webApplicationContext.getServletContext();
    }

    public MyRequestContext(HttpServletRequest request, @Nullable ServletContext servletContext) {
        this(request, null, servletContext, null);
    }

    protected final HttpServletRequest getRequest() {
        return this.request;
    }

    public final Locale getLocale() {
        return (this.locale != null ? this.locale : getFallbackLocale());
    }

    protected Locale getFallbackLocale() {
        if (jstlPresent) {
            Locale locale = JstlLocaleResolver.getJstlLocale(getRequest(), getServletContext());
            if (locale != null) {
                return locale;
            }
        }
        return getRequest().getLocale();
    }


    public MyRequestContext(HttpServletRequest request, @Nullable HttpServletResponse response,
                          @Nullable ServletContext servletContext, @Nullable Map<String, Object> model) {

        this.request = request;
        this.response = response;
        this.model = model;

        // Fetch WebApplicationContext, either from DispatcherServlet or the root context.
        // ServletContext needs to be specified to be able to fall back to the root context!
        WebApplicationContext wac = (WebApplicationContext) request.getAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        if (wac == null) {
            wac = MyRequestContextUtils.findWebApplicationContext(request, servletContext);
            if (wac == null) {
                throw new IllegalStateException("No WebApplicationContext found: not in a DispatcherServlet " +
                        "request and no ContextLoaderListener registered?");
            }
        }
        this.webApplicationContext = wac;

        Locale locale = null;
        TimeZone timeZone = null;

        // Determine locale to use for this RequestContext.
        MyLocaleResolver localeResolver = MyRequestContextUtils.getLocaleResolver(request);
        if (localeResolver instanceof MyLocaleContextResolver) {
            LocaleContext localeContext = ((MyLocaleContextResolver) localeResolver).resolveLocaleContext(request);
            locale = localeContext.getLocale();
            if (localeContext instanceof TimeZoneAwareLocaleContext) {
                timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
            }
        }
        else if (localeResolver != null) {
            // Try LocaleResolver (we're within a DispatcherServlet request).
            locale = localeResolver.resolveLocale(request);
        }

        this.locale = locale;
        this.timeZone = timeZone;

        // Determine default HTML escape setting from the "defaultHtmlEscape"
        // context-param in web.xml, if any.
        this.defaultHtmlEscape = WebUtils.getDefaultHtmlEscape(this.webApplicationContext.getServletContext());

        // Determine response-encoded HTML escape setting from the "responseEncodedHtmlEscape"
        // context-param in web.xml, if any.
        this.responseEncodedHtmlEscape =
                WebUtils.getResponseEncodedHtmlEscape(this.webApplicationContext.getServletContext());

        this.urlPathHelper = new UrlPathHelper();

        if (this.webApplicationContext.containsBean(MyRequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME)) {
            this.requestDataValueProcessor = this.webApplicationContext.getBean(
                    MyRequestContextUtils.REQUEST_DATA_VALUE_PROCESSOR_BEAN_NAME, MyRequestDataValueProcessor.class);
        }
    }

    @Nullable
    public static MyLocaleResolver getLocaleResolver(HttpServletRequest request) {
        return (MyLocaleResolver) request.getAttribute(MyDispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
    }

    private static class JstlLocaleResolver {

        @Nullable
        public static Locale getJstlLocale(HttpServletRequest request, @Nullable ServletContext servletContext) {
            Object localeObject = Config.get(request, Config.FMT_LOCALE);
            if (localeObject == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    localeObject = Config.get(session, Config.FMT_LOCALE);
                }
                if (localeObject == null && servletContext != null) {
                    localeObject = Config.get(servletContext, Config.FMT_LOCALE);
                }
            }
            return (localeObject instanceof Locale ? (Locale) localeObject : null);
        }

        @Nullable
        public static TimeZone getJstlTimeZone(HttpServletRequest request, @Nullable ServletContext servletContext) {
            Object timeZoneObject = Config.get(request, Config.FMT_TIME_ZONE);
            if (timeZoneObject == null) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    timeZoneObject = Config.get(session, Config.FMT_TIME_ZONE);
                }
                if (timeZoneObject == null && servletContext != null) {
                    timeZoneObject = Config.get(servletContext, Config.FMT_TIME_ZONE);
                }
            }
            return (timeZoneObject instanceof TimeZone ? (TimeZone) timeZoneObject : null);
        }
    }

    public final MessageSource getMessageSource() {
        return this.webApplicationContext;
    }

    @Nullable
    public TimeZone getTimeZone() {
        return (this.timeZone != null ? this.timeZone : getFallbackTimeZone());
    }

    @Nullable
    protected TimeZone getFallbackTimeZone() {
        if (jstlPresent) {
            TimeZone timeZone = JstlLocaleResolver.getJstlTimeZone(getRequest(), getServletContext());
            if (timeZone != null) {
                return timeZone;
            }
        }
        return null;
    }
}
