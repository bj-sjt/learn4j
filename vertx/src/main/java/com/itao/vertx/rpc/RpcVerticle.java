package com.itao.vertx.rpc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class RpcVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new Rpc1Verticle(), ar -> {
      if (ar.succeeded()) {
        String result = ar.result();
        System.out.println(result);
      } else {
        startPromise.fail(ar.cause());
      }
    });

    vertx.deployVerticle(new Rpc2Verticle(), ar -> {
      if (ar.succeeded()) {
        String result = ar.result();
        System.out.println(result);
      } else {
        startPromise.fail(ar.cause());
      }
    });
  }
}
