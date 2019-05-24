package org.springframework.web.servlet.mvc.method.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.*;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.*;
import org.springframework.web.method.support.*;
import org.springframework.web.servlet.MyModelAndView;
import org.springframework.web.servlet.MyView;
import org.springframework.web.servlet.mvc.method.MyAbstractHandlerMethodAdapter;
import org.springframework.web.servlet.support.MyRedirectAttributes;
import org.springframework.web.servlet.support.MyRequestContextUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-28
 **/
public class MyRequestMappingHandlerAdapter extends MyAbstractHandlerMethodAdapter
        implements BeanFactoryAware, InitializingBean {

    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private ConfigurableBeanFactory beanFactory;

    private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<>(64);

    private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap<>();

    @Nullable
    private HandlerMethodArgumentResolverComposite argumentResolvers;

    @Nullable
    private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

    private CallableProcessingInterceptor[] callableInterceptors = new CallableProcessingInterceptor[0];
    @Nullable
    private WebBindingInitializer webBindingInitializer;

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    @Nullable
    private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    protected static final String HEADER_CACHE_CONTROL = "Cache-Control";

    private boolean ignoreDefaultModelOnRedirect = false;

    private DeferredResultProcessingInterceptor[] deferredResultInterceptors = new DeferredResultProcessingInterceptor[0];

    private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("MvcAsync");

    private List<HttpMessageConverter<?>> messageConverters;

    @Nullable
    private Long asyncRequestTimeout;

    private int cacheSecondsForSessionAttributeHandlers = 0;

    private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();

    private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache = new ConcurrentHashMap<>(64);

    private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap<>(64);

    private final Map<ControllerAdviceBean, Set<Method>> modelAttributeAdviceCache = new LinkedHashMap<>();


    public MyRequestMappingHandlerAdapter() {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);  // see SPR-7316

        this.messageConverters = new ArrayList<>(4);
        this.messageConverters.add(new ByteArrayHttpMessageConverter());
        this.messageConverters.add(stringHttpMessageConverter);
        this.messageConverters.add(new SourceHttpMessageConverter<>());
        this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean supports(Object handler) {
        return true;
    }

    @Override
    protected MyModelAndView handleInternal(HttpServletRequest request,
                                            HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        MyModelAndView mav;

        mav = invokeHandlerMethod(request, response, handlerMethod);

        return mav;
    }




    protected MyServletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
        return new MyServletInvocableHandlerMethod(handlerMethod);
    }
    private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
        SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.modelAttributeCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
            this.modelAttributeCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> attrMethods = new ArrayList<>();
        // Global methods first
        this.modelAttributeAdviceCache.forEach((clazz, methodSet) -> {
            if (clazz.isApplicableToBeanType(handlerType)) {
                Object bean = clazz.resolveBean();
                for (Method method : methodSet) {
                    attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
        }
        return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
    }

    private InvocableHandlerMethod createModelAttributeMethod(WebDataBinderFactory factory, Object bean, Method method) {
        InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(bean, method);
        if (this.argumentResolvers != null) {
            attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        attrMethod.setDataBinderFactory(factory);
        return attrMethod;
    }

    private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
        Class<?> handlerType = handlerMethod.getBeanType();
        SessionAttributesHandler sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
        if (sessionAttrHandler == null) {
            synchronized (this.sessionAttributesHandlerCache) {
                sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);
                if (sessionAttrHandler == null) {
                    sessionAttrHandler = new SessionAttributesHandler(handlerType, sessionAttributeStore);
                    this.sessionAttributesHandlerCache.put(handlerType, sessionAttrHandler);
                }
            }
        }
        return sessionAttrHandler;
    }

    protected MyModelAndView invokeHandlerMethod(HttpServletRequest request,
                                               HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        try {
            //@InitBinder处理
            WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
            //@ModelAttribute处理
            ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

            MyServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
            if (this.argumentResolvers != null) {
                invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
            }
            if (this.returnValueHandlers != null) {
                invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
            }
            invocableMethod.setDataBinderFactory(binderFactory);
            invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

            ModelAndViewContainer mavContainer = new ModelAndViewContainer();
            mavContainer.addAllAttributes(MyRequestContextUtils.getInputFlashMap(request));
//            modelFactory.initModel(webRequest, mavContainer, invocableMethod);
            mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);


            //执行Controller中的RequestMapping注释的方法
            invocableMethod.invokeAndHandle(webRequest, mavContainer);

            return getModelAndView(mavContainer, modelFactory, webRequest);
        }
        finally {
            webRequest.requestCompleted();
        }
    }

    @Nullable
    private MyModelAndView getModelAndView(ModelAndViewContainer mavContainer,
                                         ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {

        modelFactory.updateModel(webRequest, mavContainer);
        if (mavContainer.isRequestHandled()) {
            return null;
        }
        ModelMap model = mavContainer.getModel();
        MyModelAndView mav = new MyModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
        if (!mavContainer.isViewReference()) {
            mav.setView((MyView) mavContainer.getView());
        }
        if (model instanceof MyRedirectAttributes) {
            Map<String, ?> flashAttributes = ((MyRedirectAttributes) model).getFlashAttributes();
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request != null) {
                MyRequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
            }
        }
        return mav;
    }

    private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
        Class<?> handlerType = handlerMethod.getBeanType();
        Set<Method> methods = this.initBinderCache.get(handlerType);
        if (methods == null) {
            methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
            this.initBinderCache.put(handlerType, methods);
        }
        List<InvocableHandlerMethod> initBinderMethods = new ArrayList<>();
        // Global methods first
        this.initBinderAdviceCache.forEach((clazz, methodSet) -> {
            if (clazz.isApplicableToBeanType(handlerType)) {
                Object bean = clazz.resolveBean();
                for (Method method : methodSet) {
                    initBinderMethods.add(createInitBinderMethod(bean, method));
                }
            }
        });
        for (Method method : methods) {
            Object bean = handlerMethod.getBean();
            initBinderMethods.add(createInitBinderMethod(bean, method));
        }
        return createDataBinderFactory(initBinderMethods);
    }



    private InvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
        InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);
        if (this.initBinderArgumentResolvers != null) {
            binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
        }
        binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
        binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
        return binderMethod;
    }

    protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
            throws Exception {

        return new MyServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }

    @Nullable
    public WebBindingInitializer getWebBindingInitializer() {
        return this.webBindingInitializer;
    }
    @Override
    public void afterPropertiesSet() {
        // Do this first, it may add ResponseBody advice beans

        if (this.argumentResolvers == null) {
            //为后续参数处理做准备
            List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
            this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.initBinderArgumentResolvers == null) {
            List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
            this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
        }
        if (this.returnValueHandlers == null) {
            List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
            this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
        }
    }


    private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // Annotation-based argument resolution
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
        resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
        resolvers.add(new MyServletModelAttributeMethodProcessor(false));
        resolvers.add(new MyServletModelAttributeMethodProcessor(true));
        return resolvers;
    }

    @Nullable
    protected ConfigurableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
    /**
     * Return the list of argument resolvers to use for {@code @InitBinder}
     * methods including built-in and custom resolvers.
     */
    private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
        return null;
    }

    /**
     * Return the list of return value handlers to use including built-in and
     * custom handlers provided via {@link #}.
     */
    private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {

        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

        handlers.add(new MyModelAndViewMethodReturnValueHandler());

        handlers.add(new MyViewMethodReturnValueHandler());

        handlers.add(new  MyViewNameMethodReturnValueHandler());
        return handlers;
    }

    public static final MethodFilter INIT_BINDER_METHODS = method ->
            AnnotationUtils.findAnnotation(method, InitBinder.class) != null;

    public static final MethodFilter MODEL_ATTRIBUTE_METHODS = method ->
            ((AnnotationUtils.findAnnotation(method, RequestMapping.class) == null) &&
                    (AnnotationUtils.findAnnotation(method, ModelAttribute.class) != null));

    public void setWebBindingInitializer(@Nullable WebBindingInitializer webBindingInitializer) {


    }
}


