## springboot集成Redis缓存实战

springboot官网

    You can inject an auto-configured RedisConnectionFactory, StringRedisTemplate,
     or vanilla RedisTemplate instance as you would any other Spring Bean.
     
可见如果我们key value都是简单的字符串，可以直接使用stringRedisTemplate;但如果是更复杂的数据存储。那就要手动生成RedisTemplete。

### StringRedisTemplate演示
- maven依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
- application.properties文件配置
```properties
# Redis数据库索引（默认为0）,如果设置为1，那么存入的key-value都存放在select 1中
spring.redis.database=0
# Redis服务器地址
spring.redis.host=127.0.0.1
# Redis服务器连接端口
spring.redis.port=6379 
# Redis服务器连接密码（默认为空）
spring.redis.password=password
```
- test类编写
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests {

	@Autowired
	private StringRedisTemplate template;

	@Test
	public void contextLoads() {


		template.opsForValue().set("name","zwd");
		Assert.assertEquals("zwd",template.opsForValue().get("name"));

	}
}
```

### RedisTemplate使用示例

- redisTemplate必须手动创建bean。

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate (RedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }
}
```
- 创建一个pojo
```java
public class User implements Serializable {

    private String name;

    private int age;

    //省略get set toString
}

```
- Test示例
```java
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
```

[项目地址](./spring-boot-redis)
