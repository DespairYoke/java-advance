# 手写spring ApplicationListener

### 模拟spring servlet触发FrameworkServlet

```java
    @Test
    public void test() {

        FrameworkServlet frameworkServlet = new FrameworkServlet();
        frameworkServlet.configureAndRefreshWebApplicationContext();
        frameworkServlet.abstractApplicationContext.onRefresh();
    }
```

#### new FrameworkServlet()

在frameworkServlet的构造中，初始化了`AbstractApplicationContext`

```java
 public FrameworkServlet() {
        this.abstractApplicationContext = new AbstractApplicationContext();
    }
```

#### frameworkServlet.configureAndRefreshWebApplicationContext()

configureAndRefreshWebApplicationContext方法中进行了监听器创建和添加。

```java
  public void configureAndRefreshWebApplicationContext() {
       abstractApplicationContext.addApplicationListener(new SourceFilteringListener(abstractApplicationContext,new FrameworkServlet.ContextRefreshListener()));
    }
```

可以看到在`abstractApplicationContext`添加了一个监听器，那`abstractApplicationContext`中定义了对`listener`的处理，看下添加方法如何实现。

```java
  private List<ApplicationListener> listeners = new CopyOnWriteArrayList<>();
    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {

        listeners.add(listener);
    }
```

可以看出，是把监听器房到一个list集合中。

再回到方法`configureAndRefreshWebApplicationContext`中，看到添加的监听器是，`SourceFilteringListener`,	参数是`abstractApplicationContext`,`ContextRefreshListener`，而在sourceFilteringListener的构造函数中，又进行了参数处理。

```java
 public SourceFilteringListener(Object source,ApplicationListener<?> delegate) {
        this.source = source;
        this.delegate = new GenericApplicationListenerAdapter(delegate);
    }
```

也就是说`SourceFilteringListener`只是转发我们的监听器，真正的处理方法，在`GenericApplicationListenerAdapter`	中。

```java
private final ApplicationListener<ApplicationEvent> delegate;

    public GenericApplicationListenerAdapter(ApplicationListener<?> delegate) {
        this.delegate = (ApplicationListener<ApplicationEvent>)delegate;
    }
```

#### frameworkServlet.abstractApplicationContext.onRefresh()

```java
public void onRefresh() {
  for (ApplicationListener listener : listeners) {
    if (((GenericApplicationListener)listener).supportsSourceType(getClass())){
      listener.onApplicationEvent(new ContextRefreshedEvent());
    }

  }
}
```

循环遍历添加的监听器，判断监听器是否支持处理。

```java
public boolean supportsSourceType(@Nullable Class<?> sourceType) {
  return (sourceType != null && sourceType.isInstance(this.source));
}
```

如果支持，则进行事件处理。又上述代码可知，`listeners`集合中只存储了一个对象`SourceFilteringListener`，所以这时执行的具体实现应该是`SourceFilteringListener`中的onApplicationEvent。

```java
   public void onApplicationEvent(ApplicationEvent event) {
        delegate.onApplicationEvent(event);
    }
```

由上述`SourceFilteringListener`的构造可知，这是的`delegate`是构造中的`GenericApplicationListenerAdapter`,所以执行``GenericApplicationListenerAdapter`中的`onApplicationEvent`。

```java
 public void onApplicationEvent(ApplicationEvent event) {
        delegate.onApplicationEvent(event);
    }
```

由`GenericApplicationListenerAdapter`的构造可知，`delegate`是一开始传递的`FrameworkServlet.ContextRefreshListener()`，所以这是执行`ContextRefreshListener`中的onApplicationEvent的事件。

```java
private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
  public ContextRefreshListener() {
  }
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    onrefresh(event);
  }
}
```

可以看出，从`FrameworkSerlvet`出发，又绕回到了`FrameworkServlet`，这即是Spring设计的神奇之处，让我们初始化其他事情后，再回调开始的方法，继续执行。

[项目地址](https://github.com/DespairYoke/java-advance/tree/master/spring5/spring-applicationlistener)