package com.example.zwd.eurekaservser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServserApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServserApplication.class, args);
	}
}
