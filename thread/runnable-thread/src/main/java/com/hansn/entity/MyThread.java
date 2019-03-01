package com.hansn.entity;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyThread extends Thread {
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
