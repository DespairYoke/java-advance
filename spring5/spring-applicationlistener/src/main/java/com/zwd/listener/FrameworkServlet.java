package com.zwd.listener;

import com.zwd.listener.context.AbstractApplicationContext;
import com.zwd.listener.event.ContextRefreshedEvent;

public class FrameworkServlet {

    public AbstractApplicationContext abstractApplicationContext;

    public FrameworkServlet() {
        this.abstractApplicationContext = new AbstractApplicationContext();
    }

    public void configureAndRefreshWebApplicationContext() {

       abstractApplicationContext.addApplicationListener(new SourceFilteringListener(abstractApplicationContext,new FrameworkServlet.ContextRefreshListener()));
    }
    private class ContextRefreshListener implements ApplicationListener<ContextRefreshedEvent> {

        public ContextRefreshListener() {
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            onrefresh(event);
        }

    }
   private void onrefresh(ContextRefreshedEvent event) {

        System.out.println(event.onEvent());
    }


}
