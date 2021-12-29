package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubrouterVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var mianRouter = Router.router(vertx);
    var subUserRouter = Router.router(vertx);
    var subPersonRouter = Router.router(vertx);

    subUserRouter
            .get("/path")
            .handler(ctx -> {
              log.info("subUserRouter");
              ctx.response().end("subUserRouter");
            });

    subUserRouter
      .get("/:id")
      .handler(ctx -> ctx.response().end(ctx.pathParam("id")));

    subPersonRouter
            .get("/path")
            .handler(ctx -> {
              log.info("subUserRouter");
              ctx.next();
            });

    subPersonRouter
            .get("/:id")
            .handler(ctx -> ctx.response().end(ctx.pathParam("id")));

    mianRouter.mountSubRouter("/user",subUserRouter);
    mianRouter.mountSubRouter("/person",subPersonRouter);
    httpServer.requestHandler(mianRouter).listen(8080);
  }
}
