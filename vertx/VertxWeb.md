# Vertx  Web

Vert.x-Web是基于Vert.x的，用于构建Web应用程序的一系列构建模块。

Vert.x Core 提供了一系列相对底层的功能用于操作HTTP， 对于一部分应用是足够的。

Vert.x Web 基于 Vert.x Core 提供了一系列更丰富的功能， 以便更容易地开发实际的 Web 应用。

Vert.x Web 的设计是强大的，非侵入式的, 并且是完全可插拔的。您可以只使用您需要的部分。 Vert.x Web 不是一个容器。

您可以使用 Vert.x Web 来构建经典的服务端 Web 应用， RESTful 应用， 实时的（服务端推送) Web 应用, 或任何您所能想到的 Web 应用类型。 应用类型的选择取决于您的喜好，而不是 Vert.x Web。

Vert.x Web 的一部分关键特性有：

- 路由(基于方法,路径等)
- 基于正则表达式的路径匹配
- 从路径中提取参数
- 内容协商
- 处理消息体
- 消息体的长度限制
- Multipart 表单
- Multipart 文件上传
- 子路由
- 支持本地会话和集群会话
- 支持 CORS(跨域资源共享)
- 错误页面处理器
- HTTP基本/摘要认证
- 基于重定向的认证
- 授权处理器
- 基于 JWT 的授权
- 用户/角色/权限授权
- 网页图标处理器
- 支持服务端模板渲染，包括以下开箱即用的模板引擎:
  - Handlebars
  - Jade
  - MVEL
  - Thymeleaf
  - Apache FreeMarker
  - Pebble
  - Rocker
- 响应时间处理器
- 静态文件服务，包括缓存逻辑以及目录监听
- 支持请求超时
- 支持 SockJS
- 桥接 Event-bus
- CSRF 跨域请求伪造
- 虚拟主机

Vert.x Web 的大部分特性是使用Handler实现的， 而且您随时可以实现您自己的处理器。

## 使用 Vert.x Web

在使用 Vert.x Web 之前，需要为您的构建工具在描述文件中添加 *dependencies* 依赖项：

- Maven:

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle：

```groovy
dependencies {
 compile 'io.vertx:vertx-web:4.1.5'
}
```

### 开发模式

Vert.x Web 默认使用生产模式。 您可以通过设置 `dev` 值到下面的其中一个来切换开发模式：

- `VERTXWEB_ENVIRONMENT` 环境变量，或
- `vertxweb.environment` 系统属性

在开发模式：

- 模板引擎缓存被禁用
- `ErrorHandler` 不显示异常详细信息
- `StaticHandler` 不处理缓存头
- GraphQL开发工具被禁用

## Vert.x Web 的基本概念

`路由器 Router` 是 Vert.x Web 的核心概念之一。 它是一个维护了零或多个 `路由 Routes` 的对象。

一个 router 接收 HTTP 请求，并查找首个匹配该请求的route, 然后将请求传递给这个route

`Route` 可以持有一个与之关联的 *handler* 用于接收请求。 您可以通过这个处理器对请求 *做一些事情*, 然后结束响应或者把请求传递给下一个匹配的处理器。

以下是一个简单的路由示例：

```java
HttpServer server = vertx.createHttpServer();

Router router = Router.router(vertx);

router.route().handler(ctx -> {

  // 所有的请求都会调用这个处理器处理
  HttpServerResponse response = ctx.response();
  response.putHeader("content-type", "text/plain");

  // 写入响应并结束处理
  response.end("Hello World from Vert.x-Web!");
});

server.requestHandler(router).listen(8080);
```

我们像以前一样创建一个HTTP服务器，然后我们创建一个 router。当我们完成这些之后， 我们创建一个简单的没有匹配条件的 rout，它能够匹配 *全部* 到来的请求。

然后，我们为该路由指定一个处理器。该处理器将处理所有到来的请求。

传递给处理器的对象是 `RoutingContext` - 它包含标准的 Vert.x `HttpServerRequest` 和 `HttpServerResponse` 还有其他各种有用的东西，让使用Vert.x-Web变得更加简单。

## 处理请求并调用下一个处理器

当 Vert.x Web 决定路由一个请求到匹配的route， 它会调用对应处理器并将一个 `RoutingContext` 实例传递给它.route可以具有不同的处理器， 您可以叠加使用 `handler`

如果您不在处理器里结束这个响应，您需要调用 `next` 方法让其他匹配的 route 来处理请求(如果有)。

您不需要在处理器执行完毕时调用 `next` 。 您可以在之后需要的时间点调用它：

```java
Route route = router.route("/some/path/");
route.handler(ctx -> {

  HttpServerResponse response = ctx.response();
  // 开启分块响应，
  // 因为我们将在执行其他处理器时添加数据
  // 仅当有多个处理器输出时
  response.setChunked(true);

  response.write("route1\n");

  // 延迟5秒后调用下一匹配route
  ctx.vertx().setTimer(5000, tid -> ctx.next());
});

route.handler(ctx -> {

  HttpServerResponse response = ctx.response();
  response.write("route2\n");

  // 延迟5秒后调用下一匹配route
  ctx.vertx().setTimer(5000, tid -> ctx.next());
});

route.handler(ctx -> {

  HttpServerResponse response = ctx.response();
  response.write("route3");

  // 现在结束响应
  ctx.response().end();
});
```

## 简单的响应

处理器非常强大， 因为它们允许您构建非常复杂的应用程序。 为了保证简单的响应， 例如直接从vert.x API返回异步响应， router 包含一个快捷的处理器：

1. 响应返回JSON。
2. 如果处理过程中发生错误，一个适当的错误会返回。
3. 如果序列化JSON中发生错误，一个适当的错误会返回。

```java
router
  .get("/some/path")
  // 这个处理器将保证这个响应会被序列化成json
  // content type被设置成 "application/json"
  .respond(
    ctx -> Future.succeededFuture(new JsonObject().put("hello", "world")));

router
  .get("/some/path")
  // 这个处理器将保证这个Pojo会被序列化成json
  // content type被设置成 "application/json"
  .respond(
    ctx -> Future.succeededFuture(new Pojo()));
```

## 使用阻塞式处理器

某些时候您可能需要在处理器里执行一些需要阻塞 Event Loop 的操作， 比如调用某个传统的阻塞式 API 或者执行密集计算。

