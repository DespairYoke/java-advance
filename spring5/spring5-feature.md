## Spring Framework 5 中的新特性

Spring 5 于 2017 年 9 月发布了通用版本 (GA)，它标志着自 2013 年 12 月以来第一个主要 Spring Framework 版本。它提供了一些人们期待已久的改进，还采用了一种全新的编程范例，以反应式宣言中陈述的反应式原则为基础。

这个版本是很长时间以来最令人兴奋的 Spring Framework 版本。Spring 5 兼容 Java™8 和 JDK 9，它集成了反应式流，以便提供一种颠覆性方法来实现端点和 Web 应用程序开发。


诚然，反应式编程不仅是此版本的主题，还是令许多开发人员激动不已的重大特性。人们对能够针对负载波动进行无缝扩展的灾备和响应式服务的需求在不断增加，Spring 5 很好地满足了这一需求。

本文将全面介绍 Spring 5。我将介绍 Java SE 8 和 Java EE 7 API 的基准升级、Spring 5 的新反应式编程模型、HTTP/2 支持，以及 Spring 通过 Kotlin 对函数式编程的全面支持。我还会简要介绍测试和性能增强，最后介绍对 Spring 核心和容器的一般性修订。

### 升级到 Java SE 8 和 Java EE 7
直到现在，Spring Framework 仍支持一些弃用的 Java 版本，但 Spring 5 已从旧包袱中解放出来。为了充分利用 Java 8 特性，它的代码库已进行了改进，而且该框架要求将 Java 8 作为最低的 JDK 版本。

Spring 5 在类路径（和模块路径）上完全兼容 Java 9，而且它通过了 JDK 9 测试套件的测试。对 Java 9 爱好者而言，这是一条好消息，因为在 Java 9 发布后，Spring 能立即使用它。

在 API 级别上，Spring 5 兼容 Java EE 8 技术，满足对 Servlet 4.0、Bean Validation 2.0 和全新的 JSON Binding API 的需求。对 Java EE API 的最低要求为 V7，该版本引入了针对 Servlet、JPA 和 Bean Validation API 的次要版本。

### 反应式编程模型
Spring 5 最令人兴奋的新特性是它的反应式编程模型。Spring 5 Framework 基于一种反应式基础而构建，而且是完全异步和非阻塞的。只需少量的线程，新的事件循环执行模型就可以垂直扩展。

该框架采用反应式流来提供在反应式组件中传播负压的机制。负压是一个确保来自多个生产者的数据不会让使用者不堪重负的概念。

Spring WebFlux 是 Spring 5 的反应式核心，它为开发人员提供了两种为 Spring Web 编程而设计的编程模型：一种基于注解的模型和 Functional Web Framework (WebFlux.fn)。

基于注解的模型是 Spring WebMVC 的现代替代方案，该模型基于反应式基础而构建，而 Functional Web Framework 是基于 @Controller 注解的编程模型的替代方案。这些模型都通过同一种反应式基础来运行，后者调整非阻塞 HTTP 来适应反应式流 API。

### 使用注解进行编程
WebMVC 程序员应该对 Spring 5 的基于注解的编程模型非常熟悉。Spring 5 调整了 WebMVC 的 @Controller 编程模型，采用了相同的注解。

在清单 1 中，BookController 类提供了两个方法，分别响应针对某个图书列表的 HTTP 请求，以及针对具有给定 id 的图书的 HTTP 请求。请注意 resource 方法返回的对象（Mono 和 Flux）。这些对象是实现反应式流规范中的 Publisher 接口的反应式类型。它们的职责是处理数据流。Mono 对象处理一个仅含 1 个元素的流，而 Flux 表示一个包含 N 个元素的流。

清单 1. 反应式控制器

```java
@RestController
public class BookController {
 
    @GetMapping("/book")
    Flux<Book> list() {
        return this.repository.findAll();
    }
 
    @GetMapping("/book/{id}")
    Mono<Book> findById(@PathVariable String id) {
        return this.repository.findOne(id);
    }
 
    // Plumbing code omitted for brevity
}
```

这是针对 Spring Web 编程的注解。现在我们使用函数式 Web 框架来解决同一个问题。

### 函数式编程
Spring 5 的新函数式方法将请求委托给处理函数，这些函数接受一个服务器请求实例并返回一种反应式类型。清单 2 演示了这一过程，其中 listBook 和 getBook 方法类似于清单 1 中的功能。

清单 2. 清单 2.BookHandler 函数类
```java
public class BookHandler {
 
    public Mono<ServerResponse> listBooks(ServerRequest request) {
        return ServerResponse.ok()
            .contentType(APPLICATION_JSON)
            .body(repository.allPeople(), Book.class);
    }
     
    public Mono<ServerResponse> getBook(ServerRequest request) {
        return repository.getBook(request.pathVariable("id"))
            .then(book -> ServerResponse.ok()
            .contentType(APPLICATION_JSON)
            .body(fromObject(book)))
            .otherwiseIfEmpty(ServerResponse.notFound().build());
    }
    // Plumbing code omitted for brevity
}

```
通过路由函数来匹配 HTTP 请求谓词与媒体类型，将客户端请求路由到处理函数。清单 3 展示了图书资源端点 URI 将调用委托给合适的处理函数：

