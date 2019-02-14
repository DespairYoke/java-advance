## MyBatis的事务

由于上文[spring-mybatis](../spring-mybatis/README.md)中已经加入事务机制，这里我们在上文的基础上进行代码调整。

#### sql语句的调整
去除insert语句，保留表创建，如下
```sql
drop table user if exists;
create table user (
  id int not null,
  name varchar(25) not null,
  constraint pk_user primary key (id)
);
```
#### 增加插入语句
```java
public interface UserMapper {

    User selectUserById(int id);

    void insertUser(User user);
}

```
对应xml修改
```xml
<insert id="insertUser" parameterType="com.zwd.mybatis.transaction.pojo.User">
    insert  into user (id,name) values (#{id},#{name})
</insert>
```
#### 测试代码调整
```java
    @Test
    public void test1() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");

        User user = new User();
        user.setId(1);
        user.setName("zwd");
        sqlSession.insert("com.zwd.mybatis.transaction.mapper.UserMapper.insertUser",user);

        User user1 = sqlSession.selectOne("com.zwd.mybatis.transaction.mapper.UserMapper.selectUserById", 1);
        System.out.println(user1);

    }

    @Test
    public void test2() {

        ClassPathXmlApplicationContext  ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

        SqlSession sqlSession = (SqlSession) ctx.getBean("sqlSession");
        
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        User user = new User();
        user.setId(1);
        user.setName("zwd");
        userMapper.insertUser(user);
        User user1 = userMapper.selectUserById(1);
        System.out.println(user1);
    }

```
先运行test2测试，可以看到结果和原来一样，如下
```java
User{id=1, name='zwd'}
```
再运行test1没有输出任何结果。可知在查询的时候事务并没有提交，所以查询为空。这就是事务机制带来的好处，保证了数据的一致性，不会因为代码出错而导致数据冗余后果。

### 测试中遇到的问题

我尝试过手动提交或关闭sqlsession，但由于事务引入，导致事务中的自动提交和关闭与手动提交关闭产生了冲突。报错如下。
```java
java.lang.UnsupportedOperationException: Manual close is not allowed over a Spring managed SqlSession

	at org.mybatis.spring.SqlSessionTemplate.close(SqlSessionTemplate.java:358)
	at com.zwd.mybatis.transaction.StartTest.test3(StartTest.java:56)
```