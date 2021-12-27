package com.itao.vertx.core;

import com.itao.vertx.bean.User1;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShareData2Verticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var sharedData = vertx.sharedData();
    var localMap = sharedData.<String, String>getLocalMap("map1");
    var localMap2 = sharedData.<String, User1>getLocalMap("map2");
    log.info(localMap.get("name"));
    log.info("{}", localMap2.get("user"));
    startPromise.complete();
  }
}
