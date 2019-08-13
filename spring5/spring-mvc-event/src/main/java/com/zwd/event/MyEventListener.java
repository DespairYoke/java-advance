package com.zwd.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class MyEventListener implements ApplicationListener<MyEvent> {

    Log log = LogFactory.getLog(getClass());
    @Override
    public void onApplicationEvent(MyEvent event) {
        log.info("事件触发"+ event.getMessage());
    }
}
