package com.itao.vertx.mysql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BatchVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("localhost")
      .setDatabase("shicgf")
      .setUser("root")
      .setPassword("root");

    // 连接池选项
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    // 创建客户端池
    MySQLPool client = MySQLPool.pool(vertx, connectOptions, poolOptions);
    // 执行预处理的批处理
    List<Tuple> batch = new ArrayList<>();
    batch.add(Tuple.of(3,"sjt",18));
    batch.add(Tuple.of(4,"sjt",18));
    client
      .preparedQuery("INSERT INTO t_user (id, name,age) VALUES (?, ? ,?)")
      .executeBatch(batch, res -> {
        if (res.succeeded()) {
          // 处理行
          RowSet<Row> rows = res.result();
          log.info("{}", rows.size());
          log.info("{}", rows.rowCount());
        } else {
          System.out.println("Batch failed " + res.cause());
        }
      });
  }
}
