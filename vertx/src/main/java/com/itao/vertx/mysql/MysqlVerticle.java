package com.itao.vertx.mysql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MysqlVerticle extends AbstractVerticle {
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
    client
      .getConnection()
      .compose(conn ->
        conn
          .query("select * from t_user where id='1434790089096777729'")
          .execute()
          .compose(rows -> {
            if (rows.size() == 1) {
              return conn.query("select * from t_user").execute();
            } else {
              return Future.failedFuture("查询结果为null");
            }
          })
          .onComplete(ar -> conn.close())
      )
      .onSuccess(rows -> {
        for(Row row : rows){
          var name = row.getString("name");
          var age = row.getInteger("age");
          log.info("name: {}, age: {}",name, age);
        }
      })
      .onFailure(Throwable::printStackTrace);
  }
}
