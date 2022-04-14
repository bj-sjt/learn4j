package com.itao.vertx.core;

import com.itao.vertx.codec.User;
import com.itao.vertx.codec.UserCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
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
    var eventBus = vertx.eventBus();
    eventBus.registerCodec(new UserCodec());
    eventBus.request("itao.eventbus", new User("tom", 18), new DeliveryOptions().setCodecName("userCodec"), ar -> {
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
