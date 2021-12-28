package com.itao.vertx.proxy1;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/hello").handler(ctx -> {
            ctx.response().end("hello world");
        });
        router.post("/hello").handler(ctx -> {
            Buffer body = ctx.getBody();
            //ctx.response().putHeader("content-length",body.length() + "");
            ctx.response().end(body);
        });
        server.requestHandler(router).listen(8080, ar -> {
           if (ar.succeeded()) {
               log.info("server is start in port [{}]", 8080);
           } else {
               log.info("server start fail: {}", ar.cause().getMessage());
           }
        });
        startPromise.complete();
    }
}
