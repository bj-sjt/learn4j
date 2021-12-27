package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BufferVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var webClient = WebClient.create(vertx);
    webClient
      .post(8080,"localhost","/buffer")
      .sendBuffer(Buffer.buffer("buffer"), ar -> {
        if (ar.succeeded()) {
          var response = ar.result();
          log.info(response.bodyAsString());
        }
      });
  }
}
