## cglib动态代理
### 引入maven依赖
```xml
<!-- https://mvnrepository.com/artifact/cglib/cglib -->
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.2.5</version>
</dependency>
```

### 创建被代理对象的自定义
```java
public class Dao {

    public void update() {
        System.out.println("PeopleDao.update()");
    }

    public void select() {
        System.out.println("PeopleDao.select()");
    }
}
```

### 创建代理对象
```java
public class DaoProxy implements MethodInterceptor {
    @Override
    public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("Before Method Invoke");
        methodProxy.invokeSuper(object, objects);
        System.out.println("After Method Invoke");

        return object;
    }
}
```

### 创建启动类
```java
public class BootStrap {

    public static void main(String[] args) {

        DaoProxy daoProxy = new DaoProxy();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Dao.class);
        enhancer.setCallback(daoProxy);

        Dao dao = (Dao) enhancer.create();
        dao.update();
        dao.select();
    }
}
```