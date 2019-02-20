package com.hansn;

import com.hansn.entity.MyThread;

public class ThreadTest {
    public static void main(String[] args) {
        MyThread myThread1 = new MyThread("A");
        MyThread myThread2 = new MyThread("B");

        myThread1.start();
        myThread2.start();
    }
}
