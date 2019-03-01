package com.hansn;

import com.hansn.entity.MyRunnable;

public class RunnableTest {
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        new Thread(myRunnable,"线程一").start();
        new Thread(myRunnable,"线程二").start();
        new Thread(myRunnable,"线程三").start();
    }
}
