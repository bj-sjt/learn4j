package com.itao.vertx.core.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var client = vertx.createHttpClient();
    client.request(HttpMethod.GET, 80, "www.baidu.com", "/", ar -> {
      if (ar.succeeded()) {
        var request = ar.result();
        request.send(ar1 -> {
          if (ar1.succeeded()) {
            var response = ar1.result();
            log.info("{}", response.statusCode());
            log.info(response.statusMessage());
          }
        });
      } else {
        log.info("error");
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
