package com.example.zwd.springcloudtaskexample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.cloud.task.closecontext_enable=false"})
public class SpringCloudTaskExampleApplicationTests {

	@Test
	public void contextLoads() {
	}

}
