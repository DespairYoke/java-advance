package com.zwd;

import com.zwd.listener.FrameworkServlet;
import org.junit.Test;

public class BootStrap {


    @Test
    public void test() {

        FrameworkServlet frameworkServlet = new FrameworkServlet();
        frameworkServlet.configureAndRefreshWebApplicationContext();
        frameworkServlet.abstractApplicationContext.onRefresh();
    }
}
