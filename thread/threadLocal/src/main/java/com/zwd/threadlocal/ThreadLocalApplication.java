package com.zwd.threadlocal;



public class ThreadLocalApplication {

    static ThreadLocal<String> localVariable = new ThreadLocal<>();

    static void printdata(String str) {

        System.out.println(str+"："+ localVariable.get());
    }
    public static void main(String[] args) {

        Thread threadOne = new Thread(new Runnable() {
            @Override
            public void run() {

                localVariable.set("one");
                printdata("one");
            }

        });

        Thread threadTwo = new Thread(new Runnable() {
            @Override
            public void run() {

                localVariable.set("two");
                printdata("two");
            }

        });

        threadOne.start();
        threadTwo.start();
    }

}
