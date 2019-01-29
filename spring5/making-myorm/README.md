## 手动实现自己的ORM框架


### 一、Mybatis框架流程简介

![image1](https://static.oschina.net/uploads/space/2018/0307/101325_JXwG_3577599.png)

### 二、梳理自己的Mybatis的设计思路

根据上文Mybatis流程，我简化了下，分为以下步骤：

![image2](https://static.oschina.net/uploads/space/2018/0307/141736_jxRl_3577599.png)

####  1.读取xml文件，建立连接
文件，它具有性能优异和非常方便使用的特点。从图中可以看出，MyConfiguration负责与人交互。待读取xml后，
将属性和连接数据库的操作封装在MyConfiguration对象中供后面的组件调用。本文将使用dom4j来读取xml。

####  2.创建SqlSession，搭建Configuration和Executor之间的桥梁
我们经常在使用框架时看到Session，Session到底是什么呢？一个Session仅拥有一个对应的数据库连接。类似
于一个前段请求Request，它可以直接调用exec(SQL)来执行SQL语句。从流程图中的箭头可以看出，
MySqlSession的成员变量中必须得有MyExecutor和MyConfiguration去集中做调配，箭头就像是一种关联关系。
我们自己的MySqlSession将有一个getMapper方法，然后使用动态代理生成对象后，就可以做数据库的操作了。

#### 3.创建Executor，封装JDBC操作数据库

 Executor是一个执行器，负责SQL语句的生成和查询缓存（缓存还没完成）的维护，
 也就是jdbc的代码将在这里完成，不过本文只实现了单表，有兴趣的同学可以尝试完成多表。
 
####  4.创建MapperProxy，使用动态代理生成Mapper对象
我们只是希望对指定的接口生成一个对象，使得执行它的时候能运行一句sql罢了，而接口无法直接调用方法，
所以这里使用动态代理生成对象，在执行时还是回到MySqlSession中调用查询，最终由MyExecutor做JDBC查询。
这样设计是为了单一职责，可扩展性更强。
### 三、实现自己的Mybatis
工程文件及目录：
![image.png](https://upload-images.jianshu.io/upload_images/15204062-2d183da1c60a5c4f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

首先，新建一个maven项目，在pom.xml中导入以下依赖
```xml
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>
        <!-- 读取xml文件 -->
        <dependency>
            <groupId>dom4j</groupId>
            <artifactId>dom4j</artifactId>
            <version>1.6.1</version>
        </dependency>

        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.29</version>
        </dependency>
    </dependencies>
```
创建我们的数据库xml配置文件config.xml：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <database>
        <property name="driverClassName">com.mysql.jdbc.Driver</property>
        <property name="url">jdbc:mysql://localhost:3306/test?useUnicode=true&amp;characterEncoding=utf8</property>
        <property name="username">root</property>
        <property name="password">root</property>
    </database>
    <mappers>
        <mapper resource="mappers/UserMapper.xml"></mapper>
    </mappers>
</configuration>
```
然后在数据库创建test库，执行如下SQL语句：
```sql
CREATE TABLE `user` (
  `id` int NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
INSERT INTO `test`.`user` (`id`, `password`, `username`) VALUES (1, '123456', 'liugh');
```
创建User实体类，和UserMapper接口和对应的xml文件:
```java
public class User {

    private int id;
    private String username;
    private String password;
    //省略get set方法
}
```
```java
public interface UserMapper {

     User getUserById(int id);
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<mapper nameSpace="com.zwd.orm.mapper.UserMapper">
    <select id="getUserById" resultType ="com.zwd.orm.domain.User">
        select * from user where id = ?
    </select>
</mapper>
```
基本操作配置完成，接下来我们开始实现MyConfiguration：
```java
package com.zwd.orm.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyConfiguration {

    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    public static List<String> mapperList = new ArrayList<>();

    /**
     * 读取xml信息并处理
     */

    public Connection build(String resource) {

        try {
            InputStream stream = loader.getResourceAsStream(resource);
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            return evalDataSource(root);
        } catch (Exception e) {
            throw new RuntimeException("error occured while evaling xml " + resource);
        }
    }

    private Connection evalDataSource(Element node) throws ClassNotFoundException {
        Iterator iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (element.getName().equals("database")) {
                return getDataBase(element);
            }
        }
        throw new RuntimeException("配置文件中没有找到database");

    }

    private Connection getDataBase(Element element) throws ClassNotFoundException {
        String driverClassName = null;
        String url = null;
        String username = null;
        String password = null;
        //获取属性节点
        for (Object item : element.elements("property")) {
            Element i = (Element) item;
            String value = getValue(i);
            String name = i.attributeValue("name");
            if (name == null || value == null) {
                throw new RuntimeException("[database]: <property> should contain name and value");
            }
            //赋值
            switch (name) {
                case "url":
                    url = value;
                    break;
                case "username":
                    username = value;
                    break;
                case "password":
                    password = value;
                    break;
                case "driverClassName":
                    driverClassName = value;
                    break;
                default:
                    throw new RuntimeException("[database]: <property> unknown name");
            }
        }

        Class.forName(driverClassName);
        Connection connection = null;
        try {
            //建立数据库链接
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    //获取property属性的值,如果有value值,则读取 没有设置value,则读取内容
    private String getValue(Element node) {
        return node.hasContent() ? node.getText() : node.attributeValue("value");
    }


    @SuppressWarnings("rawtypes")
    public MapperBean readMapper(String path) {
        MapperBean mapper = new MapperBean();
        try {
            InputStream stream = loader.getResourceAsStream(path);
            SAXReader reader = new SAXReader();
            Document document = reader.read(stream);
            Element root = document.getRootElement();
            mapper.setInterfaceName(root.attributeValue("nameSpace").trim()); //把mapper节点的nameSpace值存为接口名
            List<Function> list = new ArrayList<Function>(); //用来存储方法的List
            for (Iterator rootIter = root.elementIterator(); rootIter.hasNext(); ) {//遍历根节点下所有子节点
                Function fun = new Function();    //用来存储一条方法的信息
                Element e = (Element) rootIter.next();
                String sqltype = e.getName().trim();
                String funcName = e.attributeValue("id").trim();
                String sql = e.getText().trim();
                String resultType = e.attributeValue("resultType").trim();
                fun.setSqltype(sqltype);
                fun.setFuncName(funcName);
                Object newInstance = null;
                try {
                    newInstance = Class.forName(resultType).newInstance();
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                fun.setResultType(newInstance);
                fun.setSql(sql);
                list.add(fun);
            }
            mapper.setList(list);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return mapper;
    }

     /**
     * 获取配置文件中需要加载的xml
     * @param configpath
     * @return
     */
    public List<String> getAllMapper(String configpath) {
        InputStream stream = loader.getResourceAsStream(configpath);
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            document = reader.read(stream);
        } catch (DocumentException e) {
            throw new RuntimeException("error occured while evaling xml " + configpath);
        }
        Element root = document.getRootElement();
        Iterator iterator = root.elementIterator();

        while (iterator.hasNext()) {
           Element element =(Element) iterator.next();

           if (element.getName().equals("mappers")) {
               Iterator iterator1 = element.elementIterator();
               while (iterator1.hasNext()) {
                   Element element1 = (Element)iterator1.next();
                   String resource = element1.attributeValue("resource").trim();
                   mapperList.add(resource);
               }
           }
        }
        return mapperList;
    }

}

```
用面向对象的思想设计读取xml配置后：
```java

public class MapperBean {

    private String interfaceName;  //接口名

    private List<Function> list; //接口下所有方法
    //省略set get方法
}
```
Function对象包括sql的类型、方法名、sql语句、返回类型和参数类型。
```java

public class Function {

    private String sqltype;
    private String funcName;
    private String sql;
    private Object resultType;
    private String parameterType;
    // 省略set get方法
}
```
接下来实现我们的MySqlSession,首先的成员变量里得有Excutor和MyConfiguration，代码的精髓就在getMapper的方法里。
```java
public class MySqlsession {

    private Excutor excutor= new MyExcutor();

    private MyConfiguration myConfiguration = new MyConfiguration();

    public MySqlsession(String configpath) {

        myConfiguration.getAllMapper(configpath);
    }



    public <T> T selectOne(String statement,Object parameter){
        return excutor.query(statement, parameter);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clas){
        //动态代理调用
        return (T) Proxy.newProxyInstance(clas.getClassLoader(),new Class[]{clas},
                new MyMapperProxy(myConfiguration,this));
    }

}
```
紧接着创建Excutor和实现类：
 ```java
public interface Excutor {

     <T> T query(String statement,Object parameter);
}
```
```java
public class MyMapperProxy implements InvocationHandler {

    private  MySqlsession mySqlsession;

    private MyConfiguration myConfiguration;

    public MyMapperProxy(MyConfiguration myConfiguration,MySqlsession mySqlsession) {
        this.myConfiguration=myConfiguration;
        this.mySqlsession=mySqlsession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {



        for (String path : MyConfiguration.mapperList) {
            MapperBean readMapper = myConfiguration.readMapper(path);
            //是否是xml文件对应的接口
            if(method.getDeclaringClass().getName().equals(readMapper.getInterfaceName())){
                List<Function> list = readMapper.getList();
                if(null != list || 0 != list.size()){
                    for (Function function : list) {
                        //id是否和接口方法名一样
                        if(method.getName().equals(function.getFuncName())){
                            return mySqlsession.selectOne(function.getSql(), String.valueOf(args[0]));
                        }
                    }
                }
            }
        }
        return null;
    }
}
```
到这里，就完成了自己的Mybatis框架，我们测试一下：
```java
public class Application {

    public static void main(String[] args) {
        MySqlsession sqlsession=new MySqlsession("config.xml");
        UserMapper mapper = sqlsession.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user);
    }
}
```
测试结果：
![image.png](https://upload-images.jianshu.io/upload_images/15204062-6100f64415c92ebd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)












