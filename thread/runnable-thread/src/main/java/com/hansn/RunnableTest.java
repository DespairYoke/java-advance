package com.hansn;

import com.hansn.entity.MyThread1;

public class RunnableTest {
    public static void main(String[] args) {
        MyThread1 myThread1 = new MyThread1();
        new Thread(myThread1,"线程一").start();
        new Thread(myThread1,"线程二").start();
        new Thread(myThread1,"线程三").start();
    }
}
