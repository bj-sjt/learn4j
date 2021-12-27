package com.itao.vertx.auth;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientSession;

public class WebClientTest {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    WebClient webClient = WebClient.create(vertx);

    WebClientSession webClientSession = WebClientSession.create(webClient);

    MultiMap multiMap = MultiMap.caseInsensitiveMultiMap();
    multiMap.add("username", "sjt");
    multiMap.add("password", "sausages");
    webClientSession.post(8080, "localhost", "/login")
      .sendForm(multiMap, ar -> {
        if (ar.succeeded()) {
          HttpResponse<Buffer> result = ar.result();
          System.out.println(result.bodyAsString());
          webClientSession.get(8080, "localhost", "/private/user")
            .send(ar1 -> {
              if (ar1.succeeded()) {
                System.out.println(ar1.result().bodyAsString());
              } else {
                System.out.println(ar1.cause().getMessage());
              }
            });

        } else {
          System.out.println(ar.cause().getMessage());
        }
      });
  }
}
