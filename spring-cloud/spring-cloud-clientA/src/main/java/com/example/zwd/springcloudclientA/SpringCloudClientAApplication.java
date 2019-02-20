package com.example.zwd.springcloudclientA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SpringCloudClientAApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudClientAApplication.class, args);
	}

}
