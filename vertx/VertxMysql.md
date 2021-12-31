# 响应式 MySQL 客户端

响应式 MySQL 客户端具有简单易懂的 API，专注于可扩展性和低开销。

**特性**

- 事件驱动
- 轻量级
- 内置连接池
- 预处理查询缓存
- 支持游标
- 流式行处理
- RxJava API
- 支持内存直接映射到对象，避免了不必要的复制
- 完整的数据类型支持
- 支持存储过程
- 支持 TLS/SSL
- MySQL 实用程序命令支持
- 支持 MySQL 和 MariaDB
- 丰富的字符排序（collation）和字符集支持
- Unix 域套接字

## 使用方法

使用响应式 MySQL 客户端，需要将以下依赖项添加到项目构建工具的 *依赖* 配置中：

- Maven:

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-mysql-client</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle:

```groovy
dependencies {
 compile 'io.vertx:vertx-mysql-client:4.1.5'
}
```

## 开始

以下是最简单的连接，查询和断开连接方法

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// 连接池选项
PoolOptions poolOptions = new PoolOptions()
  .setMaxSize(5);

// 创建客户端池
MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);

// 一个简单的查询
client
  .query("SELECT * FROM users WHERE id='julien'")
  .execute(ar -> {
  if (ar.succeeded()) {
    RowSet<Row> result = ar.result();
    System.out.println("Got " + result.size() + " rows ");
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }

  // 现在关闭客户端池
  client.close();
});
```

## 连接到 MySQL

大多数时间，您将使用连接池连接到 MySQL：

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// 连接池选项
PoolOptions poolOptions = new PoolOptions()
  .setMaxSize(5);

// 创建带连接池的客户端
MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);
```

当您不再需要连接池时，您需要释放它：

```java
client.close();
```

当您需要在同一连接上执行多个操作时，您需要使用 `connection` 客户端 。

您可以轻松地从连接池中获取一个：

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// 连接池选项
PoolOptions poolOptions = new PoolOptions()
  .setMaxSize(5);

// 创建带连接池的客户端
MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);

// 从连接池获得连接
client.getConnection().compose(conn -> {
  System.out.println("Got a connection from the pool");

  // 所有操作都在同一连接上执行
  return conn
    .query("SELECT * FROM users WHERE id='julien'")
    .execute()
    .compose(res -> conn
      .query("SELECT * FROM users WHERE id='emad'")
      .execute())
    .onComplete(ar -> {
      // 释放连接池的连接
      conn.close();
    });
}).onComplete(ar -> {
  if (ar.succeeded()) {

    System.out.println("Done");
  } else {
    System.out.println("Something went wrong " + ar.cause().getMessage());
  }
});
```

## 配置

有几个选项供您配置客户端。

### 数据对象

配置客户端的简单方法就是指定 `MySQLConnectOptions` 数据对象。

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions()
  .setPort(3306)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// 连接池选项
PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

// 从数据对象创建连接池
MySQLPool pool = MySQLPool.pool(vertx, connectOptions, poolOptions);

pool.getConnection(ar -> {
  // 处理您的连接
});
```

#### 字符序（collations）和字符集（character sets）

响应式 MySQL 客户端支持配置字符序或字符集，并将它们映射到一个相关的 `java.nio.charset.Charset` 。 您可以为数据库连接指定字符集，例如

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions();

// 将连接的字符集设置为utf8而不是默认的字符集utf8mb4
connectOptions.setCharset("utf8");
```

响应式 MySQL 客户端的默认字符集是 `utf8mb4` 。字符串值，如密码和错误消息等，总是使用 `UTF-8` 字符集解码。

`characterEncoding` 选项用于设置字符串（例如查询字符串和参数值）使用的 Java 字符集，默认使用 `UTF-8` 字符集；如果设置为 `null` ，则客户端将使用 Java 的默认字符集。

您还可以为连接指定字符序，例如

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions();

// 将连接的字符序设置为 utf8_general_ci 来代替默认字符序 utf8mb4_general_ci
// 设置字符序将覆盖charset选项
connectOptions.setCharset("gbk");
connectOptions.setCollation("utf8_general_ci");
```

