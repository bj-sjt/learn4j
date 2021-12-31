# Vert.x Config

您可以使用 Vert.x Config 配置 Vert.x 应用。 它：

- 提供多种配置语法（JSON，properties，Yaml（extension）， Hocon（extension）……）
- 提供多种配置中心，例如文件、目录、HTTP、git（extension）、Redis（extension）、 系统参数和环境变量。
- 让您定义执行顺序以及覆盖规则
- 支持运行时重新定义

## 概念

该库主要围绕：

**Config Retriever** 的实例化，并用于 Vert.x 应用。 它配置了一系列配置中心（Configuration store） **Configuration store** 定义了配置信息读取路径以及格式（默认JSON）

配置以 JSON 对象格式被接收。

## 使用 Config Retriever

要使用 Config Retriever， 则需要添加如下依赖：

- Maven:

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle:

```groovy
compile 'io.vertx:vertx-config:4.1.5'
```

以上工作完成之后，您第一件要做的事情就是实例化 `ConfigRetriever` ：

```java
ConfigRetriever retriever = ConfigRetriever.create(vertx);
```

默认情况下，Config Retriever 可以使用下列配置中心进行配置 （按下列顺序）：

- Vert.x verticle `config()`
- 系统参数
- 环境变量
- `conf/config.json` 文件。这个路径可以用 `vertx-config-path` 系统参数或者 `VERTX_CONFIG_PATH` 环境变量所覆盖。

您可以指定您自己的配置中心：

```java
ConfigStoreOptions httpStore = new ConfigStoreOptions()
  .setType("http")
  .setConfig(new JsonObject()
    .put("host", "localhost").put("port", 8080).put("path", "/conf"));

ConfigStoreOptions fileStore = new ConfigStoreOptions()
  .setType("file")
  .setConfig(new JsonObject().put("path", "my-config.json"));

ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");


ConfigRetrieverOptions options = new ConfigRetrieverOptions()
  .addStore(httpStore).addStore(fileStore).addStore(sysPropsStore);

ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
```

如下可见更多关于覆盖规则和可用配置中心的细节信息。每一种配置中心都可以标记为 `可选的（optional）` 。 如果正在从一个可选的配置中心中获取（或处理）配置的时候，捕获到一个失败事件，那么这个失败事件会被日志所记录，但是执行过程并没有失败。 相反会返回一个空的 Json 对象（即：`{}`）。 想要将一个配置中心标记为可选的，那么就使用 `optional` 属性：

```java
ConfigStoreOptions fileStore = new ConfigStoreOptions()
  .setType("file")
  .setOptional(true)
  .setConfig(new JsonObject().put("path", "my-config.json"));
ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore).addStore(sysPropsStore);

ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
```

获取 Config Retriever 示例后，可按如下所示 *获取* 配置：

```java
retriever.getConfig(ar -> {
  if (ar.failed()) {
    // 获取配置失败
  } else {
    JsonObject config = ar.result();
  }
});
```

## 覆盖规则

配置中心的声明顺序非常重要， 因为它定义了覆盖顺序。对于冲突的key， *后* 声明的配置中心会覆盖之前的。

## 使用配置

获取到的配置可用于：

- 配置verticles，
- 配置端口，客户端，location等等，
- 配置Vert.x自身

### 配置一个单独的Verticle

以下示例代码可以放到 verticle 的 `start` 方法中。它获取了配置 （用默认配置中心），并且利用该配置创建了一个HTTP服务。

```java
ConfigRetriever retriever = ConfigRetriever.create(vertx);
retriever.getConfig(json -> {
  JsonObject result = json.result();

  vertx.createHttpServer()
    .requestHandler(req -> result.getString("message"))
    .listen(result.getInteger("port"));
});
```

### 配置一组Verticles

以下示例用 `verticles.json` 文件中的配置创建了2个verticle

```java
ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .addStore(new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "verticles.json"))));

retriever.getConfig(json -> {
  JsonObject a = json.result().getJsonObject("a");
  JsonObject b = json.result().getJsonObject("b");
  vertx.deployVerticle(GreetingVerticle.class.getName(), new DeploymentOptions().setConfig(a));
  vertx.deployVerticle(GreetingVerticle.class.getName(), new DeploymentOptions().setConfig(b));
});
```

