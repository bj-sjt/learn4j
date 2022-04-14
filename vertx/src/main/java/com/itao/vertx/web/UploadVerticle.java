package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class UploadVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/get").handler(ctx -> {
            String name = ctx.getBodyAsString();
            log.info("name: {}", name);
            ctx.response().end(name);
        });
        /**
         * POST http://localhost:8080/upload
         * Content-Type: multipart/form-data; boundary=WebAppBoundary
         *
         * --WebAppBoundary
         * Content-Disposition: form-data; name="name"
         *
         * tom
         * --WebAppBoundary
         * Content-Disposition: form-data; name="file"; filename="1.txt"
         *
         * < d:\1.txt
         */
        router.post("/upload").handler(ctx -> {
            String name = ctx.request().getFormAttribute("name");
            log.info("name: {}", name);
            Set<FileUpload> uploads = ctx.fileUploads();
            uploads.forEach(fu -> {
                log.info(fu.fileName());
            });
            ctx.response().end("end");
        });

        httpServer.requestHandler(router).listen(8080, ar -> {
            if (ar.succeeded()) {
                log.info("server is started in port[{}]", 8080);
            } else {
                log.error("server start failed: {}", ar.failed());
            }
        });
        startPromise.complete();
    }
}
