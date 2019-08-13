# SpringMvc事件发布处理流程

#### 首先我们从熟悉的`DispatcherServlet`说起

熟悉springmvc的人都知道(不熟悉的，可以[参考](http://zwd.ccxst.cn/spring-mvc-book/chapter1.html),`DispatcherServlet`中的`onRefresh`是通过事件回调执行的。那我们就看一下这个事件回调流程。

#### FrameworkServlet

根据`onRefresh`方法的执行回调是`ContextRefreshListener`监听的`ContextRefreshedEvent`事件触发的`onApplicationEvent`方法。而`ContextRefreshListener`在FrameworkServlet中的`configureAndRefreshWebApplicationContext`中加载，如下

```java
protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {

    wac.addApplicationListener(new SourceFilteringListener(wac, new FrameworkServlet.ContextRefreshListener()));
}
```

添加方法具体的实现在`AbstractApplicationContext`中

```java
@Override
public void addApplicationListener(ApplicationListener<?> listener) {
        this.applicationListeners.add(listener);
}
```

由此可以看到事件交给了`AbstractApplicationContext`处理。那进一步看下是如何处理listener。

#### AbstractApplicationContext

提到AbstractApplicaitonContext，自然而然的都不想到它最重要的方法`refresh`方法了，这个方法完成了很多初始化和回调等，是必执行的方法。
由上面的FrameworkServlet的引入，可以看到listener中是有我们的contextRefreshListener的，再查询发现使用listener的地方只有`registerListeners`。

```java
protected void registerListeners() {
    // Register statically specified listeners first.
    for (ApplicationListener<?> listener : getApplicationListeners()) {
        getApplicationEventMulticaster().addApplicationListener(listener);
    }
```

`getApplicationEventMulticaster`返回的是一个`ApplicationEventMulticaster`对象，而添加方法的实现在，`AbstractApplicationEventMulticaster`中实现

```java
@Override
public void addApplicationListener(ApplicationListener<?> listener) {
    synchronized (this.retrievalMutex) {
        this.defaultRetriever.applicationListeners.add(listener);
        this.retrieverCache.clear();
    }
}
```

添加到了`ListenerRetriever`中的`applicationListeners`set集合中。

由于AbstractApplicationContext中只有`registerListeners`对listener进行了添加处理，所以后续的listener处理交给了`ApplicationEventMulticaster`。

#### ApplicationEventMulticaster

`ContextRefreshedEvent`事件的触发是在`AbstractApplicationContext`中的`finishRefresh`方法中

```java
protected void finishRefresh() {

    // Publish the final event.

    publishEvent(new ContextRefreshedEvent(this));

}
```

而`multicastEvent`方法的实现是在`SimpleApplicationEventMulticaster`中

```java
public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
    ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
    for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
        Executor executor = getTaskExecutor();
        if (executor != null) {
            executor.execute(() -> invokeListener(listener, event));
        }
        else {
            invokeListener(listener, event);
        }
    }
}
```

下面我从`getApplicationListeners`方法中，看是否能找到一开始添加的listener对象。

```
protected Collection<ApplicationListener<?>> getApplicationListeners(
        ApplicationEvent event, ResolvableType eventType) {

      Collection<ApplicationListener<?>> listeners =
                            retrieveApplicationListeners(eventType, sourceType, retriever);
    return listeners;
}
```

而`retrieveApplicationListeners`中的代码如下

```
	private Collection<ApplicationListener<?>> retrieveApplicationListeners(
			ResolvableType eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever) {

		LinkedList<ApplicationListener<?>> allListeners = new LinkedList<>();
		Set<ApplicationListener<?>> listeners;
		Set<String> listenerBeans;
		synchronized (this.retrievalMutex) {
			listeners = new LinkedHashSet<>(this.defaultRetriever.applicationListeners);
			listenerBeans = new LinkedHashSet<>(this.defaultRetriever.applicationListenerBeans);
		}
        for (ApplicationListener<?> listener : listeners) {
                if (supportsEvent(listener, eventType, sourceType)) {
                    if (retriever != null) {
                        retriever.applicationListeners.add(listener);
                    }
                    allListeners.add(listener);
                }
            }
		return allListeners;
	}
```

这`defaultRetriever`就是我们一开始添加的`ListenerRetriever`对象，也就和上述的代码关联了起来。

总结：

首先我们要创建一个事件，把事件放入监听器中，然后把监听器交给Spring，等待Spring初始化完成后，触发我们的自定义事件，执行监听器中的方法。

[基于注解的使用示例](*https://github.com/DespairYoke/java-advance/blob/master/spring5/spring-event.md*)