### jdk动态代理

Spring提供了两种方式来生成代理对象，JDKProxy和Cglib。具体使用哪种方式由AopProxyFactory根据AdvisedSupport对象的配置来决定。
默认的策略是如果目标类是接口，则使用JDK动态代理技术，否则使用Cglib来生成代理。Cglib是基于字节码技术的，使用的是ASM。
asm是一个java字节码操纵框架，它能被用来动态生成类或者增强既有类的功能。ASM可以直接产生二进制class文件，
也可以在类被加载入JVM之前动态改变类行为。下面重点来看看JDK动态代理技术。

* 接口
```java
public interface UserService {

    void add();
}

```

* 实现类
```java
public class UserServiceImpl implements UserService {
    @Override
    public void add() {
        System.out.println("-----add-------");
    }
}
```

* 调用处理器
```java
public class MyInvocationHandler implements InvocationHandler {

    private Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("-----before------");

        method.invoke(target,args);
        System.out.println("-----after------");
        return null;
    }

    public Object getProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }
}
```

* 启动类
```java
public class BootStrap {

    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();

        MyInvocationHandler myInvocationHandler = new MyInvocationHandler(userService);

        UserService proxyInstance = (UserService)myInvocationHandler.getProxy();

        proxyInstance.add();
    }

}
```

### 主要方法分析
```
return Proxy.newProxyInstance(this.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
```
源码展示
```java
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         */
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }

            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }    
```
这个代码是JDK1.8中的，里面用到了1.8的一些语法，如果不太了解，建议先看看<java8 in action>这本书。代码看着不少，
实际上都在进行一些安全校验，包装之类的，真正有用的就两句： 
Class<?> cl = getProxyClass0(loader, intfs);
这句话查找或者生成代理类。跟进去：
```java
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        return proxyClassCache.get(loader, interfaces);
    }
```
就是从缓存里把接口拿将出来。然后用return cons.newInstance(new Object[]{h});这一句将接口用invocationHandler进行包装。

Java开发中常说动态代理和静态代理，而AOP就是动态代理，因为代理的类是在运行时才生成的。而一般说的代理模式写成的代码是编译期就已经生成的，叫静态代理。