您不能在普通的处理器里执行这些操作， 因此我们提供了将route设置成阻塞式处理器的功能。

阻塞式处理器和普通处理器很像， 区别是 Vert.x 会使用 Worker Pool 中的线程而不是 Event Loop 线程来处理请求。

您可以使用 `blockingHandler` 方法来建立阻塞式处理器。 以下是例子：

```java
router.route().blockingHandler(ctx -> {

  // 执行某些同步的耗时操作
  service.doSomethingThatBlocks();

  // 调用下一个处理器
  ctx.next();

});
```

默认情况下，在同一个 Context (例如同一个 Verticle 实例) 上执行的所有阻塞式处理器是顺序的， 也就意味着只有一个处理器执行完了才会继续执行下一个。 如果您不关心执行的顺序， 并且不介意阻塞式处理器以并行的方式执行， 您可以在使用 `blockingHandler` 时，设置阻塞式处理器的 `ordered` 为 false。

如果您需要在一个阻塞处理器中处理一个 multipart 类型的表单数据， 您需要首先使用一个非阻塞的处理器来调用 `setExpectMultipart(true)` 。

```java
router.post("/some/endpoint").handler(ctx -> {
  ctx.request().setExpectMultipart(true);
  ctx.next();
}).blockingHandler(ctx -> {
  // ... 执行某些阻塞操作
});
```

## 基于精确路径的路由

可以将 `Route` 设置为根据需要所匹配的 URI。 在这种情况下它只会匹配路径一致的请求。

在下面这个例子中，处理器会被路径为 `/some/path/` 的请求调用。 我们会忽略结尾的 `/` ， 所以路径 `/some/path` 或者 `/some/path//` 的请求也是匹配的

```java
Route route = router.route().path("/some/path/");

route.handler(ctx -> {
  // 这个处理器会被以下路径的请求调用：

  // `/some/path/`
  // `/some/path//`
  //
  // 但不包括:
  // `/some/path` 路径末尾的斜线会被严格限制
  // `/some/path/subdir`
});

// 路径结尾没有斜线的不会被严格限制
// 这意味着结尾的斜线是可选的
// 无论怎样都会匹配
Route route2 = router.route().path("/some/path");

route2.handler(ctx -> {
  // 这个处理器会被以下路径的请求调用：

  // `/some/path`
  // `/some/path/`
  // `/some/path//`
  //
  // 但不包括:
  // `/some/path/subdir`
});
```

## 基于路径前缀的路由

您经常需要为所有以某些路径开始的请求设置 `Route` 。 您可以使用正则表达式来实现， 但更简单的方式是在声明 `Route` 的路径时使用一个 `*` 作为结尾。

在下面的例子中处理器会匹配所有 URI 以 `/some/path` 开头的请求。

例如 `/some/path/foo.html` 和 `/some/path/otherdir/blah.css` 都会匹配。

```java
Route route = router.route().path("/some/path/*");

route.handler(ctx -> {
  // 这个处理器处理会被所有以
  // `/some/path/` 开头的请求调用， 例如：

  // `/some/path/`
  // `/some/path/subdir`
  // `/some/path/subdir/blah.html`
  //
  // 但同时：
  // `/some/path` 最终的斜杆总是可选的并配有通配符，
  //              以保持与许多客户端库的兼容性。
  // 但 **不包括**：
  // `/some/patha`
  // `/some/patha/`
  // 等等……
});
```

也可以在创建 `Route` 的时候指定任意的路径：

```java
Route route = router.route("/some/path/*");

route.handler(ctx -> {
  // 这个处理器的调用规则和上面的例子一样
});
```

## 捕捉路径参数

可以通过占位符声明路径参数并在处理请求时通过 `pathParam` 。 方法获取

以下是例子

```java
router
  .route(HttpMethod.POST, "/catalogue/products/:productType/:productID/")
  .handler(ctx -> {

    String productType = ctx.pathParam("productType");
    String productID = ctx.pathParam("productID");

    // 执行某些操作...
  });
```

占位符由 `:` 和参数名构成。 参数名由字母，数字和下划线构成。 在某些情况下，这会受到一定限制，因而用户可以切换至包括2个额外字符“-”和“ $”的扩展名称规则。 扩展参数规则可用如下系统属性启用：

```
-Dio.vertx.web.route.param.extended-pattern=true
```

在上述例子中， 如果一个 POST 请求的路径为 `/catalogue/products/tools/drill123/` ， 那么会匹配这个 `Route` ， 并且会接收参数 `productType` 的值为 `tools` ，参数 `productID` 的值为 `drill123` 。

参数并不一定是路径段。例如，以下路径参数同样有效：

```java
router
  .route(HttpMethod.GET, "/flights/:from-:to")
  .handler(ctx -> {
    // 在处理发送至/flights/AMS-SFO的请求时，将会设置：
    String from = ctx.pathParam("from"); // AMS
    String to = ctx.pathParam("to"); // SFO
    // 记住一点，如果不切换至参数命名的 “extend/扩展” 模式的话，
    // 这将不会起作用。
    // 因为在那种情况下，“-” 符号并不被认为是分隔符，
    // 而是参数名的一部分。
  });
```

## 基于正则表达式的路由

同样也可用正则表达式匹配路由的 URI 路径。

```java
Route route = router.route().pathRegex(".*foo");

route.handler(ctx -> {

  // 以下路径的请求都会调用这个处理器：

  // /some/path/foo
  // /foo
  // /foo/bar/wibble/foo
  // /bar/foo

  // 但不包括：
  // /bar/wibble
});
```

或者在创建 route 时指定正则表达式：

```java
Route route = router.routeWithRegex(".*foo");

route.handler(ctx -> {

  // 这个路由器的调用规则和上面的例子一样

});
```

## 通过正则表达式捕捉路径参数

您也可以通过正则表达式声明捕捉路径参数，以下是例子：

```java
Route route = router.routeWithRegex(".*foo");

