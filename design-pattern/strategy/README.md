###  策略模式
策略模式定义了一系列的算法，并将每一个算法封装起来，而且使他们之间可以相互替换，策略模式可以在不影响客户端的情况下发生变化。
策略模式是处理算法不同变体的一种成熟模式，策略模式通过接口或抽象类封装算法的标识，即在接口中定义一个抽象方法，实现该接口的类将实现接口中的抽象方法。策略模式把针对一个算法标识的一系列具体算法分别封装在不同的类中，使得各个类给出的具体算法可以相互替换。

策略模式的结构：

* 策略（Strategy）：策略是一个接口，该接口定义若干个算法标识，即定义了若干个抽象方法。

* 具体策略（ConcreteStrategy）：具体策略是实现策略接口的类。具体策略实现策略接口所定义的抽象方法，即给出算法标识的具体算法。

* 上下文（Context）：上下文是依赖于策略接口的类，即上下文包含有策略声明的变量。上下文中提供了一个方法，该方法委托策略变量调用具体策略所实现的策略接口中的方法。

### 举例
假如某个游乐场有几种购买门票的方式，普通的游客是不能享受优惠，经常来的游客可以办理年卡享受8折优惠，当然对于身高在1米2以下的儿童给与了5折的优惠。这是游乐场的一种营销策略。对于这三种营销策略我们可以使用if else的语句在一个类中就能实现，可是对于后续的维护可能就有其他的麻烦的地方了。如果后面又有更多的优惠策略难道我们就不停的加 else语句吗？这样就会造成代码看上去很复杂，且不容易维护。说了这些策略模式就可以解决这样的问题。

### 实战
创建一个策略角色的接口，很简单的一个接口，写一个买票的方法。
```
public interface TicketStrategy {
    void BuyTicket();
}
```

接下来创建三个具体策略类，对应的就是三种购票的方式。

普通的游客购票
```
public class Normal implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("普通游客没有优惠");
    }
}
```

办理年卡的用户购票
```
public class Vip implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("办年卡游客享受8折优惠");
    }
}
```

1米2以下儿童购票
```
public class Children implements TicketStrategy {
    @Override
    public void BuyTicket() {
        System.out.println("1米2以下儿童享受5折优惠");
    }
}
```

创建上下文类。
```
public class Context {
    private TicketStrategy ticketStrategy;

    public Context(TicketStrategy strategy){
        this.ticketStrategy = strategy;
    }

    public void setTicketStrategy(TicketStrategy ticketStrategy) {
        this.ticketStrategy = ticketStrategy;
    }

    public void BuyTicket(){
        this.ticketStrategy.BuyTicket();
    }
}
```

### 测试
```
public class StrategyTest {
    public static void main(String[] args) {
        System.out.println("普通游客策略：");
        Context context = new Context(new Normal());
        context.BuyTicket();

        System.out.println("年卡VIP游客策略：");
        context.setTicketStrategy(new Vip());
        context.BuyTicket();

        System.out.println("1米2以下儿童策略：");
        context.setTicketStrategy(new Children());
        context.BuyTicket();
    }
}
```
通过上面的例子可以看出这样写就能让代码很简洁明了。后面如果有新的售票方式我们只需要创建具体策略类，然后调用就好了。

### 优点和缺点
优点：
(1)上下文和具体策略是松耦合关系。因此上下文只知道它要使用某个实现Strategy接口类的实例，但不需要知道具体是哪个类。
(2)策略模式满足“开-闭原则”。当增加新的具体策略时，不需要修改上下文类的代码，上下文就可以引用新的具体策略的实例。

缺点：通过上面Demo我们会发现调用者必须知道所有的策略类，并自行决定使用哪一个策略类。这就意味着客户端必须理解这些算法的区别，以便适时选择恰当的算法类并且由于策略模式把每个具体的策略实现都单独封装成为类，如果备选的策略很多的话，那么对象的数目就会很可观。

