---
layout: post
title: SpringMvc初始化ViewResolver（九）
no-post-nav: true
category: arch
tags: [arch]
description: SpringMvc初始化ViewResolver
---

## ViewResolver

### ViewResolver的作用
```java
public interface ViewResolver {
	@Nullable
	View resolveViewName(String viewName, Locale locale) throws Exception;

}
```
viewResovler只有一个方法`resolveViewName`，由参数观察，可以得知，是对返回后的视图名称，和使用本地环境进行处理。下面引入使用示例供参考.
### 示例
```java
@GetMapping(value = "hello")
public String index(String name) {
    return "index";
}
```
其中`index`就是参数中的viewName。

## onRefresh初始化
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
`initViewResolvers`在`initStrategies`方法中被调用。

```java
private void initViewResolvers(ApplicationContext context) {
    this.viewResolvers = null;
    //加载app-context.xml中配置的viewResolver
    if (this.detectAllViewResolvers) {
        // Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
        Map<String, MyViewResolver> matchingBeans =
                BeanFactoryUtils.beansOfTypeIncludingAncestors(context, MyViewResolver.class, true, false);
        if (!matchingBeans.isEmpty()) {
            this.viewResolvers = new ArrayList<>(matchingBeans.values());
            // We keep ViewResolvers in sorted order.
         AnnotationAwareOrderComparator.sort(this.viewResolvers);
        }
    }
    //如果没有配置的viewResolver使用配置文件默认配置
    if (this.viewResolvers == null) {
        this.viewResolvers = getDefaultStrategies(context, MyViewResolver.class);
    }
}
```
该方法大致是先从bean工厂中拿我们的视图，也就是我们在app-context.xml配置的视图解析器，如果没有找到则调用`DispatcherServlet.properties`中配置的viewResolver。
```xml
<bean id="ViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/jsp/"/>
    <property name="suffix" value=".jsp"/>
</bean>
```
我这里配置了viewResolver,所以会从bean工厂中拿到这个class。