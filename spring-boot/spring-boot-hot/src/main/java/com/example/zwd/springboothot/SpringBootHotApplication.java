package com.example.zwd.springboothot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringBootHotApplication {

	@Value(value = "${username}")
	private String name;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootHotApplication.class, args);
	}

	@GetMapping(value = "/")
	public String index() {
		return "hello "+ name;
	}

}
