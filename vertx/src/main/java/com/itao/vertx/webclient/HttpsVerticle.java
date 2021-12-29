package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class HttpsVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    var webClient = WebClient.create(vertx, new WebClientOptions()/*.setSsl(true)*/);
    webClient
      /*.get(80, "www.baidu.com", "/")
      .ssl(true)*/
      .getAbs("https://vertx-china.github.io/docs/vertx-web-client/java/")
      .putHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36")
      .send(ar -> {
        if (ar.succeeded()) {
          var result = ar.result();
          System.out.println(result.bodyAsString());
          //System.out.println(result.bodyAsString());
        } else {
          System.out.println(ar.cause().getMessage());
        }
      });
  }
}