请注意，在数据对象上设置字符序将覆盖 **charset** 和 **characterEncoding** 选项。

您可以执行 SQL `SHOW COLLATION;` 或 `SHOW CHARACTER SET;` 获取服务器支持的字符序和字符集。

#### 连接属性

还可以使用 `setProperties` 或 `addProperty` 方法配置连接属性。注意 `setProperties` 将覆盖客户端的默认属性。

```java
MySQLConnectOptions connectOptions = new MySQLConnectOptions();

// 添加连接属性
connectOptions.addProperty("_java_version", "1.8.0_212");

// 覆盖属性
Map<String, String> attributes = new HashMap<>();
attributes.put("_client_name", "myapp");
attributes.put("_client_version", "1.0.0");
connectOptions.setProperties(attributes);
```

#### 配置 `useAffectedRows`

您可以 `useAffectedRows` 选项以决定是否在连接到服务器时设置标志 `CLIENT_FOUND_ROWS`。如果指定了 `CLIENT_FOUND_ROWS` 标志，则受影响的行计数（返回的）是查找到的行数，而不是受影响的行数。

### 连接 URI

除了使用 `MySQLConnectOptions` 数据对象进行配置外，我们还为您提供了另外一种使用连接URI进行配置的方法：

```java
String connectionUri = "mysql://dbuser:secretpassword@database.server.com:3211/mydb";

// 从连接URI创建连接池
MySQLPool pool = MySQLPool.pool(connectionUri);

// 从连接URI创建连接
MySQLConnection.connect(vertx, connectionUri, res -> {
  // 处理您的连接
});
```

目前，客户端支持以下的连接 uri 参数关键字（不区分大小写）：

- host
- port
- user
- password
- schema
- socket
- useAffectedRows

## 连接重试

您可以将客户端配置为在连接无法建立时重试。

```java
options
  .setReconnectAttempts(2)
  .setReconnectInterval(1000);
```

## 运行查询

当您不需要事务或运行单个查询时，您可以直接在连接池上运行查询。连接池将使用其中一个连接来运行查询并将结果返回给您。

这是运行简单查询的方法：

```java
client
  .query("SELECT * FROM users WHERE id='julien'")
  .execute(ar -> {
  if (ar.succeeded()) {
    RowSet<Row> result = ar.result();
    System.out.println("Got " + result.size() + " rows ");
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }
});
```

### 预处理查询

您可以对预处理查询执行相同的操作。

SQL字符串可以使用数据库语法 `?` 按位置引用参数

```java
client
  .preparedQuery("SELECT * FROM users WHERE id=?")
  .execute(Tuple.of("julien"), ar -> {
  if (ar.succeeded()) {
    RowSet<Row> rows = ar.result();
    System.out.println("Got " + rows.size() + " rows ");
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }
});
```

查询方法提供异步 `RowSet` 实例，它适用于 *SELECT* 查询。

```java
client
  .preparedQuery("SELECT first_name, last_name FROM users")
  .execute(ar -> {
  if (ar.succeeded()) {
    RowSet<Row> rows = ar.result();
    for (Row row : rows) {
      System.out.println("User " + row.getString(0) + " " + row.getString(1));
    }
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }
});
```

