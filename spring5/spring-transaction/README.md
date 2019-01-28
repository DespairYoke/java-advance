## Spring事务处理机制

### 认识事务

大家所了解的事务Transaction，它是一些列严密操作动作，要么都操作完成，要么都回滚撤销。Spring事务管理基于底层数据库本身的事务处理机制。数据库事务的基础，是掌握Spring事务管理的基础。这篇总结下Spring事务。
事务具备ACID四种特性，ACID是Atomic（原子性）、Consistency（一致性）、Isolation（隔离性）和Durability（持久性）的英文缩写。

（1）原子性（Atomicity）

事务最基本的操作单元，要么全部成功，要么全部失败，不会结束在中间某个环节。事务在执行过程中发生错误，会被回滚到事务开始前的状态，就像这个事务从来没有执行过一样。

（2）一致性（Consistency）

事务的一致性指的是在一个事务执行之前和执行之后数据库都必须处于一致性状态。如果事务成功地完成，那么系统中所有变化将正确地应用，系统处于有效状态。如果在事务中出现错误，那么系统中的所有变化将自动地回滚，系统返回到原始状态。

（3）隔离性（Isolation）

指的是在并发环境中，当不同的事务同时操纵相同的数据时，每个事务都有各自的完整数据空间。由并发事务所做的修改必须与任何其他并发事务所做的修改隔离。事务查看数据更新时，数据所处的状态要么是另一事务修改它之前的状态，要么是另一事务修改它之后的状态，事务不会查看到中间状态的数据。

（4）持久性（Durability）

指的是只要事务成功结束，它对数据库所做的更新就必须永久保存下来。即使发生系统崩溃，重新启动数据库系统后，数据库还能恢复到事务成功结束时的状态。

### 2、事务的传播特性

事务传播行为就是多个事务方法调用时，如何定义方法间事务的传播。Spring定义了7中传播行为：

（1）propagation_requierd：如果当前没有事务，就新建一个事务，如果已存在一个事务中，加入到这个事务中，这是Spring默认的选择。

（2）propagation_supports：支持当前事务，如果没有当前事务，就以非事务方法执行。

（3）propagation_mandatory：使用当前事务，如果没有当前事务，就抛出异常。

（4）propagation_required_new：新建事务，如果当前存在事务，把当前事务挂起。

（5）propagation_not_supported：以非事务方式执行操作，如果当前存在事务，就把当前事务挂起。

（6）propagation_never：以非事务方式执行操作，如果当前事务存在则抛出异常。

（7）propagation_nested：如果当前存在事务，则在嵌套事务内执行。如果当前没有事务，则执行与propagation_required类似的操作。

### 3、事务的隔离级别

（1）read uncommited：是最低的事务隔离级别，它允许另外一个事务可以看到这个事务未提交的数据。

（2）read commited：保证一个事物提交后才能被另外一个事务读取。另外一个事务不能读取该事物未提交的数据。

（3）repeatable read：这种事务隔离级别可以防止脏读，不可重复读。但是可能会出现幻象读。它除了保证一个事务不能被另外一个事务读取未提交的数据之外还避免了以下情况产生（不可重复读）。

（4）serializable：这是花费最高代价但最可靠的事务隔离级别。事务被处理为顺序执行。除了防止脏读，不可重复读之外，还避免了幻象读

（5）脏读、不可重复读、幻象读概念说明：

a.脏读：指当一个事务正字访问数据，并且对数据进行了修改，而这种数据还没有提交到数据库中，这时，另外一个事务也访问这个数据，然后使用了这个数据。因为这个数据还没有提交那么另外一个事务读取到的这个数据我们称之为脏数据。依据脏数据所做的操作肯能是不正确的。

b.不可重复读：指在一个事务内，多次读同一数据。在这个事务还没有执行结束，另外一个事务也访问该同一数据，那么在第一个事务中的两次读取数据之间，由于第二个事务的修改第一个事务两次读到的数据可能是不一样的，这样就发生了在一个事物内两次连续读到的数据是不一样的，这种情况被称为是不可重复读。

