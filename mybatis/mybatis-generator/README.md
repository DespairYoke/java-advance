## 代码自动生成器

mybatis为了方便我们使用，提供了一套代码自动生成器机制，这样省去了我们编写大量POJO的时间，使开发人员有更多的时间投注了自己的逻辑代码编写中。
下面来演示下代码自动生成器是如何使用的。

### 引入mybatis提供的配置文件和插件依赖

maven依赖
```xml
 <plugin>
        <groupId>org.mybatis.generator</groupId>
        <artifactId>mybatis-generator-maven-plugin</artifactId>
        <version>1.3.6</version>
        <configuration>
            <verbose>true</verbose>
            <overwrite>true</overwrite>
        </configuration>
 </plugin>
```
mybatis配置文件
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN" "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">
<generatorConfiguration>

    <classPathEntry
            location="/Users/zwd-admin/.m2/repository/mysql/mysql-connector-java/5.1.38/mysql-connector-java-5.1.38.jar"/>

    <context id="common-model" targetRuntime="MyBatis3">

        <commentGenerator>
            <property name="suppressDate" value="true"/>
            <property name="suppressAllComments" value="true"/>
        </commentGenerator>

        <jdbcConnection connectionURL="jdbc:mysql://localhost:3306/test"
                        driverClass="com.mysql.jdbc.Driver" password="root" userId="root" />

        <javaModelGenerator targetPackage="com.zwd.mybatis.generator.domain"
                            targetProject="src/main/java" >
            <property name="enableSubPackages" value="true"/>
            <property name="trimStrings" value="true"/>
        </javaModelGenerator>

        <sqlMapGenerator targetPackage="mappers"
                         targetProject="src/main/resources" >
            <property name="enableSubPackages" value="true"/>
        </sqlMapGenerator>

        <javaClientGenerator targetPackage="com.zwd.mybatis.generator.mapper"
                             targetProject="src/main/java" type="XMLMAPPER" >
            <property name="enableSubPackages" value="true"/>
        </javaClientGenerator>

        <table tableName="User" domainObjectName="User"
               enableCountByExample="true"
               enableUpdateByExample="true"
               enableDeleteByExample="true"
               enableSelectByExample="true"
               selectByExampleQueryId="true"
        ></table>

    </context>
</generatorConfiguration>
```
![项目结构](https://upload-images.jianshu.io/upload_images/15204062-0410cec2ae47b6b5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

需要注意事项：
* location= 后面的路径是我们引入mysql依赖时自动下载的jar包，需要找到自己的jar位置。

* javaModelGenerator targetPackage后面是我们数据库对应的实体类生成后的位置。

* sqlMapGenerator targetPackage 这是mybatis生成时，对应的mapper.xml存放的位置

* javaClientGenerator targetPackage 是生成的mapper的存储位置。

* table是我们来生成对应表的实体类，多个表就在下面复制多个。

### 如何使用插件

我们找到对应位置的maven插件依赖，找到mybatis-generator。

![插件依赖](https://upload-images.jianshu.io/upload_images/15204062-ae3c43ec22860eab.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这时我们运行插件依赖，如果显示build success，那么恭喜你生成成功。

### mybatis example的使用

在自动生成的时候，mybatis帮助我们生成了一大堆Example文件，下面展示如何使用。

通过查看Mapper接口的方法，可以看出里面有几个我们熟悉的方法，selectByPrimaryKey、insert等。这些方法我们可以直接传入相应的参数去使用。
但这种简单的增删改并不是我们实际中所需的，通常我们都是不通过主键操作的。这时就要使用到selectExample方法。mybatis提供的Eample就排上用场了。

```java
    UserExample userExample = new UserExample();
    UserExample.Criteria criteria = userExample.createCriteria();
```
criteria中提供了各种各样的方法给我们使用，可以通过组合来满足我们的需求。
然后我们通过如下代码使用组合后的sql。
```java
        List<User> users = userMapper.selectByExample(userExample);
```
