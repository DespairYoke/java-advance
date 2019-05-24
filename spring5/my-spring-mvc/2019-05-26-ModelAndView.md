---
layout: post
title: SpringMvc如何处理ModelAndView（十一）
no-post-nav: true
category: arch
tags: [arch]
description: SpringMvc如何处理ModelAndView
---

### 示例
```java
@GetMapping(value = "hello")
public MyModelAndView index(String name, ModelAndView modelAndView) {
    modelAndView.addObject("message",name);
    modelAndView.setViewName("index");
    return modelAndView;
}
```

### 返回值的处理
由于返回值类型是ModelAndView,不是简单的字符串。这时返回值的处理不再使用`ViewNameMethodReturnValueHandler`，而是使用了`ModelAndViewMethodReturnValueHandler`处理的方式来处理。具体处理方法是
```java
public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

    ModelAndView mav = (ModelAndView) returnValue;
        String viewName = mav.getViewName();
        mavContainer.setViewName(viewName);
    mavContainer.setStatus(mav.getStatus());
    mavContainer.addAllAttributes(mav.getModel());
}
```
这里只是简单的把视图名(示例中的index)和mav中的`model`(示例中的message)放入`mavContainer`。那我们接着看下这个`mavContainer`后续是如何处理。
由执行顺序可以在`RequestMappingHandlerAdapter`的`invokeHandlerMethod`方法看到，下一个对mavcontainer做处理的是`getModelAndView`方法。
```java
private ModelAndView getModelAndView(ModelAndViewContainer mavContainer,ModelFactory modelFactory, NativeWebRequest webRequest) throws Exception {

    ModelMap model = mavContainer.getModel();
    ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model, mavContainer.getStatus());
    return mav;
}
```
可见mavContainer好像一个容器，把ModelAndView从返回值放入进去，现在又取出来生成新的`ModelAndView`。跟踪代码，看下新的`ModelAndView`又被何时处理。发现是在`DispatcherServlet`的`doDispatch`方法中的`processDispatchResult`方法中进行处理。
```java
private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
@Nullable MyHandlerExecutionChain mappedHandler, @Nullable MyModelAndView mv,Exception exception) throws Exception {
    // 获取视图view
    render(mv, request, response);
}
```
此时就调用了一个`render`方法。
```java
protected void render(MyModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
  //获取适合的view
  view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
  view.render(mv.getModelInternal(), request, response);
}
```
* `resolveViewName`获取适合的`view`
* `render`视图渲染页面。

```java
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
        prepareResponse(request, response);
        renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
    }
```
* `createMergedOutputModel`创建了一个Map对象`mergedModel`，把model中的信息放入新对map对象`mergedModel`中。
* `renderMergedOutputModel`是对新map`mergedModel`处理。

```java
protected void renderMergedOutputModel(
        Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        exposeModelAsRequestAttributes(model, request);
 String dispatcherPath = prepareForRendering(request, response);
        RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
    rd.forward(request, response);
}
```
* exposeModelAsRequestAttributes 是把model中的信息放入request中
```java
protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.forEach((modelName, modelValue) -> {
          request.setAttribute(modelName, modelValue); 
        });
    }
```
* prepareForRendering 根据示例中的`index`值，找到对应的jsp路径。
* rd.forward 只是对request的转发。

总结：spring只是把`ModelandView`中的信息经过一系列处理，最终放入了`request`中，交给Servlet去处理。