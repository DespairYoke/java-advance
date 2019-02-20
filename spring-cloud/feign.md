## Feign声明式服务调用

### feign简介

Feign是一个声明式的Web Service客户端，它使得编写Web Serivce客户端变得更加简单。我们只需要使用Feign来创建一个接口并用注解来配置它既可完成。它具备可插拔的注解支持，包括Feign注解和JAX-RS注解。
Feign也支持可插拔的编码器和解码器。Spring Cloud为Feign增加了对Spring MVC注解的支持，还整合了Ribbon和Eureka来提供均衡负载的HTTP客户端实现。

### 前期准备工作
- 启动[spring-cloud-eureka](./spring-cloud-eureka)
- 启动[spring-cloud-clientA](./spring-cloud-clientA)

### Feign服务搭建

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
- application.properties配置
```properties
spring.application.name=spring-cloud-feign
server.port=8004
eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```
- 启动类案例
```java
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SpringCloudFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudFeignApplication.class, args);
	}

}
```
- FeignClient
```java
@FeignClient("eureka-client-a")
public interface ConsumerService {

    @GetMapping(value = "hello/{name}")
    String hello(@PathVariable("name") String name);

}
```

启动项目[spring-cloud-feign](./spring-cloud-feign)

访问 http://localhost:8004/hello/zwd ,可以看到服务完成调用成功。
