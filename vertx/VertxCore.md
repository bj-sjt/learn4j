# Vertx  Core

Vert.x Core 提供了下列功能：

- 编写 TCP 客户端和服务端
- 编写支持 WebSocket 的 HTTP 客户端和服务端
- 事件总线
- 共享数据 —— 本地的Map和分布式集群Map
- 周期性、延迟性动作
- 部署和撤销 Verticle 实例
- 数据报套接字
- DNS客户端
- 文件系统访问
- 高可用性
- 本地传输
- 集群

Vert.x Core 中的功能相当底层，不包含诸如数据库访问、授权或高层 Web 应用的功能。 您可以在 **Vert.x ext** （扩展包）中找到这些功能。

**Vert.x Core** 小巧而轻便，您可以只使用您需要的部分， 它可整体嵌入现存应用中。 Vert.x没有强制要求使用特定的方式构造应用。

Vert.x也支持在其他语言中使用Vert.x Core， 而且在使用诸如 JavaScript 或 Ruby 等语言编写Vert.x代码时，无需直接调用 Java的API；毕竟不同的语言有不同的代码风格， 若强行让 Ruby 开发人员遵循 Java 的代码风格会很怪异， 所以我们根据 Java API 自动生成了适应不同语言代码风格的 API。

从现在开始，我们将仅使用 **core** 以指代 Vert.x core 。

如果您在使用 Maven 或 Gradle， 将以下依赖项添加到项目描述文件的 *dependencies* 节点即可使用 **Vert.x Core** 的API：

- Maven

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-core</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle

```txt
dependencies {
 compile 'io.vertx:vertx-core:4.1.5'
}
```

## Vertx 对象

它是 Vert.x 的控制中心，也是您做几乎一切事情的基础，包括创建客户端和服务器、 获取事件总线的引用、设置定时器等等。

### 创建实例

```java
Vertx vertx = Vertx.vertx();
```

大部分应用将只会需要一个Vert.x实例，但如果您有需要也可创建多个Vert.x实例， 如：隔离的事件总线或不同组的客户端和服务器。

### 创建 Vertx 对象时指定配置项

```java
Vertx vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(40));
```

`VertxOptions` 对象有很多配置，包括集群、高可用、池大小等。

## Future的异步结果

Vert.x 4使用future承载异步结果。

异步的方法会返回一个 `Future` 对象，其包含 *成功* 或 *失败* 的异步结果。

我们不能直接操作future的异步结果，而应该设置future的handler； 当future执行完毕，结果可用时，会调用handler进行处理。

```java
FileSystem fs = vertx.fileSystem(); //获取vertx的文件系统

Future<FileProps> future = fs.props("/my_file.txt");  //获得文件的相关属性（异步）

future.onComplete((AsyncResult<FileProps> ar) -> {     //当获取到文件的属性时，会调用handler
  if (ar.succeeded()) {
    FileProps props = ar.result();
    System.out.println("File size = " + props.size());
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }
});
```

### Future组合

`compose` 方法作用于顺序组合 future：

- 若当前future成功，执行 `compose` 方法指定的方法，该方法返回新的future；当返回的新future完成时，future组合成功；
- 若当前future失败，则future组合失败。

```java
FileSystem fs = vertx.fileSystem();

Future<Void> future = fs
  .createFile("/foo")
  .compose(v -> {
    // createFile文件创建完成后执行
    return fs.writeFile("/foo", Buffer.buffer("Hello Vertx"));
  })
  .compose(v -> {
    // writeFile文件写入完成后执行
    return fs.move("/foo", "/bar");
  });
```

## Verticles

