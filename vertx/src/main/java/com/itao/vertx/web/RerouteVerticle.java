package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RerouteVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    log.info("{}", vertx);
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    router.get("/some/path/*").handler(ctx -> {

      ctx.put("foo", "bar");
      ctx.next();

    });

    router
      .get("/some/path/b")
      .handler(ctx -> {
        boolean reroute = ctx.get("reroute") != null && (boolean)ctx.get("reroute");
        if (reroute) {
          ctx.response().end("reroute");
        } else {
          ctx.response().end((String) ctx.get("foo"));
        }
      });

    router
      .get("/some/path")
      .handler(ctx -> {
        ctx.put("reroute", true);
        ctx.reroute("/some/path/b");
      });

    router.errorHandler(404, ctx -> ctx.end("not found"));
    httpServer.requestHandler(router).listen(8080);
  }


}
