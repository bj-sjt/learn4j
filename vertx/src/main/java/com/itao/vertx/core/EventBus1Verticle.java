package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus1Verticle extends AbstractVerticle {

  static final Logger log = LoggerFactory.getLogger(EventBus1Verticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(EventBus2Verticle.class.getName(), ar -> {
      if (ar.succeeded()) {
        log.info(ar.result());
      } else {
        log.error(ar.cause().getMessage());
        startPromise.fail(ar.cause());
      }
    });
    vertx.deployVerticle(EventBus3Verticle.class.getName(), ar -> {
      if (ar.succeeded()) {
        log.info(ar.result());
      } else {
        log.error(ar.cause().getMessage());
        startPromise.fail(ar.cause());
      }
    });
    var eventBus = vertx.eventBus();
    eventBus.publish("itao.eventbus", "send");
    eventBus.request("itao.eventbus", "reply", ar -> {
      if (ar.succeeded()) {
        log.info("{}", ar.result().body());
      } else {
        log.error(ar.cause().getMessage());
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