// 这个正则表达式可以匹配路径类似于：
// `/foo/bar` 的请求
// `foo` 可以通过参数 param0 获取，`bar` 可以通过参数 param1 获取
route.pathRegex("\\/([^\\/]+)\\/([^\\/]+)").handler(ctx -> {

  String productType = ctx.pathParam("param0");
  String productID = ctx.pathParam("param1");

  // 执行某些操作……
});
```

在上述的例子中，如果一个请求的路径为 `/tools/drill123/`，那么会匹配这个 `route`， 并且会接收到参数 `productType` 的值为 `tools`，参数 `productID` 的值为 `drill123`。

捕捉在正则表达式中用捕捉组表示（即用圆括号括住捕捉）

## 使用命名的捕捉组

使用序号参数名在某些场景下可能会比较麻烦。 亦可在正则表达式路径中使用命名的捕捉组。

```java
router
  .routeWithRegex("\\/(?<productType>[^\\/]+)\\/(?<productID>[^\\/]+)")
  .handler(ctx -> {

    String productType = ctx.pathParam("productType");
    String productID = ctx.pathParam("productID");

    // 执行某些操作……
  });
```

在上述的例子中，命名捕捉组将路径参数映射到同名的捕捉组中。

此外，您仍可以使用普通捕捉组访问组参数（例如：`params0, params1…`）

## 基于 HTTP 方法的路由

Route 默认会匹配所有的 HTTP 方法。

如果您只想让 route 匹配特定的 HTTP 方法，那么您可以使用 `method`

```java
Route route = router.route().method(HttpMethod.POST);

route.handler(ctx -> {

  // 所有的 POST 请求都会调用这个处理器

});
```

或者您可以在创建 Route 时和路径一起指定：

```java
Route route = router.route(HttpMethod.POST, "/some/path/");

route.handler(ctx -> {
  // 所有路径为 `/some/path/`
  // 的 POST 请求都会调用这个处理器
});
```

如果您想让 Route 指定 HTTP 方法，您也可以使用对应的 `get`， `post` 以及 `put` 等方法。 例如：

```java
router.get().handler(ctx -> {

  // 所有 GET 请求都会调用这个处理器

});

router.get("/some/path/").handler(ctx -> {

  // 所有路径以 `/some/path/` 开始的
  // GET 请求都会调用这个处理器

});

router.getWithRegex(".*foo").handler(ctx -> {

  // 所有路径以 `foo` 结尾的
  // GET 请求都会调用这个处理器

});
```

如果您想要让 route 匹配不止一个 HTTP 方法， 您可多次调用 `method` 方法：

```java
Route route = router.route().method(HttpMethod.POST).method(HttpMethod.PUT);

route.handler(ctx -> {

  // 所有 GET 或 POST 请求都会调用这个处理器

});
```

如果您的应用程序需要自定义 HTTP 动词，例如， `基于Web的分布式编写和版本控制（WebDAV）` 服务器中， 您可这样自定义动词：

```java
Route route = router.route()
  .method(HttpMethod.valueOf("MKCOL"))
  .handler(ctx -> {
    // 所有 MKCOL 请求都会调用这个处理器
  });
```

## 路由顺序

默认情况下Route按照其加入到Router的顺序进行匹配

路由器会逐级检查每条Route否匹配 如果匹配的话，该Route的handler将被调用。

如果这个handler接下来会调用 `next` 方法 则下一个匹配的路由(如果有的话)的handler将被调用。等等。

如果您想要覆盖默认的Route顺序，您可以使用 `order` 指定一个整数类型的值

Route在创建时被分配的顺序与它们被添加到Router的顺序相对应 第一个Route编号为 `0`，第二个Route编号为 `1`，以此类推。

通过给Route指定order您可以覆盖默认值，order可以为负值，举个例子 如果想要确保一个Route在order为 `0` 的Route之前执行则可以这样做

让我们更改route2的order值让他在route1之前执行

```java
router
  .route("/some/path/")
  .order(1)
  .handler(ctx -> {

    HttpServerResponse response = ctx.response();
    response.write("route1\n");

    // 现在调用下一个匹配的Route
    ctx.next();
  });

router
  .route("/some/path/")
  .order(0)
  .handler(ctx -> {

    HttpServerResponse response = ctx.response();
    // 启动response的分块响应功能，
    // 因为我们将在多个handler中将添加数据
    // 只需要一次，并且只在多个处理程序进行输出时才需要。
    response.setChunked(true);

    response.write("route2\n");

    // Now call the next matching route
    ctx.next();
  });

router
  .route("/some/path/")
  .order(2)
  .handler(ctx -> {

    HttpServerResponse response = ctx.response();
    response.write("route3");

    // Now end the response
    ctx.response().end();
  });
```

然后响应将包含以下内容

```
route2
route1
route3
```

如果两个匹配的Route都具有相同的order值，则按照他们添加的顺序被调用。

也可以指定一个路由最后调用，参考 `last`

## 基于请求MIME类型的路由

通过使用 `consumes`，您可以指定route将与的哪种请求MIME类型相匹配。

在这种情况下，请求将包含一个 `content-type` 请求头，指定请求体的MIME类型 这将匹配 `consumes` 指定的值。

基本上，`consumes` 用于描述这个handler将可以 *处理* 哪些MIME类型

匹配可以在精确的MIME类型匹配上进行：

```java
router.route()
  .consumes("text/html")
  .handler(ctx -> {

    //这个handler将会被
    //content-type 请求头设置为`text/html`的任意请求调用

  });
```

也可以指定多个精确的匹配：

```java
router.route()
  .consumes("text/html")
  .consumes("text/plain")
  .handler(ctx -> {

    // 这个handler将会被
    // content-type 请求头设置为`text/html`或者`text/plain`的任意请求调用

  });
```

支持子类型通配符的匹配：

```java
router.route()
  .consumes("text/*")
  .handler(ctx -> {

    //这个handler将会被
    //顶级类型为`text` 例如
    //content-type被设置为`text/html` 或者 `text/plain`的任意请求
    //匹配

  });
```

而且您也可以匹配顶级类型：

```java
router.route()
  .consumes("*/json")
  .handler(ctx -> {

    //这个handler将会被子类型为json的任意请求调用
    //例如content-type请求头设置为`text/json`或者
    // `application/json` 都会匹配

  });
