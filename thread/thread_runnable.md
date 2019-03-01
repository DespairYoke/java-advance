## Thread和Runnable的使用

#### 基本知识回顾 

线程是比进程更小的能独立运行的基本单位,他是进程的一部分,一个进程可以有多个进程,但至少有一个线程,即主线程执行(java的 main方法).
我们既可以编写单线程应用也可以编写多线程樱应用.

一个进程中的多个线程可以并发(同步)的去执行,在一些执行时间长,需要等待的任务上(例如:文件读写和网络传输等),多线程就比较有用了.

<div id="lijie"></div>

**怎么理解多线程?**

1.进程就是一个工厂,一个线程就是工厂中的一条生产线,一个工厂至少有一条生产线,只有一条生产线就是单线程应用,拥有多条生产线就是多线程应用
多条生产线可以同时运行.

2.我们使用迅雷可以同时下载多个视频，迅雷就是进程，多个下载任务就是线程，这几个线程可以同时运行去下载视频
  
多线程可以共享内存,充分利用CPU,通过提高资源(内存和CPU)使用率从而提高程序的执行效率.CPU使用抢占式调度模式在多个线程间随机高速的切换,
对于CPU的一个核而言,某个时刻,只执行一个线程,而CPU在多个线程间的切换速度相对我们的感觉要快很多,看上去就像是多个线程或任务在同时运行

java 天生就支持多线程并提供了两种编程方式,一种是继承Thread和实现Runnable接口:

**ThreadFor1**

```java
public class ThreadFor1 extends Thread {

    public void run() {
        for (int i = 0; i < 50; i++) {
            System.out.println(this.getName() + ":" + i);
        }
    }
}
```
**ThreadFor2**

```java
public class ThreadFor2 extends Thread {

    public void run() {
        for (int i = 51; i < 100; i++) {
            System.out.println(this.getName() + ":" + i);
        }
    }
}
```
**测试类**

```
    @Test
    public void testThread() {
        ThreadFor1 for1 = new ThreadFor1();
        for1.setName("线程A");
        ThreadFor2 for2=new ThreadFor2();
        for2.setName("线程B");
        for1.start();
        for2.start();
    }
```
**测试结果**
```
线程A:0
线程B:51
线程A:1
线程B:52
线程B:53
线程A:2
线程B:54
线程A:3
线程B:55
线程A:4
线程B:56
线程A:5
线程B:57
线程A:6
线程B:58
```

通过继承Thread类,ThreadFor1和ThreadFor2就是线程类,通过测试发现CPU在两个线程之间快速随机切换,也就是我们所说的同时执行


**RunnableFor1**
```java
public class RunnableFor1 implements Runnable {

    public void run() {
        for (int j = 0; j < 50; j++) {
            System.out.println(Thread.currentThread().getName() + ":" + j);
        }
    }
}
```
**RunnableFor2**
```java
public class RunnableFor2 implements Runnable {

    public void run() {
        for (int i = 51; i < 100; i++) {
            System.out.println(Thread.currentThread().getName()+":"+i);
        }
    }
}
```
**测试类**

```
@Test
    public void testRunnable() {
        Thread t1 = new Thread(new RunnableFor1());
        t1.setName("线程A");
        Thread t2 = new Thread(new RunnableFor2());
        t2.setName("线程B");
        t1.start();
        t2.start();
    }
```

**测试结果**
```
同上
```
RunnableFor1和RunnableFor2这个时候是通过实现Runnable类来实现的,这个时候RunnableFor1和RunnableFor2就不能叫线程类了,可以叫任务类

<div id="qubie"></div>

#### 两种方式的区别

查看源码我们发现: 
    
    继承Thread: 由于子类重写了Thread类的run(),当调用 statrt()时,直接找子类的run()方法
    
    实现Runnable: 构造函数中传入Runnable的引用,成员变量记住了它,statrt()调用run()方法时内部判断成员变量Runable的引用是否
    为空,不为空编译看的是Runnable的run(),运行是执行的是子类的run()方法.
    
继承Thread:
    
    好处: 可以直接使用Thread类中方法,代码简单    
    
    弊端: 如果已经了父类,就不能用这种方法
    
实现Runnable接口:

    好处: 即使自己定义了线程类也没有什么关系,因为有了父类也可以实现接口,而且接口是可以多实现,
    
    弊端: 不能直接使用Thread中的方法需要先获取到线程对象后,才能得到Thread的方法,代码复杂   

#### 解决线程安全问题

java 中提供了一个同步机制(锁)来解决线程安全问题,即让操作共享数据的代码在某一时间段,只被一个线程执行(锁住),
在执行过程中,其他线程不可以参与进来,这样共享数据就能同步了,简单来说,就是给某些代码加把锁.
锁是什么? 又从哪里来?锁的专业名称叫监视器 monitor,其实 java 为每个对象都自带内置了一个锁(监视器 monitor),
当某个线程执行到了某代码快时就会自动得到这个对象的锁,那么其他线程就无法执行该代码块了,一直要等到之前那个线程停止(释放锁),
需要特别注意的是:多个线程必须使用同一把锁(对象).

* 同步代码块:即给代码块上锁,变成同步代码块

* 同步方法: 即给方法上锁,变成同步方法

* jdk1.6提出的lock锁


**方式一:**

```java
public class SaleWindow1 implements Runnable {

    private int id = 10; //表示10张火车票

    public void run() {

        for (int i = 0; i < 10; i++) {
            synchronized (this){
                if (id > 0) {
                    System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
                    id--;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

```
**测试结果**

```
窗口 A卖了编号为10的火车票
窗口 A卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 A卖了编号为7的火车票
窗口 A卖了编号为6的火车票
窗口 A卖了编号为5的火车票
窗口 A卖了编号为4的火车票
窗口 A卖了编号为3的火车票
窗口 A卖了编号为2的火车票
窗口 A卖了编号为1的火车票
```

**方式二:**

```java
public class SaleWindow2 implements Runnable {

    private int id = 10; //表示10张火车票

    public void run() {

        for (int i = 0; i < 10; i++) {

            saleOne();
        }

    }

    private synchronized void saleOne() {
        if (id > 0) {
            System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
            id--;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

```
**测试结果**

```
窗口 A卖了编号为10的火车票
窗口 A卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 A卖了编号为7的火车票
窗口 A卖了编号为6的火车票
窗口 A卖了编号为5的火车票
窗口 B卖了编号为4的火车票
窗口 B卖了编号为3的火车票
窗口 B卖了编号为2的火车票
窗口 B卖了编号为1的火车票
```
**方式三:**
```java
public class SaleWindow2 implements Runnable {

  private int id = 10; //表示10张火车票
    Lock lock = new ReentrantLock();
    public void run() {

        for (int i = 0; i < 10; i++) {
            if (id > 0) {
               lock.lock();
                System.out.println(Thread.currentThread().getName() + "卖了编号为" + id + "的火车票");
                id--;
                lock.unlock();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```
**测试结果**

```
窗口 A卖了编号为10的火车票
窗口 A卖了编号为9的火车票
窗口 A卖了编号为8的火车票
窗口 A卖了编号为7的火车票
窗口 A卖了编号为6的火车票
窗口 A卖了编号为5的火车票
窗口 B卖了编号为4的火车票
窗口 B卖了编号为3的火车票
窗口 B卖了编号为2的火车票
窗口 B卖了编号为1的火车票
```