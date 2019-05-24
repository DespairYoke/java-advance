---
layout: post
title: HandlerAdapter handle方法使用（六）
no-post-nav: true
category: arch
tags: [arch]
description: HandlerAdapter handle方法使用
---

从`doService`方法开始查看
```java
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
    doDispatch(request, response);
}

```
前面都是必要参数绑定，`doDispatch`是我们要看的重点。
```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Exception dispatchException = null;
    HandlerExecutionChain mappedHandler = null;
    ModelAndView mv = null;
    HttpServletRequest processedRequest = request;
    mappedHandler = getHandler(processedRequest);
    MyHandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
//
    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

    processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
}
```
结合上述文章，这里就很容易看懂了。
* `getHandler`是根据请求，从`mappingRegisty`获取到和请求相关的`HandlerExecutionChain`。而`HandlerExecutionChain`中有个Object对象`handler`，其实这个对象就是`HandlerMethod`。再根据`HandlerMethod`获取能够处理此对象的`HandlerAdapter`，进行handle方法调用。
```java
public final ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {

    return handleInternal(request, response, (HandlerMethod) handler);
}
```
handle方法是使用了适配器模式，放在了`AbstractHandlerMethodAdapter`抽象类中，不同的具体实现类有不同的实现方式。
```java
protected ModelAndView handleInternal(HttpServletRequest request,
                                        HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

    MyModelAndView mav;

    mav = invokeHandlerMethod(request, response, handlerMethod);

    return mav;
}
```
`invokeHandlerMethod`又名称可知，应该是对方法的调用。
```java
protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
                                           HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {

    ServletWebRequest webRequest = new ServletWebRequest(request, response);
    try {
        //@InitBinder处理
        WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
        //@ModelAttribute处理
        ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

        ServletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);
        if (this.argumentResolvers != null) {
            invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
        }
        if (this.returnValueHandlers != null) {
            invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
        }
        invocableMethod.setDataBinderFactory(binderFactory);
        invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
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
```
* `getDataBinderFactory`是对注解`@InitBinder`处理。
* `getModelFactory`是对注解`@ModelAttribute`处理。
* `setHandlerMethodArgumentResolvers`中添加的`argumentResolvers`正式我们在`RequestMappingHandlerMapping`中初始化时回调`aftePropertiesSet`赋值的处理方式。
* `returnValueHandlers`也是如此，是对返回值的处理方式。

关键的执行方法是`ServletInvocableHandlerMethod`的invokeAndHandle方法。

```java
public void invokeAndHandle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
                            Object... providedArgs) throws Exception {

    Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
    setResponseStatus(webRequest);

    if (returnValue == null) {
        if (isRequestNotModified(webRequest) || getResponseStatus() != null || mavContainer.isRequestHandled()) {
            mavContainer.setRequestHandled(true);
            return;
        }
    }
    else if (StringUtils.hasText(getResponseStatusReason())) {
        mavContainer.setRequestHandled(true);
        return;
    }

    mavContainer.setRequestHandled(false);
    Assert.state(this.returnValueHandlers != null, "No return value handlers");
  
    this.returnValueHandlers.handleReturnValue(
            returnValue, getReturnValueType(returnValue), mavContainer, webRequest);

}
```
通过`invokeForRequest`方法执行具体方法，并获取执行后的返回值，这里看下示例来说明：
```java
@GetMapping(value = "hello")
public String index(String name) {
    System.out.println(name);
    return "index";
}
```
这里`returnValue`就是返回的index。
而`returnValueHandlers`就是对返回值做处理，处理的结果放入`mavContaioner`中，供后续的`getModelAndView`方法使用。（请求时，是如何处理参数，是如何对返回值进行处理，后续做详细介绍）。
```java
private MyModelAndView getModelAndView(ModelAndViewContainer mavContainer,ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {
    modelFactory.updateModel(webRequest, mavContainer);
    ModelMap model = mavContainer.getModel();
    MyModelAndView mav = new MyModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
    return mav;
}
```
这里其实就是创建了一个`ModelAndView`。

