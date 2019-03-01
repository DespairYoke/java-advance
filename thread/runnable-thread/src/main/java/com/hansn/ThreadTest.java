package com.hansn;

import com.hansn.entity.MyThread;

public class ThreadTest {
    public static void main(String[] args) {
        MyThread myThread = new MyThread();

        Thread t = new Thread(myThread,"窗口 A");
        Thread t1 = new Thread(myThread, "窗口 B");
        t.start();
        t1.start();
    }
}
