package com.itao.vertx.core;

import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class ContextVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Context context = vertx.getOrCreateContext();
    log.info("{}", context);
    context.runOnContext(v ->{
      log.info("{}", Thread.currentThread());
    });
    var httpServer = vertx.createHttpServer();
    httpServer.requestHandler(req -> {
      req.response().end("Hello vertx");
    });
    httpServer.listen(8080, ar ->{
      if (ar.succeeded()) {
        log.info("http server is start on {}", 8080);
      } else {
        startPromise.fail(ar.cause());
      }
    });
    super.start(startPromise);
  }
}
