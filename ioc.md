### 1 Spring的IoC容器系列
对IoC容器的使用者来说，我们经常接触到的BeanFactory和ApplicationContext都可以看成是容器的具体表现形式。

如果深入到Spring的实现中去看，所说的IoC容器，实际上代表着一系列功能各异的容器产品，只是容器的功能有大有小，有各自的特点。

Spring通过定义BeanDefinition来管理基于Spring的应用中的各种对象以及它们之间的相互依赖关系。对IoC容器来说，BeanDefinition就是依赖反转模式中管理的对象依赖关系的数据抽象，

也是容器实现依赖反转功能的核心数据结构，依赖反转功能都是围绕这个BeanDefinition的处理来完成的。

2 Spring IoC容器的设计


IoC容器的接口设计图

![](https://upload-images.jianshu.io/upload_images/4685968-303f37bd5de5661a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)


* 从接口BeanFactory到HierarchicalBeanFactory，再到ConfigurableBeanFactory，是一条主要的BeanFactory设计路径

BeanFactory定义了基本的IoC容器的规范。包括了getBean()（通过这个方法可以从容器中取得Bean）。

HierarchicalBeanFactory接口在继承了BeanFactory后，增加了getParentBeanFactory()，使BeanFactory具备了双亲IoC容器的管理功能。

在接下来的ConfigurableBeanFactory中，定义了一些对BeanFactory的配置功能，比如通过setParentBeanFactory()设置双亲IoC容器，通过addBeanPostProcessor()配置Bean后置处理器，等等

* 第二条接口设计主线是，以ApplicationContext为核心的接口设计

我们常用的应用上下文基本上都是ConfigurableApplicationContext或者WebApplicationContext的实现

在这个接口体系中，ListableBeanFactory和HierarchicalBeanFactory两个接口，连接BeanFactory接口定义和ApplicationConext应用上下文的接口定义。

在ListableBeanFactory接口中，细化了许多BeanFactory的接口功能，比如定义了getBeanDefinitionNames()接口方法；对于HierarchicalBeanFactory接口，我们在前文中已经提到过；对于ApplicationContext接口，它通过继承MessageSource、ResourceLoader、ApplicationEventPublisher接口，在BeanFactory简单IoC容器的基础上添加了许多对高级容器的特性的支持

* 这里涉及的是主要接口关系，而具体的IoC容器都是在这个接口体系下实现的，比如DefaultListableBeanFactory，这个基本IoC容器的实现就是实现了ConfigurableBeanFactory，从而成为一个简单IoC容器的实现。

像其他IoC容器，比如XmlBeanFactory，都是在DefaultListableBeanFactory的基础上做扩展

* 这个接口系统是以BeanFactory和ApplicationContext为核心

而BeanFactory又是IoC容器的最基本接口，在ApplicationContext的设计中，一方面，可以看到它继承了BeanFactory接口体系的接口，具备了BeanFactory IoC容器的基本功能

另一方面，通过继承MessageSource、ResourceLoadr、ApplicationEventPublisher这些接口，BeanFactory为ApplicationContext赋予了更高级的IoC容器特性。

对于ApplicationContext而言，为了在Web环境中使用它，还设计了WebApplicationContext接口，而这个接口通过继承ThemeSource接口来扩充功能。


### ApplicationContext
ApplicationContext是Spring提供的一个高级的IoC容器，它除了能够提供IoC容器的基本功能外，还为用户提供了以下的附加服务。

支持信息源，可以实现国际化。（实现MessageSource接口）

访问资源。(实现ResourcePatternResolver接口，这个后面要讲)

支持应用事件。(实现ApplicationEventPublisher接口)

在ApplicationContext中提供的附加服务

### ApplicationContext容器的设计原理

我们以常用的FileSystemXmlApplicationContext的实现为例说明ApplicationContext容器的设计原理。

```java
public FileSystemXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)  
            throws BeansException {    
        super(parent);  
        setConfigLocations(configLocations);  
        if (refresh) {  
            refresh();  
        }  
    }

```

### autowiring（自动装配的实现）

在自动装配中，Spring IoC容器的依赖自动装配功能，不需要对Bean属性的依赖关系做显式的声明，只需要在配置好autowiring属性，IoC容器会自动使用反射查找属性的类型和名称，然后基于属性的类型或者名称来自动匹配容器中管理的Bean，从而自动地完成依赖注入。

应用第一次通过getBean方法(配置了lazy-init预实例化属性的除外)向IoC容器索取Bean时，容器创建Bean实例对象，并且对Bean实例对象进行属性依赖注入，AbstractAutoWireCapableBeanFactory的populateBean方法就是实现Bean属性依赖注入的功能。

在populateBean的实现中，在处理一般的Bean之前，先对autowiring属性进行处理。如果当前的Bean配置了autowire_by_name和autowire_by_type属性，那么调用相应的autowireByName方法autowireByType方法。对于autowire_by_name，它首先通过反射机制从当前Bean中得到需要注入的属性名，然后使用这个属性名向容器申请与之同名的Bean，这样实际又触发了另一个Bean的生成和依赖注入的过程。

autowiring的实现过程：

a. 对Bean的属性迭代调用getBean方法，完成依赖Bean的初始化和依赖注入。

b. 将依赖Bean的属性引用设置到被依赖的Bean属性上。

c. 将依赖Bean的名称和被依赖的Bean的名称存储在IoC容器的集合中。

