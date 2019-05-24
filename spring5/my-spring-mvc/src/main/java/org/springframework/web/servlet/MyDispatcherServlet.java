package org.springframework.web.servlet;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.mvc.method.annotation.MyRequestMappingHandlerMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


public class MyDispatcherServlet extends MyFrameworkServlet{


    private Log logger =  LogFactory.getLog(getClass());


    private boolean cleanupAfterInclude = true;

    @Nullable
    private MyLocaleResolver localeResolver;

    @Nullable
    private MyThemeResolver themeResolver;

    @Nullable
    private MyFlashMapManager flashMapManager;

    @Nullable
    private List<MyHandlerMapping> handlerMappings;

    private static final Properties defaultStrategies;

    @Nullable
    private List<MyHandlerAdapter> handlerAdapters;

    private boolean detectAllViewResolvers = true;

    public static final String LOCALE_RESOLVER_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".LOCALE_RESOLVER";


    public static final String THEME_RESOLVER_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".THEME_RESOLVER";

    public static final String THEME_SOURCE_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".THEME_SOURCE";

    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";

    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".CONTEXT";

    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";

    public static final String HANDLER_ADAPTER_BEAN_NAME = "myhandlerAdapter";

    public static final String INPUT_FLASH_MAP_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";

    public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";

    public static final String VIEW_RESOLVER_BEAN_NAME = "myViewResolver";

    public static final String FLASH_MAP_MANAGER_ATTRIBUTE = MyDispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";

    private boolean detectAllHandlerAdapters = true;

    @Nullable
    private List<MyViewResolver> viewResolvers;

    static {
        // Load default strategy implementations from properties file.
        // This is currently strictly internal and not meant to be customized
        // by application developers.
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, MyDispatcherServlet.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
        }
    }

    protected void onRefresh(ApplicationContext context) {

        System.out.println("=======dispatherServlet onfresh");
        initStrategies(context);
    }


    public void initStrategies(ApplicationContext context) {
        initHandlerMappings(context);
        initHandlerAdapters(context);
        initViewResolvers(context);
    }

    private void initHandlerMappings(ApplicationContext context) {

        this.handlerMappings = null;
        if (this.handlerMappings == null) {
            this.handlerMappings = getDefaultStrategies(context, MyHandlerMapping.class);
            if (logger.isDebugEnabled()) {
                logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
            }
        }
    }
    private void initHandlerAdapters(ApplicationContext context) {

        this.handlerAdapters = getDefaultStrategies(context, MyHandlerAdapter.class);

    }

    private void initViewResolvers(ApplicationContext context) {
        this.viewResolvers = null;
        //加载app-context.xml中配置的viewResolver
        if (this.detectAllViewResolvers) {
            // Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
            Map<String, MyViewResolver> matchingBeans =
                    BeanFactoryUtils.beansOfTypeIncludingAncestors(context, MyViewResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.viewResolvers = new ArrayList<>(matchingBeans.values());
                // We keep ViewResolvers in sorted order.
                AnnotationAwareOrderComparator.sort(this.viewResolvers);

            }
        }
        //如果没有配置的viewResolver使用配置文件默认配置
        if (this.viewResolvers == null) {
            this.viewResolvers = getDefaultStrategies(context, MyViewResolver.class);
        }
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {

        String key = strategyInterface.getName();

        String value = defaultStrategies.getProperty(key);

        if (value != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);
            for (String className : classNames) {

                try {
                    Class<?> clazz = ClassUtils.forName(className, MyDispatcherServlet.class.getClassLoader());
                    Object strategy = createDefaultStrategy(context, clazz);
                    strategies.add((T) strategy);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return strategies;
        }else {
            return new LinkedList<>();
        }



    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
        request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
        doDispatch(request, response);
    }

    @Nullable
    public final ThemeSource getThemeSource() {
        return (getWebApplicationContext() instanceof ThemeSource ? (ThemeSource) getWebApplicationContext() : null);
    }

    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Exception dispatchException = null;
        MyHandlerExecutionChain mappedHandler = null;
        MyModelAndView mv = null;
        HttpServletRequest processedRequest = request;
        mappedHandler = getHandler(processedRequest);
        MyHandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
//
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);

    }


    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
                                       @Nullable MyHandlerExecutionChain mappedHandler, @Nullable MyModelAndView mv,
                                       @Nullable Exception exception) throws Exception {
        // 渲染页面
        render(mv, request, response);
    }

    protected void render(MyModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //  // 根据请求决定回复消息的 locale
        Locale locale =
                (this.localeResolver != null ? this.localeResolver.resolveLocale(request) : request.getLocale());
        response.setLocale(locale);

        MyView view;
        String viewName = mv.getViewName();
        if (viewName != null) {
            // We need to resolve the view name.
            view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() +
                        "' in servlet with name '" + getServletName() + "'");
            }
        }
        else {
            // No need to lookup: the ModelAndView object contains the actual View object.
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
                        "View object in servlet with name '" + getServletName() + "'");
            }
        }

        // Delegate to the View object for rendering.
        if (logger.isDebugEnabled()) {
            logger.debug("Rendering view [" + view + "] in DispatcherServlet with name '" + getServletName() + "'");
        }
        try {
            if (mv.getStatus() != null) {
                response.setStatus(mv.getStatus().value());
            }
            view.render(mv.getModelInternal(), request, response);
        }
        catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error rendering view [" + view + "] in DispatcherServlet with name '" +
                        getServletName() + "'", ex);
            }
            throw ex;
        }
    }

    @Nullable
    protected MyView resolveViewName(String viewName, @Nullable Map<String, Object> model,
                                   Locale locale, HttpServletRequest request) throws Exception {

        if (this.viewResolvers != null) {
            for (MyViewResolver viewResolver : this.viewResolvers) {
                MyView view = viewResolver.resolveViewName(viewName, locale);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    protected MyHandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        if (this.handlerAdapters != null) {
            for (MyHandlerAdapter ha : this.handlerAdapters) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Testing handler adapter [" + ha + "]");
                }
                if (ha.supports(handler)) {
                    return ha;
                }
            }
        }
        throw new ServletException("No adapter for handler [" + handler +
                "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
    }
    @Nullable
    protected MyHandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        if (this.handlerMappings != null) {
            for (MyHandlerMapping hm : this.handlerMappings) {
                if (logger.isTraceEnabled()) {
                    logger.trace(
                            "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
                }
                MyHandlerExecutionChain handler = hm.getHandler(request);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }


}
