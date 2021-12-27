package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleWebClientVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var webClient = WebClient.create(vertx);
    webClient.get("www.baidu.com","/")
      .send(ar -> {
        if (ar.succeeded()){
          var httpResponse = ar.result();
          log.info("{}", httpResponse.statusCode());
        }else {
          startPromise.fail(ar.cause());
        }
      });
    startPromise.complete();
  }
}
