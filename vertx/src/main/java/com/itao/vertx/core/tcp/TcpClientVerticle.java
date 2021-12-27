package com.itao.vertx.core.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetClientOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TcpClientVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var options = new NetClientOptions();
    options.setLogActivity(true);
    var client = vertx.createNetClient(options);
    client.connect(8080,"localhost",ar -> {
      if (ar.succeeded()) {
        log.info("connect is success");
        var socket = ar.result();
        socket.write("i am client", v ->{
          if (v.succeeded()) {
            log.info("send is success");
          } else {
            startPromise.fail(v.cause());
          }
        });
        socket.handler(buffer -> {
          log.info("receive server message: {}", buffer.toString());
        });
      } else {
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
