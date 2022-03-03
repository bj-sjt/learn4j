package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus3Verticle extends AbstractVerticle {

  static final Logger log = LoggerFactory.getLogger(EventBus3Verticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var eventBus = vertx.eventBus();
    eventBus.<String>consumer("itao.eventbus", mes -> {
      String body = mes.body();
      log.info(body);
      if("reply".equals(body)){
        mes.reply("received3");
      }
    });
    startPromise.complete();
  }
}
