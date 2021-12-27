package com.itao.vertx.auth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.auth.sqlclient.SqlAuthorization;
import io.vertx.ext.auth.sqlclient.SqlAuthorizationOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClientSession;
import io.vertx.ext.web.client.spi.CookieStore;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.FormLoginHandler;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionAuthVerticle extends AbstractVerticle {
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
    SqlAuthentication sqlAuth = SqlAuthentication.create(sqlClient, authenticationOptions);

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route()
      .handler(SessionHandler
        .create(LocalSessionStore.create(vertx))
        // 设置cookies的过期时间（-1 表示关闭浏览器就过期）
        .setCookieMaxAge(30 * 60 * 1000));
    router.route().handler(BodyHandler.create());

    // 所有以'/private/'路径开头的请求都会被保护
    router
      .route("/private/*")
      .handler(RedirectAuthHandler.create(sqlAuth));

    // 处理实际的登录请求
    // 您其中一个页面必须要POST表单登陆数据
    router.post("/login").handler(FormLoginHandler.create(sqlAuth));

    router.get("/loginpage").handler(ctx -> {
      FileSystem fileSystem = vertx.fileSystem();
      fileSystem.readFile("login.html", ar -> {
        if (ar.succeeded()) {
          ctx.response().end(ar.result());
        } else {
          ctx.response().end("error");
        }
      });
    });

    router
      .route("/public")
      .handler(ctx -> {
        log.info("public");
        ctx.response().end("public");
      });

    router
      .route("/private/user")
      .handler(ctx -> {
        // 这里的值是true
        boolean isAuthenticated = ctx.user() != null;
        log.info("private: {}", isAuthenticated);
        ctx.response().end("private");
      });
    server.requestHandler(router).listen(8080, ar -> {
      log.info("server is start in port [{}]", 8080);
    });
  }
}
