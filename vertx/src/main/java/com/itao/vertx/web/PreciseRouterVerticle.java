package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreciseRouterVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);

    router
      .route()
      .handler(ctx -> {
        ctx.response().setChunked(true);
        ctx.response().write("start\n");
        ctx.next();
      });

    router
      .route("/some/path/")
      .handler(ctx -> {
        ctx.response().end("end");
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