### 配置Vert.x自身

您也可以直接配置Vert.x自身。您需要一个临时Vert.x对象用来获取配置。 然后创建实际Vert.x实例：

```java
Vertx vertx = Vertx.vertx();
// 创建 config retriever
ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .addStore(new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "vertx.json"))));

// 获取配置
retriever.getConfig(json -> {
  JsonObject result = json.result();
  // 关闭vertx对象，我们再也用不到它了
  vertx.close();

  // 用获取到的配置创建一个新的Vert.x实例
  VertxOptions options = new VertxOptions(result);
  Vertx newVertx = Vertx.vertx(options);

  // 部署您的Verticle
  newVertx.deployVerticle(GreetingVerticle.class.getName(), new DeploymentOptions().setConfig(result.getJsonObject("a")));
});
```

### 向事件总线传播配置变更

当配置变化时，Vert.x Config 会通知您。如果您需要对这个事件做出响应，您需要自己实现这个逻辑。 例如，您可以下线/重新部署verticle或者向事件总线发送新的配置。 下列实例展示了后者的场景。它向事件总线发送新的配置。 与事件关联的 verticle可以监听这个address并更新自身：

```java
ConfigRetriever retriever = ConfigRetriever.create(vertx, new ConfigRetrieverOptions()
  .addStore(new ConfigStoreOptions().setType("file").setConfig(new JsonObject().put("path", "verticles.json"))));

retriever.getConfig(json -> {
  //...
});

retriever.listen(change -> {
  JsonObject json = change.getNewConfiguration();
  vertx.eventBus().publish("new-configuration", json);
});
```

### 配置的结构

每一个声明的数据配置中心必须要指定 `类型（type）` ，它也定义了 `格式（format）` 。 如果没有设置，默认用JSON。

一些配置中心要求额外的配置（比如路径……）。 这项配置需要用 `setConfig` 方法传入一个JSON 对象来指定。

### 文件

此配置中心仅从文件中获得配置。 他支持所有的格式。

```java
ConfigStoreOptions file = new ConfigStoreOptions()
  .setType("file")
  .setFormat("properties")
  .setConfig(new JsonObject().put("path", "path-to-file.properties"));
```

`path` 配置项是必填项。

### JSON

JSON配置中心按原样提供给定的JSON配置

```java
ConfigStoreOptions json = new ConfigStoreOptions()
  .setType("json")
  .setConfig(new JsonObject().put("key", "value"));
```

这个配置中心仅仅支持JSON格式。

### 环境变量

这个配置中心将环境变量转换为用于全局配置的 JSON obejct

```java
ConfigStoreOptions env = new ConfigStoreOptions()
  .setType("env");
```

这个配置中心不支持 `format` 配置项。 获取到的值默认会被传输到JSON兼容的结构当中（数字，字符串，布尔，JSON 对象和JSON 数组）。 如果要避免这种转换，则需要配置 `raw-data` 属性：

```java
ConfigStoreOptions env = new ConfigStoreOptions()
  .setType("env")
  .setConfig(new JsonObject().put("raw-data", true));
```

您可以配置 `raw-data` 属性（默认为 `false` ）。如果 `raw-data` 为 `true` ， 则不会对值进行转换。用 `config.getString(key)` 方法，您会得到原始值。 当操作大整型数时，这很有用。

如果您想选择一系列key值来导入，那么用 `keys` 属性。他将未选择的key值都过滤掉了。 key必须独立列出：

```java
ConfigStoreOptions env = new ConfigStoreOptions()
  .setType("env")
  .setConfig(new JsonObject().put("keys", new JsonArray().add("SERVICE1_HOST").add("SERVICE2_HOST")));
```

### 系统参数

这个配置中心将系统参数转换为用于全局配置的 JSON 对象

```java
ConfigStoreOptions sys = new ConfigStoreOptions()
  .setType("sys")
  .setConfig(new JsonObject().put("cache", false));
```

这个配置中心不支持 `format` 配置项。

