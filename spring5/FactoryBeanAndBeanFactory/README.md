## 一概述
BeanFactory 与 FactoryBean的区别， 两个名字很像，面试中也经常遇到，所以容易搞混，现从源码以及示例两方面来分析。

## 二、源码
### 2.1 BeanFactory
BeanFactory定义了 IOC 容器的最基本形式，并提供了 IOC 容器应遵守的的最基本的接口，也就是 Spring IOC 所遵守的最底层和最基本的编程规范。

BeanFactory仅是个接口，并不是IOC容器的具体实现，具体的实现有：如 DefaultListableBeanFactory 、 XmlBeanFactory 、 ApplicationContext 等。
```java
public interface BeanFactory {
    //FactoryBean前缀
    String FACTORY_BEAN_PREFIX = "&";

    //根据名称获取Bean对象
    Object getBean(String name) throws BeansException;

    ///根据名称、类型获取Bean对象
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;

    //根据类型获取Bean对象
    <T> T getBean(Class<T> requiredType) throws BeansException;

    //根据名称获取Bean对象,带参数
    Object getBean(String name, Object... args) throws BeansException;

    //根据类型获取Bean对象,带参数
    <T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

    //是否存在
    boolean containsBean(String name);

    //是否为单例
    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

    //是否为原型（多实例）
    boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

    //名称、类型是否匹配
    boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException;

    //获取类型
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    //根据实例的名字获取实例的别名
    String[] getAliases(String name);
}
```

### 2.1 FactoryBean
FactoryBean工厂类接口，用户可以通过实现该接口定制实例化 Bean 的逻辑。
```java
public interface FactoryBean<T> {

    //FactoryBean 创建的 Bean 实例
    T getObject() throws Exception;

    //返回 FactoryBean 创建的 Bean 类型
    Class<?> getObjectType();

    //返回由 FactoryBean 创建的 Bean 实例的作用域是 singleton 还是 prototype
    boolean isSingleton();
}
```
## 三、示例
3.1 普通bean
public class Dog {
    private String msg;

    public Dog(String msg){
        this.msg=msg;
    }
    public void run(){
        System.out.println(msg);
    }
}
### 3.2 实现了FactoryBean的bean
```java
public class DogFactoryBean implements FactoryBean<Dog>{

    public Dog getObject() throws Exception {
        return new Dog("DogFactoryBean.run");
    }

    public Class<?> getObjectType() {
        return DogFactoryBean.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
```
### 3.3 配置文件
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                     http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
                     http://www.springframework.org/schema/context
                     http://www.springframework.org/schema/context/spring-context-3.0.xsd
                     http://www.springframework.org/schema/aop
                     http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
                     http://www.springframework.org/schema/tx
                     http://www.springframework.org/schema/tx/spring-tx-3.0.xsd">

<bean id="dog" class="com.zwd.factorybean.domain.Dog" >
    <constructor-arg value="Dog.run"/>
</bean>

<bean id="dogFactoryBean" class="com.zwd.factorybean.factory.DogFactoryBean">
</bean>
</beans>
```
### 3.4 测试
```java
@Test
public void testBean() throws Exception {
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

    Dog dog1 = (Dog) ctx.getBean("dog");
    dog1.run();

    Dog dog2 = (Dog) ctx.getBean("dogFactoryBean");
    dog2.run();

    //使用&前缀可以获取FactoryBean本身
    FactoryBean dogFactoryBean = (FactoryBean) ctx.getBean("&dogFactoryBean");
    Dog dog3= (Dog) dogFactoryBean.getObject();
    dog3.run();
}
```
结果输出：
```java
Dog.run
DogFactoryBean.run
DogFactoryBean.run
```