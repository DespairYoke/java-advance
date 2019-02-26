package com.example.zwd.springbootredis;

import com.example.zwd.springbootredis.domain.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests {

	@Autowired
	private StringRedisTemplate template;

	@Autowired
	private RedisTemplate redisTemplate;
	@Test
	public void contextLoads() {


		template.opsForValue().set("name","zwd");
		Assert.assertEquals("zwd",template.opsForValue().get("name"));

	}

	@Test
	public void TestRedisTemplete() {

		User user = new User();
		user.setName("朱卫东");
		user.setAge(11);
		redisTemplate.opsForValue().set("user",user);
		System.out.println(redisTemplate.opsForValue().get("user"));

	}

}
