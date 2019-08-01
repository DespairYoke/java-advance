package com.zwd.boot;

import com.zwd.boot.annotation.EnableHelloWorld;
import com.zwd.boot.annotation.EnableServer;
import com.zwd.boot.server.Server;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@EnableServer
@EnableHelloWorld
public class BootStrap {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BootStrap.class);
        context.refresh();
        Object name = context.getBean("name");
        System.out.println(name);

        Server server = context.getBean(Server.class);
        server.start();
        server.close();
        context.close();
    }
}
