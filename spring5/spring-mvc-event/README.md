## Spring Framework 事件发布

### 定义事件
```java
public class MyEvent extends ApplicationEvent {

    private String message;
    public MyEvent(Object source,String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```

### 定义事件监听器
```java
@Component
public class MyEventListener implements ApplicationListener<MyEvent> {

    Log log = LogFactory.getLog(getClass());
    @Override
    public void onApplicationEvent(MyEvent event) {
        log.info("事件触发"+ event.getMessage());
    }
}
```
此事件监听器监听的是我们自定义的事件`MyEvent`。

### 事件发布
由于监听器使用注解的方式进行添加，我们就无需手动添加监听器，只需要关注事件发布即可。
```java
@Component
public class EventPulish {

    @Autowired
    private ApplicationContext applicationContext;

    public void publish(String message) {
        applicationContext.publishEvent(new MyEvent(this,message));
    }
}
```
### 配置文件或配置类的配置
都知道spring注解的加载要么是基于xml配置文件的方法，要么是基于注解驱动的方式，这里使用注解比较方便。
```java
@Configuration
@ComponentScan("com.zwd.event")
public class Configration {
}
```

### 启动项目
```java
public class EventBootstrap {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext act = new AnnotationConfigApplicationContext(Configration.class);

        EventPulish eventPulish = act.getBean(EventPulish.class);
        eventPulish.publish("我叫张三！");
    }
}
```