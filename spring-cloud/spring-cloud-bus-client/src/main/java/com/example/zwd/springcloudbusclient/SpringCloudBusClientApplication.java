package com.example.zwd.springcloudbusclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableEurekaClient
@RefreshScope
public class SpringCloudBusClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudBusClientApplication.class, args);
	}

	@Value(value = "${foo}")
	public String foo;

	@RequestMapping("/")
	public String home() {
		return "Hello World!"+foo;
	}
}
