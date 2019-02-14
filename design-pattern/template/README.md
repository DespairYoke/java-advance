### 模板模式

定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。 模板方法使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤。通俗的说的就是有很多相同的步骤的，在某一些地方可能有一些差别适合于这种模式，如大话设计模式中说到的考试场景中，每个人的试卷都是一样的，只有答案不一样。这种场景就适合于模板方法模式。我这次自己写的是一个汽车启动的过程，每一种汽车启动的过程都基本是一样的流程，无非是这一过程中存在一些细小差别。
总的来说，模板模式就是通过抽象类来定义一个逻辑模板，逻辑框架、逻辑原型，然后将无法决定的部分抽象成抽象类交由子类来实现，一般这些抽象类的调用逻辑还是在抽象类中完成的。这么看来，模板就是定义一个框架，比如造车子，需要造车轮，车身，发动机，车灯等部分组成，不同的车的区别就在于这些部件的优劣而已，但是总少不了这些东西。下面我就用一个例子来造一辆路虎越野车和一辆奥迪轿车。

### 模板模式实战

首先需要创建一个模板抽象父类，将造车需要的模块创建好。

```
public abstract class CarTemplate {
    protected String name;

    protected CarTemplate(String name){
        this.name = name;
    }

    protected abstract void buildWheel();

    protected abstract void buildEngine();

    protected abstract void buildCarbody();

    protected abstract void buildCarlight();

    public final void buildCar(){
        buildWheel();
        buildEngine();
        buildCarbody();
        buildCarlight();
    }
}
```

然后创建一个造路虎车的子类，用这个子类去继承造车的公有模块类CarTemplate。然后在方法中完善造路虎车特有的功能，代码如下：

```
public class LandRover extends CarTemplate{

    public LandRover(String name) {
        super(name);
    }

    @Override
    protected void buildWheel() {
        System.out.println(name + "越野车轮");

    }

    @Override
    protected void buildEngine() {
        System.out.println(name + "柴油发动机");
    }

    @Override
    protected void buildCarbody() {
        System.out.println(name + "SUV越野车型");
    }

    @Override
    protected void buildCarlight() {
        System.out.println(name + "普通车灯");
    }
}
```
再创建一个造奥迪的子类，同样用这个子类继承模板类，并完善造奥迪的方法，代码如下：

```
public class Audi extends CarTemplate {
    public Audi(String name) {
        super(name);
    }

    @Override
    protected void buildWheel() {
        System.out.println(name + "的普通轿车车轮");
    }

    @Override
    protected void buildEngine() {
        System.out.println(name + "的汽油发动机");
    }

    @Override
    protected void buildCarbody() {
        System.out.println(name + "的豪华舒适性车身");
    }

    @Override
    protected void buildCarlight() {
        System.out.println(name + "的独特魔力车灯");
    }
}
```
### 测试
```
public class TemplateTest {
    public static void main(String[] args) {
        CarTemplate car1 = new LandRover("路虎");
        CarTemplate car2 = new Audi("奥迪");
        car1.buildCar();
        car2.buildCar();
    }
}
```
### 执行结果
所有步骤都已完成，奥迪和路虎车运用统一模板，但是通过在子类中完善各自不同的方法而达到目的

![image.png](https://upload-images.jianshu.io/upload_images/15533540-35f7e18a84fdc0f4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)