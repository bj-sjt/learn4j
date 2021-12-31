# Vert.x Mail client (SMTP client implementation)

Vert.x邮件客户端通过本地邮件服务器或者外部邮件服务器发送邮件。

该客户端支持一些额外的验证方法，例如DIGEST-MD5。 该客户端完全支持TLS和SSL。该客户端是全异步的。 该客户端支持使用连接池复用连接。

要使用这个项目，请加入以下依赖：

- Maven：

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-mail-client</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle：

```groovy
compile 'io.vertx:vertx-mail-client:4.1.5'
```

## 创建客户端

您可在本地JVM上创建一个客户端来打开SMTP连接发送邮件。

该客户端使用配置对象，默认会创建空的配置对象， 这种情况下客户端将会连接到localhost的25端口。 在运行的邮件服务器的标准Linux环境下，本机的25端口是可用的。 有关配置对象的全部可选属性。

该客户端可以使用SMTP连接池来避免因TLS协商、 登陆产生的时间开销（这个功能可以通过设置keepAlive = false来关闭）。 客户端可以被设置成共享或非共享的， 如果设置为共享，则所有标识符相同的客户端都会使用同一个连接池。

```java
MailConfig config = new MailConfig();
MailClient mailClient = MailClient
  .createShared(vertx, config, "exampleclient");
```

MailClient.createShared的第一次调用将会实际创建一个指定配置的连接池。 后续调用将返回一个使用相同连接池的新客户端实例，因此后指定的配置将不会被使用。

如果省略连接池标识符，将会创建默认标识符的连接池。 注意，客户端仅共享在vertx实例作用域内（因此， 两个不同的vertx实例将会有相同标识名但实例对象不相同的连接池）。

创建非共享客户端的方法相同，不需要指定连接池标识符。

```java
MailConfig config = new MailConfig();
MailClient mailClient = MailClient.create(vertx, config);
```

通过TLS登陆的邮件服务器示例

```java
MailConfig config = new MailConfig();
config.setHostname("mail.example.com");
config.setPort(587);
config.setStarttls(StartTLSOptions.REQUIRED);
config.setUsername("user");
config.setPassword("password");
MailClient mailClient = MailClient.create(vertx, config);
```

## 发送邮件

一旦客户端创建好了，就可以用它来发送邮件。 因为在vert.x中邮件发送是异步的， 所以当邮件发送操作完成后才会调用结果处理程序。 多个邮件发送操作可以并发，连接池将会限制并发数， 如果没有可用连接那么新的操作将会在队列中等待。

邮件消息被构建成JSON。 MailMessage对象有 from、to、cc、bcc、subject、text、html等属性。 依据具体的值，生成的MIME消息将会有所不同。 收件人地址可以是单个地址或地址列表。

MIME编码器支持 us-ascii (7bit) headers/messages 和 utf8 (usually quoted-printable) headers/messages

```java
MailMessage message = new MailMessage();
message.setFrom("user@example.com (Example User)");
message.setTo("recipient@example.org");
message.setCc("Another User <another@example.net>");
message.setText("this is the plain message text");
message.setHtml("this is html text <a href=\"http://vertx.io\">vertx.io</a>");
```

MailAttachment对象可以使用数据存储缓冲区来创建附件， 支持base64附件。

```java
MailAttachment attachment = MailAttachment.create();
attachment.setContentType("text/plain");
attachment.setData(Buffer.buffer("attachment file"));

message.setAttachment(attachment);
```

当使用到内联附件（通常是图像）， html消息中可以引用邮件中包含的图像来展示html。 html文本中图像可以被引用为<img src="cid:contentid@domain">，相应的图像具有特征： 内联并且Message-ID头是"<contentid@domain>"。 请注意，RFC 2392需要Content-ID值的结构是Message-ID带有尖括号并且local和domain部分已经使用URL兼容编码编码过。 这些都不是强制的，大多数邮件客户端支持没有尖括号或者没有domain部分，最好是使用严格的格式标准。 正确的Content-ID值示例是"<[filename%201.jpg@example.org](mailto:filename 1.jpg@example.org)>"

```java
MailAttachment attachment = MailAttachment.create();
attachment.setContentType("image/jpeg");
attachment.setData(Buffer.buffer("image data"));
attachment.setDisposition("inline");
attachment.setContentId("<image1@example.com>");

message.setInlineAttachment(attachment);
```

发送邮件时，您可提供 AsyncResult<MailResult> 回调函数， 它会在发送操作完成或者失败时被调用。

按如下方式发送邮件：

```java
mailClient.sendMail(message)
  .onSuccess(System.out::println)
  .onFailure(Throwable::printStackTrace);
```

## Mail-client 数据对象

### MailMessage 属性

Email 字段是带真实名称或不带真实名称的通用电子邮件地址格式

- `username@example.com`
- `username@example.com (Firstname Lastname)`
- `Firstname Lastname <username@example.com>`

MailMessage对象有如下属性