您可以配置 `cache` 属性（默认为 `true`） 来决定是否在第一次访问时缓存系统参数而后不再重新加载他们。

您也可以配置 `raw-data` 属性（默认为 `false` ）。如果 `raw-data` 为 `true` ， 则不会对值进行转换。用 `config.getString(key)` 方法，您会得到原始值。 当操作大整型数时，这很有用。

此外，亦存在有 `hierarchical` 属性（默认为 `false`）。如果 `hierarchical` 为 `true`， 则系统属性将被解析为嵌套的 JSON 对象，使用点分隔的属性名称作为 JSON 对象中的路径。

例子：

```java
ConfigStoreOptions sysHierarchical = new ConfigStoreOptions()
  .setType("sys")
  .setConfig(new JsonObject().put("hierarchical", true));
java -Dserver.host=localhost -Dserver.port=8080 -jar your-application.jar
```

这将会读取系统属性为 JSON 对象相当于

```json
{
 "server": {
   "host": "localhost",
   "port": 8080
 }
}
```

### HTTP

这个配置中心从HTTP地址获取配置。 可以用任何支持的格式。

```java
ConfigStoreOptions http = new ConfigStoreOptions()
  .setType("http")
  .setConfig(new JsonObject()
    .put("host", "localhost")
    .put("port", 8080)
    .put("path", "/A"));
```

他创建了一个带有 `配置中心的配置` 的Vert.x HTTP客户端（见下一小段）。 为了简化配置，您也可以用 `host`, `port` 和 `path` 属性来分别配置他们。 您也可以用 `headers` 属性来配置可选的HTTP请求头， `timeout` 属性配置超时时间（默认3000毫秒）， `followRedirects` 属性来指定是否重定向（默认情况下为false）。

```java
ConfigStoreOptions http = new ConfigStoreOptions()
  .setType("http")
  .setConfig(new JsonObject()
    .put("host", "localhost")
    .put("port", 8080)
    .put("ssl", true)
    .put("path", "/A")
    .put("headers", new JsonObject().put("Accept", "application/json")));
```

### 事件总线

这个配置中心从事件总线获取配置。 此种配置中心可以让您在本地和分布式组件之间传输配置。

```java
ConfigStoreOptions eb = new ConfigStoreOptions()
  .setType("event-bus")
  .setConfig(new JsonObject()
    .put("address", "address-getting-the-conf")
  );
```

这个配置中心支持任何格式。

### 目录

这个配置中心和 `文件` 配置中心很相似， 但是它并不是去读单个文件，而是从一个目录中读取多个文件

这个配置中心必须要配置如下项：

- 一个 `path` - 读取文件的根目录
- 至少一个 `fileset` - 一个用于选择文件的对象
- 对于properties文件，您可以用 `raw-data` 属性来禁止类型转换。

每一个 `fileset` 都包含：

- 一个 `pattern` ：一个Ant风格的pattern用于选择文件。 这个pattern应用于相对当前工作目录的相对路径。
- 一个可选的 `format` ，它制定了文件的格式（每一个fileset可以用一个不同的format， 但是同一个fileset共用一个format）

```java
ConfigStoreOptions dir = new ConfigStoreOptions()
  .setType("directory")
  .setConfig(new JsonObject().put("path", "config")
    .put("filesets", new JsonArray()
      .add(new JsonObject().put("pattern", "dir/*json"))
      .add(new JsonObject().put("pattern", "dir/*.properties")
        .put("format", "properties"))
    ));

ConfigStoreOptions dirWithRawData = new ConfigStoreOptions()
  .setType("directory")
  .setConfig(new JsonObject().put("path", "config")
    .put("filesets", new JsonArray()
      .add(new JsonObject().put("pattern", "dir/*json"))
      .add(new JsonObject().put("pattern", "dir/*.properties")
        .put("format", "properties").put("raw-data", true))
    ));
```

### Properties 文件和原始数据

Vert.x Config可以读一个properties文件。当读取一个这样的文件，您可以传入 `raw-data` 参数来提醒Vert.x不要转换它的值。这在操作大整型数字时很有用。 这些值可以用 `config.getString(key)` 方法来获取。

