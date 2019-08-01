package com.zwd.boot.server;


public class HttpServer implements Server{
    @Override
    public void start() {
        System.out.println("httpServer 启动.....");
    }

    @Override
    public void close() {
        System.out.println("httpServer 关闭.....");
    }
}
