## Zuul2.0实现微服务网关

### zuul简介
路由是微服务架构不可或缺的一部分。例如，/可以映射到您的Web应用程序，/api/users映射到用户服务并/api/shop映射到商店服务。 Zuul是Netflix的基于JVM的路由器和服务器端负载均衡器。

### 前期准备工作
- 启动[spring-cloud-eureka](./spring-cloud-eureka)
- 启动[spring-cloud-clientA](./spring-cloud-clientA)
- 启动[spring-cloud-clientB](./spring-cloud-clientB)

### zuul服务搭建

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```
- application.properties配置
```properties
spring.application.name=spring-cloud-zuul
server.port=8006
eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/

zuul.routes.eureka-client-a.path=/eureka-client-a/**
zuul.routes.eureka-client-a.service-id=eureka-client-a

zuul.routes.eureka-client-b.path=/eureka-client-b/**
zuul.routes.eureka-client-b.service-id=eureka-client-b
```
- 启动类案例
```java
@EnableEurekaClient
@SpringBootApplication
@EnableZuulProxy
public class SpringCloudZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZuulApplication.class, args);
	}

}

```

启动项目[spring-cloud-zuul](./spring-cloud-zuul)

访问 http://localhost:8006/eureka-client-a/hello/zwd ,可以从服务a控制台看到调用成功。
访问 http://localhost:8006/eureka-client-b/hello/zwd ,可以从服务b控制台看到调用成功。
