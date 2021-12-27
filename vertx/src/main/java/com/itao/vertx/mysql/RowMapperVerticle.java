package com.itao.vertx.mysql;
import java.time.LocalDateTime;

import com.itao.vertx.bean.User;
import com.itao.vertx.bean.User1;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.templates.TupleMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RowMapperVerticle extends AbstractVerticle {
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
    RowMapper<User> ROW_USER_MAPPER = row -> {
      User user = new User();
      user.setId(row.getString("id"));
      user.setName(row.getString("name"));
      user.setAge(row.getInteger("age"));
      user.setDeleted(row.getBoolean("deleted"));
      user.setCreateTime(row.getLocalDateTime("create_time"));
      user.setModifyTime(row.getLocalDateTime("modify_time"));
      return user;
    };
    SqlTemplate
      .forQuery(client, "select * from t_user where id = #{id}")
      .mapTo(ROW_USER_MAPPER)
      .execute(Map.of("id","1434790089096777729"))
      .onSuccess(users -> {
        for (User user : users){
          log.info("{}", user);
        }
      })
      .onFailure(Throwable::printStackTrace);

    TupleMapper<User> PARAMETERS_USER_MAPPER = TupleMapper.mapper(user -> {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("id", user.getId());
      parameters.put("name", user.getName());
      parameters.put("age", user.getAge());
      parameters.put("deleted", user.isDeleted());
      parameters.put("createTime", user.getCreateTime());
      parameters.put("modifyTime", user.getModifyTime());
      return parameters;
    });
    User user = new User();
    user.setId("1");
    user.setName("sjt");
    user.setAge(0);
    user.setDeleted(false);
    user.setCreateTime(LocalDateTime.now());
    user.setModifyTime(LocalDateTime.now());

    client.withTransaction(conn -> SqlTemplate
      .forQuery(conn,
        "insert into t_user (id, name, age, deleted, create_time, modify_time) " +
          "values(#{id}, #{name}, #{age}, #{deleted}, #{createTime}, #{modifyTime})")
      .mapTo(ROW_USER_MAPPER)
      .mapFrom(PARAMETERS_USER_MAPPER)
      .execute(user)
      .onSuccess(users -> {
        for (User user1 : users){
          log.info("{}", user1);
        }
      })
      .onFailure(Throwable::printStackTrace));

  }
}