```

如果您没有在consumer中指定一个 `/` ，它会假定您指的是子类型

## 基于客户端可接收的MIME类型的路由

HTTP `accept` 请求头用于表示客户端可以接受响应的MIME类型

一个 `accept` 请求头可以包含多个MIME类型，其之间用 ‘,’ 分割

MIME类型还可以附加一个 `q` 值，这表示如果有多个响应MIME类型与接受请求头匹配，则指定一个权重 q值是0到1.0之间的数字。 如果省略，则默认为1.0。

举个例子，下面的 `accept` 请求头则指定客户端将只能接收 `text/plain` 的MIME类型数据：

Accept: text/plain

客户端将接受 `text/plain` 或 `text/html` ，没有优先级：

Accept: text/plain, text/html

客户端将接受 `text/plain` 或 `text/html` ，但因为 `text/html` 有一个更高的 `q` 值(默认q=1.0)，所以客户端会优先接收 `text/html` ：

Accept: text/plain; q=0.9, text/html

如果服务端可以同时提供 text/plain 和 text/html，在这个例子里面它应当提供 text/html。

通过使用 `produces` 您可以决定Route可以产生那个(哪些) MIME 类型 例如 下面这个handler会产生一个MIME类型为 `application/json` 的响应。

```java
router.route()
  .produces("application/json")
  .handler(ctx -> {

    HttpServerResponse response = ctx.response();
    response.putHeader("content-type", "application/json");
    response.end(someJSON);

  });
```

在这种情况下，Route将匹配带有 `accept` 请求头且匹配 `application/json` 的任何请求。

这有一些 `accept` 请求头将如何匹配的例子：

Accept: application/json Accept: application/* Accept: application/json, text/html Accept: application/json;q=0.7, text/html;q=0.8, text/plain

您还可以将您的路由标记为生成多个MIME类型。如果是这样，那么使用 `getAcceptableContentType` 找出实际被接收的MIME类型。

```java
router.route()
  .produces("application/json")
  .produces("text/html")
  .handler(ctx -> {

    HttpServerResponse response = ctx.response();

    // 获取真正可接受的MIME类型
    String acceptableContentType = ctx.getAcceptableContentType();

    response.putHeader("content-type", acceptableContentType);
    response.end(whatever);
  });
```

在上面的例子中，如果您发送了一个带有以下 `accept` 请求头的请求：

Accept: application/json; q=0.7, text/html

然后路由将匹配，`acceptableContentType` 将包含 `text/html` 两个都是可以接受的但是它有更高的 `q` 值。

## 上下文数据

您可以使用 `RoutingContext` 保存任何 在请求生命周期内您想在多个handler之间共享的数据

下面是一个例子，其中一个handler在上下文数据中设置一些数据，然后一个后续的处理程序获取它：

您可以使用 `put` 添加任何对象， 然后使用 `get` 获取任何来自于上下文的对象

一个发送到 `/some/path/other` 的请求将匹配这两个Route。

```java
router.get("/some/path").handler(ctx -> {

  ctx.put("foo", "bar");
  ctx.next();

});

router.get("/some/path/other").handler(ctx -> {

  String bar = ctx.get("foo");
  // 用bar对象做一些事情
  ctx.response().end();

});
```

您也可以使用 `data` 获取全部的上下文数据map

## 帮手函数

虽然路由上下文将允许您获取基础请求和响应对象， 但有时如果有一些捷径可以帮助您完成常见的任务，您的工作效率会更高。 有几个帮手存在于上下文中可以便于完成这项任务。

提供一个“附件”，附件是一种响应，它将触发浏览器打开配置为处理特定MIME类型的操作系统应用程序。 假设您正在生成一个PDF文件：

```java
ctx
  .attachment("weekly-report.pdf")
  .end(pdfBuffer);
```

执行重定向到另一个页面或主机。一个例子是重定向到应用程序的HTTPS变体：

```java
ctx.redirect("https://securesite.com/");

//对于目标为“back”有一个特殊的处理。
//在这种情况下，重定向会将用户发送到
//referrer url或 "/"如果没有referrer。

ctx.redirect("back");
```

向客户端发送一个JSON响应：

```java
ctx.json(new JsonObject().put("hello", "vert.x"));
// 也可以用于数组
ctx.json(new JsonArray().add("vertx").add("web"));
//或者用于任意对象
//其将根据运行时的json编码器进行转化
ctx.json(someObject);
```

常规的content-type校验：

```java
ctx.is("html"); // => true
ctx.is("text/html"); // => true

//当content-type为application/json时
ctx.is("application/json"); // => true
ctx.is("html"); // => false
```

验证有关缓存头和last modified/etag的当前值的请求是否"新鲜"

```java
ctx.lastModified("Wed, 13 Jul 2011 18:30:00 GMT");
// 现在将使用它来验证请求的新鲜度
if (ctx.isFresh()) {
  //客户端缓存值是新鲜的，
  //也许我们可以停止并返回304？
}
```

和其他一些简单的无需解释的快捷方式

```java
ctx.etag("W/123456789");

// 设置last modified 的值
ctx.lastModified("Wed, 13 Jul 2011 18:30:00 GMT");

// 便捷结束响应
ctx.end();
ctx.end("body");
ctx.end(buffer);
```

## 重新路由

到目前为止，所有路由机制都允许您以顺序的方式处理请求， 但是有时您可能希望后退。由于上下文没有公开有关上一个或下一个handler的任何信息， 主要是因为此信息是动态的， 因此有一种方法可以从当前路由器的开头重新启动整个路由。

```java
router.get("/some/path").handler(ctx -> {

  ctx.put("foo", "bar");
  ctx.next();

});

router
  .get("/some/path/B")
  .handler(ctx -> ctx.response().end());

router
  .get("/some/path")
  .handler(ctx -> ctx.reroute("/some/path/B"));
```

因此，从代码中您可以看到，如果请求首先到达 `/some/path` 且最先向上下文中添加一个值， 然后移至下一个handler，该处理程序将请求重新路由至 `/some/path/B`，从而终止请求。

您可以基于新路径或基于新路径和方法重新路由。 但是请注意，基于方法的重新路由可能会引入安全性问题，因为例如通常安全的GET请求可能会变为DELETE。

失败处理程序上也允许重新路由，但是由于重新路由的性质，当被调用时，当前状态代码和失败原因将会重置 为了在需要时重新路由的处理程序应生成正确的状态代码， 例如：

```java
router.get("/my-pretty-notfound-handler").handler(ctx -> ctx.response()
  .setStatusCode(404)
  .end("NOT FOUND fancy html here!!!"));

router.get().failureHandler(ctx -> {
  if (ctx.statusCode() == 404) {
    ctx.reroute("/my-pretty-notfound-handler");
  } else {
    ctx.next();
  }
});
```

应当清楚的是，重新路由可以在 `路径` 上使用，因此，如果您需要在重新路由之间保留或添加状态，则应使用 `RoutingContext` 对象。 例如，您想使用额外的参数重新路由到新路径：

```java
router.get("/final-target").handler(ctx -> {
  // 在这里做一些事情
});