Vert.x 通过开箱即用的方式提供了一个简单便捷的、可扩展的、类似 [Actor Model](https://en.wikipedia.org/wiki/Actor_model) 的部署和并发模型机制。 您可以用此模型机制来保管您自己的代码组件。

**这个模型是可选的，Vert.x 并不强制使用这种方式创建应用程序。**

这个模型并不是严格的 Actor 模式实现，但它确实有相似之处， 特别是在并发、扩展性和部署等方面。

使用该模型，需要将应用代码编写成多个 **Verticle**。

Verticle 是由 Vert.x 部署和运行的代码块。默认情况一个 Vert.x 实例维护了N个 Event Loop 线程（默认情况下N = CPU核数 x 2）。Verticle 实例可使用任意 Vert.x 支持的编程语言编写， 而且一个简单的应用程序也可以包含多种语言编写的 Verticle。

您可以将 Verticle 想成 [Actor Model](http://en.wikipedia.org/wiki/Actor_model) 中的 Actor。

一个应用程序通常是由在同一个 Vert.x 实例中同时运行的许多 Verticle 实例组合而成。 不同的 Verticle 实例通过向 [Event Bus](https://vertx-china.github.io/docs/vertx-core/java/#event_bus) 上发送消息来相互通信。

### 编写 Verticle

Verticle 的实现类必须实现 `Verticle` 接口。

如果您喜欢的话，可以直接实现该接口，但是通常直接从抽象类 `AbstractVerticle` 继承更简单。

```java
public class MyVerticle extends AbstractVerticle {

 // Verticle部署时调用
 public void start() {
 }

 // 可选 - Verticle撤销时调用
 public void stop() {
 }

}
```

### Verticle 异步启动和停止

```java
public class MyVerticle extends AbstractVerticle {

 private HttpServer server;

 public void start(Promise<Void> startPromise) {
   server = vertx.createHttpServer().requestHandler(req -> {
     req.response()
       .putHeader("content-type", "text/plain")
       .end("Hello from Vert.x!");
     });

   // Now bind the server:
   server.listen(8080, res -> {
     if (res.succeeded()) {
       startPromise.complete();
     } else {
       startPromise.fail(res.cause());
     }
   });
 }
}
```

### Verticle 种类

这儿有两种 Verticle：

- Standard Verticles

  这是最常用的一类 Verticle —— 它们永远运行在 Event Loop 线程上。 更多讨论详见稍后的章节。

- Worker Verticles

  这类 Verticle 会运行在 Worker Pool 中的线程上。 一个实例绝对不会被多个线程同时执行。

### Standard verticles

当 Standard Verticle 被创建时，它会被分派给一个 Event Loop 线程，并在这个 Event Loop 中执行它的 `start` 方法。 当您在一个 Event Loop 上调用了 Core API 中的方法并传入了处理器时，Vert.x 将保证用与调用该方法时相同的 Event Loop 来执行这些处理器。

### Worker verticles

Worker Verticle 和 Standard Verticle 很像，但它并不是由一个 Event Loop 来执行， 而是由 Vert.x 中的 Worker Pool 中的线程执行。

Worker Verticle 设计用于调用阻塞式代码，它不会阻塞任何 Event Loop。

### 编程方式部署Verticle

部署Verticle可以使用任意一个 `deployVerticle` 方法， 并传入一个 Verticle名称或Verticle 实例。

```java
Verticle myVerticle = new MyVerticle();
vertx.deployVerticle(myVerticle);
//或
//vertx.deployVerticle("com.itao.MyVerticle");
```

### 等待部署完成

Verticle是异步部署的，换而言之，可能在 `deploy` 方法调用返回后一段时间才会完成部署。

如果您想要在部署完成时收到通知，则可以指定一个完成处理器：

```java
vertx.deployVerticle("com.itao.MyVerticle", res -> {
  if (res.succeeded()) {
    System.out.println("Deployment id is: " + res.result());
  } else {
    System.out.println("Deployment failed!");
  }
});
```

如果部署成功，这个完成处理器的结果中将会包含部署ID的字符串。

这个部署ID可以用于撤销部署。

### 撤销Verticle

我们可以通过 `undeploy` 方法来撤销部署好的 Verticle。

撤销操作也是异步的，因此若您想要在撤销完成后收到通知，则可以指定另一个完成处理器：

```java
vertx.undeploy(deploymentID, res -> {
  if (res.succeeded()) {
    System.out.println("Undeployed ok");
  } else {
    System.out.println("Undeploy failed!");
  }
});
```

### Context 对象

 Vert.x 传递一个事件给处理器或者调用 `Verticle` 的 start 或 stop 方法时， 它会关联一个 `Context` 对象来执行。通常来说这个context会是一个 **event-loop context**，它绑定到了一个特定的 Event Loop 线程上。所以在该context上执行的操作总是 在同一个 Event Loop 线程中。对于运行内联的阻塞代码的 Worker Verticle 来说，会关联一个 Worker Context，并且所有的操作运都会运行在 Worker 线程池的线程上。

```java
Context context = vertx.getOrCreateContext();
```

### 执行周期性/延迟性操作

在 Standard Verticle 中您不能直接让线程休眠以引入延迟，因为它会阻塞 Event Loop 线程。

取而代之是使用 Vert.x 定时器。定时器可以是 **一次性** 或 **周期性** 的。

#### 一次性计时器

```java
long timerID = vertx.setTimer(1000, id -> {
  System.out.println("And one second later this is printed");
});

System.out.println("First this is printed");
```

#### 周期性计时器

```java
long timerID = vertx.setPeriodic(1000, id -> {
  System.out.println("And every second this is printed");
});

System.out.println("First this is printed");
```

#### 取消计时器

```java
vertx.cancelTimer(timerID);
```

## Event Bus

每一个 Vert.x 实例都有一个单独的 Event Bus 实例。您可以通过 `Vertx` 实例的 `eventBus` 方法来获得对应的 `EventBus` 实例。

应用中的不同组成部分可以通过 Event Bus 相互通信，您无需关心它们由哪一种语言实现， 也无需关心它们是否在同一个 Vert.x 实例中。

您甚至可以通过桥接的方式让浏览器中运行的多个JavaScript客户端在同一个 Event Bus 上相互通信。

Event Bus构建了一个跨越多个服务器节点和多个浏览器的分布式点对点消息系统。

Event Bus支持发布/订阅、点对点、请求-响应的消息传递方式。

Event Bus的API很简单。基本上只涉及注册处理器、 注销处理器以及发送和发布(publish)消息。

### 基本概念

#### 寻址

消息的发送目标被称作 **地址(address)** 。

Vert.x中的地址就是一个简单的字符串，任何字符串都合法。 不过还是建议使用某种规范来进行地址的命名。 *例如* 使用点号(`.`)来划分命名空间。

一些合法的地址形如：europe.news.feed1、acme.games.pacman、sausages 以及 X 。

#### 处理器

消息需由处理器（`Handler`）来接收。您需要将处理器注册在某个地址上。

同一个地址可以注册许多不同的处理器。

一个处理器也可以注册在多个不同的地址上。

#### 发布/订阅消息

Event Bus支持 **发布(publish)消息** 功能。

消息将被发布到一个地址上。 发布意味着信息会被传递给所有注册在该地址上的处理器。

即我们熟悉的 **发布/订阅** 消息传递模式。

#### 点对点消息传递 与 请求-响应消息传递

Event Bus也支持 **点对点** 消息模式。

消息将被发送到一个地址上，Vert.x仅会把消息发给注册在该地址上的处理器中的其中一个。

若这个地址上注册有不止一个处理器，那么Vert.x将使用 **不严格的轮询算法** 选择其中一个。

点对点消息传递模式下，可在消息发送的时候指定一个应答处理器（可选）。

当接收者收到消息并且处理完成时，它可以选择性地回复该消息。 若回复，则关联的应答处理器将会被调用。

当发送者收到应答消息时，发送者还可以继续回复这个“应答”，这个过程可以 *不断* 重复。 通过这种方式可以在两个不同的 Verticle 之间建立一个对话窗口。

这也是一个常见的消息传递模式：**请求-响应** 模式。

#### 尽力传输

Vert.x会尽它最大努力去传递消息，并且不会主动丢弃消息。这种方式称为 **尽力传输(Best-effort delivery)**。

但是，当 Event Bus 发生故障时，消息可能会丢失。

若您的应用关心消息丢失，那么您应当编写具有幂等性的处理器， 并且您的发送者应当在故障恢复后重试。

#### 消息类型

Vert.x 默认允许任何基本/简单类型、`String` 类型、 `buffers` 类型的值 作为消息发送。

不过在 Vert.x 中更规范且更通用的做法是使用 [JSON](http://json.org/) 格式来发送消息。

对于 Vert.x 支持的所有语言来说，JSON都是非常容易创建、读取和解析的，因此JSON已经成为了Vert.x中的 *通用语(lingua franca)* 。

但是若您不想用 JSON，我们也不强制您使用它。

Event Bus 非常灵活， 您可以通过自定义 `codec` 来实现任何类型对象在 Event Bus 上的传输。

### Event Bus API

#### 获取Event Bus

```java
EventBus eb = vertx.eventBus();
```

#### 注册处理器

```java
EventBus eb = vertx.eventBus();

eb.consumer("news.uk.sport", message -> {
  System.out.println("I have received a message: " + message.body());
});
```

#### 注销处理器

```java
consumer.unregister(res -> {
  if (res.succeeded()) {
    System.out.println("The handler un-registration has reached all nodes");
  } else {
    System.out.println("Un-registration failed!");
  }
});
```

#### 发布消息

```java
eventBus.publish("news.uk.sport", "Yay! Someone kicked a ball"); // 所有注册了 news.uk.sport 地址的消费者都会收到
```

#### 发送消息

```java
eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");  // 注册了 news.uk.sport 地址的消费者中只有一个会收到
```

#### 设置消息头

在 Event Bus 上发送的消息可包含头信息。您可以在发送或发布(publish)时提供一个 `DeliveryOptions` 来指定头信息

```java
DeliveryOptions options = new DeliveryOptions();
options.addHeader("some-header", "some-value");
eventBus.send("news.uk.sport", "Yay! Someone kicked a ball", options);
```

#### 消息顺序

Vert.x会按照发送者发送消息的顺序，将消息以同样的顺序传递给处理器。

#### 消息对象

您在消息处理器中接收到的对象的类型是 `Message`。

消息的 `body` 对应发送或发布(publish)的对象。

消息的头信息可以通过 `headers` 方法获取。

#### 应答消息/发送回复

当使用 `send` 方法发送消息时， Event Bus会尝试将消息传递到注册在Event Bus上的 `MessageConsumer` 中。

某些情况下，发送者可以通过 **请求/响应+** 模式来得知消费者已经收到并"处理"了该消息。

消费者可以通过调用 `reply` 方法来应答这个消息，确认该消息已被处理。

此时，它会将一个应答消息返回给发送者并调用发送者的应答处理器。

接收者：

```java
MessageConsumer<String> consumer = eventBus.consumer("news.uk.sport");
consumer.handler(message -> {
  System.out.println("I have received a message: " + message.body());
  message.reply("how interesting!");
});
```

发送者：

```java
eventBus.request("news.uk.sport", "Yay! Someone kicked a ball across a patch of grass", ar -> {
  if (ar.succeeded()) {
    System.out.println("Received reply: " + ar.result().body());
  }
});
```

#### 带超时的发送

当发送带有应答处理器的消息时，可以在 `DeliveryOptions` 中指定一个超时时间。

如果在这个时间之内没有收到应答，则会以“失败的结果”为参数调用应答处理器。

默认超时是 **30 秒**。

#### 发送失败

消息发送可能会因为其他原因失败，包括：

- 没有可用的处理器来接收消息
- 接收者调用了 `fail` 方法显式声明失败

发生这些情况时，应答处理器将会以这些异常失败结果为参数进行调用。

#### 消息编解码器

您可以在 Event Bus 中发送任何对象，只需为这个对象类型注册一个编解码器 `message codec` 即可。

每个消息编解码器都有一个名称，您需要在发送或发布消息时通过 `DeliveryOptions` 来指定：

```java
eventBus.registerCodec(myCodec);

DeliveryOptions options = new DeliveryOptions().setCodecName(myCodec.name());

eventBus.send("orders", new MyPOJO(), options);
```

若您希望某个类总是使用特定的编解码器，那么您可以为这个类注册默认编解码器。 这样您就不需要在每次发送的时候指定了：

```java
eventBus.registerDefaultCodec(MyPOJO.class, myCodec);

eventBus.send("orders", new MyPOJO());
```

您可以通过 `unregisterCodec` 方法注销某个消息编解码器。

消息编解码器的编码输入和解码输出不一定使用同一个类型。 例如您可以编写一个编解码器来发送 MyPOJO 类的对象，但是当消息发送给处理器后解码成 MyOtherPOJO 对象。

## JSON

### JSON objects

`JsonObject` 类用来描述JSON对象。

一个JSON 对象基本上只是一个 Map 结构。它具有字符串的键，值可以是任意一种JSON 支持的类型 （如 string, number, boolean）。

JSON 对象也支持 null 值。

#### 创建 JSON 对象

可以使用默认构造函数创建空的JSON对象。

您可以通过一个 JSON 格式的字符串创建JSON对象：

```java
String jsonString = "{\"foo\":\"bar\"}";
JsonObject object = new JsonObject(jsonString);
```

您可以根据Map创建JSON对象：

```java
Map<String, Object> map = new HashMap<>();
map.put("foo", "bar");
map.put("xyz", 3);
JsonObject object = new JsonObject(map);
```

#### 将键值对放入 JSON 对象

使用 `put` 方法可以将值放入到JSON对象里。

这个API是流式的，因此这个方法可以被链式地调用。

```java
JsonObject object = new JsonObject();
object.put("foo", "bar").put("num", 123).put("mybool", true);
```

#### 从 JSON 对象获取值

您可使用 `getXXX` 方法从JSON对象中获取值。例如：

```java
String val = jsonObject.getString("some-key");
int intVal = jsonObject.getInteger("some-other-key");
```

#### JSON 对象和 Java 对象间的映射

您可以根据 Java 对象的字段创建一个JSON 对象，如下所示：

你可以根据一个 JSON 对象来实例化一个Java 对象并填充字段值。如下所示：

```java
request.bodyHandler(buff -> {
  JsonObject jsonObject = buff.toJsonObject();
  User javaObject = jsonObject.mapTo(User.class);
});
```

#### 将 JSON 对象编码成字符串

您可使用 `encode` 方法将一个对象编码成字符串格式。（如要得到更优美、格式化的字符串，可以使用 `encodePrettily` 方法。）

### JSON 数组

`JsonArray` 类用来描述 JSON数组。

一个JSON 数组是一个值的序列（值的类型可以是 string、number、boolean 等）。

JSON 数组同样可以包含 `null` 值。

#### 创建 JSON 数组

可以使用默认构造函数创建空的JSON数组。

您可以根据JSON格式的字符串创建一个JSON数组：

```java
String jsonString = "[\"foo\",\"bar\"]";
JsonArray array = new JsonArray(jsonString);
```

#### 将数组项添加到JSON数组

您可以使用 `add` 方法添加数组项到JSON数组中：

```java
JsonArray array = new JsonArray();
array.add("foo").add(123).add(false);
```

#### 从 JSON 数组中获取值

您可使用 `getXXX` 方法从JSON 数组中获取值。例如：

```java
String val = array.getString(0);
Integer intVal = array.getInteger(1);
Boolean boolVal = array.getBoolean(2);
```

#### 将 JSON 数组编码成字符串

您可以使用 `encode` 编码成字符串格式。

## Buffers

在 Vert.x 内部，大部分数据被重新组织成 `Buffer` 格式。

`Buffer` 是一个可以被读取或写入的，包含0个或多个字节的序列，并且能够根据写入的字节自动扩容。 您也可以将 `Buffer` 想象成一个智能的字节数组。

### 创建 Buffer

可以使用静态方法 `Buffer.buffer` 来创建 Buffer。

Buffer可以从字符串或字节数组初始化，或者直接创建空的Buffer。

这儿有一些创建Buffer的例子。

创建一个空的Buffer：

```java
Buffer buff = Buffer.buffer();
```

从字符串创建一个Buffer，这个Buffer中的字符会以 UTF-8 格式编码：

```java
Buffer buff = Buffer.buffer("some string");
```

从字符串创建一个Buffer，这个字符串会以指定的编码方式编码，例如：

```java
Buffer buff = Buffer.buffer("some string", "UTF-16");
```

从字节数组 `byte[]` 创建Buffer：

```java
byte[] bytes = new byte[] {1, 3, 5};
Buffer buff = Buffer.buffer(bytes);
```

创建一个指定初始大小的Buffer:

```java
Buffer buff = Buffer.buffer(10000);
```

### 向Buffer写入数据

向Buffer写入数据的方式有两种：追加和随机访问。 任何一种情况下 Buffer都会自动进行扩容， 所以你不会在使用Buffer时遇到 `IndexOutOfBoundsException`。

#### 追加到Buffer

您可以使用 `appendXXX` 方法追加数据到Buffer。 Buffer类提供了追加各种不同类型数据的追加写入方法。

```java
Buffer buff = Buffer.buffer();

buff.appendInt(123).appendString("hello\n");

socket.write(buff);
```

#### 随机访问写Buffer

您还可以指定一个索引值，通过 `setXXX` 方法写入数据到 `Buffer`。 `setXXX` 也为各种不同数据类型提供了对应的方法。所有的 set 方法都会将索引值作为第一个参数 —— 这表示Buffer中开始写入数据的位置。

Buffer始终根据需要进行自动扩容。

```java
Buffer buff = Buffer.buffer();

buff.setInt(1000, 123);
buff.setString(0, "hello");
```

### 从Buffer中读取

可使用 `getXXX` 方法从 Buffer 中读取数据，`getXXX` 为各种不同数据类型提供了对应的方法， 这些方法的第一个参数是Buffer中待获取的数据的索引位置。

```java
Buffer buff = Buffer.buffer();
for (int i = 0; i < buff.length(); i += 4) {
  System.out.println("int value at " + i + " is " + buff.getInt(i));
}
```

### 使用无符号数

可使用 `getUnsignedXXX`、 `appendUnsignedXXX` 和 `setUnsignedXXX` 方法将无符号数从Buffer中读取或追加/设置到Buffer里。 这对于实现一个致力于优化带宽占用的网络协议的编解码器是非常有用的。

下边例子中，值 200 被设置到了仅占用一个字节的特定位置：

```java
Buffer buff = Buffer.buffer(128);
int pos = 15;
buff.setUnsignedByte(pos, (short) 200);
System.out.println(buff.getUnsignedByte(pos));
```

控制台中显示 "200"。

### Buffer长度

可使用 `length` 方法获取Buffer长度， Buffer的长度值是Buffer中包含的字节的最大索引 + 1。

### 拷贝Buffer

可使用 `copy` 方法创建一个Buffer的副本。

### 裁剪Buffer

裁剪得到的Buffer是完全依赖于原始Buffer的一个新的Buffer，换句话说，它不会对Buffer中的数据做拷贝。 使用 `slice` 方法裁剪一个Buffer。

### Buffer 重用

将Buffer写入到一个Socket或其他类似位置后，Buffer就不可被重用了。

## 使用共享数据的API

`共享数据（SharedData）` API允许您在如下组件中安全地共享数据：

- 应用程序的不同部分之间，或者
- 同一 Vert.x 实例中的不同应用程序之间，或者
- Vert.x 集群中的不同实例之间

在实践中, 它提供了:

- synchronous maps (local-only)
- asynchronous maps
- asynchronous locks
- asynchronous counters

### Local maps

`Local maps` 允许您在同一个 Vert.x 实例中的不同事件循环（如不同的 verticle）之间安全地共享数据。

仅允许将某些数据类型作为键值和值：

- 不可变的类型 （如 String、boolean，等等），或
- 实现了 `Shareable` 接口的类型 （比如Buffer，JSON数组，JSON对象，或您编写的Shareable实现类）。

在后一种情况中，键/值将被复制，然后再放到Map中。

这样，我们可以确保在Vert.x应用程序不同线程之间没有 *共享访问可变状态* 。 因此您不必担心需要通过同步访问来保护该状态。

```java
SharedData sharedData = vertx.sharedData();

LocalMap<String, String> map1 = sharedData.getLocalMap("mymap1");

map1.put("foo", "bar"); // String是不可变的，所以不需要复制

LocalMap<String, Buffer> map2 = sharedData.getLocalMap("mymap2");

map2.put("eek", Buffer.buffer().appendInt(123)); // Buffer将会在添加到Map之前拷贝

// 之后... 在您应用的另外一部分

map1 = sharedData.getLocalMap("mymap1");

String val = map1.get("foo");

map2 = sharedData.getLocalMap("mymap2");

Buffer buff = map2.get("eek");
```

### 异步共享的 maps

`异步共享的 maps` 允许数据被放到 map 中，并从本地或任何其他节点读取。

这使得它们对于托管Vert.x Web应用程序的服务器场中的会话状态存储非常有用。

获取Map的过程是异步的，返回结果可以传递给您指定的处理器。

```java
SharedData sharedData = vertx.sharedData();

sharedData.<String, String>getAsyncMap("mymap", res -> {
  if (res.succeeded()) {
    AsyncMap<String, String> map = res.result();
  } else {
    // 发生错误
  }
});
```

当 Vert.x 是集群模式时, 你放进map的数据，从本地以及从集群中的其他成员那里都可以访问到。

如果你的应用不需要和其它任何节点共享数据，那么你可以获取一个仅限本地的 map：

```java
SharedData sharedData = vertx.sharedData();

sharedData.<String, String>getLocalAsyncMap("mymap", res -> {
  if (res.succeeded()) {
    // 仅限本地的异步map
    AsyncMap<String, String> map = res.result();
  } else {
    // 发生错误
  }
});
```

### 异步锁

`异步锁` 允许您在集群中获取独占锁。 异步锁适用于：同一时刻仅在一个节点上执行某些操作或访问某个资源。

集群范围锁具有异步API，它和大多数等待锁释放的阻塞调用线程的API锁不相同。

可使用 `getLock` 方法获取锁。 它不会阻塞，但当锁可用时， `Lock` 的实例会被传入处理器，表示您现在拥有该锁。

若您拥有的锁没有其他调用者，集群上的任何地方都可以获得该锁。

当您用完锁后，您可以调用 `release` 方法来释放它，以便另一个调用者可获得它。

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLock("mylock", res -> {
  if (res.succeeded()) {
    // 获得锁
    Lock lock = res.result();

    // 5秒后我们释放该锁以便其他人可以得到它

    vertx.setTimer(5000, tid -> lock.release());

  } else {
    // 发生错误
  }
});
```

您可以为锁设置一个超时时间，若获取锁超时，则会通知处理器获取锁失败：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLockWithTimeout("mylock", 10000, res -> {
  if (res.succeeded()) {
    // 获得锁
    Lock lock = res.result();

  } else {
    // 获取锁失败
  }
});
```

如果你的应用不需要和其它任何节点共享锁，你可以获取一个仅限本地的锁：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLocalLock("mylock", res -> {
  if (res.succeeded()) {
    // 仅限本地的计数器
    Lock lock = res.result();

    // 5秒后我们释放该锁以便其他人可以得到它

    vertx.setTimer(5000, tid -> lock.release());

  } else {
    // 发生错误
  }
});
```

### 异步计数器

有时你会需要在本地或者在应用节点之间维护一个原子计数器。

您可以用 `Counter` 来做到这一点。

您可以通过 `getCounter` 方法获取一个实例：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getCounter("mycounter", res -> {
  if (res.succeeded()) {
    Counter counter = res.result();
  } else {
    // 发生错误
  }
});
```

在获取了一个实例后，您可以用多种方式获取当前的计数、原子地+1、-1、 加某个特定值。

如果你的应用不需要和其它任何节点共享计数器, 你可以获取一个仅限本地的计数器：

```java
SharedData sharedData = vertx.sharedData();

sharedData.getLocalCounter("mycounter", res -> {
  if (res.succeeded()) {
    // 仅限本地的计数器
    Counter counter = res.result();
  } else {
    // 发生错误
  }
});
```

## 使用 Vert.x 访问文件系统

Vert.x的 `FileSystem` 对象提供了许多操作文件系统的方法。

每个Vert.x 实例有一个文件系统对象，您可以使用 `fileSystem` 方法获取它。

每个操作都提供了阻塞和非阻塞版本，其中非阻塞版本接受一个处理器（Handler）， 当操作完成或发生错误时调用该处理器。

Vert.x 文件系统支持 copy、move、truncate、chmod 等等许多其他文件操作。

### 异步文件访问

Vert.x提供了异步文件访问的抽象，允许您操作文件系统上的文件。

```java
OpenOptions options = new OpenOptions();
fileSystem.open("myfile.txt", options, res -> {
  if (res.succeeded()) {
    AsyncFile file = res.result();
  } else {
    // 发生错误
  }
});
```

`AsyncFile` 实现了 `ReadStream` 和 `WriteStream` 接口，因此您可以将文件和其他流对象配合 *管道* 工作， 如NetSocket、HTTP请求和响应和WebSocket等。

它们还允许您直接读写。

#### 随机访问写

要使用 `AsyncFile` 进行随机访问写，请使用 `write` 方法。

这个方法的参数有：

- `buffer` ：要写入的缓冲
- `position` ：一个整数，指定在文件中写入缓冲的位置，若位置大于或等于文件大小， 文件将被扩展以适应偏移的位置。
- `handler` ：结果处理器

```java
vertx.fileSystem().open("target/classes/hello.txt", new OpenOptions(), result -> {
  if (result.succeeded()) {
    AsyncFile file = result.result();
    Buffer buff = Buffer.buffer("foo");
    for (int i = 0; i < 5; i++) {
      file.write(buff, buff.length() * i, ar -> {
        if (ar.succeeded()) {
          System.out.println("Written ok!");
          // 等等
        } else {
          System.err.println("Failed to write: " + ar.cause());
        }
      });
    }
  } else {
    System.err.println("Cannot open file " + result.cause());
  }
});
```

#### 随机访问读

要使用 `AsyncFile` 进行随机访问读，请使用 `read` 方法。

该方法的参数有：

- `buffer` ：读取数据的 Buffer
- `offset` ：读取数据将被放到 Buffer 中的偏移量
- `position` ：从文件中读取数据的位置
- `length` ：要读取的数据的字节数
- `handler` ：结果处理器

```java
vertx.fileSystem().open("target/classes/les_miserables.txt", new OpenOptions(), result -> {
  if (result.succeeded()) {
    AsyncFile file = result.result();
    Buffer buff = Buffer.buffer(1000);
    for (int i = 0; i < 10; i++) {
      file.read(buff, i * 100, i * 100, 100, ar -> {
        if (ar.succeeded()) {
          System.out.println("Read ok!");
        } else {
          System.err.println("Failed to write: " + ar.cause());
        }
      });
    }
  } else {
    System.err.println("Cannot open file " + result.cause());
  }
});
```

#### 打开选项

打开 `AsyncFile` 时，您可以传递一个 `OpenOptions` 实例， 这些选项描述了访问文件的行为。例如：您可使用 `setRead`，`setWrite` 和 `setPerms` 方法配置文件访问权限。

若打开的文件已经存在，则可以使用 `setCreateNew` 和 `setTruncateExisting` 配置对应行为。

您可以使用 `setDeleteOnClose` 标记在关闭时或JVM停止时要删除的文件。

#### 将数据刷新到底层存储

在 `OpenOptions` 中，您可以使用 `setDsync` 方法在每次写入时启用/禁用内容的自动同步。这种情况下，您可以使用 `flush` 方法手动将OS缓存中的数据写入存储设备。

该方法也可附带一个处理器来调用，这个处理器在 `flush` 完成时被调用。

#### 将 AsyncFile 作为 ReadStream 和 WriteStream

`AsyncFile` 实现了 `ReadStream` 和 `WriteStream` 接口。 您可以使用 *管道* 将数据与其他读取和写入流进行数据管送。

```java
final AsyncFile output = vertx.fileSystem().openBlocking("target/classes/plagiary.txt", new OpenOptions());

vertx.fileSystem().open("target/classes/les_miserables.txt", new OpenOptions(), result -> {
  if (result.succeeded()) {
    AsyncFile file = result.result();
    file.pipeTo(output)
      .onComplete(v -> {
        System.out.println("Copy done");
      });
  } else {
    System.err.println("Cannot open file " + result.cause());
  }
});
```

还可以使用 *管道* 将文件内容写入到HTTP 响应中，或者写入任意 `WriteStream`。

#### 从 Classpath 访问文件

当Vert.x找不到文件系统上的文件时，它尝试从类路径中解析该文件。 请注意，类路径的资源路径不以 `/` 开头。

由于Java不提供对类路径资源的异步方法， 所以当类路径资源第一次被访问时， 该文件将复制到工作线程中的文件系统。 当第二次访问相同资源时，访问的文件直接从 （工作线程的）文件系统提供。 即使类路径资源发生变化（例如开发系统中）， 也会提供之前的内容。

此（文件）缓存行为可以通过 `setFileCachingEnabled` 方法进行设定。如果系统属性中没有预先设置 `vertx.disableFileCaching` ，则其默认值为 `true`。

文件缓存的路径默认为 `.vertx`，它可以通过设置系统属性 `vertx.cacheDirBase` 进行自定义。

如果想在系统级禁用整个classpath解析功能，可以将系统属性 `vertx.disableFileCPResolving` 设置为 `true`。

如果要禁用特定应用程序的类路径解析，但默认情况下在系统范围内将其保持启用状态， 则可以通过 `setClassPathResolvingEnabled` 选项设置。

#### 关闭 AsyncFile

您可调用 `close` 方法来关闭 `AsyncFile`。 关闭是异步的，如果希望在关闭过后收到通知，您可指定一个处理器作为函数 `close` 的参数。

## 流

在Vert.x中，有许多对象可以用于读取和写入。

在 Vert.x 中，写调用是立即返回的，而写操作的实际是在内部队列中排队写入。

不难看出，若写入对象的速度比实际写入底层数据资源速度快， 那么写入队列就会无限增长， 最终导致内存耗尽。

为了解决这个问题，Vert.x API中的一些对象提供了简单的流程控制（ *回压 back-pressure* ）功能。

任何可控制的 *写入* 流对象都实现了 `WriteStream` 接口， 相应的，任何可控制的 *读取* 流对象都实现了 `ReadStream` 接口。

### ReadStream

`ReadStream`（可读流） 接口的实现类包括： `HttpClientResponse`， `DatagramSocket`， `HttpClientRequest`， `HttpServerFileUpload`， `HttpServerRequest`， `MessageConsumer`， `NetSocket`， `WebSocket`， `TimeoutStream`， `AsyncFile`。

- `handler`： 设置一个处理器，它将从 `ReadStream` 读取对象
- `pause`： 暂停处理器，暂停时，处理器中将不会收到任何对象
- `fetch`： 从stream中抓取指定数量的对象，任意对象抵达stream时，都会触发handler， fetch操作是累积的。
- `resume`： 恢复处理器，若任何对象到达目的地则handler将被触发；等价于 `fetch(Long.MAX_VALUE)`
- `exceptionHandler`： 若ReadStream发生异常，将被调用
- `endHandler`： 当流的数据读取完毕时将被调用。触发原因是读取到了 `EOF` ，可能分别来自如下： 与 `ReadStream` 关联的文件、HTTP请求、或TCP Socket的连接被关闭

可读流有 *flowing* 和 *fetch* 两个模式：

- 最初 stream 是 <i>flowing</i> 模式
- 当 stream 处于 *flowing* 模式，stream中的元素被传输到handler
- 当 stream 处于 *fetch* 模式，只会将指定数量的元素传输到handler

`pause`, `resume` 和 `fetch` 会改变ReadStream的模式

- `resume()` 设置ReadStream 为 *flowing* 模式
- `pause()` 设置ReadStream 为 *fetch* 模式 并设置demand值为0
- `fetch(long)` 请求指定数量的stream元素并将该数量加到目前的demand值当中

### WriteStream

`WriteStream`（可写流）接口的实现类包括：`HttpClientRequest`，`HttpServerResponse` `WebSocket`，`NetSocket` 和 `AsyncFile`。

函数：

- `write`： 往WriteStream写入一个对象，该方法将永远不会阻塞， 内部是排队写入并且底层资源是异步写入。
- `setWriteQueueMaxSize`： 设置写入队列容量—— `writeQueueFull` 在队列 *写满* 时返回 `true`。 注意，当写队列已满时，调用写（操作）时 数据依然会被接收和排队。 实际数量取决于流的实现，对于 `Buffer` ， size代表实际写入的字节数，而并非缓冲区的数量。
- `writeQueueFull`： 若写队列被认为已满，则返回 `true` 。
- `exceptionHandler`： `WriteStream` 发生异常时调用。
- `drainHandler`： 判定 `WriteStream` 有剩余空间时调用。

## 编写 HTTP 服务端和客户端

Vert.x 允许您轻松编写非阻塞的 HTTP 客户端和服务端。

Vert.x 支持 HTTP/1.0、HTTP/1.1 和 HTTP/2 协议。

用于 HTTP 的基本 API 对 HTTP/1.x 和 HTTP/2 是相同的，特定的API功能也可用于处理 HTTP/2 协议。

### 创建 HTTP 服务端

使用所有默认选项创建 HTTP 服务端的最简单方法如下：

```java
HttpServer server = vertx.createHttpServer();
```

### 配置 HTTP 服务端

若您不想用默认值，可以在创建服务器时传递一个 `HttpServerOptions` 实例给它：

```java
HttpServerOptions options = new HttpServerOptions().setMaxWebSocketFrameSize(1000000);

HttpServer server = vertx.createHttpServer(options);
```

### 开启服务端监听

要告诉服务器监听传入的请求，您可以使用其中一个 `listen` 方法。

在配置项中让服务器监听指定的主机和端口：

```java
vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("0.0.0.0"))
server.listen();
```

或在调用listen方法时指定主机和端口号，这样就忽略了配置项（中的主机和端口）：

```java
HttpServer server = vertx.createHttpServer();
server.listen(8080, "myhost.com");
```

在服务器实际监听时收到通知，您可以向 `listen` 提供一个处理器。 例如：

```java
HttpServer server = vertx.createHttpServer();
server.listen(8080, "myhost.com", res -> {
  if (res.succeeded()) {
    System.out.println("Server is now listening!");
  } else {
    System.out.println("Failed to bind!");
  }
});
```

### 收到传入请求的通知

在收到请求时收到通知，则需要设置一个 `requestHandler` ：

```java
HttpServer server = vertx.createHttpServer();
server.requestHandler(request -> {
  // Handle the request in here
});
```

### 处理请求(示例：http://localhost:8080/index?name=tom&age=18)

当请求到达时，Vert.x 会像对应的处理函数传入一个 `HttpServerRequest` 实例并调用请求处理函数。 此对象表示服务端 HTTP 请求。

当请求的头信息被完全读取时会调用该请求处理器。

如果请求包含请求体，那么该请求体将在请求处理器被调用后的某个时间到达服务器。

服务请求对象允许您读取 `uri`， `path`， `params` 和 `headers` 等其他信息。

每一个服务请求对象和一个服务响应对象绑定，您可以用 `response` 方法获取一个 `HttpServerResponse` 对象的引用。

#### 请求版本

在请求中指定的 HTTP 版本可通过 `version` 方法获取。

```java
request.version()
//version: HTTP_1_1
```

#### 请求方法

使用 `method` 方法读取请求中的 HTTP 方法。 （即GET、POST、PUT、DELETE、HEAD、OPTIONS等）。

```java
request.method()
//method: GET
```

#### 请求URI

使用 `uri` 方法读取请求中的URI路径。

请注意，这是在HTTP请求中传递的实际URI，它总是一个相对的URI。

```java
request.uri()
//uri: /index?name=tom&age=18
```

#### 请求路径

使用 `path` 方法读取URI中的路径部分。

```java
request.path()
//path: /index
```

#### 请求查询

使用 `query` 读取URI中的查询部分。

```java
request.query()
//query: name=tom&age=18
```

#### 请求头部

使用 `headers` 方法获取HTTP请求中的请求头部信息。

这个方法返回一个 `MultiMap` 实例。它像一个普通的Map或哈希表，并且它还允许同个键对应多个值 —— 因为HTTP允许同一个键对应多个请求头的值。

它的键不区分大小写，这意味着您可以执行以下操作：

```java
MultiMap headers = request.headers();

// 读取User-Agent
System.out.println("User agent is " + headers.get("user-agent"));

// 这样做可以得到和上边相同的结果
System.out.println("User agent is " + headers.get("User-Agent"));
```

#### 请求主机

使用 `host` 方法返回HTTP请求中的主机名。

对于 HTTP/1.x 请求返回请求头中的 `host` 值，对于 HTTP/2 请求则返回伪头中的 `:authority` 的值。

#### 请求参数

您可以使用 `params` 方法返回HTTP请求中的参数信息。

像 `headers` 方法一样，它也会返回一个 `MultiMap` 实例，因为可以有多个具有相同名称的参数。

请求参数在请求URI的 path 部分之后，例如URI是：

/page.html?param1=abc&param2=xyz

那么参数将包含以下内容：

```
param1: 'abc'
param2: 'xyz
```

请注意，这些请求参数是从请求的 URI 中解析读取的， 若您已经将表单属性存放在请求体中发送出去，并且该请求为 `multi-part/form-data` 类型请求，那么它们将不会显示在此处的参数中。

#### 远程地址

可以使用 `remoteAddress` 方法读取请求发送者的地址。

#### 绝对URI

HTTP 请求中传递的URI通常是相对的，若您想要读取请求中和相对URI对应的绝对URI， 可调用 `absoluteURI` 方法。

#### 结束处理器

当整个请求（包括所有请求体）已经被完全读取时，请求中的 `endHandler` 方法会被调用。

#### 从请求体中读取数据

HTTP请求通常包含我们需要读取的请求体。如前所述，当请求头部达到时， 请求处理器会被调用，因此请求对象在此时没有请求体。

这是因为请求体可能非常大（如文件上传），并且我们不会在内容发送给您之前将其全部缓冲存储在内存中， 这可能会导致服务器耗尽可用内存。

要接收请求体，您可在请求中调用 `handler` 方法设置一个处理器， 每次请求体的一小块数据收到时，该处理器都会被调用。以下是一个例子：

```java
request.handler(buffer -> {
  System.out.println("I have received a chunk of the body of length " + buffer.length());
});
```

传递给处理器的对象是一个 `Buffer`， 当数据从网络到达时，处理器可以多次被调用，这取决于请求体的大小。

在某些情况下（例：若请求体很小），您将需要将这个请求体聚合到内存中， 您可以按照下边的方式进行聚合：

```java
Buffer totalBuffer = Buffer.buffer();

request.handler(buffer -> {
  System.out.println("I have received a chunk of the body of length " + buffer.length());
  totalBuffer.appendBuffer(buffer);
});

request.endHandler(v -> {
  System.out.println("Full body received, length = " + totalBuffer.length());
});
```

这是一个常见的情况，Vert.x为您提供了一个 `bodyHandler` 方法来执行此操作。 当所有请求体被收到时，`bodyHandler` 绑定的处理器会被调用一次：

```java
request.bodyHandler(totalBuffer -> {
  System.out.println("Full body received, length = " + totalBuffer.length());
});
```

#### 流式请求

请求对象实现了 `ReadStream` 接口，因此您可以将请求体读取到任何 `WriteStream` 实例中。

#### 处理 HTML 表单

您可使用 `application/x-www-form-urlencoded` 或 `multipart/form-data` 这两种 content-type 来提交 HTML 表单。

对于使用 URL 编码过的表单，表单属性会被编码在URL中，如同普通查询参数一样。

对于 multipart 类型的表单，它会被编码在请求体中，而且在整个请求体被 完全读取之前它是不可用的。

Multipart 表单还可以包含文件上传。

若您想要读取 multipart 表单的属性，您应该告诉 Vert.x 您会在读取任何请求体 **之前** 调用 `setExpectMultipart` 方法， 然后在整个请求体都被读取后，您可以使用 `formAttributes` 方法来读取实际的表单属性。

```java
server.requestHandler(request -> {
  request.setExpectMultipart(true);
  request.endHandler(v -> {
    // The body has now been fully read, so retrieve the form attributes
    MultiMap formAttributes = request.formAttributes();
  });
});
```

表单属性的最大长度是 `8192` 字节。 当客户端提交了属性长度超过该限制 的表单时， 文件的上传会产生一个异常并触发 `HttpServerRequest` 的异常处理器。 您可以 使用 `setMaxFormAttributeSize` 方法来设置一个不一样的最大表单属性长度。

#### 处理文件上传

Vert.x 可以处理以 multipart 编码形式上传的的文件。

要接收文件，您可以告诉 Vert.x 使用 multipart 表单，并对请求设置 `uploadHandler` 。

当服务器每次接收到上传请求时， 该处理器将被调用一次。

传递给处理器的对象是一个 `HttpServerFileUpload` 实例。

```java
server.requestHandler(request -> {
  request.setExpectMultipart(true);
  request.uploadHandler(upload -> {
    System.out.println("Got a file upload " + upload.name());
  });
});
```

上传的文件可能很大，我们不会在单个缓冲区中包含整个上传的数据，因为这样会导致内存耗尽。 相反，上传数据是以块的形式被接收的：

```java
request.uploadHandler(upload -> {
  upload.handler(chunk -> {
    System.out.println("Received a chunk of the upload of length " + chunk.length());
  });
});
```

上传对象实现了 `ReadStream` 接口，因此您可以将请求体读取到任何 `WriteStream` 实例中。

若只是想将文件上传到服务器磁盘的某个地方，可以使用 `streamToFileSystem` 方法：

```java
request.uploadHandler(upload -> {
  upload.streamToFileSystem("myuploads_directory/" + upload.filename());
});
```

#### 处理cookies

使用 `getCookie` 可以按Cookie名读取Cookie， 或使用 `cookieMap` 获取所有Cookie。

使用 `removeCookie` 删除Cookie。

使用 `addCookie` 增加Cookie。

增加的Cookie会在响应的时候自动写到响应头，随后浏览器可以获取到设置的 Cookie 并存储起来。

（Vert.x的）cookie是 `Cookie` 的实例。 可以从中获取cookie的名字、取值、域名、路径以及其他cookie的常规属性。

设置了SameSite的Cookie禁止服务器在发送跨域请求时带上发送 （站点是否跨域，取决于可注册域），从而为伪造跨域请求攻击提供了一些保护。 这种Cookie可以通过 `setSameSite` 设置。

Cookie的SameSite属性接受三个取值:

- None - 允许在跨域请求和非跨域请求中发送
- Strict - 只能在同站点的请求中发送（请求到设置该Cookie的站点）。 如果设置Cookie的站点与当前请求的站点不一致， 则不会发送SameSite设置为Strict的Cookie
- Lax - 在跨域的子请求（例如调用加载图像或iframe）不发送这种SameSite（设为Lax的）Cookie， 但当用户从外部站点导航到URL时将发送该Cookie, 例如通过链接打开。

下面是一个查询并增加Cookie的例子：

```java
Cookie someCookie = request.getCookie("mycookie");
String cookieValue = someCookie.getValue();

// 处理Cookie的逻辑

// 增加Cookie - 会自动写入响应头
request.response().addCookie(Cookie.cookie("othercookie", "somevalue"));
```

#### 处理压缩体

Vert.x 可以处理在客户端通过 *deflate* 或 *gzip* 算法压缩过的请求体信息。

若要启用解压缩功能则您要在创建服务器时调用 `setDecompressionSupported` 方法设置配置项。

默认情况下解压缩是并未被启用的。

### 返回响应

服务器响应对象是一个 `HttpServerResponse` 实例， 它可以从request对应的 `response` 方法中读取。

您可以使用响应对象回写一个响应到 HTTP客户端。

#### 设置状态码和消息

默认的 HTTP 状态响应码为 `200`，表示 `OK`。

可使用 `setStatusCode` 方法设置不同状态代码。

您还可用 `setStatusMessage` 方法指定自定义状态消息。

若您不指定状态信息，将会使用默认的状态码响应。

#### 向 HTTP 响应写入数据

想要将数据写入 HTTP Response，您可使用任意一个 `write` 方法。

它们可以在响应结束之前被多次调用，它们可以通过以下几种方式调用：

对用单个缓冲区：

```java
HttpServerResponse response = request.response();
response.write(buffer);
```

写入字符串，这种请求字符串将使用 UTF-8 进行编码，并将结果写入到报文中。

```java
HttpServerResponse response = request.response();
response.write("hello world!");
```

写入带编码方式的字符串，这种情况字符串将使用指定的编码方式编码， 并将结果写入到报文中。

```java
HttpServerResponse response = request.response();
response.write("hello world!", "UTF-16");
```

响应写入是异步的，并且在写操作进入队列之后会立即返回。

若您只需要将单个字符串或Buffer写入到HTTP 响应，则可使用 `end` 方法将其直接写入响应中并发回到客户端。

第一次写入操作会触发响应头的写入，因此， 若您不使用 HTTP 分块，那么必须在写入响应之前设置 `Content-Length` 头， 否则不会生效。若您使用 HTTP 分块则不需要担心这点。

#### 完成 HTTP 响应

一旦您完成了 HTTP 响应，可调用 `end` 将其发回客户端。

这可以通过几种方式完成：

没有参数，直接结束响应，发回客户端：

```java
HttpServerResponse response = request.response();
response.write("hello world!");
response.end();
```

您也可以和调用 `write` 方法一样传String或Buffer给 `end` 方法。 这种方式类似于先调用带String或Buffer参数的 `write` 方法，再调用无参 `end` 方法。例如：

```java
HttpServerResponse response = request.response();
response.end("hello world!");
```

#### 完成 HTTP 响应

一旦您完成了 HTTP 响应，可调用 `end` 将其发回客户端。

这可以通过几种方式完成：

没有参数，直接结束响应，发回客户端：

```java
HttpServerResponse response = request.response();
response.write("hello world!");
response.end();
```

您也可以和调用 `write` 方法一样传String或Buffer给 `end` 方法。 这种方式类似于先调用带String或Buffer参数的 `write` 方法，再调用无参 `end` 方法。例如：

```java
HttpServerResponse response = request.response();
response.end("hello world!");
```

#### 设置响应头

HTTP 响应头可直接添加到 HTTP 响应中，通常直接操作 `headers` ：

```java
HttpServerResponse response = request.response();
MultiMap headers = response.headers();
headers.set("content-type", "text/html");
headers.set("other-header", "wibble");
```

或您可使用 `putHeader` 方法：

```java
HttpServerResponse response = request.response();
response.putHeader("content-type", "text/html").putHeader("other-header", "wibble");
```

响应头必须在写入响应体之前进行设置。

#### 分块 HTTP 响应和附加尾部

Vert.x 支持 [分块传输编码(HTTP Chunked Transfer Encoding)](http://en.wikipedia.org/wiki/Chunked_transfer_encoding) 。

这允许HTTP 响应体以块的形式写入，通常在响应体预先不知道尺寸、 需要将很大响应正文以流式传输到客户端时使用。

您可以通过如下方式开启分块模式：

```java
HttpServerResponse response = request.response();
response.setChunked(true);
```

默认是不分块的，当处于分块模式时，每次调用任意一个 `write` 方法将导致新的 HTTP 块被写出。

在分块模式下，您还可以将响应的 HTTP 响应附加尾部(trailers)写入响应， 这种方式实际上是在写入响应的最后一块。

若要向响应添加尾部，则直接添加到 `trailers` 里。

```java
HttpServerResponse response = request.response();
response.setChunked(true);
MultiMap trailers = response.trailers();
trailers.set("X-wibble", "woobble").set("X-quux", "flooble");
```

或者调用 `putTrailer` 方法。

```java
HttpServerResponse response = request.response();
response.setChunked(true);
response.putTrailer("X-wibble", "woobble").putTrailer("X-quux", "flooble");
```

#### 直接从磁盘或 Classpath 读文件

若您正在编写一个Web 服务端，一种从磁盘中读取并提供文件的方法是将文件作为 `AsyncFile` 打开并将其传送到HTTP 响应中。

或您可以使用 `readFile` 方法一次性加载它，并直接将其写入响应。

此外，Vert.x 提供了一种方法，允许您只执行一次操作， 即可直接将文件从磁盘或文件系统写入 HTTP 响应。 若底层操作系统支持，操作系统可以不拷贝到用户态， 而直接把数据从文件传输到Socket。

这是使用 `sendFile` 方法完成的，对于大文件处理通常更有效， 而这个方法对于小文件可能很慢。

这儿是一个非常简单的 Web 服务器，它使用 `sendFile` 方法从文件系统中读取并提供文件：

```java
vertx.createHttpServer().requestHandler(request -> {
  String file = "";
  if (request.path().equals("/")) {
    file = "index.html";
  } else if (!request.path().contains("..")) {
    file = request.path();
  }
  request.response().sendFile("web/" + file);
}).listen(8080);
```

发送文件是异步的，可能在调用返回一段时间后才能完成。如果要在 文件写入时收到通知，可以在 `sendFile` 方法中设置一个处理器。

当需要提供文件的一部分，从给定的字节开始，您可以像下边这样做：

```java
vertx.createHttpServer().requestHandler(request -> {
  long offset = 0;
  try {
    offset = Long.parseLong(request.getParam("start"));
  } catch (NumberFormatException e) {
    // error handling...
  }

  long end = Long.MAX_VALUE;
  try {
    end = Long.parseLong(request.getParam("end"));
  } catch (NumberFormatException e) {
    // error handling...
  }

  request.response().sendFile("web/mybigfile.txt", offset, end);
}).listen(8080);
```

若您想要从偏移量开始发送文件直到尾部，则不需要提供长度信息， 这种情况下，您可以执行以下操作：

```java
vertx.createHttpServer().requestHandler(request -> {
  long offset = 0;
  try {
    offset = Long.parseLong(request.getParam("start"));
  } catch (NumberFormatException e) {
    // error handling...
  }

  request.response().sendFile("web/mybigfile.txt", offset);
}).listen(8080);
```

#### 管道式响应

服务端响应 `HttpServerResponse` 也是一个 `WriteStream` 实例，因此您可以从任何 `ReadStream` 向其传送数据，如 `AsyncFile`， `NetSocket`， `WebSocket` 或者 `HttpServerRequest`。

这儿有一个例子，它回应了任何 PUT 方法的响应中的请求体。 它为请求体使用了管道，所以即使 HTTP 请求体远远超过内存容量， 它依旧可以正常工作。：

```java
vertx.createHttpServer().requestHandler(request -> {
  HttpServerResponse response = request.response();
  if (request.method() == HttpMethod.PUT) {
    response.setChunked(true);
    request.pipeTo(response);
  } else {
    response.setStatusCode(400).end();
  }
}).listen(8080);
```

还可以使用 `send` 方法发送 `ReadStream` 。

发送流是一个管道操作，但由于这方法是 `HttpServerResponse` 的， 当 `content-length` 响应头未设置时，此方法可以处理分块响应。

```java
vertx.createHttpServer().requestHandler(request -> {
  HttpServerResponse response = request.response();
  if (request.method() == HttpMethod.PUT) {
    response.send(request);
  } else {
    response.setStatusCode(400).end();
  }
}).listen(8080);
```

#### 处理异常

调用 `exceptionHandler` 可以设置一个处理器，用于接收 连接传递给 `requestHandler` 之前发生的异常， 或者是传递给 `webSocketHandler` 之前发生的异常，如TLS握手期间发生的异常。

#### 处理不合法的请求

Vert.x 可以处理不合法的 HTTP 请求，并且提供了一个默认的处理器以处理不合法请求， 举个例子，当请求头部过大时，它会返回 `REQUEST_HEADER_FIELDS_TOO_LARGE` 的响应。

您可以设置您自定义的 `invalidRequestHandler` 来处理 不合法的请求。您的实现类可以只处理特定的不合法的请求并将其他的错误请求委托给 `HttpServerRequest.DEFAULT_INVALID_REQUEST_HANDLER` 来处理。

#### HTTP 压缩

Vert.x 支持开箱即用的 HTTP 压缩。

这意味着在响应发送回客户端之前，您可以将响应体自动压缩。

若客户端不支持 HTTP 压缩，则它可以发回没有压缩过的请求。

这允许它同时处理支持 HTTP 压缩的客户端和不支持的客户端。

要启用压缩，可以使用 `setCompressionSupported` 方法进行配置。

默认情况下，未启用压缩。

当启用 HTTP 压缩时，服务器将检查客户端请求头中是否包含了 `Accept-Encoding` 并支持常用的 deflate 和 gzip 压缩算法。 Vert.x 两者都支持。

若找到这样的请求头，服务器将使用所支持的压缩算法之一自动压缩响应正文， 并发送回客户端。

可以通过将响应头 `content-encoding` 设置为 `identity` ，来关闭响应内容的压缩:

```java
request.response()
  .putHeader(HttpHeaders.CONTENT_ENCODING, HttpHeaders.IDENTITY)
  .sendFile("/path/to/image.jpg");
```

注意：压缩可以减少网络流量，但是CPU密集度会更高。

为了解决后边一个问题，Vert.x也允许您调整原始的 gzip/deflate 压缩算法的 “压缩级别” 参数。

压缩级别允许根据所得数据的压缩比和压缩/解压的计算成本来配置 gzip/deflate 算法。

压缩级别是从 “1” 到 “9” 的整数值，其中 “1” 表示更低的压缩比但是最快的算法，“9” 表示可用的最大压缩比但比较慢的算法。

使用高于 1-2 的压缩级别通常允许仅仅节约一些字节大小 —— 它的增益不是线性的，并取决于要压缩的特定数据 —— 但它可以满足服务器所要求的CPU周期的不可控的成本 （注意现在Vert.x不支持任何缓存形式的响应数据，如静态文件， 因此压缩是在每个请求体生成时进行的）,它可生成压缩过的响应数据、并对接收的响应解码（inflating）—— 和客户端使用的方式一致， 这种操作随着压缩级别的增长会变得更加倾向于CPU密集型。

默认情况下 —— 如果通过 `setCompressionSupported` 方法启用压缩，Vert.x 将使用 “6” 作为压缩级别， 但是该参数可通过 `setCompressionLevel` 方法来更改。

### 创建 HTTP 客户端

创建一个具有默认配置的 `HttpClient` 实例：

```java
HttpClient client = vertx.createHttpClient();
```

配置客户端选项，可按以下方式创建：

```java
HttpClientOptions options = new HttpClientOptions().setKeepAlive(false);
HttpClient client = vertx.createHttpClient(options);
```

### 发送请求

HTTP 客户端是很灵活的，您可以通过各种方式发出请求。

发送请求的第一步是获取远程服务器的HTTP连接：

```java
client.request(HttpMethod.GET,8080, "myserver.mycompany.com", "/some-uri", ar1 -> {
  if (ar1.succeeded()) {
    // 已连接到服务器
  }
});
```

HTTP客户端会连接到远程服务器，也可能复用连接池里可用的连接。

#### 默认主机和端口

通常您希望使用 HTTP 客户端向同一个主机/端口发送很多请求。为避免每次发送请求时重复设主机/端口， 您可以为客户端配置默认主机/端口：

```java
HttpClientOptions options = new HttpClientOptions().setDefaultHost("wibble.com");

// 若您需要，可设置默认端口
HttpClient client = vertx.createHttpClient(options);
client.request(HttpMethod.GET, "/some-uri", ar1 -> {
  if (ar1.succeeded()) {
    HttpClientRequest request = ar1.result();
    request.send(ar2 -> {
      if (ar2.succeeded()) {
        HttpClientResponse response = ar2.result();
        System.out.println("Received response with status code " + response.statusCode());
      }
    });
  }
});
```

#### 设置请求头

可以使用 `HttpHeaders` 设置请求头，比如:

```java
HttpClient client = vertx.createHttpClient();

// 使用MultiMap设置请求头
MultiMap headers = HttpHeaders.set("content-type", "application/json").set("other-header", "foo");

client.request(HttpMethod.GET, "some-uri", ar1 -> {
  if (ar1.succeeded()) {
    if (ar1.succeeded()) {
      HttpClientRequest request = ar1.result();
      request.headers().addAll(headers);
      request.send(ar2 -> {
        HttpClientResponse response = ar2.result();
        System.out.println("Received response with status code " + response.statusCode());
      });
    }
  }
});
```

这个headers是 `MultiMap` 的实例，它提供了添加、设置、删除条目的操作。 HTTP请求头允许一个特定的键包含多个值。

您也可以使用 `putHeader` 方法设置请求头：

```java
request.putHeader("content-type", "application/json")
       .putHeader("other-header", "foo");
```

若您想写入请求头，则您必须在写入任何请求体之前这样做来设置请求头。

#### 写请求并处理响应

`HttpClientRequest` 的 `request` 方法会连接到远程服务器， 或复用一个已有连接。获得的请求实例已预先填充了一些数据， 例如主机或请求URI，但您需要将此请求发送到服务器。

调用 `send` 方法可以发送HTTP请求， 如 `GET` 请求，并异步处理 `HttpClientResponse` 响应。

```java
client.request(HttpMethod.GET,8080, "myserver.mycompany.com", "/some-uri", ar1 -> {
  if (ar1.succeeded()) {
    HttpClientRequest request = ar1.result();

    // 发送请求并处理响应
    request.send(ar -> {
      if (ar.succeeded()) {
        HttpClientResponse response = ar.result();
        System.out.println("Received response with status code " + response.statusCode());
      } else {
        System.out.println("Something went wrong " + ar.cause().getMessage());
      }
    });
  }
});
```

您也可以发送带请求体的请求。

使用 `send` 方法可以发送 String 类型的请求体， 如果 `Content-Length` 请求头没有预先设置，则会自动设置。

```java
client.request(HttpMethod.GET,8080, "myserver.mycompany.com", "/some-uri", ar1 -> {
  if (ar1.succeeded()) {
    HttpClientRequest request = ar1.result();

    // 发送请求并处理响应
    request.send("Hello World", ar -> {
      if (ar.succeeded()) {
        HttpClientResponse response = ar.result();
        System.out.println("Received response with status code " + response.statusCode());
      } else {
        System.out.println("Something went wrong " + ar.cause().getMessage());
      }
    });
  }
});
```

使用 `send` 方法可以发送Buffer类型的请求体， 如果 `Content-Length` 请求头没有预先设置，则会自动设置。

```java
request.send(Buffer.buffer("Hello World"), ar -> {
  if (ar.succeeded()) {
    HttpClientResponse response = ar.result();
    System.out.println("Received response with status code " + response.statusCode());
  } else {
    System.out.println("Something went wrong " + ar.cause().getMessage());
  }
});
```

使用 `send` 方法可以发送Stream类型的请求体， 如果 `Content-Length` 请求头没有预先设置，则会设置分块传输的 `Content-Encoding` 请求头。

```java
request
  .putHeader(HttpHeaders.CONTENT_LENGTH, "1000")
  .send(stream, ar -> {
  if (ar.succeeded()) {
    HttpClientResponse response = ar.result();
    System.out.println("Received response with status code " + response.statusCode());
  } else {
    System.out.println("Something went wrong " + ar.cause().getMessage());
  }
});
```

#### 发送流的请求体

HttpClient的 `send` 方法在调用后马上发起请求。

但有时候需要对请求体的写入做底层控制。

`HttpClientRequest` 可用于写请求体.

下面是发起带请求体的POST请求的例子：

```java
HttpClient client = vertx.createHttpClient();

client.request(HttpMethod.POST, "some-uri")
  .onSuccess(request -> {
    request.response().onSuccess(response -> {
      System.out.println("Received response with status code " + response.statusCode());
    });

    // 现在可以对请求做各种配置
    request.putHeader("content-length", "1000");
    request.putHeader("content-type", "text/plain");
    request.write(body);

    // 确认请求可以结束
    request.end();
});

// 或使用链式调用风格:

client.request(HttpMethod.POST, "some-uri")
  .onSuccess(request -> {
    request
      .response(ar -> {
        if (ar.succeeded()) {
          HttpClientResponse response = ar.result();
          System.out.println("Received response with status code " + response.statusCode());
        }
      })
      .putHeader("content-length", "1000")
      .putHeader("content-type", "text/plain")
      .end(body);
});
```

也有一些方法可用于写入 UTF-8 编码的字符串，或以其他特定编码写入 buffer：

```java
request.write("some data");

// 指定字符串编码
request.write("some other data", "UTF-16");

// 通过buffer写入
Buffer buffer = Buffer.buffer();
buffer.appendInt(123).appendLong(245l);
request.write(buffer);
```

如果您的 http 请求只需要写入一个字符串或 buffer， 可以直接调用 `end` 方法。

```java
request.end("some simple data");

// 通过调用一次end方法，写入一个buffer并结束请求
Buffer buffer = Buffer.buffer().appendDouble(12.34d).appendLong(432l);
request.end(buffer);
```

当您写入请求时，第一次调用 `write` 方法将先将请求头写入到请求报文中。

实际写入操作是异步的，它可能在调用返回一段时间后才发生。

带请求体的非分块 HTTP 请求需要提供 `Content-Length` 头。

因此，若您不使用 HTTP 分块，则必须在写入请求之前设置 `Content-Length` 头， 否则会出错。

若您在调用其中一个 `end` 方法处理 String 或 Buffer，在写入请求体之前，Vert.x 将自动计算并设置 `Content-Length`。

若您在使用 HTTP 分块模式，则不需要 `Content-Length` 头， 因此您不必先计算大小。

#### 结束HTTP请求流

一旦完成了 HTTP 请求的准备工作，您必须调用其中一个 `end` 方法来 发送该请求（结束请求）。

结束一个请求时，若请求头尚未被写入，会导致它们被写入，并且请求被标记 成完成的。

请求可以通过多种方式结束。无参简单结束请求的方式如：

```java
request.end();
```

或可以在调用 `end` 方法时提供 String 或 Buffer，这个和先调用带 String/Buffer 参数的 `write` 方法之后再调用无参 `end` 方法一样：

```java
request.end("some-data");

// 使用buffer结束
Buffer buffer = Buffer.buffer().appendFloat(12.3f).appendInt(321);
request.end(buffer);
```

#### 使用流式请求

`HttpClientRequest` 实例实现了 `WriteStream` 接口。

这意味着您可以从任何 `ReadStream` 实例将数据pipe到请求中。

例如，您可以将磁盘上的文件直接管送到HTTP 请求体中，如下所示：

```java
request.setChunked(true);
file.pipeTo(request);
```

#### 分块 HTTP 请求

Vert.x 支持 [HTTP Chunked Transfer Encoding](http://en.wikipedia.org/wiki/Chunked_transfer_encoding) 请求。

这允许使用块方式写入HTTP 请求体，这个在请求体比较大需要流式发送到服务器， 或预先不知道大小时很常用。

您可使用 `setChunked` 将 HTTP 请求设置成分块模式。

在分块模式下，每次调用 `write` 方法将导致新的块被写入到报文。 这种模式中，无需先设置请求头中的 `Content-Length`。

```java
request.setChunked(true);

// 写一些块
for (int i = 0; i < 10; i++) {
  request.write("this-is-chunk-" + i);
}

request.end();
```

#### 请求超时

您可使用 `setTimeout` 或 `setTimeout` 设置一个特定 HTTP 请求的超时时间。

若请求在超时期限内未返回任何数据，则异常将会被传给异常处理器 （若已提供），并且请求将会被关闭。

### 处理 HTTP 响应

您可以在请求方法中指定处理器或通过 `HttpClientRequest` 对象直接设置处理器来接收 `HttpClientResponse` 的实例。

您可以通过 `statusCode` 和 `statusMessage` 方法从响应中查询响应的状态码和状态消息：

```java
request.send(ar2 -> {
  if (ar2.succeeded()) {

    HttpClientResponse response = ar2.result();

    // 状态代码,如:200、404
    System.out.println("Status code is " + response.statusCode());

    // 状态消息,如:OK、Not Found
    System.out.println("Status message is " + response.statusMessage());
  }
});

// 与上面类似，设置一个请求发送完成的handler并结束请求
request
  .response(ar2 -> {
    if (ar2.succeeded()) {

      HttpClientResponse response = ar2.result();

      // 状态代码,如:200、404
      System.out.println("Status code is " + response.statusCode());

      // 状态消息,如:OK、Not Found
      System.out.println("Status message is " + response.statusMessage());
    }
  })
  .end();
```

#### 使用流式响应

`HttpClientResponse` 实现了 `ReadStream` ， 这意味着您可以pipe数据到任何 `WriteStream` 实例。

#### 响应头和尾

HTTP 响应可包含头信息。您可以使用 `headers` 方法来读取响应头。

该方法返回的对象是一个 `MultiMap` 实例，因为 HTTP 响应头中单个键可以关联多个值。

```java
String contentType = response.headers().get("content-type");
String contentLength = response.headers().get("content-lengh");
```

分块 HTTP 响应还可以包含响应尾(trailer) —— 这实际上是在发送响应体的最后一个（数据）块。

您可使用 `trailers` 方法读取响应尾，尾数据也是一个 `MultiMap` 。

#### 读取请求体

当从报文中读取到响应头时，响应处理器就会被调用。

如果响应中包含响应体，那么响应体可能会在读取完header后，以多个分片的形式到达。 我们不会等待所有响应到达才调用响应处理器，因为响应可能会非常大， 我们可能会等待很长一段时间，或者因为巨大的响应体而耗尽内存。

当响应体的某部分（数据）到达时，`handler` 方法绑定的回调函数将会被调用， 其中传入的 `Buffer` 中包含了响应体的这一分片内容：

```java
client.request(HttpMethod.GET, "some-uri", ar1 -> {

  if (ar1.succeeded()) {
    HttpClientRequest request = ar1.result();
    request.send(ar2 -> {
      HttpClientResponse response = ar2.result();
      response.handler(buffer -> {
        System.out.println("Received a part of the response body: " + buffer);
      });
    });
  }
});
```

若您知道响应体不是很大，并想在处理之前在内存中聚合所有响应体数据， 那么您可以自行聚合：

```java
request.send(ar2 -> {

  if (ar2.succeeded()) {

    HttpClientResponse response = ar2.result();

    // 创建空的缓冲区
    Buffer totalBuffer = Buffer.buffer();

    response.handler(buffer -> {
      System.out.println("Received a part of the response body: " + buffer.length());

      totalBuffer.appendBuffer(buffer);
    });

    response.endHandler(v -> {
      // 现在所有的响应体都读取了
      System.out.println("Total response body length is " + totalBuffer.length());
    });
  }
});
```

或者当响应已被完全读取时，您可以使用 `body` 方法以便读取整个响应体：

```java
request.send(ar1 -> {

  if (ar1.succeeded()) {
    HttpClientResponse response = ar1.result();
    response.body(ar2 -> {

      if (ar2.succeeded()) {
        Buffer body = ar2.result();
        // 现在所有的响应体都读取了
        System.out.println("Total response body length is " + body.length());
      }
    });
  }
});
```

#### 响应完成处理器

当整个响应体被完全读取或者无响应体的响应头被完全读取时，响应的 `endHandler` 就会被调用。

#### 请求和响应组合使用

http客户端接口可以按下面的模式使用，非常简单：

1. 调用 `request` 打开连接
2. 调用 `send` 或 `write`/`end` 发送请求到服务器
3. 处理 `HttpClientResponse` 响应的开始
4. 处理响应事件

您可以使用Vert.x的Future组合的方式来简化代码，但是API是事件驱动的， 因此您需要充分了解它的工作过程，否则可能会遇到数据争夺 （即丢失事件导致数据损坏）的情况。

HttpClient 客户端有意地避免返回 `Future<HttpClientResponse>` ， 因为如果在 event-loop 之外设置 Future 的完成处理器可能会导致线程竞争。

```java
Future<HttpClientResponse> get = client.get("some-uri");

// 假设客户端返回的响应是Future
//（假设此事件 *不* 在event-loop中）
// 在这个例子里，会引入潜在的数据竞争
Thread.sleep(100);

get.onSuccess(response -> {

  // 响应事件此时可能已经发生
  response.body(ar -> {

  });
});
```

将 `HttpClientRequest` 的使用限制在一个verticle的范围内是最简单的解决方案， 因为Verticle为避免数据竞争，会确保按顺序处理事件。

```java
vertx.deployVerticle(() -> new AbstractVerticle() {
 @Override
 public void start() {

   HttpClient client = vertx.createHttpClient();

   Future<HttpClientRequest> future = client.request(HttpMethod.GET, "some-uri");
 }
}, new DeploymentOptions());
```

在verticle外使用HttpClient进行交互时，可以安全地使用“组合”(compose)， 只要不延迟响应事件即可。例如，直接在event-loop上处理响应。

```java
Future<JsonObject> future = client
  .request(HttpMethod.GET, "some-uri")
  .compose(request -> request
    .send()
    .compose(response -> {
      // Process the response on the event-loop which guarantees no races
      if (response.statusCode() == 200 &&
          response.getHeader(HttpHeaders.CONTENT_TYPE).equals("application/json")) {
        return response
          .body()
          .map(buffer -> buffer.toJsonObject());
      } else {
        return Future.failedFuture("Incorrect HTTP response");
      }
    }));

// Listen to the composed final json result
future.onSuccess(json -> {
  System.out.println("Received json result " + json);
}).onFailure(err -> {
  System.out.println("Something went wrong " + err.getMessage());
});
```

如果需要延迟响应处理，则需要 `pause` （暂停）响应或使用 `pipe`， 当涉及另一个异步操作时，这可能是必需的。

```java
Future<Void> future = client
  .request(HttpMethod.GET, "some-uri")
  .compose(request -> request
    .send()
    .compose(response -> {
      // 在event-loop上处理响应，从而确保没有数据竞争
      if (response.statusCode() == 200) {

        // 创建一个管道，会暂停响应
        Pipe<Buffer> pipe = response.pipe();

        // 把文件写入磁盘
        return fileSystem
          .open("/some/large/file", new OpenOptions().setWrite(true))
          .onFailure(err -> pipe.close())
          .compose(file -> pipe.to(file));
      } else {
        return Future.failedFuture("Incorrect HTTP response");
      }
    }));
```

#### 从响应中读取Cookie

您可以通过 `cookies` 方法从响应中获取 Cookie 列表。

或者您可以在响应中自己解析 `Set-Cookie` 头。

#### 30x 重定向处理器

客户端可配置成根据 `Location` 响应头遵循HTTP 重定向规则：

- GET或HEAD请求的HTTP响应码：`301`、`302`、`307` 或 `308`
- GET请求的HTTP响应码 `303`

这有个例子：

```java
client.request(HttpMethod.GET, "some-uri", ar1 -> {
  if (ar1.succeeded()) {

    HttpClientRequest request = ar1.result();
    request.setFollowRedirects(true);
    request.send(ar2 -> {
      if (ar2.succeeded()) {

        HttpClientResponse response = ar2.result();
        System.out.println("Received response with status code " + response.statusCode());
      }
    });
  }
});
```

默认情况最大的重定向数为 `16`，您可使用 `setMaxRedirects` 方法设置。

```java
HttpClient client = vertx.createHttpClient(
    new HttpClientOptions()
        .setMaxRedirects(32));

client.request(HttpMethod.GET, "some-uri", ar1 -> {
  if (ar1.succeeded()) {

    HttpClientRequest request = ar1.result();
    request.setFollowRedirects(true);
    request.send(ar2 -> {
      if (ar2.succeeded()) {

        HttpClientResponse response = ar2.result();
        System.out.println("Received response with status code " + response.statusCode());
      }
    });
  }
});
```

没有放之四海而皆准的策略，缺省的重定向策略可能不能满足您的需要。

默认重定向策略可使用自定义实现更改：

```java
client.redirectHandler(response -> {

  // 仅仅遵循301状态代码
  if (response.statusCode() == 301 && response.getHeader("Location") != null) {

    // 计算重定向URI
    String absoluteURI = resolveURI(response.request().absoluteURI(), response.getHeader("Location"));

    // 创建客户端将使用的新的可用请求
    return Future.succeededFuture(new RequestOptions().setAbsoluteURI(absoluteURI));
  }

  // （其他情况）不需要重定向
  return null;
});
```

这个策略将会处理接收到的原始 `HttpClientResponse` ，并返回 `null` 或 `Future<HttpClientRequest>` 。

- 当返回的是 `null` 时，处理原始响应
- 当返回的是 `Future` 时，请求将在它成功完成后发送
- 当返回的是 `Future` 时，请求失败时将调用设置的异常处理器

返回的请求必须是未发送的，这样原始请求处理器才会被发送而且客户端之后才能发送请求。

大多数原始请求设置将会传播（拷贝）到新请求中：

- 请求头，除非您已经设置了一些头
- 请求体，除非返回的请求使用了 `GET` 方法
- 响应处理器
- 请求异常处理器
- 请求超时



