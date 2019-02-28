//package com.zwd.state;
//
//import org.junit.Test;
//
//public class ThreadState {
//
//    @Test
//    public void testJoin() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("i am "+Thread.currentThread());
//            }
//        });
//
//        t.start();
////        t.join();
//        System.out.println("i am "+Thread.currentThread());
//    }
//}
