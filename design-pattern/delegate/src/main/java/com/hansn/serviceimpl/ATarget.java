package com.hansn.serviceimpl;

import com.hansn.service.Target;

public class ATarget implements Target {
    //A员工做具体的事情
    @Override
    public void dosomething(String commond) {
        System.out.println("A员工做具体的事情"+commond + "");
    }
}
