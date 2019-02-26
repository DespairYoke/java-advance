## 自定义 Spring Boot Starter

众所周知，Spring Boot由众多Starter组成，随着版本的推移Starter家族成员也与日俱增。在传统Maven项目中通常将一些层、组件拆分为模块来管理，以便相互依赖复用，在Spring Boot项目中我们则可以创建自定义Spring Boot Starter来达成该目的。

现在我们来创建一个自己的starter。

- 功能介绍

这里讲一下我们的Starter要实现的功能，很简单，提供一个Service，包含一个能够将字符串加上前后缀的方法String wrap(String word)。

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
</dependency>
```

这里说下`artifactId`的命名问题，Spring 官方 Starter通常命名为`spring-boot-starter-{name}`如 `spring-boot-starter-web`， Spring官方建议非官方Starter
命名应遵循`{name}-spring-boot-starter`的格式。

- 属性读取类
```java
@ConfigurationProperties("example.service")
public class ExampleServiceProperties {

    private String prefix;

    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
```

- service
```java
public class ExampleService {

    private String prefix;

    private String suffix;

    public ExampleService(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String wrap(String word) {
        return prefix + word + suffix;
    }
}
```
- 自动加载配置
```java
@Configuration
@ConditionalOnClass(ExampleService.class)
@EnableConfigurationProperties(ExampleServiceProperties.class)
public class ExampleAutoConfigure {

    @Autowired
    private ExampleServiceProperties properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "example.service",value = "enabled",havingValue = "true")
    ExampleService exampleService (){
        return  new ExampleService(properties.getPrefix(),properties.getSuffix());
    }

}
```
`@ConditionalOnClass` 表示有这个类的存在。
`@EnableConfigurationProperties` 自动配置属性类。
`@ConditionalOnMissingBean` 当ExampleService bean缺失时，执行下段代码。
`@ConditionalOnProperty` 当example.service.enabled=true时，执行代码。

- spring.factories

在resources下创建MATA-INF/spring.factories，内容如下
```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.example.zwd.springbootstarter.config.ExampleAutoConfigure
```
`注意`：value为自动配置类的classpath。

创建完成，进行打包。
#### 如何使用？

- 引入依赖
```xml
<dependency>
    <groupId>com.java.advance</groupId>
    <artifactId>example-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

- application.properties配置参数
```properties
example.service.enabled=true
example.service.prefix=####
example.service.suffix=@@@@
```
- 启动类示例
```java
@SpringBootApplication
@RestController
public class SpringbootStarterTest {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootStarterTest.class, args);
    }

    @Autowired
    private ExampleService exampleService;

    @GetMapping("/input")
    public String input(String word){
        return exampleService.wrap(word);
    }
}
```

启动成功和访问 http://localhost:8080/input?word=name，返回结果如下

    ####name@@@@
    
表示starter可以正常使用。