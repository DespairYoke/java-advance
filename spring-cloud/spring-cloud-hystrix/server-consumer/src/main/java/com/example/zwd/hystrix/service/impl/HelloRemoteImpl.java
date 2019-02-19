package com.example.zwd.hystrix.service.impl;

import com.example.zwd.hystrix.service.HelloRemote;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zwd
 * @date 2018/10/2 15:57
 * @Email stephen.zwd@gmail.com
 */
@Component
public class HelloRemoteImpl implements HelloRemote {
    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return "hello "+name+",this message send failed";
    }
}
