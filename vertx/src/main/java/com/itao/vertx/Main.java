package com.itao.vertx;

import com.itao.vertx.proxy1.ProxyVerticle;
import com.itao.vertx.proxy1.ServerVerticle;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ProxyVerticle(), ar -> {
      if (ar.succeeded()) {
        log.info(ar.result());
      } else {
        log.error(ar.cause().getMessage());
      }
    });
    vertx.deployVerticle(new ServerVerticle(), ar -> {
      if (ar.succeeded()) {
        log.info(ar.result());
      } else {
        log.error(ar.cause().getMessage());
      }
    });
  }
}
