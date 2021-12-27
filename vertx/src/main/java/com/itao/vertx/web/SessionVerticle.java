package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class SessionVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    var sessionStore = LocalSessionStore.create(vertx, "itao.session");
    var sessionHandler = SessionHandler.create(sessionStore);
    router.route().handler(sessionHandler);
    router.get("/session").handler(ctx -> {
      var session = ctx.session();
      var name = session.<String>get("name");
      if(name != null){
        log.info(name);
        session.put("name", UUID.randomUUID().toString());
      } else {
        session.put("name", "noValue");
      }
      ctx.response().end("session");
    });
    httpServer.requestHandler(router).listen(8080);
  }
}
