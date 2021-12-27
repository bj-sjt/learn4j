package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouterNextVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    router
      .route("/path")
      .handler(ctx -> {
        ctx.response().setChunked(true); //如果不加这行则必须指定conten-length
        ctx.response().write("route1\n");
        ctx.next();
      });

    router
      .route("/path")
      .handler(ctx -> {
        ctx.response().write("route2\n");
        ctx.next();
      });

    router
      .route("/path")
      .handler(ctx -> {
        ctx.response().write("route3");
        ctx.response().end();
      });

    httpServer
      .requestHandler(router)
        .listen(8080,ar -> {
          if (ar.succeeded()) {
            log.info("server is start in port: {}", 8080);
          } else {
            startPromise.fail(ar.cause());
          }
        });
    startPromise.complete();
  }
}
