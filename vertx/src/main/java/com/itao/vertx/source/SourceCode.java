package com.itao.vertx.source;

import com.itao.vertx.core.http.HttpServerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceCode {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(HttpServerVerticle::new, new DeploymentOptions(), ar -> {
      if (ar.succeeded()) {
        log.info("HttpServerVerticle deploy success [id: {}]", ar.result());
      } else {
        log.info("HttpServerVerticle deploy fail: {}", ar.cause().getMessage());
      }
    });
  }
}
