## java进阶

## 源码分析专题

### 常用设计模式

* [Proxy 代理模式](https://github.com/DespairYoke/java-advance/tree/master/design-pattern/proxy)

* [Factory 工厂模式](https://github.com/DespairYoke/java-advance/blob/master/design-pattern/factory/README.md)

* [SingLeton 单例模式](./design-pattern/signle/README.md)

* [Delegate 委派模式](https://github.com/DespairYoke/java-advance/tree/master/design-pattern/delegate)

* [Strategy 策略模式](./design-pattern/strategy/README.md)

* [Prototype 原型模式](./design-pattern/prototype/README.md)

* [Template 模板模式](./design-pattern/template/README.md)

* [Observer 观察者模式](./design-pattern/observer/README.md)

### 线程与并发

* [Thread和Runnable的使用](./thread/thread_runnable.md)

* [四大线程池简介](./thread/thread_pool.md)

* [ThreadLocal简介](./thread/threadlocal.md)

* [线程运行状态图简介及join,wait,sleep,yield使用](./thread/thread-excute.md)

* [锁的使用](./thread/thread-lock.md)

* [深度理解synchronized、volatile、cas](./thread/thread_static.md)
### Spring5

* [IOC容器设计原理及高级特性](./ioc.md)

* [AOP设计原理](./spring5/aop/README.md)

* [FactoryBean与BeanFactory](https://github.com/DespairYoke/java-advance/blob/master/spring5/FactoryBeanAndBeanFactory/README.md)

* [Spring事务处理机制](https://github.com/DespairYoke/java-advance/blob/master/spring5/spring-transaction/README.md)

* [Spring CGLIB动态代理](https://github.com/DespairYoke/java-advance/blob/master/spring5/cglib/README.md)

* [基于注解编程的mvc](./spring5/spring-mvc/README.md)

* [基于注解的事件驱动](./spring5/spring-mvc-event/README.md)

* [基于SpringJDBC手写ORM框架](https://github.com/DespairYoke/java-advance/blob/master/spring5/making-myorm/README.md)

* [SpringMVC九大组件](./spring5/springMVC-component.md)

* [手写实现SpringMVC框架](./spring5/my-spring-mvc/README.md)

* SpringMVC和Struts2对比分析

* [Spring5新特性](./spring5/spring5-feature.md)

### SpringMvc

[gitbook 地址](https://zwd.ccxst.cn/spring-mvc-book)
* [SpringMvc从Servlet到DispatcherServlet（一）](./spring5/my-spring-mvc/2019-05-16-DispatcherServlet.md)

* [SpringMvc初始化handlerMapping（二）](./spring5/my-spring-mvc/2019-05-17-handlerMapping.md)

* [SpringMvc中RequestCondition的作用（三）](./spring5/my-spring-mvc/2019-05-18-RequestCondition.md)

* [SpringMvc请求如何获取相关HandlerMapping（四）](./spring5/my-spring-mvc/2019-05-19-HandlerMapping.md)

* [SpringMvc初始化HandlerAdapters（五）](./spring5/my-spring-mvc/2019-05-20-HandlerAdapters.md)

* [HandlerAdapter handle方法使用（六）](./spring5/my-spring-mvc/2019-05-21-HandlerAdapter.md)

* [SpringMvc如何实现参数填充HandlerMethodArgumentResolver（七）](./spring5/my-spring-mvc/2019-05-22-HandlerMethodArgumentResolver.md)

* [SpringMvc中HandlerMethodReturnValueHandler对结果处理（八）](./spring5/my-spring-mvc/2019-05-23-HandlerMethodReturnValueHandler.md)

* [SpringMvc初始化ViewResolver（九）](./spring5/my-spring-mvc/2019-05-24-ViewResolver.md)

* [SpringMvc中的View（十）](./spring5/my-spring-mvc/2019-05-25-View.md)

* [SpringMvc如何处理ModelAndView（十一）](./spring5/my-spring-mvc/2019-05-26-ModelAndView.md)

* [SpringMvc事件发布处理流程(十二)](./spring5/my-spring-mvc/chapter12.md)

* [手写spring ApplicationListener(十三)](./spring5/my-spring-mvc/chapter13.md)

### myBatis

* [代码自动生成器](./mybatis/mybatis-generator/README.md)

* [Mybatis关联查询、嵌套查询](./mybatis/mybatis-link-query/README.md)

* [缓存使用场景及选择策略](./mybatis/mybatis-cache/README.md)

* [Spring集成下的SqlSession与Mapper](./mybatis/spring-mybatis/README.md)

* [spring下MyBatis的事务](./mybatis/mybatis-transaction-manager/README.md)

* [分析MyBaits的事务](./mybatis/mybatis-transaction-analysis/README.md)

* [分析MyBatis的动态代理的真正实现](./mybatis/mybatis-proxy/README.md)

* [手写实现Mini版的MyBatis](https://github.com/DespairYoke/java-advance/blob/master/spring5/making-myorm/README.md)

## JVM

## 微服务架构专题

### 微框架(base on springboot2.1.3)

* [springboot热部署实战](./spring-boot/hot.md)

* springboot核心组件[Starter](./spring-boot/starter.md)、Actuator、AutoConfiguration、CLi

* [springboot集成MyBatis实现多数据源路由实战](./spring-boot/mulidatasource.md)

* [springboot集成Redis缓存实战](./spring-boot/redis.md)

* [springboot集成Swagger2构建API管理及测试体系](./spring-boot/swagger.md)

* [springboot实现多环境配置动态解析](./spring-boot/profile.md)

* [springboot注解驱动@Enable模块的使用](./spring-boot/spring-boot-enable/README.md)

* [springboot2.0集成quartz(基于数据库)](https://github.com/DespairYoke/java-advance/tree/master/spring-boot/spring-boot-quartz)

* [springboot2.0日志探索](https://github.com/DespairYoke/java-advance/tree/master/spring-boot/spring-boot-log)

### Spring Cloud(base on springboot2.1.3 and spring cloud Greenwich.RELEASE)

* [Eureka注册中心](./spring-cloud/eureka.md)

* [Ribbon集成REST实现负载均衡](./spring-cloud/ribbon.md)

* [Ribbon核心源码分析](./spring-cloud/ribbon_core.md)

* [OpenFeign声明式服务调用](./spring-cloud/feign.md)

* [HyStrilx服务熔断降级方式](./spring-cloud/hystrix.md)

* [Zuul2.0实现微服务网关](./spring-cloud/zuul.md)

* [Spring Cloud Gateway实现微服务网关](./spring-cloud/gateway.md)

* [Config分布式统一配置中心](./spring-cloud/config.md)

* [Sleuth调用链路跟踪](./spring-cloud/sleuth.md)

* [Bus消息总线](./spring-cloud/bus.md)

* [gRPC实现远程调用]()

### Docker的虚拟化

* Docker的镜像、仓库、容器

* Docker File构建LNMP环境部署个人博客Wordpress

* Docker Compose构建LNMP环境部署个人博客Wordpress

* Docker网络组成、路由互联、Openvswith

* 基于Swarm构建Docker集群实战

* Kubernates简介

## 面试题总结

* hashmap结构；什么对象能做为key

* hashtable,concurrentHashMap,hashtable比较

* String,StringBuilder,StringBuffer

* wait,sleep分别是谁的方法，区别
 
* 对象的深浅复制

* 分布式系统如何负载均衡？如何确定访问的资源在哪个服务器上？

[答案及更多](./interview.md)


