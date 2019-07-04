## 基于注解的springMvc



### 编程替换web.xml
```java
public class MyAbstractAnnotationConfigDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {  //web.xml
        return new Class[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() { //DispatcherServlet
        return new Class[] {DispatcherServletConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
}
```

### 替换<context: commponetScan>

```java
@ComponentScan(basePackages = "com.zwd")
public class DispatcherServletConfiguration {
}
```

### 开启mvc 并配置ViewResolver
```java
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ViewResolver viewResolver () {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;

    }
}
```

### 使用maven tomcat插件打包
```java
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.1</version>
    <executions>
        <execution>
            <id>tomcat-run</id>
            <goals>
                <goal>exec-war-only</goal>
            </goals>
            <phase>package</phase>
            <configuration>
                <!-- ServletContext path -->
                <path>/</path>
            </configuration>
        </execution>
    </executions>
</plugin>
```
`mvn clean package` 会生成一个可执行的jar包。
`java -jar ...` 执行jar包，访问`localhost:8080`