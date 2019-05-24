---
layout: post
title: SpringMvc从Servlet到DispatcherServlet（一）
no-post-nav: true
category: arch
tags: [arch]
description: springMvc从Servlet到DispatcherServlet
---

DispatcherServlet最主要的功能函数是`onRefresh` 和 `doService`。

* onRefresh
```java
@Override
protected void onRefresh(ApplicationContext context) {
    initStrategies(context);
}
```
* doService
```java
@Override
protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {

    request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
    request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
    request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
    request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
    doDispatch(request, response);
}
```
下面讲述spring是如何调用这俩个函数的。
### Servlet
从jsr规范中，可以看出

```java
A servlet is managed through a well defined life cycle that defines how it is loaded
and instantiated, is initialized, handles requests from clients, and is taken out of
service. This life cycle is expressed in the API by the init, service, and destroy
methods of the javax.servlet.Servlet interface that all servlets must implement
directly or indirectly through the GenericServlet or HttpServlet abstract classes.
```
我们想要使用Servlet，就必须实现这个两个类，而spring也是如此
```java
public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable {
}
```
HttpServlet中有个`init`方法，当一个请求来时，会触发servlet的生命周期并调用init方法进行初始化。而spring重写了init方法
```java
public final void init() throws ServletException {


    /**
     * 把web.xml中的init-param中的key value拿到
     */
    PropertyValues pvs = new MyServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
    /**
     * 当<servlet>没有参数不走此if</servlet>
     */
    if (!pvs.isEmpty()) {
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
        ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
        bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
        initBeanWrapper(bw);
        bw.setPropertyValues(pvs, true);
    }

    initServletBean();
}
```
* BeanWrapper是对`contextConfigLocation`进行处理。关键在`initServletBean`中。
进入`initServletBean`中查看
```java
protected final void initServletBean() throws ServletException {
    //...
    this.webApplicationContext = initWebApplicationContext();
    //...
}
```
初始化bean的关键是初始化`WebApplicationContext`。
而WebApplicationContext又做了如下事情
```java
protected WebApplicationContext initWebApplicationContext() {
    WebApplicationContext rootContext =
         WebApplicationContextUtils.getWebApplicationContext(getServletContext()); //null
    WebApplicationContext wac = null;
if (wac == null) {
wac = createWebApplicationContext(rootContext);
}
}
```
首次加载显然不可能有rootContext的，所以这里还要深入`createWebApplicationContext`中。
```java
protected WebApplicationContext createWebApplicationContext(@Nullable WebApplicationContext parent) {
return createWebApplicationContext((ApplicationContext) parent);
}
```
```java
protected WebApplicationContext createWebApplicationContext(@Nullable ApplicationContext parent) {
/**
 * 获取加载方式，默认为{@link XmlWebApplicationContext}
 */
Class<?> contextClass = getContextClass();

ConfigurableWebApplicationContext wac =
        (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);

/**
 * 获取环境 {@link StandardEnvironment}
 */
wac.setEnvironment(getEnvironment());  //StandardServletEnvironment
wac.setParent(parent);            //null
String configLocation = getContextConfigLocation();  //null
if (configLocation != null) {
    wac.setConfigLocation(configLocation);
}
configureAndRefreshWebApplicationContext(wac);

return wac;
}
```
可见这个WebApplicationContext默认使用的是`XmlWebApplicationContext`。使用此上下文进行web解析。
```java
protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
wac.addApplicationListener(new SourceFilteringListener(wac, new ContextRefreshListener()));//添加一个事件监听器
    wac.refresh();
}
```
这里终于看到refresh()的影子，我们继续往下走，发现来到了`AbstractApplicationContext`中，一大堆代码springbean代码，无从下手。
```java
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        prepareRefresh();
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
        prepareBeanFactory(beanFactory);
        try {
            postProcessBeanFactory(beanFactory);
            invokeBeanFactoryPostProcessors(beanFactory);
            registerBeanPostProcessors(beanFactory);
            initMessageSource();
            initApplicationEventMulticaster();
            onRefresh();

            registerListeners();

            finishBeanFactoryInitialization(beanFactory);

            finishRefresh();
        }

```
这里利用断点的形式来逆向查看代码执行过程。发现在`finishRefresh`中执行了事件发布`publishEvent(new ContextRefreshedEvent(this));`而这个发布对象是我们在`configureAndRefreshWebApplicationContext`中添加的事件监听器。所以执行完后会回调`ContextRefreshListener`中的`onApplicationEvent`方法
```java
private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        FrameworkServlet.this.onApplicationEvent(event);
    }
}
```
而`onApplicationEvent`中调用的则是我们朝思暮想的onRefresh函数。
```java
public void onApplicationEvent(ContextRefreshedEvent event) {
    this.refreshEventReceived = true;
    onRefresh(event.getApplicationContext());
}
```
### doService的来历
所有的请求tomcat会路有转发到Servlet的`service`方法。
spring则在FrameworkServlet中重写了service方法。
```java
protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
         processRequest(request, response);
}
```
对service请求近一步处理
```java
protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doService(request, response);
}        
```
而`doService`被`DispatcherServlet`重写。到这里doService就回被调用，进行对应的请求处理。
