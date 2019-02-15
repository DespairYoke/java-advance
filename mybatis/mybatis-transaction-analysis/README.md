## MyBatis的事务分析

结合mybatis3官网，我们使用最简单的demo进行事务分析。

### mybatis事务简介

mybatis事务有两种事务工厂类型，JDBC和MANAGED分别对应JdbcTransactionFactory.class和ManagedTransactionFactory.class;

* 如果type=”JDBC”则使用JdbcTransactionFactory事务工厂则MyBatis独立管理事务，直接使用JDK提供的JDBC来管理事务的各个环节：提交、回滚、关闭等操作；

* 如果type=”MANAGED”则使用ManagedTransactionFactory事务工厂则MyBatis不在ORM层管理事务而是将事务管理托付给其他框架，如上文中[Spring下的事务](../mybatis-transaction-manager/README.md)；

下面使用JDBC进行事务演示分析


### 本地数据库表创建
```sql
CREATE TABLE IF NOT EXISTS `test`.`user` (
  `id` INT(11) NOT NULL,
  `name` VARCHAR(45) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin
```
### 结合mybatis官网开启jdbc事务
```xml
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/test?useUnicode=true"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mapper/UserMapper.xml"/>
    </mappers>
</configuration>
```
### 运行测试代码
```java
    @Test
    public void test1() throws IOException {

        String resource = "mybatis/mybatis-config.xml";

        InputStream inputStream = Resources.getResourceAsStream(resource);

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = sqlSessionFactory.openSession();
        User user = new User();
        try {
            user.setName("abc");
            sqlSession.insert("com.zwd.mybatis.analysis.mapper.UserMapper.insertUser",user);
        }finally {
            sqlSession.close();
        }
        System.out.println(user);

    }
```
查看数据库后，发现并没有我们插入成功后的数据，当加入commit语句后数据从第二个id开始插入。
这说明我们上次插入的数据发生了回滚。

### debug源码跟踪
* 在从SessionFactory中获取sqlSession时候会根据environment配置获取相应的事务工厂TransactionFactory，并从中获取事务实例当
做参数传递给Executor，用来从中获取Connection数据库连接；
```java
public SqlSession openSession() {
  return openSessionFromDataSource(configuration.getDefaultExecutorType(), null, false);
}
```
* getTransactionFactoryFromEnvironment()方法根据XML配置获取JdbcTransactionFactory或者ManagedTransactionFactory；
ManagedTransactionFactory类中的commit()方法和rollback()方法都为空，事务相关操作不发挥作用

```java
private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
  Transaction tx = null;
  try {
    final Environment environment = configuration.getEnvironment();
    final TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(environment);
    tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
    final Executor executor = configuration.newExecutor(tx, execType);
    return new DefaultSqlSession(configuration, executor, autoCommit);
  } catch (Exception e) {
    closeTransaction(tx); // may have fetched a connection so lets call close()
    throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}

```
* commit的代码跟踪
```java
@Override
public void commit() {
  commit(false);
}

@Override
public void commit(boolean force) {
  try {
    executor.commit(isCommitOrRollbackRequired(force));
    dirty = false;
  } catch (Exception e) {
    throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```
* exector.commit()事务提交方法归根到底是调用了transaction.commit()事务的提交方法；
这里的transaction就是根据配置对应的JdbcTransaction或者ManagedTransaction；
```java
public void commit(boolean required) throws SQLException {
  if (closed) {
    throw new ExecutorException("Cannot commit, transaction is already closed");
  }
  clearLocalCache();
  flushStatements();
  if (required) {
    transaction.commit();
  }
}
```
* 如果是JdbcTransaction的commit()方法，通过调用connection.commit()方法通过数据库连接实现事务提交；
```java
public void commit() throws SQLException {
  if (connection != null && !connection.getAutoCommit()) {
    if (log.isDebugEnabled()) {
      log.debug("Committing JDBC Connection [" + connection + "]");
    }
    connection.commit();
  }
}
```
* 如果是ManagedTransaction的commit()方法，则为空方法不进行任何操作；
```java
@Override
public void commit() throws SQLException {
  // Does nothing
}

@Override
public void rollback() throws SQLException {
  // Does nothing
}
```