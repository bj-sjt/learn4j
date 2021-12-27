package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBus2Verticle extends AbstractVerticle {

  static final Logger log = LoggerFactory.getLogger(EventBus2Verticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var eventBus = vertx.eventBus();
    eventBus.consumer("itao.eventbus", mes -> {
      String body = (String) mes.body();
      log.info(body);
      if("reply".equals(body)){
        mes.reply("received2");
      }
    });
    startPromise.complete();
  }
}