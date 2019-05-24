package com.example.zwd.springbootredis;

import com.liumapp.redis.operator.config.RedisConfig;
import com.liumapp.redis.operator.string.StringUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests {

//	@Autowired
//	private StringRedisTemplate template;
//
//	@Autowired
//	private RedisTemplate redisTemplate;

	@Autowired
	private StringUtil stringUtil;
//	@Test
//	public void contextLoads() {
//
//
//		template.opsForValue().set("name","zwd");
//
//		System.out.println(template.opsForValue().get("login_17739176405"));
//
//	}

	@Test
	public void TestRedisTemplete() {


		stringUtil.get("login_17316913108");
//		System.out.println(redisTemplate.opsForValue().get("login_17316913108"));

	}

}
