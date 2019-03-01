package com.hansn.entity;

public class MyRunnable implements Runnable{
    private int ticket = 10;

    @Override
    public synchronized void run() {
        for (int i = 0; i <100; i++) {
            if (this.ticket>0) {
                System.out.println("卖票：ticket"+this.ticket--);
            }
        }
    }
}
