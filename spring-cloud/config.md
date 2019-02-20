## Config分布式统一配置中心

### config简介
在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。
在Spring Cloud中，有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），
也支持放在远程Git仓库中。在spring cloud config 组件中，分两个角色，一是config server，二是config client。

### 构建Config Server

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
- 创建文件夹和文件供后续使用
实例：在[spring-cloud]()下创建config-repo，在config-repo创建config-client-dev.properties供后续使用
文件内容
```properties
foo = foo version 21
```
- bootstrap.properties配置
```properties
spring.application.name=config-server
server.port=8888

spring.cloud.config.server.git.uri=https://github.com/DespairYoke/java-advance
spring.cloud.config.server.git.search-paths=spring-cloud/config-repo
spring.cloud.config.server.git.username=username
spring.cloud.config.server.git.password=password
```
属性介绍

    spring.cloud.config.server.git.uri：配置git仓库地址
    spring.cloud.config.server.git.searchPaths：配置仓库路径
    spring.cloud.config.label：配置仓库的分支
    spring.cloud.config.server.git.username：访问git仓库的用户名
    spring.cloud.config.server.git.password：访问git仓库的用户密码

- 启动类案例
```java
@EnableConfigServer
@SpringBootApplication
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}

}
```
### 构建Config client

- maven依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

- bootstrap.properties配置
```properties
spring.application.name=config-client

server.port=8111
spring.cloud.config.label=master

spring.cloud.config.uri=http://localhost:8888

spring.cloud.config.profile=dev
```
    spring.cloud.config.label 指明远程仓库的分支
  
    spring.cloud.config.profile
       - dev开发环境配置文件
       - test测试环境
       - pro正式环境
    spring.cloud.config.uri= http://localhost:8888/ 指明配置服务中心的网址。

- 启动类案例
```java
@SpringBootApplication
@RestController
public class SpringCloudConfigClientApplication {

	@Value(value = "${foo}")
	public String foo;

	@RequestMapping("/")
	public String home() {
		return "Hello World!"+foo;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}
}
```
- 由于使用git控制配置文件，所以首先要把项目推到git上
- 启动项目[spring-cloud-config-server](./spring-cloud-config-server)
- 启动项目[spring-cloud-config-client](./spring-cloud-config-client)
打开网址访问 http://localhost:8111/，输入结果如下，说明配置文件读取成功
```properties
Hello World!foo version 21
```
