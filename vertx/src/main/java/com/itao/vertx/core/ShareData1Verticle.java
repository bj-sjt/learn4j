package com.itao.vertx.core;

import com.itao.vertx.bean.User1;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShareData1Verticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var sharedData = vertx.sharedData();
    var localMap = sharedData.<String,String>getLocalMap("map1");
    localMap.put("name","tomcat");

    var localMap2 = sharedData.<String,User1>getLocalMap("map2");
    localMap2.put("user",new User1("tom", 18));

    vertx.deployVerticle(ShareData2Verticle.class.getName(),ar -> {
      if (ar.succeeded()){
        log.info(ar.result());
      } else {
        startPromise.fail(ar.cause());
      }
    });

    startPromise.complete();
  }
}
