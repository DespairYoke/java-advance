## 什么是代理模式
抽象点说是一个类代表另一个类的功能，或者说一个对象为另一个对象提供一个代理或者占位符以控制对这个对象的访问。同样我也会举例子来说明，这里我举一个买车票的例子。通常我们我们买车票需要去车站买，但是这样会很麻烦，可能要坐很久的车去车站，然后在排队买票。但是如果我们去一个卖车票的代理点买车票可能就会省去这么多的事情。这样车票售卖处就代理你购买车票。

## 代理模式结构
![image.png](https://upload-images.jianshu.io/upload_images/15533540-6ee9f8867c9cf762.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 应用
下面我们就用具体的代码来实现上面这个买车票的静态代理。

首先创建一个售票服务的接口，它有售票咨询和退票的业务可以供客户选择。
```java
public interface TicketService {
    //售票
    void sellTicket();
    //咨询
    void Consultation();
    //退票
    void ReturnTicket();
}
```

然后创建一个售票服务接口实现类，就好比是车站。
```java
public class Station implements TicketService {
    @Override
    public void sellTicket() {
        System.out.println("售票");
    }
    @Override
    public void Consultation() {
        System.out.println("咨询");
    }
    @Override
    public void ReturnTicket() {
        System.out.println("退票");
    }
}
```
然后创建一个代理售票点
```java
public class StationProxy implements TicketService {
    private Station station;

    public StationProxy(Station station){
        this.station = station;
    }
    @Override
    public void sellTicket() {
        System.out.println("欢迎使用车票代售点进行购票，每张票将会收取5元手续费！");
        station.sellTicket();
        System.out.println("欢迎下次光临");
    }
    @Override
    public void Consultation() {
        System.out.println("欢迎咨询，咨询不收取费用");
        station.Consultation();
        System.out.println("欢迎下次光临");
    }
    @Override
    public void ReturnTicket() {
        System.out.println("欢迎使用车票代售点进行退票，每张票将会收取5元手续费！");
        station.ReturnTicket();
        System.out.println("欢迎下次光临");
    }
}
```

创建购买车票的角色,去代理点完成购买车票的需求
```java
public class ProxyTest {
    public static void main(String[] args) {
        Station station = new Station();
        StationProxy stationProxy = new StationProxy(station);
        stationProxy.sellTicket();
    }
}
```
可以看到这个购买车票的客户是去的车票代理点，直接购买车票，那代理点能否帮助他正常购买到车票呢？请看下面的执行结果

![image.png](https://upload-images.jianshu.io/upload_images/15533540-818864dbf8b81779.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


从结果看到售票代理点成功帮助客户购买到车票，节省了客户去车站排队等待的时间和精力。代理模式有点像是委派模式中的中介，前面的文章也提到过静态代理和策略模式是委派模式的一种组合。那当然除了静态代理还有动态代理和CGLIB代理，感兴趣的伙伴可以自己去研究研究。