package com.itao.vertx.core.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServerOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpServerVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var options = new NetServerOptions();
    options
      .setHost("localhost")
      .setPort(8080)
      .setLogActivity(true);
    var netServer = vertx.createNetServer(options);
    netServer.connectHandler(ns -> {
      log.info("lcoal address:{}",ns.localAddress());
      log.info("remote address:{}",ns.remoteAddress());
      ns.handler(buffer -> {
        log.info("receive client message: {}", buffer.toString());
        ns.write("i am server");
      });
    });

    netServer.listen(ar -> {
      if (ar.succeeded()) {
        log.info("server is start in port {}", 8080);
        var server = ar.result();
      }else {
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
