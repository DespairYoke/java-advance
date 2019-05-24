---
layout: post
title: SpringMvc初始化HandlerAdapters（五）
no-post-nav: true
category: arch
tags: [arch]
description: springMvc初始化HandlerAdapters
---
## 从onRefresh入手
```java
protected void onRefresh(ApplicationContext context) {
    initStrategies(context);
}
```

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
`initHandlerAdapters`在`initStrategies`方法中被调用。
```java
private void initHandlerAdapters(ApplicationContext context) {

    this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);

}
```
和`handlerMapping`的处理方式类似，都是从`DispatcherServlet.properties`文件中加载要处理的类型。
```properties
org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
	org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
	org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter
```
从配置文件中发现有三个adapter供给我们处理。
下面对每个类是否有`afterPropertiesSet`方法回调。
* HttpRequestHandlerAdapter 没有
* SimpleControllerHandlerAdapter 没有
* RequestMappingHandlerAdapter 有

所以只有`RequestMappingHandlerAdapter`回调了`afterPropertiesSet`方法。
```java
public void afterPropertiesSet() {
    // Do this first, it may add ResponseBody advice beans
    initControllerAdviceCache();

    if (this.argumentResolvers == null) {
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
```
这里只是填充了`argumentResolvers`、`initBinderArgumentResolvers`和`returnValueHandlers`，这些参数都是对参数和返回值的处理集合，后续文章会做详情介绍。