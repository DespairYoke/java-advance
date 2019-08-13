package com.zwd.boot.server;


public class FtpServer implements Server {

    @Override
    public void start() {
        System.out.println("ftpServer 启动.....");
    }

    @Override
    public void close() {
        System.out.println("ftpServer 关闭.....");
    }
}