c.幻象读：一个事务先后读取一个范围的记录，但两次读取的纪录数不同，我们称之为幻象读（两次执行同一条 select 语句会出现不同的结果，第二次读会增加一数据行，并没有说这两次执行是在同一个事务中）

4、事务几种实现方式

（1）编程式事务管理对基于 POJO 的应用来说是唯一选择。我们需要在代码中调用beginTransaction()、commit()、rollback()等事务管理相关的方法，这就是编程式事务管理。

（2）基于 TransactionProxyFactoryBean的声明式事务管理

（3）基于 @Transactional 的声明式事务管理

（4）基于Aspectj AOP配置事务

### 5、举例说明事务不同实现

编程式事务基本已经OUT了，所有就省略了，主要回顾 下声明式事务。
以用户购买股票为例
新建用户对象、股票对象、以及dao、service层
--------------------- 

```java
/**
 * 账户对象
 *
 */
public class Account {

    private int accountid;
    private String name;
    private int balance;


    public int getAccountid() {
        return accountid;
    }
    public void setAccountid(int accountid) {
        this.accountid = accountid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getBalance() {
        return balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
    }
}
```

```java
/**
 * 股票对象
 *
 */
public class Stock {
 
	private int stockid;
	private String name;
	private Integer count;
	
	public Stock() {
		super();
	}
	 
	public Stock(int stockid, String name, Integer count) {
		super();
		this.stockid = stockid;
		this.name = name;
		this.count = count;
	}
 
	public int getStockid() {
		return stockid;
	}
 
	public void setStockid(int stockid) {
		this.stockid = stockid;
	}
 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
}
```
DAO层

```java
public interface AccountDao {
 
	void addAccount(String name,double money);
	
	void updateAccount(String name,double money,boolean isbuy);
	
}

```
```java
public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {
 
	@Override
	public void addAccount(String name, double money) {
		String sql = "insert account(name,balance) values(?,?);";
		this.getJdbcTemplate().update(sql,name,money);
		
	}
 
	@Override
	public void updateAccount(String name, double money, boolean isbuy) {
		String sql = "update account set balance=balance+? where name=?";
		if(isbuy)
			sql = "update account set balance=balance-? where name=?";
		this.getJdbcTemplate().update(sql, money,name);
	}
	
}

```

```java
public interface StockDao {
	
	void addStock(String sname,int count);
	
	void updateStock(String sname,int count,boolean isbuy);
 
}

```

```java
public class StockDaoImpl extends JdbcDaoSupport implements StockDao {
 
	@Override
	public void addStock(String sname, int count) {
		String sql = "insert into stock(name,count) values(?,?)";
		this.getJdbcTemplate().update(sql,sname,count);
	}
 
	@Override
	public void updateStock(String sname, int count, boolean isbuy) {
		String sql = "update stock set count = count-? where name = ?";
		if(isbuy)
			sql = "update stock set count = count+? where name = ?";
		this.getJdbcTemplate().update(sql, count,sname);
	}
	
}

```
Service

```java
public interface BuyStockService {
 
	public void addAccount(String accountname, double money);
	
	public void addStock(String stockname, int amount);
	
	public void buyStock(String accountname, double money, String stockname, int amount) throws BuyStockException;
	
}

```

```java
public class BuyStockServiceImpl implements BuyStockService{
	
	private AccountDao accountDao;
	private StockDao stockDao;
	
	@Override
	public void addAccount(String accountname, double money) {
		accountDao.addAccount(accountname,money);
	}
 
	@Override
	public void addStock(String stockname, int amount) {
		stockDao.addStock(stockname,amount);
	}
 
	@Override
	public void buyStock(String accountname, double money, String stockname, int amount) throws BuyStockException {
		boolean isBuy = true;
		accountDao.updateAccount(accountname, money, isBuy);
		if(isBuy==true){
			throw new BuyStockException("购买股票发生异常");
		}
			stockDao.updateStock(stockname, amount, isBuy);
	}
 
	public AccountDao getAccountDao() {
		return accountDao;
	}
 
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
 
	public StockDao getStockDao() {
		return stockDao;
	}
 
	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}
	
}

```

