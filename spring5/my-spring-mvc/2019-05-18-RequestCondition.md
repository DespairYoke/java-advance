---
layout: post
title: SpringMvc中RequestCondition的作用（三）
no-post-nav: true
category: arch
tags: [arch]
description: sSpringMvc中RequestCondition的作用
---

### 初始化
springmvc初始化时，会回调`RequestMappingHandlerMapping`中的afterPropertiesSet方法。
```java
@Override
public void afterPropertiesSet() {
    initHandlerMethods();
}
```
追踪到`initHandlerMethods`中
```java
if (beanType != null && isHandler(beanType)) {
    detectHandlerMethods(beanName);
}
```
* isHander(beanType)见名知意，是对beanType类型判断，具体实现代码为
```java
protected boolean isHandler(Class<?> beanType) {
    return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
            AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
}
```
最关键的是`detectHandlerMethods(beanName)`
负责对所有的controller类进行处理
```java
Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
    (MethodIntrospector.MetadataLookup<T>) method -> {
            return getMappingForMethod(method, userType);
    });
```
* method为方法名，userType为当前处理的类。遍历该类中的所有方法，对方法进行参数路径请求方式等就行处理。
下面看下`getMappingForMethod`是如何对不同方法进行处理的。
```java
protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
    RequestMappingInfo info = createRequestMappingInfo(method);
    if (info != null) {
        RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType); //handlerType 是类 这里为了解决类上的@RequestMapping使用
        if (typeInfo != null) {
            info = typeInfo.combine(info);
        }
    }
    return info;
}
```

可以看出为每个方法都创建了一个RequestMappingInfo，而RequestMappingInfo正是`RequestCondition`集合，记录了请路径、参数、方式、头等。可以看下对象内容

```java
private final MyPatternsRequestCondition patternsCondition;

private final MyRequestMethodsRequestCondition methodsCondition;

private final MyParamsRequestCondition paramsCondition;

private final MyHeadersRequestCondition headersCondition;

private final MyConsumesRequestCondition consumesCondition;

private final MyProducesRequestCondition producesCondition;

private final MyRequestConditionHolder customConditionHolder;
```

获取后的RequestMappingInfo会注册到HandlerMethod中

```java
methods.forEach((method, mapping) -> {
    Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
    registerHandlerMethod(handler, invocableMethod, mapping);
});
```

最终注册到`mappingRegistry`中

```java
protected void registerHandlerMethod(Object handler, Method method, T mapping) {
    this.mappingRegistry.register(mapping, handler, method);
    System.out.println("ssss");
}
```

### 客户端发起请求

当客户端发起请求后，tomcat的servlet会转发到`DispatcherServlet`的servce函数回调。经过`doDispatch`,`getHandler`,再到`AbstractHandlerMapping`的getHandler，`getHandlerInternal`,`lookupHandlerMethod`
看下`lookupHandlerMethod`代码

```java
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        List<Match> matches = new ArrayList<>();
        List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
        }
```

这时，看到了我们初始化时，加载的`mappingRegistry`，这时就能匹配成功，找到对应的方法。