```java
ConfigStoreOptions propertyWithRawData = new ConfigStoreOptions()
  .setFormat("properties")
  .setType("file")
  .setConfig(new JsonObject().put("path", "raw.properties").put("raw-data", true)
  );
```

一些properties配置可能本来就是多级的。 当读取到这样的文件，您可以设置 `hierarchical` 参数来提醒Vert.x，当操作这个多级属性时，则将配置转换到 Json 对象当中， 这与前述方法设置扁平结构形成了对比。

例如：

```properties
server.host=localhost
server.port=8080
multiple.values=1,2,3
```

获取值：

```java
ConfigStoreOptions propertyWithHierarchical = new ConfigStoreOptions()
  .setFormat("properties")
  .setType("file")
  .setConfig(new JsonObject().put("path", "hierarchical.properties").put("hierarchical", true)
  );
ConfigRetrieverOptions options = new ConfigRetrieverOptions()
  .addStore(propertyWithHierarchical);

ConfigRetriever configRetriever = ConfigRetriever.create(Vertx.vertx(), options);

configRetriever.configStream().handler(config -> {
  String host = config.getJsonObject("server").getString("host");
  Integer port = config.getJsonObject("server").getInteger("port");
  JsonArray multiple = config.getJsonObject("multiple").getJsonArray("values");
  for (int i = 0; i < multiple.size(); i++) {
    Integer value = multiple.getInteger(i);
  }
});
```

监听配置的变更

Configuration Retriever 定期获取配置信息， 如果获取到的结果与当前不同，那么您可以重新配置应用。 默认情况下配置每5秒重新加载一次。

```java
ConfigRetrieverOptions options = new ConfigRetrieverOptions()
  .setScanPeriod(2000)
  .addStore(store1)
  .addStore(store2);

ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), options);
retriever.getConfig(json -> {
  // 初始化获取配置
});

retriever.listen(change -> {
  // 之前的配置
  JsonObject previous = change.getPreviousConfiguration();
  // 新配置
  JsonObject conf = change.getNewConfiguration();
});
```

## 获取上一次的配置

您可以获取上一次获取到的配置而不用 "等到" 被获取的时候。 这需要用如下方式实现：

```java
JsonObject last = retriever.getCachedConfig();
```

## 以流的方式读取配置

`ConfigRetriever` 提供了一个访问配置流的方法。 这是一个 `JsonObject` 的 `ReadStream` 。通过注册正确的处理器集合，您 会收到通知：

- 当获取到一个新的配置
- 当获取配置时发生错误
- 当 configuration retriever 关闭（即 `endHandler` 被调用）

```java
ConfigRetrieverOptions options = new ConfigRetrieverOptions()
  .setScanPeriod(2000)
  .addStore(store1)
  .addStore(store2);

ConfigRetriever retriever = ConfigRetriever.create(Vertx.vertx(), options);
retriever.configStream()
  .endHandler(v -> {
    // retriever 关闭
  })
  .exceptionHandler(t -> {
    // 当获取配置时捕获到错误
  })
  .handler(conf -> {
    // 配置
  });
```

## 处理配置

您可以配置一个 *processor* ，它可以校验并更新配置。 可以通过调用 `setConfigurationProcessor` 方法来实现。

处理过程绝对不能返回 `null` 。处理器获取到配置然后返回处理过的配置。 如果处理器不更新配置，它必须将输入的配置返回。 处理器可以抛出异常（例如，校验失败）。

## 扩展 Config Retriever

通过实现如下方式，您可以扩展配置：

- `ConfigProcessor` SPI 来增加对新格式的支持
- `ConfigStoreFactory` SPI来增加配置中心的支持（获取配置数据的位置）

## 其他格式

尽管Vert.x对于开箱即用的格式都提供了支持，但是 Vert.x Config 还是提供了额外的格式以供您的应用使用。

### Yaml 配置格式

Yaml 配置格式扩展了 Vert.x Configuration Retriever 并提供了对 Yaml格式 的支持

#### 使用Yaml配置格式

要使用Yaml配置格式， 添加如下依赖：

- Maven:

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-yaml</artifactId>
 <version>4.1.5</version>
</dependency>
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle:

