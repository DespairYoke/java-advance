## Bus消息总线

### Bus简介
Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控。
本文要讲述的是用Spring Cloud Bus实现通知微服务架构的配置文件的更改。

### 示例演示

功能说明，修改配置文件而不重启服务，进行刷新加载。

结合[config](./config.md) 我们对spring-cloud-config-server改造成我们sprign-cloud-bus

- 添加maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

- 添加bootstrap.properties配置属性
```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

spring.cloud.bus.enabled=true
spring.cloud.bus.trace.enabled=true
management.endpoints.web.exposure.include=bus-refresh
```