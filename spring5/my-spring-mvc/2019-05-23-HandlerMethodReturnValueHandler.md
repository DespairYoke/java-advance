---
layout: post
title: SpringMvc中HandlerMethodReturnValueHandler对结果处理（八）
no-post-nav: true
category: arch
tags: [arch]
description: SpringMvc中HandlerMethodReturnValueHandler对结果处理
---

#### 示例
```java
@Controller
public class HelloController {

    @GetMapping(value = "hello")
    public String index(String name) {
        System.out.println(name);
        return "index";
    }

}
```
请求示例： http://localhost:8080/spring-mvc/hello?name=zwd

我们知道当浏览器发来请求时，我们会去`HandlerMapping`中找到对应的请求地址，获得响应的处理方法如`hello`,封装在`HandlerExecutionChain`中，而`HandlerExecutionChain`中有个`HandlerMethod`，是需要处理的对象。再根据`HandlerMethod`找到对应的`HandlerAdapter`去处理请求。经过一系列处理，最后请求转发到`ServletInvocableHandlerMethod`中。可以从`RequestMappingHandlerAdapter`看到下面这段代码。
```java
ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
```
下面我们跳过其他，转到`ServletInvocableHandlerMethod`。ServletInvocableHandlerMethod应该同时负责处理返回值的工作。再往下看可以发现，是通过
```java
public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,Object... providedArgs) throws Exception {
    this.returnValueHandlers.handleReturnValue(
            returnValue, getReturnValueType(returnValue), mavContainer, webRequest);
}
```
这个方法对HandlerMethod进行处理的，再跟踪下去，发现了下面的方法：
```java
public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
        ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
    HandlerMethodReturnValueHandler handler = selectHandler(returnValue, returnType);
    handler.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
}
```
* `selectHandler`选能够处理当前结果的HandlerMethodReturnValueHandler，由于示例是简单的请求方式，此处选择为`ViewNameMethodReturnValueHandler`。

```java
public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
    if (returnValue instanceof CharSequence) {
        String viewName = returnValue.toString();
        mavContainer.setViewName(viewName);
        if (isRedirectViewName(viewName)) {
            mavContainer.setRedirectModelScenario(true);
        }
    }
}
```
可以看到只是把returnValue放入`mavContrainer`的`viewName`中。
