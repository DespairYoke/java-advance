## springboot配置mybatis多数据源

    所谓多数据源，其实连接两dataSource，而每个dataSource生成的sqlFactory管理着不同的xml。
    只要使用时，根据业务选择自己所需的mapper即可。

springboot1.x时，可用DataSourceBuilder进行生成数据源，2.x以后必须手动配置数据源。
- 1.x配置
```java
@Bean(name = "test1DataSource")
@ConfigurationProperties(prefix = "spring.datasource.test1")
@Primary
public DataSource testDataSource() {
    return DataSourceBuilder.create().build();
}

```
- 2.x配置
```java
@Autowired
Environment env;
@Bean(name = "test1DataSource")
@Primary
public DataSource testDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getProperty("test1.driverClassName"));
    dataSource.setUrl(env.getProperty("test1.url"));
    dataSource.setUsername(env.getProperty("test1.username"));
    dataSource.setPassword(env.getProperty("test1.password"));
    return dataSource;
}
```
### 项目搭建

- applicaiton.properties配置文件
```properties
mybatis.config-locations=classpath:mybatis/mybatis-config.xml

test1.driverClassName= com.mysql.cj.jdbc.Driver
test1.url = jdbc:mysql://localhost:3306/zwdtest?useUnicode=true
test1.username = root
test1.password = root


test2.driverClassName = com.mysql.cj.jdbc.Driver
test2.url = jdbc:mysql://localhost:3306/zwdtest?useUnicode=true
test2.username = root
test2.password = root
```
根据自己需求添加或更改所需数据源。

- 配置类(重点)
每个datasource都对应着自己的配置类，这里查看一下test1的配置上类。如下

```java
@Configuration
@MapperScan(basePackages = "com.neo.mapper.test1", sqlSessionTemplateRef  = "test1SqlSessionTemplate")
public class DataSource1Config {

    @Autowired
    Environment env;
    @Bean(name = "test1DataSource")
    @Primary
    public DataSource testDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("test1.driverClassName"));
        dataSource.setUrl(env.getProperty("test1.url"));
        dataSource.setUsername(env.getProperty("test1.username"));
        dataSource.setPassword(env.getProperty("test1.password"));
        return dataSource;
    }

    @Bean(name = "test1SqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("test1DataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/mapper/test1/*.xml"));
        return bean.getObject();
    }

    @Bean(name = "test1TransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("test1DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "test1SqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("test1SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
```
`@MapperScan(basePackages = "com.neo.mapper.test1"`扫描对应的包。

`classpath:mybatis/mapper/test1/*.xml`是此sqlfactory所能操作的xml的路径。不同的连接路径不同。

- 如何使用

从项目目录可以看出mapper分为test1,test2,这是连接两个不同的数据库，当我们想要操作时，只需要选择不同包下的mapper。