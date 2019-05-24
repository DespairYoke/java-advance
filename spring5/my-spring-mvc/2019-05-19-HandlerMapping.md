---
layout: post
title: springMvc请求如何获取相关HandlerMapping（四）
no-post-nav: true
category: arch
tags: [arch]
description: springMvc请求如何获取相关HandlerMapping
---

在Servlet如何关联spring中，说过当请求来后会被转发到spring`DispatcherServlet`类中的`doService`方法。
```java
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
   doDispatch(request, response);
}
```
转发到doDispatch中
```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    mappedHandler = getHandler(processedRequest);
}
```
`getHandler`获取相关
```java
protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    if (this.handlerMappings != null) {
        for (MyHandlerMapping hm : this.handlerMappings) {
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
            }
            HandlerExecutionChain handler = hm.getHandler(request);
            if (handler != null) {
                return handler;
            }
        }
    }
    return null;
}
```
可以看出，循环遍历初始化时加载的handlerMappings。在初始化时加载是`BeanNameUrlHandlerMapping`和`RequestMappingHandlerMapping`(不知道的，可以参考上篇文章`spingMvc初始HandlerMapping`)。
`BeanNameUrlHandlerMapping`不满条件返回null，而`RequestMappingHandlerMapping`满足条件，通过getHandler返回我们想要的`HandlerExecutionChain`。
```java
public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    Object handler = getHandlerInternal(request);

    HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);
    return executionChain;
}
```
`getHandlerExecutionChain`可见是对handler和request的封装，真正的处理还是如何获取到这个`handler`。
具体代码写在了`AbstractHandlerMethodMapping`中
```java
protected HandlerMethod getHandlerInternal(HttpServletRequest request) throws Exception {
    String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);

    this.mappingRegistry.acquireReadLock();
    try {
        HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
        }
        return (handlerMethod != null ? handlerMethod.createWithResolvedBean() : null);
    }
    finally {
        this.mappingRegistry.releaseReadLock();
    }
}
```
在前文分析中，handlerMethod在初始化的时候放在了，`mappingRegistry`中，由上述代码，可以看出是从mappingRegistry中，匹配请求，并返回handlerMethod。
那我们看下`lookupHandlerMethod`方法吧。
```java
protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
    List<Match> matches = new ArrayList<>();
    List<T> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
    if (directPathMatches != null) {
        addMatchingMappings(directPathMatches, matches, request);
    }
    if (matches.isEmpty()) {
        // No choice but to go through all mappings...
        addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, request);
    }

    if (!matches.isEmpty()) {
        Comparator<Match> comparator = new MatchComparator(getMappingComparator(request));
        matches.sort(comparator);
        if (logger.isTraceEnabled()) {
            logger.trace("Found " + matches.size() + " matching mapping(s) for [" + lookupPath + "] : " + matches);
        }
        Match bestMatch = matches.get(0);
        if (matches.size() > 1) {
            if (CorsUtils.isPreFlightRequest(request)) {
                return PREFLIGHT_AMBIGUOUS_MATCH;
            }
            Match secondBestMatch = matches.get(1);
            if (comparator.compare(bestMatch, secondBestMatch) == 0) {
                Method m1 = bestMatch.handlerMethod.getMethod();
                Method m2 = secondBestMatch.handlerMethod.getMethod();
                throw new IllegalStateException("Ambiguous handler methods mapped for HTTP path '" +
                        request.getRequestURL() + "': {" + m1 + ", " + m2 + "}");
            }
        }
        handleMatch(bestMatch.mapping, lookupPath, request);
        return bestMatch.handlerMethod;
    }
    else {
        return handleNoMatch(this.mappingRegistry.getMappings().keySet(), lookupPath, request);
    }
}
```
果不其然是对我们的请求路径进行`最佳匹配`。
这样我们就回去到初始化`handlerMapping`时生成的`handlerMethod`，为后续handlerAdapter做好参数准备。