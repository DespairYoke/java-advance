## 什么是委派模式

这里我以一个简单的例子来形容，公司有大boss和部门leader以及部门里的员工，现在大boss给部门leader下达了任务，而作为部门leader肯定是对任务进行具体的规划然后委派给部门里的员工去完成。这中间的关系就类似于委派模式。作为大boss他可以不知道任务具体是哪个员工做的，甚至可以不知道员工的存在，只要以结果为导向，最终能完成任务就可以。作为部门leader相当于一个中介，全程跟进员工的工作进度，最终像大boss汇报工作。作为员工只要完成任务可以不知道任务最终是大boss 下达的。
接下来就以具体的代码来实现这样一个委派模式的关系

### 创建一个员工干活的接口Target

```java
public interface Target {
    public void dosomething(String commond);
}
```

### 创建两个员工类ATarget和BTarget

用这两个员工去实现干活的接口，但是现在员工还不知道他具体需要干什么，因为是部门leader给他们分配任务。

A员工的工作类
```java
public class ATarget implements Target {
    //A员工做具体的事情
    @Override
    public void dosomething(String commond) {
        System.out.println("A员工做具体的事情"+commond + "");
    }
}
```

B员工工作类

```java
public class BTarget implements Target {
    //B员工需要做的事情
    @Override
    public void dosomething(String commond) {
        System.out.println("B员工做具体的事情"+commond);
    }
}
```
创建一个部门leader的类去实现Target，用这个leader去给员工分配具体的工作任务。

```java
public class Leader implements Target {
    //领导委派员工做具体的事情
    private Map<String,Target> target = new HashMap<String, Target>();
    public Leader(){
        //领导委派员工A和员工B分别做不同的事情
        target.put("打印文件", new ATarget());
        target.put("测试项目", new BTarget());
    }
    @Override
    public void dosomething(String commond) {
        target.get(commond).dosomething(commond);
    }
}
```
接下来就是大boss上场，但是大boss不会去直接给做事的员工下达命令，而是给leader直接下达命令,比如下达一个打印文件的工作。
```java
public class DelegateTest {
    public static void main(String[] args) {
        new Leader().dosomething("打印文件");
    }
}
```
可以看出来部门leader类是一个至关重要的类，起到了承上启下的中介作用，这就是一个委派。然后我们来运行程序，来看看大boss没有给员工直接下达工作命令，员工能否完成任务。

![image.png](https://upload-images.jianshu.io/upload_images/15533540-60424a7bb99870b9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以清楚的看出A员工完成了大boss交给的打印文件的任务（顺利完成任务，可以去申请加薪了）

从上面可以看出来委派模式就是静态代理和策略模式的一种特殊组合，代理模式注重的是过程，委派模式注重的是结果。策略模式注重的是可扩展（外部扩展），委派模式注重的是内部的灵活和复用（委派模式以结果为导向）。
