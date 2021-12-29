package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RouterVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var httpServer = vertx.createHttpServer(new HttpServerOptions().setLogActivity(true));
        var router = Router.router(vertx);
        router.route().handler(ctx -> {
            var request = ctx.request();
            log.info("remote address: " + request.remoteAddress());
            // 当 route() 方法中无参数时会拦截所有路径，
            // 此时如果调用了 ctx.response().end() 方法
            // 则请求不会匹配其他路由, 应使用 ctx.next()
            ctx.response().end("Hello Vertx Web");
        }).failureHandler(ctx -> {
            log.error("全局异常捕获");
        });

        router.route("/a").handler(ctx -> {
            ctx.response().end("a");
        });

        router.route("/b").handler(ctx -> {
            ctx.response().end("b");
        });

        router.errorHandler(403, ctx -> {
            log.error("error 404");
        });

        httpServer.requestHandler(router).listen(8080, ar -> {
            if (ar.succeeded()) {
                log.info("server is start in port: {}", 8080);
            } else {
                startPromise.fail(ar.cause());
            }
        });
        startPromise.complete();
    }
}