// 将会带着查询字符串重定向到 /final-target
router.get().handler(ctx -> ctx.reroute("/final-target?variable=value"));

// 一个更安全的方法是将变量添加至上下文中
router.get().handler(ctx -> ctx
  .put("variable", "value")
  .reroute("/final-target"));
```

重新路由也会重新解析查询参数。请注意，先前的查询参数将被丢弃。 该方法还将静默地丢弃并忽略路径中的任何html片段。 这是为了使重新路由的语义在常规请求和重新路由之间保持一致。

如果需要将更多信息传递给新请求， 则应使用在HTTP事务的整个生命周期中保留的上下文。

## 子路由器

有时，如果您有很多handler，则可以将它们拆分为多个Router。 如果要在不同路径根的不同应用程序中重用一组handler，这也很有用。

为此，您可以将Router挂载在另一个Router *挂载点* 上。安装的Router称为 *子路由器*。 子路由器可以挂载其他子路由器，因此您可以根据需要拥有多个级别的子路由器。

让我们看一个简单的子路由挂载在其他路由上面的例子

该子路由器将维护简单的虚构REST API对应的handler。我们将其挂载在另一个路由器上。 其未显示REST API的完整实现。

这是一个子路由器

```java
Router restAPI = Router.router(vertx);

restAPI.get("/products/:productID").handler(ctx -> {

  // TODO 处理产品查找
  ctx.response().write(productJSON);

});

restAPI.put("/products/:productID").handler(ctx -> {

  // TODO 添加一个新产品
  ctx.response().end();

});

restAPI.delete("/products/:productID").handler(ctx -> {

  // TODO 删除一个产品
  ctx.response().end();

});
```

如果将此路由器用作顶级路由器， 则对诸如 `/products/product1234` 之类的url的GET/PUT/DELETE请求将调用该API。

但是，假设我们已经有另一个路由器描述的网站：

```java
Router mainRouter = Router.router(vertx);

// 处理静态资源
mainRouter.route("/static/*").handler(myStaticHandler);

mainRouter.route(".*\\.templ").handler(myTemplateHandler);
```

现在，我们可以将子路由器挂载在主路由器上，挂载点在本例中为 `/productsAPI`。

```java
mainRouter.mountSubRouter("/productsAPI", restAPI);
```

这意味着现在可以通过以下路径访问REST API：`/productsAPI/products/product1234`。

在使用子路由器之前，必须满足一些规则

- 路由路径必须以通配符结尾。
- 允许使用参数，但不能完全的使用正则表达式模式。
- 在此调用之前或之后，只能注册1个处理程序（但它们可以在同一路径的新路由对象上注册）
- 每个路径对象仅1个路由器

验证是在将路由器添加到http服务器时进行的。这意味着由于子路由器的动态特性，在构建期间无法获得任何验证错误。 它们取决于要验证的上下文。

## 路由匹配失败

如果没有路由符合任何特定请求，则Vert.x-Web将根据匹配失败发出错误消息

- 404 没有匹配的路径
- 405 路径匹配但是请求方法不匹配
- 406 路径匹配，请求方法匹配但是它无法提供内容类型与 `Accept` 请求头匹配的响应
- 415 路径匹配，请求方法匹配但是它不能接受 `Content-type`
- 400 路径匹配，请求方法匹配但是它接收空方法体

您可以使用 `errorHandler` 手动管理这些错误。

## 错误处理

除了设置处理程序以处理请求之外，您还可以设置处理程序以处理路由过程中的错误

Failure 处理器与普通处理器（handler）有完全相同的路由匹配条件

例如，您可以提供一个错误处理器，该处理程序仅处理某些路径或某些HTTP方法上的错误。

这使您可以为应用程序的不同部分设置不同的错误处理器。

这是一个示例错误处理器，仅在将GET请求路由到以 `/somepath/` 开头的路径时发生的失败时 才会调用该错误处理器：

```java
Route route = router.get("/somepath/*");

route.failureHandler(ctx -> {

  // 以 '/somepath/'
  // 开头的路径时发生的错误时
  // 这个将会被调用

});
```

如果handler引发异常，或者如果handler调用 `fail` 并指定HTTP状态代码来故意发出失败信号， 则会触发错误处理路由。

如果从handler中捕获到异常，则将导致失败，并发出状态代码 `500`。

处理错误时，将向故障处理器传递路由上下文，该路由上下文还允许获取故障或故障代码， 以便错误处理器可以使用它来生成失败响应。

```java
Route route1 = router.get("/somepath/path1/");

route1.handler(ctx -> {

  // 让我们抛出一个RuntimeException
  throw new RuntimeException("something happened!");

});

Route route2 = router.get("/somepath/path2");

route2.handler(ctx -> {

  // 这是一个故意使请求传递状态码的错误
  // 比如 403-访问被拒绝
  ctx.fail(403);

});

// 定义一个错误处理器
// 它将会被上面handler里面发生的任何异常触发
Route route3 = router.get("/somepath/*");

route3.failureHandler(failureRoutingContext -> {

  int statusCode = failureRoutingContext.statusCode();

  // RuntimeException的状态码将为500
  // 或403，表示其他失败
  HttpServerResponse response = failureRoutingContext.response();
  response.setStatusCode(statusCode).end("Sorry! Not today");

});
```

如果在错误处理器执行时在状态消息头中出现非法字符，则发生错误， 那么原始状态消息将从错误代码更改为默认消息。 这是保持HTTP协议语义正常工作的一种折衷， 而不是在没有正确完成协议的情况下突然崩溃并关闭套接字。

## 请求体处理

`BodyHandler` 允许您获取请求体， 限制请求体大小和处理文件上传

您应该确保对于任何需要此功能的请求，请求体处理器都应在匹配的路由上

使用此处理器需要将其尽快安装在路由器中， 因为它需要安装处理程序以使用HTTP请求体，并且必须在执行任何异步调用之前完成此操作。

```java
router.route().handler(BodyHandler.create());
```

如果之前需要异步调用，则应暂停 `HttpServerRequest` 然后再恢复， 以便在请求体处理器准备好处理它们之前，不传递请求事件。

```java
router.route().handler(ctx -> {

  HttpServerRequest request = ctx.request();

  // 暂停请求
  request.pause();

  someAsyncCall(result -> {

    // 恢复请求
    request.resume();

    // 继续处理
    ctx.next();
  });
});