```groovy
compile 'io.vertx:vertx-config:4.2.0'
compile 'io.vertx:vertx-config-yaml:4.2.0'
```

#### 指定配置中心以使用yaml

将该库加入classpath或者项目依赖后，您需要配置 `ConfigRetriever` 来使用这个格式：

```java
ConfigStoreOptions store = new ConfigStoreOptions()
  .setType("file")
  .setFormat("yaml")
  .setConfig(new JsonObject()
    .put("path", "my-config.yaml")
  );

ConfigRetriever retriever = ConfigRetriever.create(vertx,
    new ConfigRetrieverOptions().addStore(store));
```

您必须将 `format` 设置为 `yaml` 。

## 其他配置中心

尽管Vert.x对于开箱即用的配置中心都提供了支持，但是 Vert.x Config 还是提供了额外的格式以供您的应用使用。

### Git配置中心

git配置中心是对 Vert.x Configuration Retriever 的扩展， 用于从Git仓库获取配置。

#### 使用git配置中心

要使用Git配置中心， 则添加如下依赖：

- Maven：

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-git</artifactId>
 <version>4.1.5</version>
</dependency>
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle：

```groovy
compile 'io.vertx:vertx-config:4.1.5'
compile 'io.vertx:vertx-config-git:4.1.5'
```

#### 设置配置中心

将该库加入classpath或者项目依赖后，您需要配置 `ConfigRetriever` 以使用该配置中心：

```java
ConfigStoreOptions git = new ConfigStoreOptions()
    .setType("git")
    .setConfig(new JsonObject()
        .put("url", "https://github.com/cescoffier/vertx-config-test.git")
        .put("path", "local")
        .put("filesets",
            new JsonArray().add(new JsonObject().put("pattern", "*.json"))));

ConfigRetriever retriever = ConfigRetriever.create(vertx,
    new ConfigRetrieverOptions().addStore(git));
```

配置需要如下信息：

- `url` ：git仓库的地址
- `path` ：仓库被clone的路径（本地目录）
- `user` ：仓库的git用户名（默认不需要认证）
- `password` ： git用户的密码
- `idRsaKeyPath` ：私有仓库所需的 ssh rsa 密钥 uri
- `fileset` ：指定要读取的文件集合 （与配置中心的目录配置意义相同）

您也可以配置要使用的分支（ `branch` 参数，默认为 `master` ）， 以及远程仓库名（ `remote` 参数，默认为 `origin` ）

#### 如何工作

如果本地的仓库目录（由 `path` 参数指定）不存在，那么配置中心会 clone 远程仓库到这个本地目录。 然后它会读取符合 `fileset` 配置的文件。

如果本地的仓库目录（由 `path` 参数指定）存在，它会尝试更新（按需切换分支）。 如果更新失败，则视为获取配置失败。

配置中心会周期性更新 git 仓库来检查配置是否更新。

### Redis配置中心

Reids配置中心扩展了 Vert.x Configuration Retriever 并提供了从Redis服务获取配置的方法。

### 使用Redis配置中心

要使用Redis配置中心， 添加如下依赖：

- Maven:

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config-redis</artifactId>
 <version>4.1.5</version>
</dependency>
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-config</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle:

```groovy
compile 'io.vertx:vertx-config:4.1.5'
compile 'io.vertx:vertx-config-redis:4.1.5'
```

## 设置配置中心

将该库加入classpath或者项目依赖后，您需要配置 `ConfigRetriever` 来使用这个配置中心：

```java
ConfigStoreOptions store = new ConfigStoreOptions()
    .setType("redis")
    .setConfig(new JsonObject()
        .put("host", "localhost")
        .put("port", 6379)
        .put("key", "my-configuration")
    );

ConfigRetriever retriever = ConfigRetriever.create(vertx,
    new ConfigRetrieverOptions().addStore(store));
```

配置中心的配置用于创建 `Redis` 对象。 更多细节请查阅 Vert.x Redis Client 文档

另外，您可以设置 `key` 属性来指示配置中心中的某一 *field* ， 默认为 `configuration` 属性。

Redis客户端使用 `HGETALL` 配置项来获取配置。