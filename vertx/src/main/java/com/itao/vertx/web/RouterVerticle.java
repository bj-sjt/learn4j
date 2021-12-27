package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouterVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true));
    var router = Router.router(vertx);
    router
      .route()
      .handler(ctx -> {
        var request = ctx.request();
        log.info("remote address: " + request.remoteAddress());
        ctx.response().end("Hello Vertx Web");
      });

    router
      .route("/a")
      .handler(ctx -> {
        ctx.response().end("a");
      });

    router
      .route("/b")
      .handler(ctx -> {
        ctx.response().end("b");
      });

    httpServer
      .requestHandler(router)
      .listen(8080, ar -> {
        if (ar.succeeded()) {
          log.info("server is start in port: {}", 8080);
        } else {
          startPromise.fail(ar.cause());
        }
      });
    startPromise.complete();
  }
}
