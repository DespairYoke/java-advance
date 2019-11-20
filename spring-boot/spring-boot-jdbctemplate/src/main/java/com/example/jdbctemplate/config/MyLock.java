package com.example.jdbctemplate.config;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;


/**
 * @description:
 * @author: zwd
 * @create: 2019-11-19 17:12
 */
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
                System.out.println("推出"+ current);
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