// 这个请求体处理器将会被所有的Route调用
router.route().handler(BodyHandler.create());
```

上传可能是DDoS攻击的来源，为了减少攻击面，建议 设置合适的 `setBodyLimit` (例如 10MB的上传限制 或者 100KB的json大小限制).

### 获取请求体

如果您知道请求体是个JSON，然后您可以使用 `getBodyAsJson` ， 如果您知道他是个字符串，您可以使用 `getBodyAsString`， 或者使用 `getBody` 获取buffer

### 限制请求体大小

为了限制请求体大小，创建请求体处理器然后使用 `setBodyLimit` 指定最大请求体大小 这对于防止过大请求体导致耗尽内存很有用

如果尝试发送大于最大大小的请求体， 则会发送HTTP状态代码413- `Request Entity Too Large`。

默认情况下没有最大请求体大小限制

### 合并表单属性

默认情况下，请求体处理器会将所有表单属性合并到请求参数中 如果您不想这样做，您可以通过 `setMergeFormAttributes` 关闭这个功能

### 处理文件上传

请求体处理器还用于处理多部分文件的上传。

如果请求体处理器处于与请求匹配的路由上， 则任何文件上传将自动流式传输到uploads目录，默认情况下为 `file-uploads`。

每一个文件都会自动生成一个文件名，而且文件上传将通过 `fileUploads` 在路由上下文中可用。

这是一个例子：

```java
router.route().handler(BodyHandler.create());

router.post("/some/path/uploads").handler(ctx -> {

  Set<FileUpload> uploads = ctx.fileUploads();
  // 使用uploads做一些事情

});
```

每个文件上传均由一个 `FileUpload` 实例描述， 该实例允许访问各种属性，例如名称，文件名和大小。

## 处理cookie

Vert.x-Web 有开箱即用的cookie支持

### 操作 cookies

您可以使用 `getCookie` 按名获取一个cookie 或者使用 `cookieMap` 获取整个set集合。

使用 `removeCookie` 移除一个cookie。

使用 `addCookie`，添加一个cookie。

当写入响应头后，这组Cookie会自动写回到响应中， 以便浏览器可以存储它们。

cookie被 `Cookie` 实例所描述。 它允许您获取名称、 值、域、路径和其他cookie属性

这是一个查询并添加cookie的例子：

```java
Cookie someCookie = ctx.getCookie("mycookie");
String cookieValue = someCookie.getValue();

// 使用cookie做一些事情

// 添加一个cookie——它将自动写回到响应中
ctx.addCookie(Cookie.cookie("othercookie", "somevalue"));
```

## 处理session

Vert.x-Web提供了开箱即用的session支持

session存活在在浏览器会话周期的HTTP请求之间， 它给予了您一个可以储存seession作用域信息的地方，比如购物车

Vert.x-Web 使用session cookie来识别session 这个session cookie是临时的，而且当其关闭的时候您的浏览器会将其删除

我们并不会将您session中的真实数据放到session cookie中——这个cookie只是简单的使用标记符在服务器上寻找真实的session 这个标记符是一个使用安全随机数生成的随机UUID 所以它应该实际上是不可被推测出来的

cookie在HTTP请求和响应中通过网络传递，因此确保在使用会话时使用HTTPS始终是明智的。 如果您尝试通过直接HTTP使用会话，则Vert.x会警告您。

为了启用您应用程序中session， 您必须在应用程序逻辑之前的匹配路由上具有一个 `SessionHandler`

这个session处理器会处理session cookie的生成和寻找对应session 所以您无需自己去做这些事情

在响应头发回给客户端之后，session中的数据会自动地保存在session储存器中 但是请注意，因为这个机制， 它并不保证这个数据在客户端收到响应之前完全保留 在这个场景中你可以强制刷新一下 除非刷新操作失败，否则这将禁用自动保存过程。 这样可以在完成响应之前控制状态，例如：

```java
ChainAuthHandler chain =
  ChainAuthHandler.any()
    .add(authNHandlerA)
    .add(ChainAuthHandler.all()
      .add(authNHandlerB)
      .add(authNHandlerC));

// 保护您的路由
router.route("/secure/resource").handler(chain);
// 您的应用
router.route("/secure/resource").handler(ctx -> {
  // do something...
});
```

Vert.x Web支持不使用cookie的session，称为"无cookie"session。 作为替代，Vert.x Web可以将session ID嵌入页面URL内。这样，所有页面链接都将包含session ID字符串。 当访问者单击其中的某些链接时，它将从页面URL读取session ID，因此我们不需要cookie支持即可进行功能性session。

启动无cookies session

```java
router.route()
  .handler(SessionHandler.create(store).setCookieless(true));
```

知道在这种情况下session ID会被应用传递给最终的用户这一点非常重要，通常来讲通过把他渲染到 HTML 页面或者脚本上 有一些非常重要的规则，session ID 会由 `/optional/path/prefix'('sessionId')'/path/suffix` 上的以下模式标识。

举个例子，给出一个路径 `http://localhost:2677/WebSite1/(S(3abhbgwjg33aqrt3uat2kh4d))/api/` , session ID在这种情况下，是 `3abhbgwjg33aqrt3uat2kh4d`

如果两个用户共享一个相同的session ID， 他们也将共享同样的session变量，而且网站会将其认为是同一个访问者 如果session被用于储存私密或者敏感的数据将是一个安全危机，或允许访问网站的受限区域 当cookie被使用时，session ID可以通过SSL和标记cookie为secure进行保护。 但是在无cookie session的情况下，session id是URL的一部分，而且这非常容易受到攻击

### session储存

创建一个session处理器，您需要一个session储存器实例。 这个session储存器是一个可以为您的应用储存实际session的对象

session存储器负责保存安全的伪随机数生成器，以保证安全的session ID。 此PRNG独立于储存器，这意味着从储存器A获得的会话ID不能获取储存器B的会话ID， 因为它们具有不同的种子和状态。

默认情况下，PRNG使用混合模式，生成种子的时候会阻塞，生成时并不阻塞 PRNG也将每5分钟重新设置64位新的熵。而且这也可以通过系统属性进行设置