- `from` String 类型，表示发件人地址和MAIL FROM字段
- `to` String 或 String 列表类型，表示单个收件人地址和RCPT TO字段
- `cc` 同to
- `bcc` 同to
- `bounceAddress` String类型，表示错误地址（MAIL FROM），如果未设置则使用from字段值
- `text` String类型，表示邮件的text/plain部分
- `html` String类型，表示邮件的text/html部分
- `attachment` 表示消息的单个或多个附件
- `inlineAttachment` 表示单个或多个内联附件（通常是图像）
- `headers` MultiMap类型，表示除MIME消息所必要的消息头之外需额外添加的消息头
- `fixedHeaders` boolean类型，如果值为true，则只有headers字段的值才会作为消息的消息头

最后两个属性允许操作自定义的消息头， 例如。提供调用程序选择的消息id或者设置不同于默认情况下生成的头。除非您知道自己在做什么， 否则可能会生成无效消息。

### 邮件附件属性

邮件附件对象由如下属性

- `data` 缓冲区，包含附件的二进制数据
- `stream` ReadStream，表示附件二进制数据的源
- `size` int类型，当 `stream` 作为二进制数据的源时，描述附件大小
- `contentType` String类型，表示附件的内容类型（例如：text/plain或text/plain;charset="UTF8"，默认为application/octet-stream）
- `description` String类型，表示附件的描述信息（放置在附件的描述头部分），可选
- `disposition` String类型，表示附件的部署类型（可以是“inline”或“attachment”，默认为attachment）
- `name` String类型，表示附件的文件名（它被放入附件的disposition和Content-Type头中），可选
- `contentId` String类型，表示附件的Content-Id（用于标识内联图像），可选
- `headers` MultiMap类型，除了默认头之外的附加头，可选

### MailConfig 选项

该配置具有如下属性

- `hostname` 连接smtp服务器的主机名（默认是localhost）
- `port` 连接smtp服务器的端口（默认是25）
- `startTLS` StartTLSOptions可选DISABLED，OPTIONAL或者REQUIRED，默认是OPTIONAL
- `login` LoginOption可选DISABLED，NONE或者REQUIRED，默认是NONE
- `username` 用于登陆的用户名（当LoginOption是REQUIRED时为必须）
- `password` 用于登陆的密码（当LoginOption是REQUIRED时为必须）
- `ssl` 连接邮件服务器是否使用ssl（默认是false），设置为true则使用465端口建立ssl连接（默认是false）
- `ehloHostname` 在EHLO中使用，用于创建message-id，如果未设置，将使用自己的主机名，如果主机名不是FQDN或者主机名是localhost则不推荐使用（可选的）
- `authMethods` 用空格分割的允许认证方法列表，可以用来禁止一些认证方法或定义一个必需的认证方法（可选的）
- `keepAlive` 是否启用连接池（默认是true）
- `maxPoolSize` 连接池中能保留的连接数的最大值（启用连接池）或者可以同时打开的最大连接数（不启用连接池），默认是10
- `trustAll` 是否接受服务端的所有证书（默认是false）
- `keyStore` 密钥文件的文件名，可用于信任自定义生成的服务器证书（可选的）
- `keyStorePassword` 用于解密密钥库（可选的）
- `allowRcptErrors` 如果为true，允许收件人列表中的地址发送撕逼爱，但至少有一个发送成功（默认是false）
- `disableEsmtp` 如果为true，ESMTP-related命令将不会被调用（如果您的smtp服务器甚至没有为EHLO命令提供正确的错误响应代码，则设置此选项）（默认是false）
- `userAgent` 表示邮件用户代理（MUA）名称用来生成multipart邮件和message-id的边界，默认是 `vertxmail`
- `enableDKIM` 如果为true，则在DKIM配置设置好了的情况下DKIM签名将会启用，默认是 `false`
- `dkimSignOptions` `DKIMSignOptions` 列表用于执行DKIM签名
- `pipelining` 如果邮件服务端允许，则开启管道。默认是 `true`
- `multiPartOnly` 布尔类型，指代该编码的邮件消息是否是仅由多部分（multipart）消息组成。默认为 `false`
- `poolCleanerPeriod` 整型，以毫秒为单位的连接池清理周期。默认值为 `1000 ms`。
- `poolCleanerPeriodUnit` 清理池的时间单位，默认为 `TimeUnit.MILLISECONDS`
- `keepAliveTimeout` 整型，SMTP 连接处于活跃状态（keep alive）的超时时间（以秒为单位）。默认值为`300 s`。
- `keepAliveTimeoutUnit`, 保证池中连接处于活跃状态（keep alive）的时间单位。默认为`TimeUnit.SECONDS`
- `ntDomain`, 字符串类型, 用于NTLM协议身份认证的域名，如果 `用户名` 格式为:`<DOMAIN>\<UserName>` 则使用 `\` 前的部分作为域名
- `workstation`, 字符串类型, 用于NTLM协议身份认证的工作站名称。

### MailResult对象

MailResult对象有下列成员变量

- `messageID` 这封邮件的Message-ID
- `recipients` 成功投递的收件人列表 （如果allowRcptErrors值为true，数量可能比预期收件人少）