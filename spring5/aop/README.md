### 从JVM看aop
#### 1. Java程序运行在JVM中的特征
 当我们在某个类Foo中写好了一个main()方法，然后执行java Foo，你的Java程序之旅就开启了，如下：
```java
public class Foo {
    public static void main(String[] args) {
        // your codes begins here
    }
}
```
那么在这个执行的过程中，JVM都为你干了什么呢？

当你执行java Foo 的时候，JVM会创建一个主线程main，这个主线程以上述的main()方法作为入口，开始执行你的代码。每一个线程在内存中都会维护一个属于自己的栈(Stack),记录着整个程序执行的过程。栈里的每一个元素称为栈帧(Stack Frame)，栈帧表示着某个方法调用，会记录方法调用的信息；实际上我们在代码中调用一个方法的时候，在内存中就对应着一个栈帧的入栈和出栈。

在某个特定的时间点，一个Main线程内的栈会呈现如下图所示的情况：
![image.png](https://upload-images.jianshu.io/upload_images/15204062-8f2e485156fb1a29.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 2.  Java程序执行流 【了解AOP、连接点(Join Point)、切入点(point cut)   的概念 】

   如果从虚拟机线程栈的角度考虑Java程序执行的话，那么，你会发现，真个程序运行的过程就是方法调用的过程。我们按照方法执行的顺序，将方法调用排成一串，这样就构成了Java程序流。
我们将上述的线程栈里的方法调用按照执行流排列，会有如下类似的图：
![image.png](https://upload-images.jianshu.io/upload_images/15204062-c68802ea6250eac2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

基于时间序列，我们可以将方法调用排成一条线。而每个方法调用则可以看成Java执行流中的一个节点。这个节点在AOP的术语中，被称为Join Point，即连接点。 一个Java程序的运行的过程，就是若干个连接点连接起来依次执行的过程。

在我们正常的面向对象的思维中， 我们考虑的是如何按照时间序列通过方法调用来实现我们的业务逻辑。那么，什么是AOP（即面向切面的编程）呢？

通常面向对象的程序，代码都是按照时间序列纵向展开的，而他们都有一个共性：即都是已方法调用作为基本执行单位展开的。 将方法调用当做一个连接点，那么由连接点串起来的程序执行流就是整个程序的执行过程。

`AOP(Aspect Oriented Programming)则是从另外一个角度来考虑整个程序的，AOP将每一个方法调用，即连接点作为编程的入口，针对方法调用进行编程。从执行的逻辑上来看，相当于在之前纵向的按照时间轴执行的程序横向切入。相当于将之前的程序横向切割成若干的面，即Aspect.每个面被称为切面。`
所以，根据我的理解，AOP本质上是针对方法调用的编程思路。

![image.png](https://upload-images.jianshu.io/upload_images/15204062-e8c4b5cecdb9f97d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 既然AOP是针对切面进行的编程的，那么，你需要选择哪些切面(即 连接点Joint Point)作为你的编程对象呢？

   因为切面本质上是每一个方法调用，选择切面的过程实际上就是选择方法的过程。那么，被选择的切面(Aspect)在AOP术语里被称为切入点(Point Cut).  切入点实际上也是从所有的连接点(Join point)挑选自己感兴趣的连接点的过程。

![image.png](https://upload-images.jianshu.io/upload_images/15204062-a13d8ccc4bede894.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
既然AOP是针对方法调用(连接点)的编程， 现在又选取了你感兴趣的自己感兴趣的链接点---切入点（Point Cut）了，那么，AOP能对它做什么类型的编程呢？AOP能做什么呢？ 

了解这个之前，我们先要知道一个非常重要的问题： 既然AOP是对方法调用进行的编程，那么，AOP如何捕获方法调用的呢？ 弄清楚这个问题，你不得不了解设计模式中的代理模式了。下面我们先来了解一下引入了代理模式的Java程序执行流是什么样子的。

#### 3.    引入了代理模式的Java程序执行流(AOP实现的机制)
我们假设在我们的Java代码里，都为实例对象通过代理模式创建了代理对象，访问这些实例对象必须要通过代理，那么，加入了proxy对象的Java程序执行流会变得稍微复杂起来。

我们来看下加入了proxy对象后，Java程序执行流的示意图：
![image.png](https://upload-images.jianshu.io/upload_images/15204062-028cc6909d5c9f39.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

加入了代理模式的Java程序执行流，使得所有的方法调用都经过了代理对象。对于Spring AOP框架而言，它负责控制着真个容器内部的代理对象。当我们调用了某一个实例对象的任何一个非final的public方法时，整个Spring框架都会知晓。

此时的SpringAOP框架在某种程度上扮演着一个上帝的角色：它知道你在这个框架内所做的任何操作，你对每一个实例对象的非final的public方法调用都可以被框架察觉到！

![image.png](https://upload-images.jianshu.io/upload_images/15204062-b6515da4afd25b47.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 4. Spring AOP的工作原理

前面已经介绍了AOP编程首先要选择它感兴趣的连接点----即切入点(Point cut)，那么，AOP能对切入点做什么样的编程呢？ 我们先将代理模式下的某个连接点细化，你会看到如下这个示意图所表示的过程：

![image.png](https://upload-images.jianshu.io/upload_images/15204062-5de973d01e2499e4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



![image.png](https://upload-images.jianshu.io/upload_images/15204062-7778f4449470ecf4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



![image.png](https://upload-images.jianshu.io/upload_images/15204062-65ed81c4bdd953ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

#### 5、Spring AOP的核心---ProxyFactoryBean

*ProxyFactoryBean提供了如下信息:*

1). Proxy应该感兴趣的Adivce列表；

2). 真正的实例对象引用ticketService;

3).告诉ProxyFactoryBean使用基于接口实现的JDK动态代理机制实现proxy: 

4). Proxy应该具备的Interface接口：TicketService;

根据这些信息，ProxyFactoryBean就能给我们提供我们想要的Proxy对象了！那么，ProxyFactoryBean帮我们做了什么？


![image.png](https://upload-images.jianshu.io/upload_images/15204062-9a1fe83c5aae2d9b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 Spring 使用工厂Bean模式创建每一个Proxy，对应每一个不同的Class类型，在Spring中都会有一个相对应的ProxyFactoryBean. 以下是ProxyFactoryBean的类图。
![image.png](https://upload-images.jianshu.io/upload_images/15204062-d0e29120279ad17c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 如上所示，对于生成Proxy的工厂Bean而言，它要知道对其感兴趣的Advice信息，而这类的信息，被维护到Advised中。Advised可以根据特定的类名和方法名返回对应的AdviceChain，用以表示需要执行的Advice串。


#### 6、基于JDK面向接口的动态代理JdkDynamicAopProxy生成代理对象
JdkDynamicAopProxy类实现了AopProxy，能够返回Proxy，并且，其自身也实现了InvocationHandler角色。也就是说，当我们使用proxy时，我们对proxy对象调用的方法，都最终被转到这个类的invoke()方法中。

```java
final class JdkDynamicAopProxy implements AopProxy, InvocationHandler, Serializable {
        //省略若干...
	/** Proxy的配置信息，这里主要提供Advisor列表，并用于返回AdviceChain */
	private final AdvisedSupport advised;
 
	/**
	 * Construct a new JdkDynamicAopProxy for the given AOP configuration.
	 * @param config the AOP configuration as AdvisedSupport object
	 * @throws AopConfigException if the config is invalid. We try to throw an informative
	 * exception in this case, rather than let a mysterious failure happen later.
	 */
	public JdkDynamicAopProxy(AdvisedSupport config) throws AopConfigException {
		Assert.notNull(config, "AdvisedSupport must not be null");
		if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
			throw new AopConfigException("No advisors and no TargetSource specified");
		}
		this.advised = config;
	}
 
 
	@Override
	public Object getProxy() {
		return getProxy(ClassUtils.getDefaultClassLoader());
	}
        //返回代理实例对象
	@Override
	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating JDK dynamic proxy: target source is " + this.advised.getTargetSource());
		}
		Class<?>[] proxiedInterfaces = AopProxyUtils.completeProxiedInterfaces(this.advised);
		findDefinedEqualsAndHashCodeMethods(proxiedInterfaces);
                //这里的InvocationHandler设置成了当前实例对象，即对这个proxy调用的任何方法，都会调用这个类的invoke()方法
                //这里的invoke方法被调用，动态查找Advice列表，组成ReflectMethodInvocation
		return Proxy.newProxyInstance(classLoader, proxiedInterfaces, this);
	}
	/**
	 * 对当前proxy调用其上的任何方法，都将转到这个方法上
         * Implementation of {@code InvocationHandler.invoke}.
	 * <p>Callers will see exactly the exception thrown by the target,
	 * unless a hook method throws an exception.
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodInvocation invocation;
		Object oldProxy = null;
		boolean setProxyContext = false;
 
		TargetSource targetSource = this.advised.targetSource;
		Class<?> targetClass = null;
		Object target = null;
 
		try {
			if (!this.equalsDefined && AopUtils.isEqualsMethod(method)) {
				// The target does not implement the equals(Object) method itself.
				return equals(args[0]);
			}
			if (!this.hashCodeDefined && AopUtils.isHashCodeMethod(method)) {
				// The target does not implement the hashCode() method itself.
				return hashCode();
			}
			if (!this.advised.opaque && method.getDeclaringClass().isInterface() &&
					method.getDeclaringClass().isAssignableFrom(Advised.class)) {
				// Service invocations on ProxyConfig with the proxy config...
				return AopUtils.invokeJoinpointUsingReflection(this.advised, method, args);
			}
 
			Object retVal;
 
			if (this.advised.exposeProxy) {
				// Make invocation available if necessary.
				oldProxy = AopContext.setCurrentProxy(proxy);
				setProxyContext = true;
			}
 
			// May be null. Get as late as possible to minimize the time we "own" the target,
			// in case it comes from a pool.
			target = targetSource.getTarget();
			if (target != null) {
				targetClass = target.getClass();
			}
 
			// Get the interception chain for this method.获取当前调用方法的拦截链
			List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
 
			// Check whether we have any advice. If we don't, we can fallback on direct
			// reflective invocation of the target, and avoid creating a MethodInvocation.
                        //如果没有拦截链，则直接调用Joinpoint连接点的方法。
			if (chain.isEmpty()) {
				// We can skip creating a MethodInvocation: just invoke the target directly
				// Note that the final invoker must be an InvokerInterceptor so we know it does
				// nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
				Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
				retVal = AopUtils.invokeJoinpointUsingReflection(target, method, argsToUse);
			}
			else {
				// We need to create a method invocation...
                                //根据给定的拦截链和方法调用信息，创建新的MethodInvocation对象，整个拦截链的工作逻辑都在这个ReflectiveMethodInvocation里 
				invocation = new ReflectiveMethodInvocation(proxy, target, method, args, targetClass, chain);
				// Proceed to the joinpoint through the interceptor chain.
				retVal = invocation.proceed();
			}
 
			// Massage return value if necessary.
			Class<?> returnType = method.getReturnType();
			if (retVal != null && retVal == target && returnType.isInstance(proxy) &&
					!RawTargetAccess.class.isAssignableFrom(method.getDeclaringClass())) {
				// Special case: it returned "this" and the return type of the method
				// is type-compatible. Note that we can't help if the target sets
				// a reference to itself in another returned object.
				retVal = proxy;
			}
			else if (retVal == null && returnType != Void.TYPE && returnType.isPrimitive()) {
				throw new AopInvocationException(
						"Null return value from advice does not match primitive return type for: " + method);
			}
			return retVal;
		}
		finally {
			if (target != null && !targetSource.isStatic()) {
				// Must have come from TargetSource.
				targetSource.releaseTarget(target);
			}
			if (setProxyContext) {
				// Restore old proxy.
				AopContext.setCurrentProxy(oldProxy);
			}
		}
	}
}

```
#### 7、基于Cglib子类继承方式的动态代理CglibAopProxy生成代理对象

```java
package org.springframework.aop.framework;
/**
 * CGLIB-based {@link AopProxy} implementation for the Spring AOP framework.
 *
 * <p>Formerly named {@code Cglib2AopProxy}, as of Spring 3.2, this class depends on
 * Spring's own internally repackaged version of CGLIB 3.</i>.
 */
@SuppressWarnings("serial")
class CglibAopProxy implements AopProxy, Serializable {
 
	// Constants for CGLIB callback array indices
	private static final int AOP_PROXY = 0;
	private static final int INVOKE_TARGET = 1;
	private static final int NO_OVERRIDE = 2;
	private static final int DISPATCH_TARGET = 3;
	private static final int DISPATCH_ADVISED = 4;
	private static final int INVOKE_EQUALS = 5;
	private static final int INVOKE_HASHCODE = 6;
 
 
	/** Logger available to subclasses; static to optimize serialization */
	protected static final Log logger = LogFactory.getLog(CglibAopProxy.class);
 
	/** Keeps track of the Classes that we have validated for final methods */
	private static final Map<Class<?>, Boolean> validatedClasses = new WeakHashMap<Class<?>, Boolean>();
 
 
	/** The configuration used to configure this proxy */
	protected final AdvisedSupport advised;
 
	protected Object[] constructorArgs;
 
	protected Class<?>[] constructorArgTypes;
 
	/** Dispatcher used for methods on Advised */
	private final transient AdvisedDispatcher advisedDispatcher;
 
	private transient Map<String, Integer> fixedInterceptorMap;
 
	private transient int fixedInterceptorOffset;
 
 
	/**
	 * Create a new CglibAopProxy for the given AOP configuration.
	 * @param config the AOP configuration as AdvisedSupport object
	 * @throws AopConfigException if the config is invalid. We try to throw an informative
	 * exception in this case, rather than let a mysterious failure happen later.
	 */
	public CglibAopProxy(AdvisedSupport config) throws AopConfigException {
		Assert.notNull(config, "AdvisedSupport must not be null");
		if (config.getAdvisors().length == 0 && config.getTargetSource() == AdvisedSupport.EMPTY_TARGET_SOURCE) {
			throw new AopConfigException("No advisors and no TargetSource specified");
		}
		this.advised = config;
		this.advisedDispatcher = new AdvisedDispatcher(this.advised);
	}
 
	/**
	 * Set constructor arguments to use for creating the proxy.
	 * @param constructorArgs the constructor argument values
	 * @param constructorArgTypes the constructor argument types
	 */
	public void setConstructorArguments(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
		if (constructorArgs == null || constructorArgTypes == null) {
			throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
		}
		if (constructorArgs.length != constructorArgTypes.length) {
			throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length +
					") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
		}
		this.constructorArgs = constructorArgs;
		this.constructorArgTypes = constructorArgTypes;
	}
 
 
	@Override
	public Object getProxy() {
		return getProxy(null);
	}
 
	@Override
	public Object getProxy(ClassLoader classLoader) {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating CGLIB proxy: target source is " + this.advised.getTargetSource());
		}
 
		try {
			Class<?> rootClass = this.advised.getTargetClass();
			Assert.state(rootClass != null, "Target class must be available for creating a CGLIB proxy");
 
			Class<?> proxySuperClass = rootClass;
			if (ClassUtils.isCglibProxyClass(rootClass)) {
				proxySuperClass = rootClass.getSuperclass();
				Class<?>[] additionalInterfaces = rootClass.getInterfaces();
				for (Class<?> additionalInterface : additionalInterfaces) {
					this.advised.addInterface(additionalInterface);
				}
			}
 
			// Validate the class, writing log messages as necessary.
			validateClassIfNecessary(proxySuperClass, classLoader);
 
			// Configure CGLIB Enhancer...
			Enhancer enhancer = createEnhancer();
			if (classLoader != null) {
				enhancer.setClassLoader(classLoader);
				if (classLoader instanceof SmartClassLoader &&
						((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
					enhancer.setUseCache(false);
				}
			}
			enhancer.setSuperclass(proxySuperClass);
			enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
			enhancer.setStrategy(new ClassLoaderAwareUndeclaredThrowableStrategy(classLoader));
 
			Callback[] callbacks = getCallbacks(rootClass);
			Class<?>[] types = new Class<?>[callbacks.length];
			for (int x = 0; x < types.length; x++) {
				types[x] = callbacks[x].getClass();
			}
			// fixedInterceptorMap only populated at this point, after getCallbacks call above
			enhancer.setCallbackFilter(new ProxyCallbackFilter(
					this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
			enhancer.setCallbackTypes(types);
 
			// Generate the proxy class and create a proxy instance.
			return createProxyClassAndInstance(enhancer, callbacks);
		}
		catch (CodeGenerationException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.advised.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (IllegalArgumentException ex) {
			throw new AopConfigException("Could not generate CGLIB subclass of class [" +
					this.advised.getTargetClass() + "]: " +
					"Common causes of this problem include using a final class or a non-visible class",
					ex);
		}
		catch (Exception ex) {
			// TargetSource.getTarget() failed
			throw new AopConfigException("Unexpected AOP exception", ex);
		}
	}
 
	protected Object createProxyClassAndInstance(Enhancer enhancer, Callback[] callbacks) {
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setCallbacks(callbacks);
		return (this.constructorArgs != null ?
				enhancer.create(this.constructorArgTypes, this.constructorArgs) :
				enhancer.create());
	}
 
	/**
	 * Creates the CGLIB {@link Enhancer}. Subclasses may wish to override this to return a custom
	 * {@link Enhancer} implementation.
	 */
	protected Enhancer createEnhancer() {
		return new Enhancer();
	}
 
 
 
	private Callback[] getCallbacks(Class<?> rootClass) throws Exception {
		// Parameters used for optimisation choices...
		boolean exposeProxy = this.advised.isExposeProxy();
		boolean isFrozen = this.advised.isFrozen();
		boolean isStatic = this.advised.getTargetSource().isStatic();
 
		// Choose an "aop" interceptor (used for AOP calls).
		Callback aopInterceptor = new DynamicAdvisedInterceptor(this.advised);
 
		// Choose a "straight to target" interceptor. (used for calls that are
		// unadvised but can return this). May be required to expose the proxy.
		Callback targetInterceptor;
		if (exposeProxy) {
			targetInterceptor = isStatic ?
					new StaticUnadvisedExposedInterceptor(this.advised.getTargetSource().getTarget()) :
					new DynamicUnadvisedExposedInterceptor(this.advised.getTargetSource());
		}
		else {
			targetInterceptor = isStatic ?
					new StaticUnadvisedInterceptor(this.advised.getTargetSource().getTarget()) :
					new DynamicUnadvisedInterceptor(this.advised.getTargetSource());
		}
 
		// Choose a "direct to target" dispatcher (used for
		// unadvised calls to static targets that cannot return this).
		Callback targetDispatcher = isStatic ?
				new StaticDispatcher(this.advised.getTargetSource().getTarget()) : new SerializableNoOp();
 
		Callback[] mainCallbacks = new Callback[] {
				aopInterceptor,  // for normal advice
				targetInterceptor,  // invoke target without considering advice, if optimized
				new SerializableNoOp(),  // no override for methods mapped to this
				targetDispatcher, this.advisedDispatcher,
				new EqualsInterceptor(this.advised),
				new HashCodeInterceptor(this.advised)
		};
 
		Callback[] callbacks;
 
		// If the target is a static one and the advice chain is frozen,
		// then we can make some optimisations by sending the AOP calls
		// direct to the target using the fixed chain for that method.
		if (isStatic && isFrozen) {
			Method[] methods = rootClass.getMethods();
			Callback[] fixedCallbacks = new Callback[methods.length];
			this.fixedInterceptorMap = new HashMap<String, Integer>(methods.length);
 
			// TODO: small memory optimisation here (can skip creation for methods with no advice)
			for (int x = 0; x < methods.length; x++) {
				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(methods[x], rootClass);
				fixedCallbacks[x] = new FixedChainStaticTargetInterceptor(
						chain, this.advised.getTargetSource().getTarget(), this.advised.getTargetClass());
				this.fixedInterceptorMap.put(methods[x].toString(), x);
			}
 
			// Now copy both the callbacks from mainCallbacks
			// and fixedCallbacks into the callbacks array.
			callbacks = new Callback[mainCallbacks.length + fixedCallbacks.length];
			System.arraycopy(mainCallbacks, 0, callbacks, 0, mainCallbacks.length);
			System.arraycopy(fixedCallbacks, 0, callbacks, mainCallbacks.length, fixedCallbacks.length);
			this.fixedInterceptorOffset = mainCallbacks.length;
		}
		else {
			callbacks = mainCallbacks;
		}
		return callbacks;
	}
 
 
	/**
	 * General purpose AOP callback. Used when the target is dynamic or when the
	 * proxy is not frozen.
	 */
	private static class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {
 
		private final AdvisedSupport advised;
 
		public DynamicAdvisedInterceptor(AdvisedSupport advised) {
			this.advised = advised;
		}
 
		@Override
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object oldProxy = null;
			boolean setProxyContext = false;
			Class<?> targetClass = null;
			Object target = null;
			try {
				if (this.advised.exposeProxy) {
					// Make invocation available if necessary.
					oldProxy = AopContext.setCurrentProxy(proxy);
					setProxyContext = true;
				}
				// May be null. Get as late as possible to minimize the time we
				// "own" the target, in case it comes from a pool...
				target = getTarget();
				if (target != null) {
					targetClass = target.getClass();
				}
				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
				Object retVal;
				// Check whether we only have one InvokerInterceptor: that is,
				// no real advice, but just reflective invocation of the target.
				if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
					// We can skip creating a MethodInvocation: just invoke the target directly.
					// Note that the final invoker must be an InvokerInterceptor, so we know
					// it does nothing but a reflective operation on the target, and no hot
					// swapping or fancy proxying.
					Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
					retVal = methodProxy.invoke(target, argsToUse);
				}
				else {
					// We need to create a method invocation...
					retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
				}
				retVal = processReturnType(proxy, target, method, retVal);
				return retVal;
			}
			finally {
				if (target != null) {
					releaseTarget(target);
				}
				if (setProxyContext) {
					// Restore old proxy.
					AopContext.setCurrentProxy(oldProxy);
				}
			}
		}
		//省略...
	}
 
 
	/**
	 * Implementation of AOP Alliance MethodInvocation used by this AOP proxy.
	 */
	private static class CglibMethodInvocation extends ReflectiveMethodInvocation {
 
		private final MethodProxy methodProxy;
 
		private final boolean publicMethod;
 
		public CglibMethodInvocation(Object proxy, Object target, Method method, Object[] arguments,
				Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {
 
			super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);
			this.methodProxy = methodProxy;
			this.publicMethod = Modifier.isPublic(method.getModifiers());
		}
 
		/**
		 * Gives a marginal performance improvement versus using reflection to
		 * invoke the target when invoking public methods.
		 */
		@Override
		protected Object invokeJoinpoint() throws Throwable {
			if (this.publicMethod) {
				return this.methodProxy.invoke(this.target, this.arguments);
			}
			else {
				return super.invokeJoinpoint();
			}
		}
	}
 
}

```
#### 8、各种Advice是的执行顺序是如何和方法调用进行结合的？

JdkDynamicAopProxy 和CglibAopProxy只是创建代理方式的两种方式而已，实际上我们为方法调用添加的各种Advice的执行逻辑都是统一的。在Spring的底层，会把我们定义的各个Adivce分别 包裹成一个 MethodInterceptor,这些Advice按照加入Advised顺序，构成一个AdivseChain.

比如我们下面的代码：
```java
        //5. 添加不同的Advice
 
        proxyFactoryBean.addAdvice(afterReturningAdvice);
        proxyFactoryBean.addAdvice(aroundAdvice);
        proxyFactoryBean.addAdvice(throwsAdvice);
        proxyFactoryBean.addAdvice(beforeAdvice);
        proxyFactoryBean.setProxyTargetClass(false);
        //通过ProxyFactoryBean生成
        TicketService ticketService = (TicketService) proxyFactoryBean.getObject();
        ticketService.sellTicket();

```
当我们调用 ticketService.sellTicket()时，Spring会把这个方法调用转换成一个MethodInvocation对象，然后结合上述的我们添加的各种Advice,组成一个ReflectiveMethodInvocation:

![image.png](https://upload-images.jianshu.io/upload_images/15204062-d126608e309e313b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 各种Advice本质而言是一个方法调用拦截器，现在让我们看看各个Advice拦截器都干了什么？

![](https://upload-images.jianshu.io/upload_images/15204062-1f41786ee9e602cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

*  拦截原理
![](https://upload-images.jianshu.io/upload_images/15204062-a0ffff221e4a2a42.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

