package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class SubrouterVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var mianRouter = Router.router(vertx);
    var subRouter = Router.router(vertx);

    subRouter
      .get("/:id")
      .handler(ctx -> ctx.response().end(ctx.pathParam("id")));
    subRouter
      .get("/path")
      .handler(ctx -> ctx.response().end("path"));
    mianRouter.mountSubRouter("/user",subRouter);
    httpServer.requestHandler(mianRouter).listen(8080);
  }
}
