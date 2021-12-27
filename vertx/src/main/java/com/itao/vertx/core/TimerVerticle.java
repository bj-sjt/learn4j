package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerVerticle extends AbstractVerticle {

  static final Logger log = LoggerFactory.getLogger(TimerVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    log.info("start");
    long timerId = vertx.setTimer(1000L, id -> {
      log.info("id:{}", id);
    });
    log.info("timerId:{}", timerId);
    vertx.cancelTimer(timerId);
    log.info("timer is cancel");
  }
}
