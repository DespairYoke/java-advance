## Spring集成下的SqlSession与Mapper

### spring集成jdbc
* 1、为了方便我使用了HSQLDB来做数据库，数据代码如下
```sql
drop table user if exists;

create table user (
  id int not null,
  name varchar(25) not null,
  constraint pk_user primary key (id)
);

insert into user(id,name) values (1, 'zwd');
```
* 2、引入db配置
```xml
    <!-- in-memory database and a datasource -->
    <jdbc:embedded-database id="dataSource">
        <jdbc:script location="classpath:db/database.sql"/>
    </jdbc:embedded-database>
```
### 集成mybatis并配偶所需参数
```xml
 <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource" />
        <property name="mapperLocations" value="classpath:mapper/*.xml" />
    </bean>

    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <constructor-arg index="0" ref="sqlSessionFactory" />
    </bean>
```
### 编写测试代码

```java
    @Test
    public void test1() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        User user=sqlSession.selectOne("com.zwd.mybatis.spring.mapper.UserMapper.selectUserById",1);


        System.out.println(user);
    }

    @Test
    public void test2() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");


        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        User user = userMapper.selectUserById(1);

        System.out.println(user);
    }
```
此SqlSession使用的两种方式都可运行。

* 测试结果
```java
User{id=1, name='zwd'}
```
