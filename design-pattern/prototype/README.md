### 原型模式
原型模式是用于创建重复的对象，同时又能保证性能。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式。这种模式是实现了一个原型接口，该接口用于创建当前对象的克隆。当直接创建对象的代价比较大时，则采用这种模式。例如，一个对象需要在一个高代价的数据库操作之后被创建。我们可以缓存该对象，在下一个请求时返回它的克隆，在需要的时候更新数据库，以此来减少数据库调用。
现在我们就用原型模式来模拟复制东西，来复制一份个人简历。

下面就是某某的求职简历
```
public class Resume implements Cloneable {
    private String name;
    private String birthday;
    private String sex;
    private String school;
    private String timearea;
    private String company;

    public Resume(String name) {
        this.name = name;
    }

    /**
     * @desc 设定个人基本信息
     * @param birthday 生日
     * @param sex 性别
     * @param school 毕业学校
     * @return void
     */
    public void setPersonInfo(String birthday,String sex,String school){
        this.birthday = birthday;
        this.sex = sex;
        this.school = school;
    }

    /**
     * @desc 设定工作经历
     * @param timearea 工作年限
     * @param company 所在公司
     * @return void
     */
    public void setWorkExperience(String timearea,String company){
        this.timearea = timearea;
        this.company = company;
    }

    /**
     * 克隆该实例
     */
    public Object clone(){
        Resume resume = null;
        try {
            resume = (Resume) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return resume;
    }

    public void display(){
        System.out.println("姓名：" + name);
        System.out.println("生日:" + birthday + ",性别:" + sex + ",毕业学校：" + school);
        System.out.println("工作年限:" + timearea + ",公司:" + company);
    }
}
```

客户端的验证测试操作
```
public static void main(String[] args) {
        //原型A对象
        Resume a = new Resume("闫猪头");
        a.setPersonInfo("2.16", "男", "家里蹲大学");
        a.setWorkExperience("2018.09.05", "搬砖工地");

        //克隆B对象
        Resume b = (Resume) a.clone();

        //输出A和B对象
        System.out.println("----------------A--------------");
        a.display();
        System.out.println("----------------B--------------");
        b.display();

        /*
         * 测试A==B?
         * 对任何的对象x，都有x.clone() !=x，即克隆对象与原对象不是同一个对象
         */
        System.out.print("A==B?");
        System.out.println( a == b);

        /*
         * 对任何的对象x，都有x.clone().getClass()==x.getClass()，即克隆对象与原对象的类型一样。
         */
        System.out.print("A.getClass()==B.getClass()?");
        System.out.println(a.getClass() == b.getClass());
    }
```
执行结果

![image.png](https://upload-images.jianshu.io/upload_images/15533540-94b6c36cef62a453.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 总结
优点： 1、性能提高。 

2、逃避构造函数的约束。

缺点： 1、配备克隆方法需要对类的功能进行通盘考虑，这对于全新的类不是很难，但对于已有的类不一定很容易，特别当一个类引用不支持串行化的间接对象，或者引用含有循环结构的时候。
 2、必须实现 Cloneable 接口。


