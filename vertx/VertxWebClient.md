# Vert.x Web Client

Vert.x Web Client 是一个异步的 HTTP 和 HTTP/2 客户端。

Web Client使得发送 HTTP 请求以及从 Web 服务器接收 HTTP 响应变得更加便捷，同时提供了额外的高级功能，例如：

- Json body的编码和解码
- 请求和响应泵
- 请求参数的处理
- 统一的错误处理
- 提交表单

## 使用Web Client

如需使用Vert.x Web Client，请先加入以下依赖，到您的build描述 *dependencies* 部分 ：

- Maven：

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-web-client</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle ：

```groovy
dependencies {
 compile 'io.vertx:vertx-web-client:4.1.5'
}
```

## 创建Web Client

可以创建一个缺省 `WebClient` 实例：

```java
WebClient client = WebClient.create(vertx);
```

还可以使用配置项来创建客户端：

```java
WebClientOptions options = new WebClientOptions()
  .setUserAgent("My-App/1.2.3");
options.setKeepAlive(false);
WebClient client = WebClient.create(vertx, options);
```

Web Client配置项继承自 `HttpClient` 配置项，您可以设置其中任何一个项。

如果已在程序中创建 `HttpClient`，可用以下方式复用：

```java
WebClient client = WebClient.wrap(httpClient);
```

## 发送请求

### 无请求体的简单请求

通常，您想发送一个无请求体的HTTP请求。以下是一般情况下的 HTTP GET， OPTIONS和HEAD 请求

```java
WebClient client = WebClient.create(vertx);

// 发送GET请求
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .send()
  .onSuccess(response -> 
    System.out.println("Received response with status code" + response.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));

// 发送HEAD请求
client
  .head(8080, "myserver.mycompany.com", "/some-uri")
  .send()
  .onSuccess(response -> 
    System.out.println("Received response with status code" + response.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

您可用以下链式方式向请求URI添加查询参数

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .addQueryParam("param", "param_value")
  .send()
  .onSuccess(response -> System.out
    .println("Received response with status code" + response.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

在请求URI中的参数将会被预填充

```java
HttpRequest<Buffer> request = client
  .get(
    8080,
    "myserver.mycompany.com",
    "/some-uri?param1=param1_value&param2=param2_value");

// 添加 param3
request.addQueryParam("param3", "param3_value");

// 覆盖 param2
request.setQueryParam("param2", "another_param2_value");
```

设置请求URI将会自动清除已有的查询参数

```java
HttpRequest<Buffer> request = client
  .get(8080, "myserver.mycompany.com", "/some-uri");

// 添加 param1
request.addQueryParam("param1", "param1_value");

// 覆盖 param1 并添加 param2
request.uri("/some-uri?param1=param1_value&param2=param2_value");
```

### 填充请求体

如需要发送请求体，可使用相同的API，并在最后加上 `sendXXX` 方法，发送相应的请求体。

使用 `sendBuffer` 发送一个buffer body

```java
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendBuffer(buffer)
  .onSuccess(res -> {
    // OK
  });
```

发送single buffer很有用，但是通常您不想完全将内容加载到内存中，因为它可能太大，或者您想同时处理多个请求，或者每个请求只想使用最小的（消耗）。 为此，Web Client可以使用 `ReadStream<Buffer>` 的（例如 `AsyncFile` 是一个ReadStream<Buffer>） `sendStream` 方法发送。

```java
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendStream(stream)
  .onSuccess(res -> {
    // OK
  });
```

Web Client负责为您设置泵传输（transfer pump）。如果流长度未知则使用分块传输（chunked transfer）编码。

当您知道流的大小，您应该在HTTP header中设置 `content-length`

```java
fs.open("content.txt", new OpenOptions(), fileRes -> {
  if (fileRes.succeeded()) {
    ReadStream<Buffer> fileStream = fileRes.result();

    String fileLen = "1024";

    // 用POST方法发送文件
    client
      .post(8080, "myserver.mycompany.com", "/some-uri")
      .putHeader("content-length", fileLen)
      .sendStream(fileStream)
      .onSuccess(res -> {
        // OK
      })
    ;
  }
});
```

这个POST方法不会被分块传输。

#### JSON bodies

有时您需要发送JSON body请求，可使用 `sendJsonObject` 发送一个 `JsonObject`

```java
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendJsonObject(
    new JsonObject()
      .put("firstName", "Dale")
      .put("lastName", "Cooper"))
  .onSuccess(res -> {
    // OK
  });
