## 从caffineCache看缓存源码

### 总纲
spring通过xml或者注解驱动的方式，检测被标记的方法，并生成对应的代理类；利用切面技术在方法执行前，执行缓存判断，如果有相同参数对应的缓存，则不执行该方法直接返回。

### springboot的整合代码示例
```java
@Cacheable(value = "user",key = "#id")
public User getUser(Long id) {
    return userRepository.getOne(id);
}

@CachePut(value = "user",key = "#user.id")
public User update(User user) {
    user = userRepository.save(user);
    return user;
}
@CacheEvict(value = "user",key = "#id")
public void deleteUser(Long id) {
    userRepository.delete(id);
}
```
### Bean的来去
配置caffineCache会在启动类上加入@EnableCaching，而@EnableCaching和其他Enable模块一样，都会自动装配。
```java
@Import(CachingConfigurationSelector.class)
public @interface EnableCaching {
	AdviceMode mode() default AdviceMode.PROXY;
}
```
import导入的Selector会执行selectImports方法
```java
public String[] selectImports(AdviceMode adviceMode) {
    switch (adviceMode) {
        case PROXY:
            return getProxyImports();
        case ASPECTJ:
            return getAspectJImports();
        default:
            return null;
    }
}
```
AdviceMode默认的时PROXY，所以这里走getProxyImports()
```java
private String[] getProxyImports() {
    List<String> result = new ArrayList<String>();
    result.add(AutoProxyRegistrar.class.getName());
    result.add(ProxyCachingConfiguration.class.getName());
    if (jsr107Present && jcacheImplPresent) {
        result.add(PROXY_JCACHE_CONFIGURATION_CLASS);
    }
    return result.toArray(new String[result.size()]);
}
```
这里注册了两个bean AutoProxyRegistrar  ProxyCachingConfiguration
ProxyCachingConfiguration配置类中注册了三个bean
- BeanFactoryCacheOperationSourceAdvisor
- CacheOperationSource
- CacheInterceptor

### bean的生命周期中的创建
在bean的创建执行后置处理器时，会执行AnnotationAwareAspectJAutoProxyCreator的postProcessAfterInitialization方法
后续执行wrapIfNecessary(bean, beanName, cacheKey);
```java
protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
    // Create proxy if we have advice.
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
    if (specificInterceptors != DO_NOT_PROXY) {
        this.advisedBeans.put(cacheKey, Boolean.TRUE);
        Object proxy = createProxy(
                bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
        this.proxyTypes.put(cacheKey, proxy.getClass());
        return proxy;
    }
    this.advisedBeans.put(cacheKey, Boolean.FALSE);
    return bean;
}
```
getAdvicesAndAdvisorsForBean会发现使用@Cache相关注解的类，生成对应的代理对象
```java
protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, TargetSource targetSource) {
    List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
    if (advisors.isEmpty()) {
        return DO_NOT_PROXY;
    }
    return advisors.toArray();
}
```
```java
protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
    List<Advisor> candidateAdvisors = findCandidateAdvisors();
    List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    extendAdvisors(eligibleAdvisors);
    if (!eligibleAdvisors.isEmpty()) {
        eligibleAdvisors = sortAdvisors(eligibleAdvisors);
    }
    return eligibleAdvisors;
}
```
candidateAdvisors集合包含了上述中我们注入的BeanFactoryCacheOperationSourceAdvisor，当match上需要处理的bean会生成对应的代理类。

### 处理请求
当有请求来时，CacheInterceptor bean 就开始上场了。由于是代理所以会走拦截器方法。
```java
public Object invoke(final MethodInvocation invocation) throws Throwable {
    Method method = invocation.getMethod();

    CacheOperationInvoker aopAllianceInvoker = new CacheOperationInvoker() {
        @Override
        public Object invoke() {
            try {
                return invocation.proceed();
            }
            catch (Throwable ex) {
                throw new ThrowableWrapper(ex);
            }
        }
    };

    try {
        return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
    }
    catch (CacheOperationInvoker.ThrowableWrapper th) {
        throw th.getOriginal();
    }
}
```
先走方法自己的拦截，此方法没有拦截则走execute
```java
protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
    // Check whether aspect is enabled (to cope with cases where the AJ is pulled in automatically)
    if (this.initialized) {
        Class<?> targetClass = getTargetClass(target);
        Collection<CacheOperation> operations = getCacheOperationSource().getCacheOperations(method, targetClass);
        if (!CollectionUtils.isEmpty(operations)) {
            return execute(invoker, method, new CacheOperationContexts(operations, method, args, target, targetClass));
        }
    }

    return invoker.invoke();
}
```
operations 是根据方法上的注解获取到需要执行的操作集合。
注解对应的操作：CacheEvictOperation
- @Cacheable  ====  CacheableOperation
- @CacheEvict ====  CacheEvictOperation
- @CachePut    ====  CachePutOperation
注解执行顺序 
```java
// Process any early evictions
processCacheEvicts(contexts.get(CacheEvictOperation.class), true,
				CacheOperationExpressionEvaluator.NO_RESULT);

// Check if we have a cached item matching the conditions
Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));

// Collect any explicit @CachePuts
collectPutRequests(contexts.get(CachePutOperation.class), cacheValue, cachePutRequests);

// Process any late evictions
processCacheEvicts(contexts.get(CacheEvictOperation.class), false, cacheValue);
```
### @Cacheable 
```java
if (cacheHit != null && cachePutRequests.isEmpty() && !hasCachePut(contexts)) {
			// If there are no put requests, just use the cache hit
			cacheValue = cacheHit.get();
			returnValue = wrapCacheValue(method, cacheValue);
		}
		else {
			// Invoke the method if we don't have a cache hit
			returnValue = invokeOperation(invoker);
			cacheValue = unwrapReturnValue(returnValue);
		}
```
从源码中可以看出如果匹配到返回值，则直接返回 否则执行方法。

### @CachePut
会每次都会执行方法，更新缓存，确保缓存和数据库数据同步。

### @CacheEvict
CacheEvict有三种清除方式
- 设置最大值，超过最大值，会把使用较少的或经常未使用的清除。
- 设置定时清除，到达时间后自动清除。
- 弱引用的使用，允许垃圾回收时，进行回收。