自定义异常类

```java
public class BuyStockException extends Exception {
 
	public BuyStockException() {
		super();
	}
 
	public BuyStockException(String message) {
		super(message);
	}
 
}

```
#### （1）基于 TransactionProxyFactoryBean的声明式事务管理

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
        http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd
        http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.2.xsd
        http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-aop-4.2.xsd
        ">

    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!-- 注册数据源 C3P0 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"  >
        <property name="driverClass" value="${jdbc.driverClass}"></property>
        <property name="jdbcUrl"  value="${jdbc.url}"></property>
        <property name="user"  value="${jdbc.username}"></property>
        <property name="password" value="${jdbc.password}"></property>
    </bean>

    <bean id="accountDao" class="com.zwd.spring.transaction.daoImpl.AccountDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="stockDao" class="com.zwd.spring.transaction.daoImpl.StockDaoImpl">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean id="buyStockService" class="com.zwd.spring.transaction.serviceImpl.BuyStockServiceImpl">
        <property name="accountDao" ref="accountDao"></property>
        <property name="stockDao" ref="stockDao"></property>
    </bean>


    <!-- 事务管理器 -->

    <bean id="myTracnsactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 事务代理工厂 -->
    <!-- 生成事务代理对象 -->
    <bean id="serviceProxy" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
        <property name="transactionManager" ref="myTracnsactionManager"></property>
        <property name="target" ref="buyStockService"></property>
        <property name="transactionAttributes">
            <props>
                <!-- 主要 key 是方法
                    ISOLATION_DEFAULT  事务的隔离级别
                    PROPAGATION_REQUIRED  传播行为
                -->
                <prop key="add*">ISOLATION_DEFAULT,PROPAGATION_REQUIRED</prop>
                <!-- -Exception 表示发生指定异常回滚，+Exception 表示发生指定异常提交 -->
                <prop key="buyStock">ISOLATION_DEFAULT,PROPAGATION_REQUIRED,-BuyStockException</prop>
            </props>
        </property>

    </bean>


</beans>  

```
![](https://img-blog.csdn.net/20171216133238666)

通过结果和观察数据库数据变化，可以看出我们声明的异常回滚发生了效果。

####（2）基于 @Transactional 的声明式事务管理

其他类不做改变，只改变购买股票接口实现类和配置文件

```java
public class BuyStockServiceImpl implements BuyStockService{
 
	private AccountDao accountDao;
	private StockDao stockDao;
	
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED)
	@Override
	public void addAccount(String accountname, double money) {
		accountDao.addAccount(accountname,money);
		
	}
 
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED)
	@Override
	public void addStock(String stockname, int amount) {
		stockDao.addStock(stockname,amount);
		
	}
 
	public BuyStockServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,rollbackFor=BuyStockException.class)
	@Override
	public void buyStock(String accountname, double money, String stockname, int amount) throws BuyStockException {
		boolean isBuy = true;
		accountDao.updateAccount(accountname, money, isBuy);
		if(isBuy==true){
			throw new BuyStockException("购买股票发生异常");
		}
			stockDao.updateStock(stockname, amount, isBuy);
		
	}
 
	public AccountDao getAccountDao() {
		return accountDao;
	}
 
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
 
	public StockDao getStockDao() {
		return stockDao;
	}
 
	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}
	
}
```

```xml
	<context:property-placeholder location="classpath:jdbc.properties"/>
	
	<!-- 注册数据源 C3P0 -->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"  >
		 <property name="driverClass" value="${jdbc.driverClass}"></property>
		 <property name="jdbcUrl"  value="${jdbc.url}"></property>
         <property name="user"  value="${jdbc.username}"></property>
         <property name="password" value="${jdbc.password}"></property>
	</bean>
	
	<bean id="accountDao" class="com.zwd.spring.transaction.daoImpl.AccountDaoImpl">
		<property name="dataSource" ref="dataSource"/>
	</bean>
	
	<bean id="stockDao" class="com.zwd.spring.transaction.daoImpl.StockDaoImpl">
		<property name="dataSource" ref="dataSource"/>
	</bean>
	
	<bean id="buyStockService" class="tcom.zwd.spring.transaction.serviceImpl.BuyStockServiceImpl">
		<property name="accountDao" ref="accountDao"></property>
		<property name="stockDao" ref="stockDao"></property>
	</bean>
	
	
	<!-- 事务管理器 -->
	<bean id="myTracnsactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>
	</bean>
	
	<!-- 启用事务注解 -->
	<tx:annotation-driven transaction-manager="myTracnsactionManager"/>

