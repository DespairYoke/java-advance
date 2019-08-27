## springboot2.0使用hsql

### 依赖引入
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 实体类编写
```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Demo {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
```

### dao层
```java
public interface DaoService extends CrudRepository<Demo,Long> {

    Demo findByName(String name);

}
```

### 测试用例
```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootStrap.class)
public class TestHsql {

    @Autowired
    private DaoService daoService;


    @Test
    public void testSave() {
        Demo demoInfo = new Demo();
        demoInfo.setName("张三");
        demoInfo.setAge(20);
        daoService.save(demoInfo);

        demoInfo = new Demo();
        demoInfo.setName("李四");
        demoInfo.setAge(30);
        daoService.save(demoInfo);

        testFind();

        testFindByName();
    }

    
    public void testFind(){
         List  list = (List<Demo>) daoService.findAll();
         list.stream().forEach(System.out::println);
    }

    public void testFindByName(){
        Demo demo = daoService.findByName("张三");
        System.out.println(demo);
    }

}

```