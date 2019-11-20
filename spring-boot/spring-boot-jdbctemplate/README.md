## 手写AQS

### 预备知识
- volatile 读是原子操作 写是原子操作 读写不是原子操作

- queque peek()返回队列头的数据，不删除 poll()返回并删除

- LockSupport park() 阻塞当前线程，unpark(Thread)唤醒线程
### 项目核心代码

[项目传送门](https://github.com/DespairYoke/java-advance/tree/master/spring-boot/spring-boot-mybatis-jdbctemplate)

```java
public class MyLock {

    private volatile int state = 0;

    private volatile Thread lockHolder;

    private static long stateOffset;

    private static Unsafe unsafe = null;

    private LinkedBlockingQueue waiter = new LinkedBlockingQueue();

    static {

        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            stateOffset = unsafe.objectFieldOffset(MyLock.class.getDeclaredField("state"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lock() {
        if (aquire()) {
            return;
        }
        Thread current = Thread.currentThread();
        System.out.println(current);
        waiter.add(current);
        for (; ; ) {
            if (waiter.peek() == current && aquire()) {
                System.out.println("推出" + current);
                waiter.poll();
                return;
            }
            System.out.println(current);
            LockSupport.park();
        }
    }


    public void unlock() {

        if (unsafe.compareAndSwapInt(this, stateOffset, 1, 0)) {
            setLockHolder(null);
            Thread thread1 = (Thread) waiter.peek();
            LockSupport.unpark(thread1);
            System.out.println("释放线程");
        }
    }

    public int getState() {
        return state;
    }

    public boolean compareAndSwapState(int except, int update) {
        return unsafe.compareAndSwapInt(this, stateOffset, except, update);

    }

    public void setLockHolder(Thread lockHolder) {
        this.lockHolder = lockHolder;
    }

    private boolean aquire() {
        int s = getState();
        Thread t = Thread.currentThread();
        if (s == 0) {
            if ((waiter.size() == 0 || t == waiter.peek()) && compareAndSwapState(0, 1)) {
                setLockHolder(t);
                return true;
            }
        }
        return false;
    }
}
```
此实现为公平锁，先入队的线程，先执行。
第一个获取到锁的线程不入队列，直接返回成功，当后续线程过来时，获取不到锁，进入队列，循环等待锁的释放；当锁释放时，唤醒队列头的线程，线程继续执行。