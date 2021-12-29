package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleResopnseVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    /*router
      .route()
      .respond(
        ctx -> Future.succeededFuture(new JsonObject().put("name", "tom"))
      );*/
    router
      .get("/some/path")
       // 在这种情况下，处理器确保连接被终止
      .respond(ctx ->
              ctx.response()
                 .setChunked(true)
                 .write("Write some text..."));
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
