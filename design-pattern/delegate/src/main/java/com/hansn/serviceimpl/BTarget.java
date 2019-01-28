package com.hansn.serviceimpl;

import com.hansn.service.Target;

public class BTarget implements Target {
    //B员工需要做的事情
    @Override
    public void dosomething(String commond) {
        System.out.println("B员工做具体的事情"+commond);
    }
}
