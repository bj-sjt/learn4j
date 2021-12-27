package com.itao.vertx.mysql;

import com.itao.vertx.bean.User;
import com.itao.vertx.bean.UserParametersMapper;
import com.itao.vertx.bean.UserRowMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlTemplateVerticle extends AbstractVerticle {
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

    var u = new User();
    u.setId("1434790089251966980");
    SqlTemplate
      .forQuery(client, "select * from t_user where id = #{id}")
      .mapTo(UserRowMapper.INSTANCE)
      .mapFrom(UserParametersMapper.INSTANCE)
      .execute(u)
      .onSuccess(users -> {
        for (User user : users){
          log.info("{}", user);
        }
      })
      .onFailure(Throwable::printStackTrace);
  }
}
