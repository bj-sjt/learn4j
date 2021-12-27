package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeriodicVerticle extends AbstractVerticle {
  static final Logger log = LoggerFactory.getLogger(PeriodicVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    log.info("start");
    long periodicId = vertx.setPeriodic(1000L, id ->{
      log.info("id:{}",id);
    });
    log.info("periodicId:{}",periodicId);
    //vertx.cancelTimer(periodicId);

  }
}