```

在Java，Groovy以及Kotlin中，您可以使用 `sendJson` 方法，它使用 `Json.encode` 方法映射一个 POJO（Plain Old Java Object） 到一个 Json 对象

```java
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendJson(new User("Dale", "Cooper"))
  .onSuccess(res -> {
    // OK
  });
```

#### 表单提交

您可以使用 `sendForm` 的变体发送http表单提交。

```java
MultiMap form = MultiMap.caseInsensitiveMultiMap();
form.set("firstName", "Dale");
form.set("lastName", "Cooper");

// 用URL编码方式提交表单
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendForm(form)
  .onSuccess(res -> {
    // OK
  });
```

默认情况下，提交表单header中的 `content-type` 属性值为 `application/x-www-form-urlencoded`，您还可将其替换为 `multipart/form-data`：

```java
MultiMap form = MultiMap.caseInsensitiveMultiMap();
form.set("firstName", "Dale");
form.set("lastName", "Cooper");

// 提交multipart form表单
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .putHeader("content-type", "multipart/form-data")
  .sendForm(form)
  .onSuccess(res -> {
    // OK
  });
```

如果你想上传文件的同时发送属性，您可以创建一个 `MultipartForm` ，然后使用 `sendMultipartForm` 。

```java
MultipartForm form = MultipartForm.create()
  .attribute("imageDescription", "a very nice image")
  .binaryFileUpload(
    "imageFile",
    "image.jpg",
    "/path/to/image",
    "image/jpeg");

// 提交multipart form表单
client
  .post(8080, "myserver.mycompany.com", "/some-uri")
  .sendMultipartForm(form)
  .onSuccess(res -> {
    // OK
  });
```

### 填充请求头

您可使用headers的multi-map 填充请求头：

```java
HttpRequest<Buffer> request = client
  .get(8080, "myserver.mycompany.com", "/some-uri");

MultiMap headers = request.headers();
headers.set("content-type", "application/json");
headers.set("other-header", "foo");
```

此处 Headers 是一个 `MultiMap` 实例，提供了添加、设置以及删除头属性操作的入口。HTTP headers允许某个特定的key有多个值。

您还可使用 putHeader 写入headers属性：

```java
HttpRequest<Buffer> request = client
  .get(8080, "myserver.mycompany.com", "/some-uri");

request.putHeader("content-type", "application/json");
request.putHeader("other-header", "foo");
```

### 重用请求

`send` 方法可被安全的重复多次调用，这使得它可以很容易的配置以及重用 `HttpRequest` 对象

```java
HttpRequest<Buffer> get = client
  .get(8080, "myserver.mycompany.com", "/some-uri");

get
  .send()
  .onSuccess(res -> {
    // OK
  });

// 又一些请求
get
  .send()
  .onSuccess(res -> {
    // OK
  });
```

不过要当心 `HttpRequest` 实例是可变的（mutable）. 因此，您应该在修改已被缓存了的实例之前，使用 `copy` 方法。

```java
HttpRequest<Buffer> get = client
  .get(8080, "myserver.mycompany.com", "/some-uri");

get
  .send()
  .onSuccess(res -> {
    // OK
  });

// "get" 请求实例保持未修改
get
  .copy()
  .putHeader("a-header", "with-some-value")
  .send()
  .onSuccess(res -> {
    // OK
  });
```

### 超时

您可通过 `timeout` 。方法设置超时时间。

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .timeout(5000)
  .send()
  .onSuccess(res -> {
    // OK
  })
  .onFailure(err -> {
    // 当是由java.util.concurrent.TimeoutException导致时，或许是一个超时
  });
```

若请求在设定时间内没有返回任何数据，则一个异常将会传递给响应处理器。

## 处理HTTP响应