- io.vertx.ext.auth.prng.algorithm 比如: SHA1PRNG
- io.vertx.ext.auth.prng.seed.interval 比如: 1000 (every second)
- io.vertx.ext.auth.prng.seed.bits 比如: 128

除非您注意到PRNG算法会影响应用程序的性能， 否则大多数用户都不需要配置这些值。

Vert.x-Web具有两个开箱即用的sesion存储实现，如果您愿意，也可以编写自己的会话存储

这些实现应遵循 `ServiceLoader` 约定， 所有从类路径下运行时可以用的储存都将被暴露出来 当有多个实现可用时，第一个可以实例化并成功配置的实现将成为默认设置。 如果没有可用的，则默认值取决于创建Vert.x的模式。 如果集群模式可用，则默认配置为为集群储存，否则为本地存储。

#### 本地session储存

通过这个储存器，session可以在内存中本地化储存，而且只在这个实例中可用

如果只有一个Vert.x实例正在应用程序中使用粘性session， 并且已将负载均衡器配置为始终将HTTP请求路由到同一Vert.x实例，则此存储是合适的。

如果您不能确保所有请求都将在同一服务器上终止，请不要使用此存储 因为服务器可能会在不知道对应session的情况下，终结您的请求

本地session储存器通过shared local map实现，而且会由回收器清理过期的session

回收间隔将可以用json信息进行设置，它所对应的key值为 `reaperInterval` .

下面是一个创建本地 `session储存器` 的例子

```java
SessionStore store1 = LocalSessionStore.create(vertx);

// 创建一个指定local shared map名的本地session储存
// 如果您有多个应用在同一个Vert.x 实例中而且您想使用为不同的应用不同的map，
// 这将非常有用
SessionStore store2 = LocalSessionStore.create(
  vertx,
  "myapp3.sessionmap");

// 创建一个本地session储存器，
// 其制定了local shared map名和设置了10s的清理周期用于清理过期session
SessionStore store3 = LocalSessionStore.create(
  vertx,
  "myapp3.sessionmap",
  10000);
```

### 创建session处理器

一旦您创建好session储存器，您就可以开始创建session处理器了，并且把他添加到Route中。 那您应该确保将会话处理程序路由到应用程序处理程序之前。

这里有个例子

```java
Router router = Router.router(vertx);

//使用默认配置创建一个集群session储存器
SessionStore store = ClusteredSessionStore.create(vertx);

SessionHandler sessionHandler = SessionHandler.create(store);

// session处理器控制用于session的cookie
// 举个例子，它可以包含同站策略（译者注：即samesite policy）的配置
// 比如这个，使用严格模式的同站策略
sessionHandler.setCookieSameSite(CookieSameSite.STRICT);

// 确保所有请求都可以路由经过这个session处理器
router.route().handler(sessionHandler);

// 现在您的应用程序可以开始处理了
router.route("/somepath/blah/").handler(ctx -> {

  Session session = ctx.session();
  session.put("foo", "bar");
  // 等等

});
```

session处理器会确保您的session会从session储存器中被自动地找出来(或者当session不存在时创建一个)， 然后在到达您的应用程序处理器之前将其放置在路由上下文中

### 使用session

在您的处理器中您可以通过 `session` 获取到session实例

您可以通过 `put`将数据放到session中 您可以通过 `get` 从session中获取数据 同时您也可以通过 `remove` 从session移除数据。

session中对象的键往往是字符串类型。 对于本地session储存器其值可以是任何类型，对于集群session储存器中它可以是任何基础类型或 `Buffer`, `JsonObject`, `JsonArray` 或者一个可序列化的对象，因为这些值必须在整个集群中序列化。

这是一个操作session中数据的例子

```java
router.route().handler(sessionHandler);

//现在是您的程序在处理
router.route("/somepath/blah").handler(ctx -> {

  Session session = ctx.session();

  // 放置一些数据到session中
  session.put("foo", "bar");

  // 从session获取数据
  int age = session.get("age");

  // 从session中移除数据
  JsonObject obj = session.remove("myobj");

});
```

在响应完成之后seession会被自动写回到储存器中

您通过 `destroy` 手动销毁session 它会将session从上下文和session储存器中移除 请注意，如果没有session，则将为通过session处理器的下一个来自浏览器的请求自动创建一个新会话。

### session超时

如果session的未访问时间超过超时时间，则session将自动超时。 当一个session超时时，它将会被从储存中移除

当请求到达，session被查找以及当响应完成且会话被存储回存储器中时， session将被自动标记为已访问。

您也可以使用 `setAccessed` 手动为session打上已访问标记

当创建session处理器时可以设置session的超时时间，其默认值为30分钟

## 处理静态资源

Vert.x-Web带有开箱即用的处理器，用于处理静态Web资源， 因此您可以非常轻松地编写静态Web服务器。

您需要一个 `StaticHandler` 实例处理静态资源，比如 `.html`, `.css`, `.js` 或者其他任意静态资源。

对静态处理器处理的路径的任何请求都由文件系统上的目录或类路径提供文件。 默认静态文件目录为 `webroot`，但可以配置。

在下面的示例中，所有对以 `/static/` 开头的路径的请求都将从目录 `webroot` 得到响应：

```java
router.route("/static/*").handler(StaticHandler.create());
```

当Vert.x首次在类路径中找到资源时，它将提取该资源并将其缓存在磁盘上的临时目录中 ，因此不必每次都这样做。

处理器会处理范围感知的请求。当客户端向静态资源发出请求时， 处理程序将通过在 `Accept-Ranges` 标头上声明该单元来通知其可以处理范围感知的请求。 包含带有正确单位以及起始索引和结束索引的 `Range` 标头的进一步请求将收到带有正确 `Content-Range` 标头的部分响应。

### 配置主页

对根路径 `/` 的任何请求都将导致主页得到处理。默认情况下主页是 `index.html` 它可以通过 `setIndexPage`.配置

### 更改web root

默认情况下，静态资源将从目录 `webroot` 提供。可以通过 `setWebRoot` 进行配置这个。

### 处理隐藏文件

默认情况下，服务器将提供隐藏文件（以 `.` 开头的文件）。

如果您不希望提供隐藏文件，则可以使用以下命令对其进行配置 `setIncludeHidden`。

## 跨域处理

