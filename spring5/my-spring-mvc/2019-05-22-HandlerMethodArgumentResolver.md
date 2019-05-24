---
layout: post
title: SpringMvc如何实现参数填充HandlerMethodArgumentResolver（七）
no-post-nav: true
category: arch
tags: [arch]
description: SpringMvc如何实现参数填充HandlerMethodArgumentResolver
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
invocableMethod.invokeAndHandle(webRequest, mavContainer);
```
这个方法对HandlerMethod进行处理的，再跟踪下去，发现了下面的方法：
```java
Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
```
至此，已经完成了对hello方法的调用，毫无疑问，参数赋值应该就是在invokeForRequest方法中了，再看下该方法的代码，终于发现了获取参数值的方法：
```java
Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
```
进入`getMethodArgumentValues`
```java
if (this.argumentResolvers.supportsParameter(parameter)) {
    try {
        args[i] = this.argumentResolvers.resolveArgument(
                parameter, mavContainer, request, this.dataBinderFactory);
        continue;
    }
    catch (Exception ex) {
        if (logger.isDebugEnabled()) {
            logger.debug(getArgumentResolutionErrorMessage("Failed to resolve", i), ex);
        }
        throw ex;
    }
}
```
发现里面有个`argumentResolvers`，根据名字也可以看出是对参数的处理集合，而supportsParameter方法则是检测改参数类型`parameter`该由去处理。
值得一提的是springMvc中用到了一个类做为所有的参数处理实现类的集合`HandlerMethodArgumentResolverComposite`。循环判断是否支持和进行处理，也放在此类中。所有的实现类，是在初始化adaper进行填充的。代码可以在`RequestMappingHandlerAdpter`中看到
```java
public void afterPropertiesSet() {
// Do this first, it may add ResponseBody advice beans

if (this.argumentResolvers == null) {
    List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
    this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
}
}       
```
使用debug方式，发现最终处理的实现类是`RequestParamMethodArgumentResolver`中的resolveArgument方法
```java
public final Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

    NamedValueInfo namedValueInfo = getNamedValueInfo(parameter);
    MethodParameter nestedParameter = parameter.nestedIfOptional();

    Object resolvedName = resolveStringValue(namedValueInfo.name);
    if (resolvedName == null) {
        throw new IllegalArgumentException(
                "Specified name must not resolve to null: [" + namedValueInfo.name + "]");
    }

    Object arg = resolveName(resolvedName.toString(), nestedParameter, webRequest);
    //...
}   
```
* resolvedName 是参数名称，示例中的name参数.
* arg是参数名称对应的值，根据示例浏览器请求可知是zwd.