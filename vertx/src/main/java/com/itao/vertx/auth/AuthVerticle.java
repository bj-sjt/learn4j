package com.itao.vertx.auth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.VertxContextPRNG;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.auth.sqlclient.SqlAuthorization;
import io.vertx.ext.auth.sqlclient.SqlAuthorizationOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setPort(3306)
      .setHost("localhost")
      .setDatabase("auth")
      .setUser("root")
      .setPassword("root");

    // 连接池选项
    PoolOptions poolOptions = new PoolOptions()
      .setMaxSize(5);

    // 创建客户端池
    MySQLPool sqlClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

    SqlAuthenticationOptions authenticationOptions = new SqlAuthenticationOptions();
    // authenticationOptions.setAuthenticationQuery(sql) // 指定用户 查询的sql
    SqlAuthorizationOptions authorizationOptions = new SqlAuthorizationOptions();
    // authorizationOptions.setRolesQuery(sql)  // 指定角色 查询的sql
    // authorizationOptions.getPermissionsQuery(sql) // 指定权限 查询的sql
    SqlAuthentication sqlAuth = SqlAuthentication.create(sqlClient, authenticationOptions);
    SqlAuthorization sqlAuthZ = SqlAuthorization.create(sqlClient, authorizationOptions);

    // 对密码加密  id 可选{sha1, pbkdf2, sha256, sha512}
    String hash = sqlAuth.hash(
      "sha256", // hashing algorithm (OWASP recommended)
      VertxContextPRNG.current().nextString(32), // secure random salt
      "sausages" // password
    );

    JsonObject authInfo = new JsonObject()
      .put("username", "sjt")
      .put("password", "sausages");
    // 认证
    sqlAuth.authenticate(authInfo)
      .onSuccess(user -> {
        // 获取 user 的所有权限
        sqlAuthZ.getAuthorizations(user)
          .onSuccess(v -> {
            // 匹配是否有 write 权限
            if (PermissionBasedAuthorization.create("write").match(user)) {
              log.info("permission -> write");
            }
          });
      })
      .onFailure(System.out::println);

  }
}
