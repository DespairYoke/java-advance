### 观察者模式

观察者模式也被称为发布-订阅（Publish/Subscribe）模式，它属于行为型模式的一种。观察者模式定义了一种一对多的依赖关系，一个主题对象可被多个观察者对象同时监听。当这个主题对象状态变化时，会通知所有观察者对象并作出相应处理逻辑。

### 观察者模式包含四个角色

抽象被观察者角色：也就是一个抽象主题，它把所有对观察者对象的引用保存在一个集合中，每个主题都可以有任意数量的观察者。抽象主题提供一个接口，可以增加和删除观察者角色。一般用一个抽象类和接口来实现。

抽象观察者角色：为所有的具体观察者定义一个接口，在得到主题通知时更新自己。

具体被观察者角色：也就是一个具体的主题，在集体主题的内部状态改变时，所有登记过的观察者发出通知。

具体观察者角色：实现抽象观察者角色所需要的更新接口，一边使本身的状态与制图的状态相协调。


前面说到观察者模式也被称为发布-订阅模式，这里我就用一个微信公众号发布文章和用户订阅公众号接收文章的例子。用户订阅了公众号就可以接收公众号发布的信息文章，当用户对此类型公众号不感兴趣的时候就可以取消订阅，这时候就接收不到公众号发布的文章。用户就是一个观察者，公众号就是被观察者，被观察者与观察者之间的关系是一对多。接下来我将用具体代码来实现这个demo。

### 具体实现

* 1.首先创建一个被观察者抽象接口,创建注册观察者，取消观察者和提醒所有观察者更新消息的方法，用途是用户订阅、取消订阅和接收消息。
```
public interface Observable {
    //注册观察者
    void registerObserver(Observer observer);
    //取消观察者
    void removeObserver(Observer observer);
    //通知所有观察者更新消息
    void notifyObserver();
}
```

* 2.定义一个抽象观察者接口
```
public interface Observer {
    void update(String message);

}
```

* 3.定义被观察者，实现了Observable接口，对Observable接口的三个方法进行了具体实现，同时有一个List集合，用以保存注册的观察者，等需要通知观察者时，遍历该集合即可。
```
public class WeChatServer implements Observable{

    private List<Observer> list;
    private String message;

    public WeChatServer(){
        list = new ArrayList<Observer>();
    }

    @Override
    public void registerObserver(Observer o) {
        list.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        if(!list.isEmpty()){
            list.remove(o);
        }
    }

    @Override
    public void notifyObserver() {
        for(int i = 0; i < list.size(); i++){
            Observer observer = list.get(i);
            observer.update(message);
        }
    }
    public void setInfomation(String s){
        this.message = s;
        System.out.println("公众号推送消息是"+s);
        notifyObserver();
    }
}
```

* 4.创建具体的观察者，这里具体的观察者也就是用户。
```
public class User implements Observer {
    private String name;
    private String message;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        this.message = message;
        read();
    }

    public void read(){
        System.out.println(name + "接收到推送消息" + message);
    }
}
```

* 5.接下来就是具体的测试类，假设现在有三个用户订阅了公众号，公众号发布了一条信息是PHP是世界上最好的语言，此时java开发工程师李四接收到信息后颇为不满，于是果断取消订阅。后来公众号又发布了一条信息是：java是世界上最好的语言，此时取消订阅的李四已经接收不到这条信息了。

```
public class ObserverTest {
    public static void main(String[] args) {
        WeChatServer server = new WeChatServer();

        Observer userZhang = new User("ZhangSan");
        Observer userLi = new User("LiSi");
        Observer userWang = new User("WangWu");

        server.registerObserver(userZhang);
        server.registerObserver(userLi);
        server.registerObserver(userWang);
        server.setInfomation("PHP是世界上最好用的语言！");

        System.out.println("----------------------------------------------");
        //李四取消订阅
        server.removeObserver(userLi);
        server.setInfomation("JAVA是世界上最好用的语言！");
    }
}
```

执行结果如下：

![image.png](https://upload-images.jianshu.io/upload_images/15533540-cec57d1f7bca3ce7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)