package com.itao.vertx.core;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextVerticle extends AbstractVerticle {

  static final Logger log = LoggerFactory.getLogger(ContextVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Context context = vertx.getOrCreateContext();
    log.info(context.deploymentID());
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
