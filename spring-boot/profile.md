## springboot实现多环境配置动态解析

在项目整个流程中，开发、测试和线上可能会用不同的环境配置。如果每次都更改配置的话，太过麻烦和笨重。springboot为了方便
提供了spring.profiles.active机制。只要按照application-{profile}.properties建立文件，即可切换不同的配置。

### 项目测试

- 在resource下新建三个文件
```properties
application-dev.properties //开发环境配置文件
application-rc.properties //线上环境配置文件
application-test.properties //测试环境配置文件
```
- 选择使用的配置文件
在resource/application.properties配置文件中添加一下配置项目：
```properties
spring.profiles.active=dev
```
- .测试多环境配置
我们分别在
```properties
application-dev.properties //开发环境配置文件
application-rc.properties //线上环境配置文件
application-test.properties //测试环境配置文件
```
文件中添加以下配置：
```properties
#端口号
server.port=8081
server.port=8082
server.port=8083
```
依次修改application.properties文件中的以下配置：
```properties
spring.profiles.active=dev、test、rc
```
修改后依次重启服务。
 当使用dev的时候控制台输出一下内容：
 ```properties
2019-02-26 16:35:39.137  INFO 6089 --- [           main] o.a.catalina.core.AprLifecycleListener   : The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [/Users/zwd-admin/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.]
2019-02-26 16:35:39.690  INFO 6089 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-02-26 16:35:39.690  INFO 6089 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 6071 ms
2019-02-26 16:35:40.082  INFO 6089 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2019-02-26 16:35:40.401  INFO 6089 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path ''
2019-02-26 16:35:40.405  INFO 6089 --- [           main] c.e.z.s.SpringBootProfilesApplication    : Started SpringBootProfilesApplication in 8.212 seconds (JVM running for 9.8)
```
使用rc的时候控制台输出一下内容：
```properties
2019-02-26 16:36:36.399  INFO 6096 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2252 ms
2019-02-26 16:36:36.759  INFO 6096 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2019-02-26 16:36:37.097  INFO 6096 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8082 (http) with context path ''
2019-02-26 16:36:37.101  INFO 6096 --- [           main] c.e.z.s.SpringBootProfilesApplication    : Started SpringBootProfilesApplication in 3.775 seconds (JVM running for 4.76)
```
使用test的时候控制台输出一下内容：
```properties
2019-02-26 16:37:07.174  INFO 6100 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-02-26 16:37:07.174  INFO 6100 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 3532 ms
2019-02-26 16:37:07.505  INFO 6100 --- [           main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService 'applicationTaskExecutor'
2019-02-26 16:37:07.760  INFO 6100 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8083 (http) with context path ''
2019-02-26 16:37:07.770  INFO 6100 --- [           main] c.e.z.s.SpringBootProfilesApplication    : Started SpringBootProfilesApplication in 4.898 seconds (JVM running for 6.37)
```

可以看出在服务启动的时候分别用了不同的配置文件。

[项目地址](./spring-boot-profiles)