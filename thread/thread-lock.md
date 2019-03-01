### 锁的使用

- 锁的简介
在Lock接口出现之前，Java程序是靠synchronized关键字实现锁功能的。JDK1.5之后并发包中新增了Lock接口以及相关实现类来实现锁功能。
Lock比synchorinized更灵活。

- 使用示例
```java
public class ReentrantLockTest implements Runnable {

    Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        ReentrantLockTest reentrantLockTest = new ReentrantLockTest();
        Thread t1 = new Thread(reentrantLockTest);
        Thread t2 = new Thread(reentrantLockTest);
        Thread t3 = new Thread(reentrantLockTest);
        Thread t4 = new Thread(reentrantLockTest);
        Thread t5 = new Thread(reentrantLockTest);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
    }


    @Override
    public void run() {
        lock.lock();
        try {
            for (int i = 0; i < 5; i++) {

                Thread.sleep(1000);

                System.out.println("ThreadName=" + Thread.currentThread().getName() + (" " + (i + 1)));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    
}

```
运行结果
```properties
ThreadName=Thread-0 1
ThreadName=Thread-0 2
ThreadName=Thread-0 3
ThreadName=Thread-0 4
ThreadName=Thread-0 5
ThreadName=Thread-1 1
ThreadName=Thread-1 2
ThreadName=Thread-1 3
ThreadName=Thread-1 4
ThreadName=Thread-1 5
ThreadName=Thread-2 1
ThreadName=Thread-2 2
ThreadName=Thread-2 3
ThreadName=Thread-2 4
ThreadName=Thread-2 5
ThreadName=Thread-3 1
ThreadName=Thread-3 2
ThreadName=Thread-3 3
ThreadName=Thread-3 4
ThreadName=Thread-3 5
ThreadName=Thread-4 1
ThreadName=Thread-4 2
ThreadName=Thread-4 3
ThreadName=Thread-4 4
ThreadName=Thread-4 5
```
从运行结果可以看出，当一个线程运行完毕后才把锁释放，其他线程才能执行，其他线程的执行顺序是不确定的。

-  使用Condition实现等待/通知机制
```java
public class UseSingleConditionWaitNotify {

	public static void main(String[] args) throws InterruptedException {

		MyService service = new MyService();

		ThreadA a = new ThreadA(service);
		a.start();

		Thread.sleep(3000);

		service.signal();

	}

	static public class MyService {

		private Lock lock = new ReentrantLock();
		public Condition condition = lock.newCondition();

		public void await() {
			lock.lock();
			try {
				System.out.println(" await时间为" + System.currentTimeMillis());
				condition.await();
				System.out.println("这是condition.await()方法之后的语句，condition.signal()方法之后我才被执行");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				lock.unlock();
			}
		}

		public void signal() throws InterruptedException {
			lock.lock();
			try {				
				System.out.println("signal时间为" + System.currentTimeMillis());
				condition.signal();
				Thread.sleep(3000);
				System.out.println("这是condition.signal()方法之后的语句");
			} finally {
				lock.unlock();
			}
		}
	}

	static public class ThreadA extends Thread {

		private MyService service;

		public ThreadA(MyService service) {
			super();
			this.service = service;
		}

		@Override
		public void run() {
			service.await();
		}
	}
}
```
- 公平锁与非公平锁
Lock锁分为：公平锁 和 非公平锁。公平锁表示线程获取锁的顺序是按照线程加锁的顺序来分配的，即先来先得的FIFO先进先出顺序。
而非公平锁就是一种获取锁的抢占机制，是随机获取锁的，和公平锁不一样的就是先来的不一定先的到锁，这样可能造成某些线程一直拿不到锁，
结果也就是不公平的了。
```java
public class FairorNofairLock {

	public static void main(String[] args) throws InterruptedException {
		final Service service = new Service(true);//true为公平锁，false为非公平锁

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				System.out.println("★线程" + Thread.currentThread().getName()
						+ "运行了");
				service.serviceMethod();
			}
		};

		Thread[] threadArray = new Thread[10];
		for (int i = 0; i < 10; i++) {
			threadArray[i] = new Thread(runnable);
		}
		for (int i = 0; i < 10; i++) {
			threadArray[i].start();
		}

	}
	static public class Service {

		private ReentrantLock lock;

		public Service(boolean isFair) {
			super();
			lock = new ReentrantLock(isFair);
		}

		public void serviceMethod() {
			lock.lock();
			try {
				System.out.println("ThreadName=" + Thread.currentThread().getName()
						+ "获得锁定");
			} finally {
				lock.unlock();
			}
		}

	}
}
```
运行结果
```java
★线程Thread-0运行了
ThreadName=Thread-0获得锁定
★线程Thread-1运行了
ThreadName=Thread-1获得锁定
★线程Thread-2运行了
ThreadName=Thread-2获得锁定
★线程Thread-3运行了
ThreadName=Thread-3获得锁定
★线程Thread-4运行了
ThreadName=Thread-4获得锁定
★线程Thread-5运行了
ThreadName=Thread-5获得锁定
★线程Thread-6运行了
ThreadName=Thread-6获得锁定
★线程Thread-7运行了
ThreadName=Thread-7获得锁定
★线程Thread-8运行了
ThreadName=Thread-8获得锁定
★线程Thread-9运行了
ThreadName=Thread-9获得锁定
```
不加锁的运行效果
```java
★线程Thread-0运行了
★线程Thread-4运行了
★线程Thread-3运行了
★线程Thread-1运行了
★线程Thread-2运行了
ThreadName=Thread-0获得锁定
★线程Thread-5运行了
ThreadName=Thread-5获得锁定
★线程Thread-6运行了
ThreadName=Thread-6获得锁定
ThreadName=Thread-4获得锁定
ThreadName=Thread-3获得锁定
ThreadName=Thread-1获得锁定
★线程Thread-7运行了
ThreadName=Thread-2获得锁定
★线程Thread-8运行了
ThreadName=Thread-7获得锁定
ThreadName=Thread-8获得锁定
★线程Thread-9运行了
ThreadName=Thread-9获得锁定
```