package com.itao.vertx.core.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var server = vertx.createHttpServer();
    server.requestHandler(request -> {
      MultiMap headers = request.headers();
      // 读取User-Agent
      System.out.println("User agent is " + headers.get("user-agent"));
      // 这样做可以得到和上边相同的结果
      System.out.println("User agent is " + headers.get("User-Agent"));
      //request.pipeTo(request.response());
      request.response().end("end");
    });

    server.listen(8080, "localhost",ar -> {
      if(ar.succeeded()){
        log.info("server is start in port: {}" ,8080);
      }else{
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
