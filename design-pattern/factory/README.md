## 什么是工厂模式
老规矩先用比较难理解的官方语言来表达，工厂模式是 Java 中最常用的设计模式之一。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。在工厂模式中，我们在创建对象时不会对客户端暴露创建逻辑，并且是通过使用一个共同的接口来指向新创建的对象。现在在以一个具体的例子来简单简化下工厂模式。假如我现在去餐馆吃饭，餐馆有回锅肉，有鱼，有烤鸭可供我选择，然后我点餐餐馆为我做出具体的菜。说完这个例子下面我也会用代码来实现这个例子，给大家简单理解下工厂模式。

首先我们来创建一个餐馆的接口,因为这里只要有做菜就行，所以写一个cook的方法。

```java
public interface Resaurant {
    void cook();
}
```

接下来写三个实现类，分别是做回锅肉的，做鱼的，做烤鸭的，用这三个实现类去实现餐馆的接口。

```java
public class Duck implements Resaurant{
    public void cook() {
        System.out.println("来一份烤鸭");
    }
}
```

```java
public class Fish implements Resaurant{
    public void cook() {
        System.out.println("来一份红烧鱼");
    }
}
```

```java
public class Meet implements Resaurant {
    public void cook() {
        System.out.println("来一份回锅肉");
    }
}
```

现在餐馆已经具备了做回锅肉，做鱼，做烤鸭的功能，但是客人来了并不知道餐馆有这些菜，这时候就需要我们来给餐馆做一个菜单，客人来了就可以根据菜单点餐，这样就省去了很多的麻烦对不对？

```java
public class Wait {
    public static final int MEAN_MEET = 1;
    public static final int MEAN_FISH = 2;
    public static final int MEAN_DUCK = 3;

    public static Resaurant getMean(int meantype){
        switch (meantype){
            case MEAN_MEET :
                return new Meet();
            case MEAN_FISH :
                return new Fish();
            case MEAN_DUCK :
                return new Duck();
            default:
                return null;
        }
    }
}
```

菜单也有了，现在客人来了可以点餐了，假如客人根据菜单点了一份烤鸭，那餐馆就可以直接给客人制作一份美味的烤鸭
```java
public class Factory {
    public static void main(String[] args) {
        //简单工厂模式
        Resaurant resaurant = Wait.getMean(Wait.MEAN_DUCK);
        resaurant.cook();
    }
}
```

来看看执行结果

![image.png](https://upload-images.jianshu.io/upload_images/15533540-3ab3f9b916823700.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

通过上面的例子就给大家简单介绍了一下简单工厂模式。但是工厂模式除了简单工厂模式还有工厂方法模式和抽象工厂模式，下面我再已餐馆这个例子给大家扩展一下工厂方法模式。工厂方法模式就是把简单工厂中具体的工厂类，划分成两层：抽象工厂层+具体的工厂子类层。

首先我们来创建一个抽象工厂类

```java
public abstract class CookFactory {
    public abstract Resaurant createRestaurant();
}
```

创建两个具体需要的产品实现类去继承上面这个抽象类
```java
public class DuckFactory extends CookFactory {
    @Override
    public Resaurant createRestaurant() {
        return new Duck();
    }
}
```

```java
public class FishFactory extends CookFactory {
    public Resaurant createRestaurant() {
        return new Fish();
    }
}
```

烤鸭和鱼都做好了，开始享用吧！

```java
public class Cook {
    //工厂方法模式
    public static void main(String[] args) {
        Resaurant duck = new DuckFactory().createRestaurant();
        duck.cook();
        Resaurant fish = new FishFactory().createRestaurant();
        fish.cook();
    }
}
```
看下执行结果

![image.png](https://upload-images.jianshu.io/upload_images/15533540-b15279d8f829d391.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

千呼万唤始出来，红烧鱼和烤鸭出锅咯！

## 优点和缺点
优点： 1、一个调用者想创建一个对象，只要知道其名称就可以了。 2、扩展性高，如果想增加一个产品，只要扩展一个工厂类就可以。 3、屏蔽产品的具体实现，调用者只关心产品的接口。

缺点：每次增加一个产品时，都需要增加一个具体类和对象实现工厂，使得系统中类的个数成倍增加，在一定程度上增加了系统的复杂度，同时也增加了系统具体类的依赖。这并不是什么好事。