或 *UPDATE*/*INSERT* 查询:

```java
client
  .preparedQuery("INSERT INTO users (first_name, last_name) VALUES (?, ?)")
  .execute(Tuple.of("Julien", "Viet"), ar -> {
  if (ar.succeeded()) {
    RowSet<Row> rows = ar.result();
    System.out.println(rows.rowCount());
  } else {
    System.out.println("Failure: " + ar.cause().getMessage());
  }
});
```

`Row` 使您可以按索引访问数据

```java
System.out.println("User " + row.getString(0) + " " + row.getString(1));
```

或按名字

```java
System.out.println("User " + row.getString("first_name") + " " + row.getString("last_name"));
```

客户端在这里不会使用任何魔术，并且无论您的SQL文本如何，列名都将用表中的名称标识。

您可以访问多样的类型

```java
String firstName = row.getString("first_name");
Boolean male = row.getBoolean("male");
Integer age = row.getInteger("age");
```

您可以使用缓存的预处理语句执行一次性的预处理查询：

```java
connectOptions.setCachePreparedStatements(true);
client
  .preparedQuery("SELECT * FROM users WHERE id = ?")
  .execute(Tuple.of("julien"), ar -> {
    if (ar.succeeded()) {
      RowSet<Row> rows = ar.result();
      System.out.println("Got " + rows.size() + " rows ");
    } else {
      System.out.println("Failure: " + ar.cause().getMessage());
    }
  });
```

您可以创建一个 `PreparedStatement` 并自己管理生命周期。

```java
sqlConnection
  .prepare("SELECT * FROM users WHERE id = ?", ar -> {
    if (ar.succeeded()) {
      PreparedStatement preparedStatement = ar.result();
      preparedStatement.query()
        .execute(Tuple.of("julien"), ar2 -> {
          if (ar2.succeeded()) {
            RowSet<Row> rows = ar2.result();
            System.out.println("Got " + rows.size() + " rows ");
            preparedStatement.close();
          } else {
            System.out.println("Failure: " + ar2.cause().getMessage());
          }
        });
    } else {
      System.out.println("Failure: " + ar.cause().getMessage());
    }
  });
```

### 批处理

您可以执行预处理的批处理

```java
List<Tuple> batch = new ArrayList<>();
batch.add(Tuple.of("julien", "Julien Viet"));
batch.add(Tuple.of("emad", "Emad Alblueshi"));

// 执行预处理的批处理
client
  .preparedQuery("INSERT INTO USERS (id, name) VALUES (?, ?)")
  .executeBatch(batch, res -> {
  if (res.succeeded()) {

    // 处理行
    RowSet<Row> rows = res.result();
  } else {
    System.out.println("Batch failed " + res.cause());
  }
});
```

## MySQL LAST_INSERT_ID

往表中插入一条记录后，可以获得自增值。

```java
client
  .query("INSERT INTO test(val) VALUES ('v1')")
  .execute(ar -> {
    if (ar.succeeded()) {
      RowSet<Row> rows = ar.result();
      long lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
      System.out.println("Last inserted id is: " + lastInsertId);
    } else {
      System.out.println("Failure: " + ar.cause().getMessage());
    }
  });
```

## 使用连接

### 获取连接

当需要执行顺序查询（无事务）时，可以创建一个新连接或从连接池中借用一个。 请注意在从拿到连接到将连接释放回连接池这之间的连接状态，服务端可能由于某些原因比如空闲时间超时，而关闭这条连接。

```java
pool
  .getConnection()
  .compose(connection ->
    connection
      .preparedQuery("INSERT INTO Users (first_name,last_name) VALUES (?, ?)")
      .executeBatch(Arrays.asList(
        Tuple.of("Julien", "Viet"),
        Tuple.of("Emad", "Alblueshi")
      ))
      .compose(res -> connection
        // 对行执行一些操作
        .query("SELECT COUNT(*) FROM Users")
        .execute()
        .map(rows -> rows.iterator().next().getInteger(0)))
      // 将连接返回到连接池中
      .eventually(v -> connection.close())
  ).onSuccess(count -> {
  System.out.println("Insert users, now the number of users is " + count);
});
```

可以创建预处理查询语句：

```java
connection
  .prepare("SELECT * FROM users WHERE first_name LIKE ?")
  .compose(pq ->
    pq.query()
      .execute(Tuple.of("Julien"))
      .eventually(v -> pq.close())
  ).onSuccess(rows -> {
  // 所有的行
});
```

### 简单连接 API

当您创建了一个连接池, 您可以调用 `withConnection` 并传入一个使用连接进行处理的函数。

它从连接池中借用一个连接，并使用该连接调用函数。

该函数必须返回一个任意结果的 Future。

Future 完成后, 连接将归还至连接池，并提供全部的结果。

```java
pool.withConnection(connection ->
  connection
    .preparedQuery("INSERT INTO Users (first_name,last_name) VALUES (?, ?)")
    .executeBatch(Arrays.asList(
      Tuple.of("Julien", "Viet"),
      Tuple.of("Emad", "Alblueshi")
    ))
    .compose(res -> connection
      // Do something with rows
      .query("SELECT COUNT(*) FROM Users")
      .execute()
      .map(rows -> rows.iterator().next().getInteger(0)))
).onSuccess(count -> {
  System.out.println("Insert users, now the number of users is " + count);
});
```

## 使用事务

### 带事务的连接

您可以使用SQL `BEGIN`/`COMMIT`/`ROLLBACK` 执行事务，如果您必须这么做，就必须使用 `SqlConnection` 自己管理事务。

或者您使用 `SqlConnection` 的事务API：

```java
pool.getConnection()
  // 事务必须使用一个连接
  .onSuccess(conn -> {
    // 开始事务
    conn.begin()
      .compose(tx -> conn
        // 各种语句
        .query("INSERT INTO Users (first_name,last_name) VALUES ('Julien','Viet')")
        .execute()
        .compose(res2 -> conn
          .query("INSERT INTO Users (first_name,last_name) VALUES ('Emad','Alblueshi')")
          .execute())
        // 提交事务
        .compose(res3 -> tx.commit()))
      // 将连接返回到连接池
      .eventually(v -> conn.close())
      .onSuccess(v -> System.out.println("Transaction succeeded"))
      .onFailure(err -> System.out.println("Transaction failed: " + err.getMessage()));
  });
```

当数据库服务器报告当前事务失败时（例如，臭名昭著的 *current transaction is aborted, commands ignored until end of transaction block*）， 事务被回滚，此时 `completion` Future 会失败， 并返回 `TransactionRollbackException` 异常：

```java
tx.completion()
  .onFailure(err -> {
    System.out.println("Transaction failed => rolled back");
  });
```

### 简单事务 API

当您创建了一个连接池, 您可以调用 `withTransaction` 并传入一个使用连接进行处理的函数。

它从连接池中借用一个连接，开始事务，并且，在此事务范围内所有执行操作的客户端调用该函数。

该函数必须返回一个任意结果的Future。

- 当Future成功，客户端提交这个事务
- 当Future失败，客户端回滚这个事务

事务完成后, 连接将返回到连接池中，并提供全部的结果。

```java
pool.withTransaction(client -> client
  .query("INSERT INTO Users (first_name,last_name) VALUES ('Julien','Viet')")
  .execute()
  .flatMap(res -> client
    .query("INSERT INTO Users (first_name,last_name) VALUES ('Emad','Alblueshi')")
    .execute()
    // 映射一个消息结果
    .map("Users inserted")))
  .onSuccess(v -> System.out.println("Transaction succeeded"))
  .onFailure(err -> System.out.println("Transaction failed: " + err.getMessage()));
```

## 游标和流

默认情况下，执行预处理查询将获取所有行，您可以使用 `Cursor` 控制想读取的行数：

```java
connection.prepare("SELECT * FROM users WHERE age > ?", ar1 -> {
  if (ar1.succeeded()) {
    PreparedStatement pq = ar1.result();

    // 创建游标
    Cursor cursor = pq.cursor(Tuple.of(18));

    // 读取50行
    cursor.read(50, ar2 -> {
      if (ar2.succeeded()) {
        RowSet<Row> rows = ar2.result();

        // 检查更多 ?
        if (cursor.hasMore()) {
          // 重复这个过程...
        } else {
          // 没有更多行-关闭游标
          cursor.close();
        }
      }
    });
  }
});
```

游标提前释放时应将其关闭：

```java
cursor.read(50, ar2 -> {
  if (ar2.succeeded()) {
    // 关闭游标
    cursor.close();
  }
});
```

游标还可以使用流式API，这可以更加方便，尤其是在Rx化的版本中。

```java
connection.prepare("SELECT * FROM users WHERE age > ?", ar1 -> {
  if (ar1.succeeded()) {
    PreparedStatement pq = ar1.result();

    // 一次获取50行
    RowStream<Row> stream = pq.createStream(50, Tuple.of(18));

    // 使用流
    stream.exceptionHandler(err -> {
      System.out.println("Error: " + err.getMessage());
    });
    stream.endHandler(v -> {
      System.out.println("End of stream");
    });
    stream.handler(row -> {
      System.out.println("User: " + row.getString("last_name"));
    });
  }
});
```

当这些行已传递给处理程序时，该流将批量读取 `50` 行并将其流化。 然后读取新一批的 `50` 行数据，依此类推。

流可以恢复或暂停，已加载的行将保留在内存中，直到被送达，游标将停止迭代。

## MySQL 类型映射

当前客户端支持以下 MySQL 类型

- BOOL,BOOLEAN (`java.lang.Byte`)
- TINYINT (`java.lang.Byte`)
- TINYINT UNSIGNED(`java.lang.Short`)
- SMALLINT (`java.lang.Short`)
- SMALLINT UNSIGNED(`java.lang.Integer`)
- MEDIUMINT (`java.lang.Integer`)
- MEDIUMINT UNSIGNED(`java.lang.Integer`)
- INT,INTEGER (`java.lang.Integer`)
- INTEGER UNSIGNED(`java.lang.Long`)
- BIGINT (`java.lang.Long`)
- BIGINT UNSIGNED(`io.vertx.sqlclient.data.Numeric`)
- FLOAT (`java.lang.Float`)
- FLOAT UNSIGNED(`java.lang.Float`)
- DOUBLE (`java.lang.Double`)
- DOUBLE UNSIGNED(`java.lang.Double`)
- BIT (`java.lang.Long`)
- NUMERIC (`io.vertx.sqlclient.data.Numeric`)
- NUMERIC UNSIGNED(`io.vertx.sqlclient.data.Numeric`)
- DATE (`java.time.LocalDate`)
- DATETIME (`java.time.LocalDateTime`)
- TIME (`java.time.Duration`)
- TIMESTAMP (`java.time.LocalDateTime`)
- YEAR (`java.lang.Short`)
- CHAR (`java.lang.String`)
- VARCHAR (`java.lang.String`)
- BINARY (`io.vertx.core.buffer.Buffer`)
- VARBINARY (`io.vertx.core.buffer.Buffer`)
- TINYBLOB (`io.vertx.core.buffer.Buffer`)
- TINYTEXT (`java.lang.String`)
- BLOB (`io.vertx.core.buffer.Buffer`)
- TEXT (`java.lang.String`)
- MEDIUMBLOB (`io.vertx.core.buffer.Buffer`)
- MEDIUMTEXT (`java.lang.String`)
- LONGBLOB (`io.vertx.core.buffer.Buffer`)
- LONGTEXT (`java.lang.String`)
- ENUM (`java.lang.String`)
- SET (`java.lang.String`)
- JSON (`io.vertx.core.json.JsonObject`, `io.vertx.core.json.JsonArray`, `Number`, `Boolean`, `String`, `io.vertx.sqlclient.Tuple#JSON_NULL`)
- GEOMETRY(`io.vertx.mysqlclient.data.spatial.*`)

元组解码在存储值时使用上述类型

请注意：在Java中，没有无符号数字值的具体表示形式，因此客户端会将无符号值转换为相关的Java类型。

### 隐式类型转换

当执行预处理语句时，响应式 MySQL 客户端支持隐式类型转换。 假设您的表中有一个 `TIME` 列，下面的两个示例都是有效的。

```java
client
  .preparedQuery("SELECT * FROM students WHERE updated_time = ?")
  .execute(Tuple.of(LocalTime.of(19, 10, 25)), ar -> {
  // 处理结果
});
// 这个也适用于隐式类型转换
client
  .preparedQuery("SELECT * FROM students WHERE updated_time = ?")
  .execute(Tuple.of("19:10:25"), ar -> {
  // 处理结果
});
```

MySQL 数据类型编码是根据参数值推断的。下面是具体的类型映射：

| 参数值                              | MySQL 类型编码      |
| ----------------------------------- | ------------------- |
| null                                | MYSQL_TYPE_NULL     |
| java.lang.Byte                      | MYSQL_TYPE_TINY     |
| java.lang.Boolean                   | MYSQL_TYPE_TINY     |
| java.lang.Short                     | MYSQL_TYPE_SHORT    |
| java.lang.Integer                   | MYSQL_TYPE_LONG     |
| java.lang.Long                      | MYSQL_TYPE_LONGLONG |
| java.lang.Double                    | MYSQL_TYPE_DOUBLE   |
| java.lang.Float                     | MYSQL_TYPE_FLOAT    |
| java.time.LocalDate                 | MYSQL_TYPE_DATE     |
| java.time.Duration                  | MYSQL_TYPE_TIME     |
| java.time.LocalTime                 | MYSQL_TYPE_TIME     |
| io.vertx.core.buffer.Buffer         | MYSQL_TYPE_BLOB     |
| java.time.LocalDateTime             | MYSQL_TYPE_DATETIME |
| io.vertx.mysqlclient.data.spatial.* | MYSQL_TYPE_BLOB     |
| default                             | MYSQL_TYPE_STRING   |

## 集合类查询

您可以将查询API与Java集合类结合使用：

```java
Collector<Row, ?, Map<Long, String>> collector = Collectors.toMap(
  row -> row.getLong("id"),
  row -> row.getString("last_name"));

// 运行查询使用集合类
client.query("SELECT * FROM users").collecting(collector).execute(ar -> {
    if (ar.succeeded()) {
      SqlResult<Map<Long, String>> result = ar.result();

      // 获取用集合类创建的map
      Map<Long, String> map = result.value();
      System.out.println("Got " + map);
    } else {
      System.out.println("Failure: " + ar.cause().getMessage());
    }
  });
```

集合类处理不能保留 `Row` 的引用，因为只有一个 Row 对象用于处理整个集合。

Java `Collectors` 提供了许多有趣的预定义集合类，例如， 您可以直接用 Row 中的集合轻松拼接成一个字符串：

```java
Collector<Row, ?, String> collector = Collectors.mapping(
  row -> row.getString("last_name"),
  Collectors.joining(",", "(", ")")
);

// 运行查询使用集合类
client.query("SELECT * FROM users").collecting(collector).execute(ar -> {
    if (ar.succeeded()) {
      SqlResult<String> result = ar.result();

      // 获取用集合类创建的String
      String list = result.value();
      System.out.println("Got " + list);
    } else {
      System.out.println("Failure: " + ar.cause().getMessage());
    }
  });
```

## MySQL 存储过程

您可以在查询中运行存储过程。结果将按照 [MySQL 协议](https://dev.mysql.com/doc/dev/mysql-server/8.0.12/page_protocol_command_phase_sp.html) 从服务器获取，无需任何魔法。

```java
client.query("CREATE PROCEDURE multi() BEGIN\n" +
  "  SELECT 1;\n" +
  "  SELECT 1;\n" +
  "  INSERT INTO ins VALUES (1);\n" +
  "  INSERT INTO ins VALUES (2);\n" +
  "END;").execute(ar1 -> {
  if (ar1.succeeded()) {
    // 创建存储过程成功
    client
      .query("CALL multi();")
      .execute(ar2 -> {
      if (ar2.succeeded()) {
        // 处理结果
        RowSet<Row> result1 = ar2.result();
        Row row1 = result1.iterator().next();
        System.out.println("First result: " + row1.getInteger(0));

        RowSet<Row> result2 = result1.next();
        Row row2 = result2.iterator().next();
        System.out.println("Second result: " + row2.getInteger(0));

        RowSet<Row> result3 = result2.next();
        System.out.println("Affected rows: " + result3.rowCount());
      } else {
        System.out.println("Failure: " + ar2.cause().getMessage());
      }
    });
  } else {
    System.out.println("Failure: " + ar1.cause().getMessage());
  }
});
```

Note: 目前尚不支持绑定OUT参数的预处理语句。

# SQL 客户端模版

SQL 客户端模版是一个用来方便执行SQL查询的库。

## 用法

要使用 SQL 客户端模版，需添加如下依赖：

- Maven：

```xml
<dependency>
 <groupId>io.vertx</groupId>
 <artifactId>vertx-sql-client-templates</artifactId>
 <version>4.1.5</version>
</dependency>
```

- Gradle：

```groovy
dependencies {
 implementation 'io.vertx:vertx-sql-client-templates:4.1.5'
}
```

## 开始

以下是 SQL 模版最简易的使用方式。

一个 SQL 模版接收 *已命名的* 参数，因此，默认情况下，它会接收一个map作为参数载体，而非接收元组（tuple）作为参数。

一个SQL 模版默认情况下生成一个类似 `PreparedQuery` 的 `RowSet<Row>` 。 实际上这个模版是 `PreparedQuery` 的轻量级封装。

```java
Map<String, Object> parameters = Collections.singletonMap("id", 1);

SqlTemplate
  .forQuery(client, "SELECT * FROM users WHERE id=#{id}")
  .execute(parameters)
  .onSuccess(users -> {
    users.forEach(row -> {
      System.out.println(row.getString("first_name") + " " + row.getString("last_name"));
    });
  });
```

当您需要执行一个插入或更新操作，而您并不关心执行结果，您可以用 `SqlTemplate.forUpdate` ：

```java
Map<String, Object> parameters = new HashMap<>();
parameters.put("id", 1);
parameters.put("firstName", "Dale");
parameters.put("lastName", "Cooper");

SqlTemplate
  .forUpdate(client, "INSERT INTO users VALUES (#{id},#{firstName},#{lastName})")
  .execute(parameters)
  .onSuccess(v -> {
    System.out.println("Successful update");
  });
```

## 模板语法

模板语法使用 `#{XXX}` 的语法，其中 `{XXX}` 是一个有效的 [java identifier](https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8) 字符串 （不受关键字约束）

您可以用反斜杠（`\`）来转义 `` 字符，例如 `\{foo}` 会被解析成 `#{foo}` 字符串，而不是名为 `foo` 的参数。

## 行映射

默认情况下模版以 `Row` 作为结果值类型。

您可以提供一个自定义的 `RowMapper` 来实现底层的映射操作：

```java
RowMapper<User> ROW_USER_MAPPER = row -> {
  User user = new User();
  user.id = row.getInteger("id");
  user.firstName = row.getString("firstName");
  user.lastName = row.getString("lastName");
  return user;
};
```

实现底层映射操作：

```java
SqlTemplate
  .forQuery(client, "SELECT * FROM users WHERE id=#{id}")
  .mapTo(ROW_USER_MAPPER)
  .execute(Collections.singletonMap("id", 1))
  .onSuccess(users -> {
    users.forEach(user -> {
      System.out.println(user.firstName + " " + user.lastName);
    });
  });
```

## JSON行映射

JSON 行映射是一个简单的模板映射，它用 `toJson` 将数据行映射成JSON对象。

```java
SqlTemplate
  .forQuery(client, "SELECT * FROM users WHERE id=#{id}")
  .mapTo(Row::toJson)
  .execute(Collections.singletonMap("id", 1))
  .onSuccess(users -> {
    users.forEach(user -> {
      System.out.println(user.encode());
    });
  });
```

## 参数映射

模板默认接收一个 `Map<String, Object>` 作为输入参数。

您可以提供一个自定义的映射（Mapper）：

```java
TupleMapper<User> PARAMETERS_USER_MAPPER = TupleMapper.mapper(user -> {
  Map<String, Object> parameters = new HashMap<>();
  parameters.put("id", user.id);
  parameters.put("firstName", user.firstName);
  parameters.put("lastName", user.lastName);
  return parameters;
});
```

实现参数映射：

```java
User user = new User();
user.id = 1;
user.firstName = "Dale";
user.firstName = "Cooper";

SqlTemplate
  .forUpdate(client, "INSERT INTO users VALUES (#{id},#{firstName},#{lastName})")
  .mapFrom(PARAMETERS_USER_MAPPER)
  .execute(user)
  .onSuccess(res -> {
    System.out.println("User inserted");
  });
```

## JSON 参数映射

（译者注：原文为 anemic json parameters mapping，即anemic mapping，指单纯的属性映射，无行为）

JSON 参数映射是一个在模板参数和JSON对象之间的简单映射：

```java
JsonObject user = new JsonObject();
user.put("id", 1);
user.put("firstName", "Dale");
user.put("lastName", "Cooper");

SqlTemplate
  .forUpdate(client, "INSERT INTO users VALUES (#{id},#{firstName},#{lastName})")
  .mapFrom(TupleMapper.jsonObject())
  .execute(user)
  .onSuccess(res -> {
    System.out.println("User inserted");
  });
```

## 用Jackson的数据绑定功能做映射

您可以用Jackson的数据绑定功能来实现映射。

您需要添加 jackson-databind 依赖：

- Maven:

```xml
<dependency>
 <groupId>com.fasterxml.jackson.core</groupId>
 <artifactId>jackson-databind</artifactId>
 <version>${jackson.version}</version>
</dependency>
```

- Gradle:

```groovy
dependencies {
 compile 'com.fasterxml.jackson.core:jackson-databind:${jackson.version}'
}
```

行映射是通过用键值对（key/value pair）来创建 `JsonObject` 实现的，然后 调用 `mapTo` 来将它映射为任何Java类。

```java
SqlTemplate
  .forQuery(client, "SELECT * FROM users WHERE id=#{id}")
  .mapTo(User.class)
  .execute(Collections.singletonMap("id", 1))
  .onSuccess(users -> {
    users.forEach(user -> {
      System.out.println(user.firstName + " " + user.lastName);
    });
  });
```

相似的，参数映射是用 `JsonObject.mapFrom` 将对象映射为 `JsonObject` 而实现的， 而后用 key/value pairs 来生成模板参数。

```java
User u = new User();
u.id = 1;

SqlTemplate
  .forUpdate(client, "INSERT INTO users VALUES (#{id},#{firstName},#{lastName})")
  .mapFrom(User.class)
  .execute(u)
  .onSuccess(res -> {
    System.out.println("User inserted");
  });
```

### Java Date/Time API 映射

您可以用 *jackson-modules-java8* 的Jackson扩展包来实现对 `java.time` 的映射。

您需要加入 Jackson JSR 310 datatype 依赖：

- Maven:

```xml
<dependency>
 <groupId>com.fasterxml.jackson.datatype</groupId>
 <artifactId>jackson-datatype-jsr310</artifactId>
 <version>${jackson.version}</version>
</dependency>
```

- Gradle :

```groovy
dependencies {
 compile 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jackson.version}'
}
```

然后您需要将时间模块注册到 Jackson的 `ObjectMapper` ：

```java
ObjectMapper mapper = io.vertx.core.json.jackson.DatabindCodec.mapper();

mapper.registerModule(new JavaTimeModule());
```

您可以用 `java.time` 包中的类型，例如 `LocalDateTime` ：

```java
public class LocalDateTimePojo {

 public LocalDateTime localDateTime;

}
```