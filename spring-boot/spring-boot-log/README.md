## springboot2.0日志探索

参考官网介绍
```
Spring Boot uses Commons Logging for all internal logging but leaves the underlying log implementation open.
 Default configurations are provided for Java Util Logging, Log4J2, and Logback. 
 In each case, loggers are pre-configured to use console output with optional file output also available.
```
Spring Boot使用Commons Logging进行所有内部日志记录，但保留底层日志实现。为Java Util Logging，Log4J2和 Logback提供了默认配置 。在每种情况下，记录器都预先配置为使用控制台输出，同时还提供可选的文件输出。

```
By default, Spring Boot logs only to the console and does not write log files. If you want to write log files in addition to the console output,
 you need to set a logging.file.name or logging.file.path property (for example, in your application.properties).
```
默认情况下，Spring Boot仅记录到控制台，不会写入日志文件。如果除了控制台输出之外还要编写日志文件，则需要设置 logging.file.name或logging.file.path属性（例如，在您的中 application.properties）。

logging.file.name | logging.file.path | Example | Description 
-----------|-----|-----------|------------
(none) | (none) | | Console only logging.
Specific file | none) | my.log   |Writes to the specified log file. Names can be an exact location or relative to the current directory.
(none) | Specific directory | /var/log   | Writes spring.log to the specified directory. Names can be an exact location or relative to the current directory.                                
                                
### 实战中遇到的日志配置
springboot结合mybatis时，可以配置属性
```properties
mybatis.configuration.log-impl
```
如果不配置，默认sql正确语句不会显示在控制台

如果想显示这配置如下：
```properties
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```
结合官网，如果想要日志显示在文件中则需要配置
```properties
loging.file.name=
loging.file.path=
```

配置日志等级        
TRACE，DEBUG，INFO，WARN，ERROR，FATAL
```properties
logging.level.<logger-name>=<level>
```
logger-name就是我们的包名，想要管控什么包下的日志会被打印，可以使用该属性进行配置

### 日志组介绍
如果很多包下的日志配相同级别，可以使用日志组进行配置，不需要一个一个编写
```properties
logging.group.tomcat=org.apache.catalina, org.apache.coyote, org.apache.tomcat
logging.level.tomcat=TRACE
```

### 自定义日志配置
```properties
logging.config=
```
Logging System | Customization
---------------|-------------
Logback | logback-spring.xml, logback-spring.groovy, logback.xml, or logback.groovy
Log4j2  | log4j2-spring.xml or log4j2.xml 
JDK (Java Util Logging) | logging.properties         

官网建议：使用-spring做为日志文件