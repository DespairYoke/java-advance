package com.example.zwd.springcloudtaskexample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableTask
public class SpringCloudTaskExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudTaskExampleApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return new HelloWorldCommandLineRunner();
	}

	public static class HelloWorldCommandLineRunner implements CommandLineRunner {
		public void run(String... strings) throws Exception {
			System.out.println("Hello World!");
		}
	}

}