清单 3. Router 函数
```java
BookHandler handler = new BookHandler();
 
RouterFunction<ServerResponse> personRoute =
    route(
        GET("/books/{id}")
        .and(accept(APPLICATION_JSON)), handler::getBook)
        .andRoute(
    GET("/books")
        .and(accept(APPLICATION_JSON)), handler::listBooks);

```
这些示例背后的数据存储库也支持完整的反应式体验，该体验是通过 Spring Data 对反应式 Couchbase、Reactive MongoDB 和 Cassandra 的支持来实现的。

### 使用 REST 端点执行反应式编程
新的编程模型脱离了传统的 Spring WebMVC 模型，引入了一些很不错的新特性。

举例来说，WebFlux 模块为 RestTemplate 提供了一种完全非阻塞、反应式的替代方案，名为 WebClient。清单 4 创建了一个 WebClient，并调用 books 端点来请求一本给定 id 为 1234 的图书。

清单 4. 通过 WebClient 调用 REST 端点
```java
Mono<Book> book = WebClient.create("http://localhost:8080")
      .get()
      .url("/books/{id}", 1234)
      .accept(APPLICATION_JSON)
      .exchange(request)
      .then(response -> response.bodyToMono(Book.class));

```
### HTTP/2 支持
HTTP/2 幕后原理：要了解 HTTP/2 如何提高传输性能，减少延迟，并帮助提高应用程序吞吐量，从而提供经过改进的丰富 Web 体验，请查阅我的有关这项期待已久的升级的文章。

Spring Framework 5.0 将提供专门的 HTTP/2 特性支持，还支持人们期望出现在 JDK 9 中的新 HTTP 客户端。尽管 HTTP/2 的服务器推送功能已通过 Jetty servlet 引擎的 ServerPushFilter 类向 Spring 开发人员公开了很长一段时间，但如果发现 Spring 5 中开箱即用地提供了 HTTP/2 性能增强，Web 优化者们一定会为此欢呼雀跃。

Java EE Servlet 规范预计将于 2017 年第 4 季度发布，Servlet 4.0 支持将在 Spring 5.1 中提供。到那时，HTTP/2 特性将由 Tomcat 9.0、Jetty 9.3 和 Undertow 1.4 原生提供。

### Kotlin 和 Spring WebFlux
Kotlin 是一种来自 JetBrains 的面向对象的语言，它支持函数式编程。它的主要优势之一是与 Java 有非常高的互操作性。通过引入对 Kotlin 的专门支持，Spring 在 V5 中全面吸纳了这一优势。它的函数式编程风格与 Spring WebFlux 模块完美匹配，它的新路由 DSL 利用了函数式 Web 框架以及干净且符合语言习惯的代码。可以像清单 5 中这样简单地表达端点路由：

清单 5. Kotlin 的用于定义端点的路由 DSL
```java
@Bean
fun apiRouter() = router {
    (accept(APPLICATION_JSON) and "/api").nest {
        "/book".nest {
            GET("/", bookHandler::findAll)
            GET("/{id}", bookHandler::findOne)
        }
        "/video".nest {
            GET("/", videoHandler::findAll)
            GET("/{genre}", videoHandler::findByGenre)
        }
    }
}
```
使用 Kotlin 1.1.4+ 时，还添加了对 Kotlin 的不可变类的支持（通过带默认值的可选参数），以及对完全支持 null 的 API 的支持。

### 使用 Lambda 表达式注册 bean
作为传统 XML 和 JavaConfig 的替代方案，现在可以使用 lambda 表达式注册 Spring bean，使 bean 可以实际注册为提供者。清单 6 使用 lambda 表达式注册了一个 Book bean。

清单 6. 将 Bean 注册为提供者
```java
GenericApplicationContext context = new GenericApplicationContext();
context.registerBean(Book.class, () -> new 
              Book(context.getBean(Author.class))
        );
```
### Spring WebMVC 支持最新的 API
全新的 WebFlux 模块提供了许多新的、令人兴奋的功能，但 Spring 5 也迎合了愿意继续使用 Spring MVC 的开发人员的需求。Spring 5 中更新了模型-视图-控制器框架，以兼容 WebFlux 和最新版的 Jackson 2.9 和 Protobuf 3.0，甚至包括对新的 Java EE 8 JSON-Binding API 的支持。

除了 HTTP/2 特性的基础服务器实现之外，Spring WebMVC 还通过 MVC 控制器方法的一个参数来支持 Servlet 4.0 的 PushBuilder。最后，WebMVC 全面支持 Reactor 3.1 的 Flux 和 Mono 对象，以及 RxJava 1.3 和 2.1，它们被视为来自 MVC 控制器方法的返回值。这项支持的最终目的是支持 Spring Data 中的新的反应式 WebClient 和反应式存储库。

