package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var webClient = WebClient.create(vertx);
    MultiMap form = MultiMap.caseInsensitiveMultiMap();
    form.set("firstName", "Dale");
    form.set("lastName", "Cooper");
    webClient
      .post(8080,"localhost","/form")
      .sendForm(form)
      .onSuccess(response -> {
        log.info(response.bodyAsString());
      });
  }
}
