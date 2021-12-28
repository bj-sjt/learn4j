package com.itao.vertx.proxy2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProxyVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer proxyServer = vertx.createHttpServer();
        HttpClientOptions options = new HttpClientOptions().setDefaultHost("localhost").setDefaultPort(8080);
        HttpClient proxyClient = vertx.createHttpClient(options);

        proxyServer.requestHandler(request -> {
            HttpServerResponse response = request.response();
            //response.setChunked(true);
            request.pause();
            proxyClient.request(request.method(), request.uri(), ar -> {
                if (ar.succeeded()) {
                    HttpClientRequest req = ar.result();
                    request.resume();
                    req.send(request, ar2 -> {
                        if (ar2.succeeded()) {
                            HttpClientResponse resp = ar2.result();
                            response.setStatusCode(resp.statusCode());
                            resp.bodyHandler(response::end);
                        }
                    });
                } else {
                    log.error("{}", ar.cause().getMessage());
                }
            });
        });
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
