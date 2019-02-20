## HyStrilx服务熔断降级方式

### HyStrilx简介
断路器模式源于Martin Fowler的Circuit Breaker一文。“断路器”本身是一种开关装置，用于在电路上保护线路过载，当线路中有电器发生短路时，“断路器”能够及时的切断故障电路，
防止发生过载、发热、甚至起火等严重后果。

`hystrilx`不单独使用，此组件一般和`ribbon`、`feign`结合使用

### 前期准备工作
- 启动[spring-cloud-eureka](./spring-cloud-eureka)
- 启动[spring-cloud-clientA](./spring-cloud-clientA)

### HyStrilx服务搭建(结合ribbon)

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```
- application.properties配置
```properties
spring.application.name=spring-cloud-hystrix
server.port=8005
eureka.client.serviceUrl.defaultZone=http://localhost:8000/eureka/
```
- 启动类案例
```java
@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
public class SpringCloudHystrixApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudHystrixApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
```

启动项目[spring-cloud-hystrix](./spring-cloud-hystrix)

访问 http://localhost:8005/ribbon ,可以从服务a控制台看到调用成功。

关闭服务[spring-cloud-clientA](./spring-cloud-clientA)
再次访问 http://localhost:8005/ribbon ,返回结果是被熔断降级后的回调。

### Feign整合

feign自带hystrix，所以不需要添加，任何依赖，只需要配置fallback属性即可。

- 使用@FeignClient注解中的fallback属性指定回调类
```java
@FeignClient(value = "eureka-client-a",fallback = ConsumerServiceHystrix.class)
public interface ConsumerService {

    @GetMapping(value = "hello/{name}")
    String hello(@PathVariable("name") String name);

}
```
创建回调类`ConsumerServiceHystrix`

```java
@Component
public class ConsumerServiceHystrix implements ConsumerService{
    @Override
    public String hello(String name) {
        return "error";
    }
}
```
