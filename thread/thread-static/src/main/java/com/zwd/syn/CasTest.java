package com.zwd.syn;

import java.util.concurrent.atomic.AtomicInteger;

public class CasTest implements Runnable{
    public static  int race = 0;

    private static AtomicInteger atomicInteger= new AtomicInteger(0);
    public static void increase() {
        race++;
        atomicInteger.getAndIncrement();
    }

    public static void main(String[] args) throws InterruptedException {

        CasTest casTest = new CasTest();
        Thread threads1 = new Thread(casTest,"窗口A");
        Thread threads2 = new Thread(casTest,"窗口B");
        threads1.start();
        threads2.start();
        threads1.join();
        threads2.join();
        System.out.println("race: "+race);
        System.out.println("atomic: "+atomicInteger);
    }

    @Override
    public void run() {
        for (int i=0;i<10;i++) {
            try {
                increase();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
