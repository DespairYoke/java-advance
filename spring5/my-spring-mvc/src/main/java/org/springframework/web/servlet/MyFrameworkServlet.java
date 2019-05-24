package org.springframework.web.servlet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SourceFilteringListener;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.ServletRequestHandledEvent;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
public abstract class MyFrameworkServlet extends MyHttpServletBean{

    @Nullable
    private WebApplicationContext webApplicationContext;

    private Log logger = LogFactory.getLog(getClass());

    public static final Class<?> DEFAULT_CONTEXT_CLASS = XmlWebApplicationContext.class;

    private Class<?> contextClass = DEFAULT_CONTEXT_CLASS;

    /** Flag used to detect whether onRefresh has already been called */
    private boolean refreshEventReceived = false;

    @Nullable
    private String contextConfigLocation;

    @Nullable
    private String contextId;

    @Nullable
    private String namespace;

    @Nullable
    private String contextInitializerClasses;

    private boolean publishEvents = true;

    public static final String DEFAULT_NAMESPACE_SUFFIX = "-servlet";

    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

    private boolean threadContextInheritable = false;


    private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers =
            new ArrayList<>();

    @Override
    protected final void initServletBean() throws ServletException {
        getServletContext().log("Initializing Spring FrameworkServlet '" + getServletName() + "'");
        if (this.logger.isInfoEnabled()) {
            this.logger.info("FrameworkServlet '" + getServletName() + "': initialization started");
        }
        long startTime = System.currentTimeMillis();

        try {
            this.webApplicationContext = initWebApplicationContext();
            initFrameworkServlet();
        }
        catch (ServletException | RuntimeException ex) {
            this.logger.error("Context initialization failed", ex);
            throw ex;
        }

        if (this.logger.isInfoEnabled()) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this.logger.info("FrameworkServlet '" + getServletName() + "': initialization completed in " +
                    elapsedTime + " ms");
        }
    }


    protected WebApplicationContext initWebApplicationContext() {
        WebApplicationContext rootContext =
                WebApplicationContextUtils.getWebApplicationContext(getServletContext()); //null
        WebApplicationContext wac = null;

//        if (this.webApplicationContext != null) {
//            // A context instance was injected at construction time -> use it
//            wac = this.webApplicationContext;
//            if (wac instanceof ConfigurableWebApplicationContext) {
//                ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) wac;
//                if (!cwac.isActive()) {
//                    // The context has not yet been refreshed -> provide services such as
//                    // setting the parent context, setting the application context id, etc
//                    if (cwac.getParent() == null) {
//                        // The context instance was injected without an explicit parent -> set
//                        // the root application context (if any; may be null) as the parent
//                        cwac.setParent(rootContext);
//                    }
//                    configureAndRefreshWebApplicationContext(cwac);
//                }
//            }
//        }
//        if (wac == null) {
//            // No context instance was injected at construction time -> see if one
//            // has been registered in the servlet context. If one exists, it is assumed
//            // that the parent context (if any) has already been set and that the
//            // user has performed any initialization such as setting the context id
//            wac = findWebApplicationContext();
//        }
        if (wac == null) {
            // No context instance is defined for this servlet -> create a local one
            wac = createWebApplicationContext(rootContext);
        }

//        if (!this.refreshEventReceived) {
//            // Either the context is not a ConfigurableApplicationContext with refresh
//            // support or the context injected at construction time had already been
//            // refreshed -> trigger initial onRefresh manually here.
//            onRefresh(wac);
//        }
//
//        if (this.publishContext) {
//            // Publish the context as a servlet context attribute.
//            String attrName = getServletContextAttributeName();
//            getServletContext().setAttribute(attrName, wac);
//            if (this.logger.isDebugEnabled()) {
//                this.logger.debug("Published WebApplicationContext of servlet '" + getServletName() +
//                        "' as ServletContext attribute with name [" + attrName + "]");
//            }
//        }

        return wac;
    }


    protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
        return createWebApplicationContext((ApplicationContext) parent);
    }

    protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
        /**
         * 获取加载方式，默认为{@link XmlWebApplicationContext}
         */
        Class<?> contextClass = getContextClass();

        ConfigurableWebApplicationContext wac =
                (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

        /**
         * 获取环境 {@link StandardEnvironment}
         */
        wac.setEnvironment(getEnvironment());  //StandardServletEnvironment
        wac.setParent(parent);            //null
        String configLocation = getContextConfigLocation();  //null
        if (configLocation != null) {
            wac.setConfigLocation(configLocation);
        }
        configureAndRefreshWebApplicationContext(wac);

        return wac;
    }

    public Class<?> getContextClass() {
        return this.contextClass;
    }

    @Nullable
    public String getContextConfigLocation() {
        return this.contextConfigLocation;
    }

    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            // The application context id is still set to its original default value
            // -> assign a more useful id based on available information
            if (this.contextId != null) {
                wac.setId(this.contextId);
            }
            else {
                // Generate default id...
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
                        ObjectUtils.getDisplayString(getServletContext().getContextPath()) + '/' + getServletName());
            }
        }

        wac.setServletContext(getServletContext());
        wac.setServletConfig(getServletConfig());
        wac.setNamespace(getNamespace());
        wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));

        // The wac environment's #initPropertySources will be called in any case when the context
        // is refreshed; do it eagerly here to ensure servlet property sources are in place for
        // use in any post-processing or initialization that occurs below prior to #refresh
        ConfigurableEnvironment env = wac.getEnvironment();
        if (env instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment) env).initPropertySources(getServletContext(), getServletConfig());
        }

        postProcessWebApplicationContext(wac);
        applyInitializers(wac);
        wac.refresh();
    }


    public String getNamespace() {
        return (this.namespace != null ? this.namespace : getServletName() + DEFAULT_NAMESPACE_SUFFIX);
    }


    private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            MyFrameworkServlet.this.onApplicationEvent(event);
        }
    }


    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.refreshEventReceived = true;
        onRefresh(event.getApplicationContext());
    }

    protected void onRefresh(ApplicationContext context) {
        // For subclasses: do nothing by default.
    }

    protected void initFrameworkServlet() throws ServletException {
    }

    protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
    }


    protected void applyInitializers(ConfigurableApplicationContext wac) {
        String globalClassNames = getServletContext().getInitParameter(ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM);
        if (globalClassNames != null) {
            for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(loadInitializer(className, wac));
            }
        }

        if (this.contextInitializerClasses != null) {
            for (String className : StringUtils.tokenizeToStringArray(this.contextInitializerClasses, INIT_PARAM_DELIMITERS)) {
                this.contextInitializers.add(loadInitializer(className, wac));
            }
        }

        AnnotationAwareOrderComparator.sort(this.contextInitializers);
        for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
            initializer.initialize(wac);
        }
    }

    private ApplicationContextInitializer<ConfigurableApplicationContext> loadInitializer(
            String className, ConfigurableApplicationContext wac) {
        try {
            Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
            Class<?> initializerContextClass =
                    GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
            if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
                throw new ApplicationContextException(String.format(
                        "Could not apply context initializer [%s] since its generic parameter [%s] " +
                                "is not assignable from the type of application context used by this " +
                                "framework servlet: [%s]", initializerClass.getName(), initializerContextClass.getName(),
                        wac.getClass().getName()));
            }
            return BeanUtils.instantiateClass(initializerClass, ApplicationContextInitializer.class);
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException(String.format("Could not load class [%s] specified " +
                    "via 'contextInitializerClasses' init-param", className), ex);
        }
    }

    /**
     * 通过反射初始化ContextConfigLocation
     */
    public void setContextConfigLocation(@Nullable String contextConfigLocation) {
        this.contextConfigLocation = contextConfigLocation;
    }

    /**
     * 通过继承重写httpServletBean的serivce
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());

        String method = request.getMethod();
        if (httpMethod == HttpMethod.PATCH || httpMethod == null) {
            processRequest(request, response);
        }
        else {
            super.service(request, response);
        }
    }

    /**
     * 对请求近一步处理 {@link #service}
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        Throwable failureCause = null;

        LocaleContext previousLocaleContext = LocaleContextHolder.getLocaleContext();
        LocaleContext localeContext = buildLocaleContext(request);

        RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes requestAttributes = buildRequestAttributes(request, response, previousAttributes);

        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.registerCallableInterceptor(MyFrameworkServlet.class.getName(), new RequestBindingInterceptor());

        initContextHolders(request, localeContext, requestAttributes);

        try {
            doService(request, response);
        }
        catch (ServletException | IOException ex) {
            failureCause = ex;
            throw ex;
        }
        catch (Throwable ex) {
            failureCause = ex;
            throw new NestedServletException("Request processing failed", ex);
        }

        finally {
            resetContextHolders(request, previousLocaleContext, previousAttributes);
            if (requestAttributes != null) {
                requestAttributes.requestCompleted();
            }

            if (logger.isDebugEnabled()) {
                if (failureCause != null) {
                    this.logger.debug("Could not complete request", failureCause);
                }
                else {
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        logger.debug("Leaving response open for concurrent processing");
                    }
                    else {
                        this.logger.debug("Successfully completed request");
                    }
                }
            }

            publishRequestHandledEvent(request, response, startTime, failureCause);
        }
    }

    @Nullable
    protected LocaleContext buildLocaleContext(HttpServletRequest request) {
        return new SimpleLocaleContext(request.getLocale());
    }

    @Nullable
    protected ServletRequestAttributes buildRequestAttributes(HttpServletRequest request,
                                                              @Nullable HttpServletResponse response, @Nullable RequestAttributes previousAttributes) {

        if (previousAttributes == null || previousAttributes instanceof ServletRequestAttributes) {
            return new ServletRequestAttributes(request, response);
        }
        else {
            return null;  // preserve the pre-bound RequestAttributes instance
        }
    }

    private class RequestBindingInterceptor implements CallableProcessingInterceptor {

        @Override
        public <T> void preProcess(NativeWebRequest webRequest, Callable<T> task) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                initContextHolders(request, buildLocaleContext(request),
                        buildRequestAttributes(request, response, null));
            }
        }
        @Override
        public <T> void postProcess(NativeWebRequest webRequest, Callable<T> task, Object concurrentResult) {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                resetContextHolders(request, null, null);
            }
        }
    }


    private void initContextHolders(HttpServletRequest request,
                                    @Nullable LocaleContext localeContext, @Nullable RequestAttributes requestAttributes) {

        if (localeContext != null) {
            LocaleContextHolder.setLocaleContext(localeContext, this.threadContextInheritable);
        }
        if (requestAttributes != null) {
            RequestContextHolder.setRequestAttributes(requestAttributes, this.threadContextInheritable);
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Bound request context to thread: " + request);
        }
    }

    protected abstract void doService(HttpServletRequest request, HttpServletResponse response)
            throws Exception;


    private void resetContextHolders(HttpServletRequest request,
                                     @Nullable LocaleContext prevLocaleContext, @Nullable RequestAttributes previousAttributes) {

        LocaleContextHolder.setLocaleContext(prevLocaleContext, this.threadContextInheritable);
        RequestContextHolder.setRequestAttributes(previousAttributes, this.threadContextInheritable);
        if (logger.isTraceEnabled()) {
            logger.trace("Cleared thread-bound request context: " + request);
        }
    }

    private void publishRequestHandledEvent(HttpServletRequest request, HttpServletResponse response,
                                            long startTime, @Nullable Throwable failureCause) {

        if (this.publishEvents && this.webApplicationContext != null) {
            // Whether or not we succeeded, publish an event.
            long processingTime = System.currentTimeMillis() - startTime;
            this.webApplicationContext.publishEvent(
                    new ServletRequestHandledEvent(this,
                            request.getRequestURI(), request.getRemoteAddr(),
                            request.getMethod(), getServletConfig().getServletName(),
                            WebUtils.getSessionId(request), getUsernameForRequest(request),
                            processingTime, failureCause, response.getStatus()));
        }
    }

    @Nullable
    protected String getUsernameForRequest(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        return (userPrincipal != null ? userPrincipal.getName() : null);
    }

    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Nullable
    public final WebApplicationContext getWebApplicationContext() {
        return this.webApplicationContext;
    }
}
