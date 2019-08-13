
## 注解驱动@Enable模块的使用

@Enable注解，通常是和@Import结合使用，@Import有两种导入方式
```java
一；导入带有@Configuration 注解的类
二：导入实现 ImportSelector 和 ImportBeanDefinitionRegistrar 的类
```

### @Configuration
```java
@Configuration
public class HelloWorldConfigration {

    @Bean
    String name(){
        return "我是name";
    }
}
```

创建Enable注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(HelloWorldConfigration.class)
public @interface EnableHelloWorld {
}
```
启动类配置
```java
@EnableHelloWorld
public class BootStrap {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BootStrap.class);
        context.refresh();
        Object name = context.getBean("name");
        System.out.println(name);
        context.close();
    }
}
```

### 实现接口方式
示例：实现Http,Ftp动态选择
#### ImportSelector
定义接口
```java
public interface Server {

    void start();

    void close();
}
```

分别实现
```java
public class FtpServer implements Server {

    @Override
    public void start() {
        System.out.println("ftpServer 启动.....");
    }

    @Override
    public void close() {
        System.out.println("ftpServer 关闭.....");
    }
}

```

```java
public class HttpServer implements Server{
    @Override
    public void start() {
        System.out.println("httpServer 启动.....");
    }

    @Override
    public void close() {
        System.out.println("httpServer 关闭.....");
    }
}

```
@Enable模块实现
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ServerImportSelector.class)
public @interface EnableServer {

    String value() default "ftpServer";
}
```
选择器的实现
```java
public class ServerImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {

        MultiValueMap<String, Object> map = metadata.getAllAnnotationAttributes(EnableServer.class.getName());
        String value = (String) map.get("value").get(0);
        if (value.equals("ftpServer")) {
            return new String[]{FtpServer.class.getName()};
        }
        if (value.equals("httpServer")) {
            return new String[]{HttpServer.class.getName()};
        }

        return new String[0];
    }
}
```
启动类
```java
@EnableServer
@EnableHelloWorld
public class BootStrap {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BootStrap.class);
        context.refresh();
        Object name = context.getBean("name");
        System.out.println(name);

        Server server = context.getBean(Server.class);
        server.start();
        server.close();
        context.close();
    }
}
```

#### ImportBeanDefinitionRegistrar
其他不变，新建一个实现类，实现ImportBeanDefinitionRegistrar
```java
public class ServerImportRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        MultiValueMap<String, Object> map = metadata.getAllAnnotationAttributes(EnableServer.class.getName());
        String value = (String) map.get("value").get(0);
        if (value.equals("ftpServer")) {

            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            TypeFilter typeFilter = new AssignableTypeFilter(FtpServer.class);
            scanner.addIncludeFilter(typeFilter);
            scanner.scan("com.zwd.boot.server");

        }
        if (value.equals("httpServer")) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);

            TypeFilter typeFilter = new AssignableTypeFilter(HttpServer.class);
            scanner.addIncludeFilter(typeFilter);

        }
    }
}
```
然后更改@Enable模块
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import(ServerImportSelector.class)
@Import(ServerImportRegistrar.class)
public @interface EnableServer {

    String value() default "ftpServer";
}

```
启动项目，输出如下结果，表示成功
```java
我是name
ftpServer 启动.....
ftpServer 关闭.....
```