### 使用 JUnit 5 执行条件和并发测试
JUnit 和 Spring 5：Spring 5 全面接纳了函数式范例，并支持 JUnit 5 及其新的函数式测试风格。还提供了对 JUnit 4 的向后兼容性，以确保不会破坏旧代码。

Spring 5 的测试套件通过多种方式得到了增强，但最明显的是它对 JUnit 5 的支持。现在可以在您的单元测试中利用 Java 8 中提供的函数式编程特性。清单 7 演示了这一支持：

清单 7. 清单 7.JUnit 5 全面接纳了 Java 8 流和 lambda 表达式
```java
@Test
void givenStreamOfInts_SumShouldBeMoreThanFive() {
    assertTrue(Stream.of(20, 40, 50)
      .stream()
      .mapToInt(i -> i)
      .sum() > 110, () -> "Total should be more than 100");
}
```
迁移到 JUnit 5：如果您对升级到 JUnit 5 持观望态度，Steve Perry 的分两部分的深入剖析教程将说服您冒险尝试。

Spring 5 继承了 JUnit 5 在 Spring TestContext Framework 内实现多个扩展 API 的灵活性。举例而言，开发人员可以使用 JUnit 5 的条件测试执行注解 @EnabledIf 和 @DisabledIf 来自动计算一个 SpEL (Spring Expression Language) 表达式，并适当地启用或禁用测试。借助这些注解，Spring 5 支持以前很难实现的复杂的条件测试方案。Spring TextContext Framework 现在能够并发执行测试。

### 使用 Spring WebFlux 执行集成测试
Spring Test 现在包含一个 WebTestClient，后者支持对 Spring WebFlux 服务器端点执行集成测试。WebTestClient 使用模拟请求和响应来避免耗尽服务器资源，并能直接绑定到 WebFlux 服务器基础架构。

WebTestClient 可绑定到真实的服务器，或者使用控制器或函数。在清单 8 中，WebTestClient 被绑定到 localhost：

清单 8. 绑定到 localhost 的 WebTestClient
```java
WebTestClient testClient = WebTestClient
  .bindToServer()
  .baseUrl("http://localhost:8080")
  .build();

```
在清单 9 中，测试了 RouterFunction：

清单 9. 将 WebTestClient 绑定到 RouterFunction
```java
RouterFunction bookRouter = RouterFunctions.route(
  RequestPredicates.GET("/books"),
  request -> ServerResponse.ok().build()
);
  
WebTestClient
  .bindToRouterFunction(bookRouter)
  .build().get().uri("/books")
  .exchange()
  .expectStatus().isOk()
  .expectBody().isEmpty();
```
### 包清理和弃用
Spring 5 中止了对一些过时 API 的支持。遭此厄运的还有 Hibernate 3 和 4，为了支持 Hibernate 5，它们遭到了弃用。另外，对 Portlet、Velocity、JasperReports、XMLBeans、JDO 和 Guava 的支持也已中止。

包级别上的清理工作仍在继续：Spring 5 不再支持 beans.factory.access、jdbc.support.nativejdbc、mock.staticmock（来自 spring-aspects 模块）或 web.view.tiles2M。Tiles 3 现在是 Spring 的最低要求。

### 对 Spring 核心和容器的一般更新
Spring Framework 5 改进了扫描和识别组件的方法，使大型项目的性能得到提升。目前，扫描是在编译时执行的，而且向 META-INF/spring.components 文件中的索引文件添加了组件坐标。该索引是通过一个为项目定义的特定于平台的应用程序构建任务来生成的。

标有来自 javax 包的注解的组件会添加到索引中，任何带 @Index 注解的类或接口都会添加到索引中。Spring 的传统类路径扫描方式没有删除，而是保留为一种后备选择。有许多针对大型代码库的明显性能优势，而托管许多 Spring 项目的服务器也会缩短启动时间。

Spring 5 还添加了对 @Nullable 的支持，后者可用于指示可选的注入点。使用者现在必须准备接受 null 值。此外，还可以使用此注解来标记可以为 null 的参数、字段和返回值。@Nullable 主要用于 IntelliJ IDEA 等 IDE，但也可用于 Eclipse 和 FindBugs，它使得在编译时处理 null 值变得更方便，而无需在运行时发送 NullPointerExceptions。

Spring Logging 还提升了性能，自带开箱即用的 Commons Logging 桥接器。现在已通过资源抽象支持防御性编程，为 getFile 访问提供了 isFile 指示器。

### 结束语
Spring 5 的首要特性是新的反应式编程模型，这代表着对提供可无缝扩展、基于 Spring 的响应式服务的重大保障。随着人们对 Spring 5 的采用，开发人员有望看到反应式编程将会成为使用 Java 语言的 Web 和企业应用程序开发的未来发展道路。

未来的 Spring Framework 版本将继续反映这一承诺，因为 Spring Security、Spring Data 和 Spring Integration 有望采用反应式编程的特征和优势。

总之，Spring 5 代表着一次大受 Spring 开发人员欢迎的范例转变，同时也为其他框架指出了一条发展之路。