```

可以看出，使用@Transactional注解的方式配置文件要简单的多，将事务交给事务注解驱动。它有个缺陷是他会把所有的连接点都作为切点将事务织入进去，
显然只需要在buyStock()方法织入事务即可。下面看看最后一种实现，它就可以精准的织入到指定的连接点

#### （3）基于Aspectj AOP配置事务
```java
public class BuyStockServiceImpl implements BuyStockService{
 
	private AccountDao accountDao;
	private StockDao stockDao;
	
	@Override
	public void addAccount(String accountname, double money) {
		accountDao.addAccount(accountname,money);
		
	}
 
	@Override
	public void addStock(String stockname, int amount) {
		stockDao.addStock(stockname,amount);
		
	}
 
	public BuyStockServiceImpl() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void buyStock(String accountname, double money, String stockname, int amount) throws BuyStockException {
		boolean isBuy = true;
		accountDao.updateAccount(accountname, money, isBuy);
		if(isBuy==true){
			throw new BuyStockException("购买股票发生异常");
		}
			stockDao.updateStock(stockname, amount, isBuy);
		
	}
 
	public AccountDao getAccountDao() {
		return accountDao;
	}
 
	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}
 
	public StockDao getStockDao() {
		return stockDao;
	}
 
	public void setStockDao(StockDao stockDao) {
		this.stockDao = stockDao;
	}
	
}

```

```xml
	<context:property-placeholder location="classpath:jdbc.properties"/>
	
	<!-- 注册数据源 C3P0 -->
	<bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource"  >
		 <property name="driverClass" value="${jdbc.driverClass}"></property>
		 <property name="jdbcUrl"  value="${jdbc.url}"></property>
         <property name="user"  value="${jdbc.username}"></property>
         <property name="password" value="${jdbc.password}"></property>
	</bean>
	
	<bean id="accountDao" class="com.zwd.spring.transaction.daoImpl.AccountDaoImpl">
		<property name="dataSource" ref="dataSource"/>
	</bean>
	
	<bean id="stockDao" class="com.zwd.spring.transaction.daoImpl.StockDaoImpl">
		<property name="dataSource" ref="dataSource"/>
	</bean>
	
	<bean id="buyStockService" class="com.zwd.spring.transaction.serviceImpl.BuyStockServiceImpl">
		<property name="accountDao" ref="accountDao"></property>
		<property name="stockDao" ref="stockDao"></property>
	</bean>
	
	
	<!-- 事务管理器 -->
	<bean id="myTracnsactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource"></property>
	</bean>
	
	<tx:advice id="txAdvice" transaction-manager="myTracnsactionManager">
		<tx:attributes>
			<!-- 为连接点指定事务属性 -->
			<tx:method name="add*" isolation="DEFAULT" propagation="REQUIRED"/>
			<tx:method name="buyStock" isolation="DEFAULT" propagation="REQUIRED" rollback-for="BuyStockException"/>
		</tx:attributes>
	</tx:advice>
	
	<aop:config>
		<!-- 切入点配置 -->
		<aop:pointcut expression="execution(* *..serviceImpl.*.*(..))" id="point"/>
		<aop:advisor advice-ref="txAdvice" pointcut-ref="point"/>
	</aop:config>

```