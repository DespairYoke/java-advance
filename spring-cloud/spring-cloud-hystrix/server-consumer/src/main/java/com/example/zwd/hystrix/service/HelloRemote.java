package com.example.zwd.hystrix.service;

import com.example.zwd.hystrix.service.impl.HelloRemoteImpl;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zwd
 * @date 2018/10/2 15:09
 * @Email stephen.zwd@gmail.com
 */
@FeignClient(name = "server-producer",fallback = HelloRemoteImpl.class)
public interface HelloRemote {
    @RequestMapping(value = "/hello")
    String hello(@RequestParam(value = "name") String name);
}