Web Client请求发送之后，您总是在单个 `HttpResponse` 中处理单个异步结果 。

当响应被成功接收到之后，相应的回调函数将会被调用。

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

### 响应解码

缺省状况下，Web Client提供一个response body作为 `Buffer` ，并且未运用任何解码器。

可以使用 `BodyCodec` 实现以下自定义response body解码：

- 文本字符串
- Json 对象
- Json 映射的 POJO
- `WriteStream`

一个body解码器可以将任意二进制数据流解码为特定的对象实例，从而节省了您自己在响应处理器里解码的步骤。

使用 `BodyCodec.jsonObject` 解码一个 Json 对象：

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .as(BodyCodec.jsonObject())
  .send()
  .onSuccess(res -> {
    JsonObject body = res.body();

    System.out.println(
      "Received response with status code" +
        res.statusCode() +
        " with body " +
        body);
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

在Java，Groovy以及Kotlin中，可以自定义Json映射POJO解码：

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .as(BodyCodec.json(User.class))
  .send()
  .onSuccess(res -> {
    User user = res.body();

    System.out.println(
      "Received response with status code" +
        res.statusCode() +
        " with body " +
        user.getFirstName() +
        " " +
        user.getLastName());
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

这个编码器将响应缓存泵入到 `WriteStream` 中，并且在异步结果响应中，发出操作成功或失败的信号。

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .as(BodyCodec.pipe(writeStream))
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

经常会看到API返回一个JSON对象流。例如，Twitter API可以提供一个推文回馈。处理这个情况，您可以使用 `BodyCodec.jsonStream`。传递一个JSON解析器，该解析器从HTTP响应中开始读取JSON流。

```java
JsonParser parser = JsonParser.newParser().objectValueMode();
parser.handler(event -> {
  JsonObject object = event.objectValue();
  System.out.println("Got " + object.encode());
});
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .as(BodyCodec.jsonStream(parser))
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

最后，如您对响应结果不感兴趣，可用 `BodyCodec.none` 简单的丢弃response body。

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .as(BodyCodec.none())
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

若无法预知响应内容类型，您依旧可以在获取结果之后，用 `bodyAsXXX()` 方法将其转换成指定的类型

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .send()
  .onSuccess(res -> {
    // 将结果解码为Json对象
    JsonObject body = res.bodyAsJsonObject();

    System.out.println(
      "Received response with status code" +
        res.statusCode() +
        " with body " +
        body);
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

### 响应谓词

默认的，仅当在网络级别发生错误时，Vert.x Web Client请求才以错误结尾。

换言之， 您必须在收到响应后手动执行健全性检查：

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .send()
  .onSuccess(res -> {
    if (
      res.statusCode() == 200 &&
        res.getHeader("content-type").equals("application/json")) {
      // 将结果解码为Json对象
      JsonObject body = res.bodyAsJsonObject();

      System.out.println(
        "Received response with status code" +
          res.statusCode() +
          " with body " +
          body);
    } else {
      System.out.println("Something went wrong " + res.statusCode());
    }
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

您可以灵活的替换成清晰简明的 *response predicates* 。

`Response predicates` 当响应不符合条件会使请求失败。

Web Client附带了一组开箱即用的谓词，可供使用：

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .expect(ResponsePredicate.SC_SUCCESS)
  .expect(ResponsePredicate.JSON)
  .send()
  .onSuccess(res -> {
    // 安全地将body解码为json对象
    JsonObject body = res.bodyAsJsonObject();
    System.out.println(
      "Received response with status code" +
        res.statusCode() +
        " with body " +
        body);
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

当现有谓词不满足您的需求时，您还可以创建自定义谓词：

```java
Function<HttpResponse<Void>, ResponsePredicateResult> methodsPredicate =
  resp -> {
    String methods = resp.getHeader("Access-Control-Allow-Methods");
    if (methods != null) {
      if (methods.contains("POST")) {
        return ResponsePredicateResult.success();
      }
    }
    return ResponsePredicateResult.failure("Does not work");
  };

// 发送预检CORS请求
client
  .request(
    HttpMethod.OPTIONS,
    8080,
    "myserver.mycompany.com",
    "/some-uri")
  .putHeader("Origin", "Server-b.com")
  .putHeader("Access-Control-Request-Method", "POST")
  .expect(methodsPredicate)
  .send()
  .onSuccess(res -> {
    // 立即处理POST请求
  })
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

#### 预定义谓词

为了方便起见，Web Client附带了一些常见用例的谓词。

对于状态码，例如 `ResponsePredicate.SC_SUCCESS` ，验证响应具有 `2xx` 代码，您也可以自定义创建一个

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .expect(ResponsePredicate.status(200, 202))
  .send()
  .onSuccess(res -> {
    // ....
  });
```

对于content types，例如 `ResponsePredicate.JSON` ，验证响应具有JSON数据，您也可以自定义创建一个

```java
client
  .get(8080, "myserver.mycompany.com", "/some-uri")
  .expect(ResponsePredicate.contentType("some/content-type"))
  .send()
  .onSuccess(res -> {
    // ....
  });
```

#### 创建自定义失败

默认情况下，响应谓词（包括预定义的）使用默认的错误转换器，它将丢弃body并传递一条简单消息。您可以通过自定义异常类来替换错误转换器：

```java
ResponsePredicate predicate = ResponsePredicate.create(
  ResponsePredicate.SC_SUCCESS,
  result -> new MyCustomException(result.message()));
```

为避免丢失此信息，在错误发生之前，可以在转换器被调用之前等待响应body被完全接收：

```java
ErrorConverter converter = ErrorConverter.createFullBody(result -> {

  // 响应body被完全接收之后调用
  HttpResponse<Buffer> response = result.response();

  if (response
    .getHeader("content-type")
    .equals("application/json")) {

    // 错误body是JSON数据
    JsonObject body = response.bodyAsJsonObject();

    return new MyCustomException(
      body.getString("code"),
      body.getString("message"));
  }

  // 返回自定义的消息
  return new MyCustomException(result.message());
});

ResponsePredicate predicate = ResponsePredicate
  .create(ResponsePredicate.SC_SUCCESS, converter);
```

### 处理 30x 重定向

默认情况下，客户端跟随着重定向，您可以在 `WebClientOptions` 配置默认行为：

```java
WebClient client = WebClient
  .create(vertx, new WebClientOptions().setFollowRedirects(false));
```

客户端最多可以跟随 `16` 个请求重定向，可以在相同的配置中进行更改：

```java
WebClient client = WebClient
  .create(vertx, new WebClientOptions().setMaxRedirects(5));
```

## 使用 HTTPS

Vert.x Web Client 可以用跟 Vert.x `HttpClient` 完全一样的方式配置使用HTTPS。

您可以指定每个请求的行为

```java
client
  .get(443, "myserver.mycompany.com", "/some-uri")
  .ssl(true)
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

或使用带有绝对URI参数的创建方法

```java
client
  .getAbs("https://myserver.mycompany.com:4043/some-uri")
  .send()
  .onSuccess(res ->
    System.out.println("Received response with status code" + res.statusCode()))
  .onFailure(err ->
    System.out.println("Something went wrong " + err.getMessage()));
```

## 会话管理

Vert.x Web提供了Web会话管理设施；使用它，您需要对于每个用户（会话）创建一个 `WebClientSession` ，并使用它来代替 `WebClient` 。

### 创建一个 WebSession

您像下面一样创建一个 `WebClientSession` 实例

```java
WebClient client = WebClient.create(vertx);
WebClientSession session = WebClientSession.create(client);
```

### 发出请求

一旦创建， `WebClientSession` 可以代替 `WebClient` 去做HTTP(s) 请求并且自动管理你正在调用的，从服务器收到的所有cookie。

### 设置会话级别headers

您可以按以下步骤设置任何会话级别的headers到要添加的每个请求：

```java
WebClientSession session = WebClientSession.create(client);
session.addHeader("my-jwt-token", jwtToken);
```

然后headers将被添加到每个请求中； 注意 这些headers将发送给所有主机；如果你需要发送不同的headers到不同的主机， 您必须将它们手动添加到每个单个请求中，并且不添加到 `WebClientSession` 。