[Cross Origin Resource Sharing](http://en.wikipedia.org/wiki/Cross-origin_resource_sharing)是一种安全的机制， 用于允许从一个域请求资源并从另一个域提供资源。

Vert.x-Web包含一个 `CorsHandler` ，用于帮您处理CORS协议

```java
router.route()
  .handler(
    CorsHandler.create("vertx\\.io")
      .allowedMethod(HttpMethod.GET));

router.route().handler(ctx -> {

  // 您的app处理器

});
```

## 模板

Vert.x Web 为若干流行的模板引擎提供了开箱即用的支持，通过这种方式来提供生成动态页面的能力。 您也可以很容易地添加您自己的实现。

`TemplateEngine` 定义了使用模板引擎的接口。 当渲染模板时会调用 `render` 方法。

最简单的使用模板的方式不是直接调用模板引擎，而是使用模板处理器 `TemplateHandler` 。 这个处理器会根据 HTTP 请求的路径来调用模板引擎。

缺省情况下，模板处理器会在 `templates` 目录中查找模板文件。这是可以配置的。

该处理器会返回渲染的结果，并默认设置 Content-Type 消息头为 `text/html` 。这也是可以配置的。

您需要在创建模板处理器时提供您想要使用的模板引擎实例。 Vert.x Web 并未嵌入模板引擎的实现，您需要配置项目来访问它们。 Vert.x Web 提供了每一种模板引擎的配置。

以下是例子：

```java
TemplateEngine engine = HandlebarsTemplateEngine.create();
TemplateHandler handler = TemplateHandler.create(engine);

// 这会将所有以 `/dynamic` 开头的 GET 请求路由到模板处理器上
// 例如 /dynamic/graph.hbs 会查找模板 /templates/graph.hbs
router.get("/dynamic/*").handler(handler);

// 将所有以 `.hbs` 结尾的 GET 请求路由到模板处理器上
router.getWithRegex(".+\\.hbs").handler(handler);
```

### Thymeleaf 模板引擎

您需要在项目中添加以下 *依赖* 以使用 Thymeleaf 模板引擎： `io.vertx:vertx-web-templ-thymeleaf:4.1.5`。 并通过此方法以创建 Thymeleaf 模板引擎实例： `io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine#create(io.vertx.core.Vertx)`。

```java
ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    SessionStore store = SessionStore.create(vertx);
    router.route().handler(SessionHandler.create(store));
    router.route("/*").handler(StaticHandler.create());
    router.route("/template").handler(ctx -> {
      JsonObject jsonObject = new JsonObject()
          .put("name", "tom")
          .put("age", 18)
          .put("url", "https://www.baidu.com");
      engine.render(jsonObject, "index.html", ar -> {
        if (ar.succeeded()) {
          ctx.session().put("abc", jsonObject);
          var result = ar.result();
          ctx.response().end(result);
        }
      });
    });
```

在使用 Thymeleaf 模板引擎时，如果不指定模板文件的扩展名， 则默认会查找扩展名为 `.html` 的文件。

在 Thymeleaf 模板中可以通过 `context` 上下文变量来访问路由上下文 `RoutingContext` 对象。 这意味着您可使用任何基于上下文里的信息来渲染模板， 包括请求、响应、会话或者上下文数据。

## 错误处理器

您可使用模版处理器自行渲染错误页面， 但是Vert.x-Web同样为您提供了开箱即用且“好看的”错误处理器，可为您渲染错误页面。

该处理器是 `ErrorHandler`。 要使用该错误处理器，仅需要将其设置为您希望覆盖的错误路径的失败处理器即可（译者注：例如router.route("/*").failureHandler(ErrorHandler.create(vertx))）。

## 请求日志

Vert.x-Web通过内置处理器 `LoggerHandler` 来记录请求日志。 您需在挂载任何可能导致 `RoutingContext` 失败的处理器之前挂载该处理器。

默认情况下，请求日志将会被记录到Vert.x logger中，亦可通过更改配置使用JUL logging, log4j 或 SLF4J记录。

## 提供网页图标

Vert.x-Web通过内置处理器 `FaviconHandler` 以提供网页图标。

图标可以指定为文件系统上的某个路径，否则 Vert.x Web 默认会在 classpath 上寻找名为 `favicon.ico` 的文件。 这意味着您可以将图标打包到包含您应用的 jar 包里。

## 超时处理器

Vert.x-Web内置一个超时处理器以处理超时请求。

可通过 `TimeoutHandler` 配置。

如果一个请求超时，则会给客户端返回一个 503 的响应。

下面的例子设置了一个超时处理器。对于所有以 `/foo` 路径开头的请求， 都会在执行时间超过 5 秒之后自动超时。

```java
router.route("/foo/").handler(TimeoutHandler.create(5000));
```

## 响应时间处理器

该处理器会将从接收到请求到写入响应的消息头之间的毫秒数写入到响应的 `x-response-time` 里， 例如：

x-response-time: 1456ms

## 内容类型（Content type）处理器

`ResponseContentTypeHandler` 会自动设置响应的 `Content-Type` 消息头。 假设我们要构建一个 RESTful 的 Web 应用，我们需要在所有处理器里设置消息类型：

```java
router
  .get("/api/books")
  .produces("application/json")
  .handler(ctx -> findBooks()
    .onSuccess(books -> ctx.response()
      .putHeader("Content-Type", "application/json")
      .end(toJson(books))).onFailure(ctx::fail));
```

随着 API 接口数量的增长，设置内容类型会变得很麻烦。 可以通过在相应的 Route 上添加 `ResponseContentTypeHandler` 来避免这个问题：

```java
router.route("/api/*").handler(ResponseContentTypeHandler.create());
router
  .get("/api/books")
  .produces("application/json")
  .handler(ctx -> findBooks()
    .onSuccess(books -> ctx.response()
      .end(toJson(books))).onFailure(ctx::fail));
```

处理器会通过 `getAcceptableContentType` 方法来选择适当的内容类型。 因此，您可以很容易地使用同一个处理器以提供不同类型的数据：

```java
router.route("/api/*").handler(ResponseContentTypeHandler.create());

router
  .get("/api/books")
  .produces("text/xml")
  .produces("application/json")
  .handler(ctx -> findBooks()
    .onSuccess(books -> {
      if (ctx.getAcceptableContentType().equals("text/xml")) {
        ctx.response().end(toXML(books));
      } else {
        ctx.response().end(toJson(books));
      }
    })
    .onFailure(ctx::fail));
```

## 