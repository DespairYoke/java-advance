package com.hansn.entity;

public class MyThread extends Thread {
    private String name;

    public MyThread(String name) {
        this.name = name;
    }
    @Override
    public synchronized void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(name+":"+i);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
