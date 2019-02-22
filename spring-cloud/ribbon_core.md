## Ribbon源码分析


#### 带着想法读源码
当不使用`@LoadBalanced`时，会发生什么样的后果。
```java
java.net.UnknownHostException: eureka-client-a
	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:184) ~[na:1.8.0_151]
```
从报错可以看出直接把服务名当成ip去请求，显然是找不到的。所以加上注解应该有代码拦截了这个请求，并把服务名转换成ip。

查看注解`@LoadBalanced`
```java
/**
 * Annotation to mark a RestTemplate bean to be configured to use a LoadBalancerClient.
 * @author Spencer Gibb
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface LoadBalanced {
}
```
注释写的很明显，标记一个restTemplete去配置LoadBlancerClient,那我们看下LoadBlancerClient
```java
public interface LoadBalancerClient extends ServiceInstanceChooser {

	/**
	 * Executes request using a ServiceInstance from the LoadBalancer for the specified
	 * service.
	 */
	<T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException;

	/**
	 * Executes request using a ServiceInstance from the LoadBalancer for the specified
	 * service.
	 */
	<T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException;

	/**
	 * Creates a proper URI with a real host and port for systems to utilize.
	 * Some systems use a URI with the logical service name as the host,
	 * such as http://myservice/path/to/service.  This will replace the
	 * service name with the host:port from the ServiceInstance.
	 */
	URI reconstructURI(ServiceInstance instance, URI original);
}
```
- 使用LoadBalancer中的ServiceInstance执行指定的服务。
- 创建一个带有真实主机和端口的URI，供系统使用。一些系统使用带有逻辑服务名称的URI作为主机，例如http://myservice/path/to/service。这将取代主机的服务名称:来自ServiceInstance的端口。

查看继承的接口
```java
public interface ServiceInstanceChooser {

    /**
     * Chooses a ServiceInstance from the LoadBalancer for the specified service.
     * @param serviceId The service ID to look up the LoadBalancer.
     * @return A ServiceInstance that matches the serviceId.
     */
    ServiceInstance choose(String serviceId);
}
```

- 从LoadBalancer中为指定的服务选择一个ServiceInstance。

`LoadBalancerClient`是一个接口，应该会有一个类配置它。在同包下有一个`LoadBlancerAutoConfiguration`。内容如下
```java
@Configuration
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnBean(LoadBalancerClient.class)
@EnableConfigurationProperties(LoadBalancerRetryProperties.class)
public class LoadBalancerAutoConfiguration {
    //...
}
```
虽然没有看到直接生成`LoadBalancerClient`的bean。但是由注解可以看出这个类和`LoadBalancerClient`有一定的关联。仔细查看后里面有一段代码值得思考。
```java
@Bean
public LoadBalancerInterceptor ribbonInterceptor(
        LoadBalancerClient loadBalancerClient,
        LoadBalancerRequestFactory requestFactory) {
    return new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
}
```
发现把loadbalancerClient和请求放入一个拦截器中，联想到请可能被此处处理，查看拦截器代码
```java
	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {
		final URI originalUri = request.getURI();
		String serviceName = originalUri.getHost();
		Assert.state(serviceName != null, "Request URI does not contain a valid hostname: " + originalUri);
		return this.loadBalancer.execute(serviceName, requestFactory.createRequest(request, body, execution));
	}
```
debug代码，继续往下走
```java
public <T> T execute(String serviceId, LoadBalancerRequest<T> request, Object hint) throws IOException {
    ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
    Server server = getServer(loadBalancer, hint);
    if (server == null) {
        throw new IllegalStateException("No instances available for " + serviceId);
    }
    RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
            serviceId), serverIntrospector(serviceId).getMetadata(server));

    return execute(serviceId, ribbonServer, request);
}
```
发现ip的出现时RibbonLoadBalancerClient的execte下的getServer或的。
查看getServer
```java
protected Server getServer(ILoadBalancer loadBalancer, Object hint) {
    if (loadBalancer == null) {
        return null;
    }
    // Use 'default' on a null hint, or just pass it on?
    return loadBalancer.chooseServer(hint != null ? hint : "default");
}
```
进入loadBalancer
```java
	/**
	 * Choose a server from load balancer.
	 * 
	 * @param key An object that the load balancer may use to determine which server to return. null if 
	 *         the load balancer does not use this parameter.
	 * @return server chosen
	 */
	public Server chooseServer(Object key);
	
```
从注解可以看出是从负载均衡中挑出一个server，那负载均衡的具体实现就应该在实现类里面。而实现类调用的是`ZoneAwareLoadBalancer`，
所以默认使用`ZoneAwareLoadBalancer`进行负载算法。
后续代码太过复杂，不做分析。

