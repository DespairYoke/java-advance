---
layout: post
title: SpringMvc初始化handlerMapping（二）
no-post-nav: true
category: arch
tags: [arch]
description: SpringMvc初始化handlerMapping
---

前面我们分析了spring如何接入Servlet，执行onRefresh方法，而HandlerMapping的初始化就在OnRefresh中调用。
```java
    protected void onRefresh(ApplicationContext context) {
        System.out.println("=======dispatherServlet onfresh");
        initStrategies(context);
    }
```
`initStrategies`方法是所有初始化的集合
```java
protected void initStrategies(ApplicationContext context) {
    initMultipartResolver(context);
    initLocaleResolver(context);
    initThemeResolver(context);
    initHandlerMappings(context);
    initHandlerAdapters(context);
    initHandlerExceptionResolvers(context);
    initRequestToViewNameTranslator(context);
    initViewResolvers(context);
    initFlashMapManager(context);
}
```
这里我们只关注`initHandlerMappings(context)`。
```java
private void initHandlerMappings(ApplicationContext context) {
  this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
}
```
我们忽略其他直接看重点`getDefaultStrategies`,此函数是加载所有的`HandlerMapping.class`.
```java
protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
    String key = strategyInterface.getName();
	String value = defaultStrategies.getProperty(key);
    for (String className : classNames) {
            Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
            Object strategy = createDefaultStrategy(context, clazz); 
    }
}
```
* `defaultStrategies.getProperty(key)`是通过加载DispatcherServlet同级目录下的`DispatcherServlet.properties`文件，获取需要执行的实现类。properties文件内容如下
```properties
org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
```
由文件内容可知获取后类是`BeanNameUrlHandlerMapping`和`RequestMappingHandlerMapping`.
*  `createDefaultStrategy`对获取后的class进行进行bean实例化。
```java
protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
    return context.getAutowireCapableBeanFactory().createBean(clazz);
}
```
实例化时，我们可以注意到一个细节是`InitializingBean`类的中的`afterPropertiesSet`方法回调。
```java
protected void invokeInitMethods(String beanName, final Object bean, @Nullable RootBeanDefinition mbd)
        throws Throwable {
       if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
       ((InitializingBean) bean).afterPropertiesSet();
       }   
}
```
而`BeanNameUrlHandlerMapping`和`RequestMappingHandlerMapping`中，只有`RequestMappingHandlerMapping`满足afterPropertiesSet。也正是我们想要的执行回调。
```java
@Override
public void afterPropertiesSet() {
    this.config = new RequestMappingInfo.BuilderConfiguration();
    this.config.setUrlPathHelper(getUrlPathHelper());
    this.config.setPathMatcher(getPathMatcher());
    this.config.setSuffixPatternMatch(this.useSuffixPatternMatch);
    this.config.setTrailingSlashMatch(this.useTrailingSlashMatch);
    this.config.setRegisteredSuffixPatternMatch(this.useRegisteredSuffixPatternMatch);
    this.config.setContentNegotiationManager(getContentNegotiationManager());

    super.afterPropertiesSet();
}
```
* useSuffixPatternMatch 默认为true，作用是匹配`url.*`
* useTrailingSlashMatch 默认为true, 作用是尾部加上`/`

重点在`super.afterPropertiesSet();`
```java
@Override
public void afterPropertiesSet() {
    initHandlerMethods();
}
```
回调的目的是初始化所有的方法`HandlerMethods`
```java
protected void initHandlerMethods() {
    for (String beanName : beanNames) {
        if (beanType != null && isHandler(beanType)) {
            detectHandlerMethods(beanName);
        }
    }
    handlerMethodsInitialized(getHandlerMethods());
}
```
* `isHandler`方法时对bean类型进行判断
```java
@Override
protected boolean isHandler(Class<?> beanType) {
    return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
            AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
}
```
* `detectHandlerMethods`是对满足条件的bean进行处理。
```java
protected void detectHandlerMethods(final Object handler) {
    Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
            (MethodIntrospector.MetadataLookup<T>) method -> {
                    return getMappingForMethod(method, userType);
                    } //处理类中所有的方法
        methods.forEach((method, mapping) -> {
            Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
            registerHandlerMethod(handler, invocableMethod, mapping);//把每个处理过后的method,和处理时的相关信息mapping保存
        });
    }
}
```
下面看下spring如何对method进行处理的。
```java
protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
    RequestMappingInfo info = createRequestMappingInfo(method);
    if (info != null) {
        RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
        if (typeInfo != null) {
            info = typeInfo.combine(info);
        }
    }
    return info;
}
```
原来spring创建了一个`RequestMappingInfo`来保存method相关信息。具体什么信息，跟踪到`createRequestMappingInfo`中。
```java
protected RequestMappingInfo createRequestMappingInfo(
        RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {

    RequestMappingInfo.Builder builder = RequestMappingInfo
            .paths(resolveEmbeddedValuesInPatterns(requestMapping.path()))
            .methods(requestMapping.method())
            .params(requestMapping.params())
            .headers(requestMapping.headers())
            .consumes(requestMapping.consumes())
            .produces(requestMapping.produces())
            .mappingName(requestMapping.name());
    if (customCondition != null) {
        builder.customCondition(customCondition);
    }
    return builder.options(this.config).build();
}
```
原来是储存`@RequestMapping`注解的信息，如常用的`value=hello`
* 再回到`detectHandlerMethods`方法，看下`registerHandlerMethod`方法
```java
protected void registerHandlerMethod(Object handler, Method method, T mapping) {
    this.mappingRegistry.register(mapping, handler, method);
}
```
发现spring用一个名为`mappingRegistry`类，存储mapping,handler和method。
到此initHandlerMapping就完成了所有操作。那我们发挥自己的想象，当请求来时，一定会来mappingRegistry进行匹配相关信息。
后续会有相关介绍，请等待下文。