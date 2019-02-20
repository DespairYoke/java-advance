package com.example.zwd.springcloudfeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "eureka-client-a")
public interface ConsumerService {

    @GetMapping(value = "hello/{name}")
    String hello(@PathVariable("name") String name);

}
