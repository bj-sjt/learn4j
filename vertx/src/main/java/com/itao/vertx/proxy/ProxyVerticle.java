package com.itao.vertx.proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.HttpProxy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer proxyServer = vertx.createHttpServer();
        Router proxyRouter = Router.router(vertx);
        HttpClient proxyClient = vertx.createHttpClient();

        HttpProxy httpProxy = HttpProxy.reverseProxy(proxyClient);
        httpProxy.origin(8080, "localhost");

        proxyRouter
                .route().handler(ProxyHandler.create(httpProxy));
        proxyServer.requestHandler(proxyRouter);

        proxyServer.listen(9090, ar -> {
            if (ar.succeeded()) {
                log.info("Proxy sever is start in port [{}]", 9090);
            } else {
                log.info("Proxy sever start fail: {}", ar.cause().getMessage());
            }
        });
        startPromise.complete();
    }
}
