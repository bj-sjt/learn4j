package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var fs = vertx.fileSystem();
    var router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router
      .post("/buffer")
      .handler(ctx -> {
        var body = ctx.getBody();
        log.info("{}", body);
        ctx.end("server buffer");
      });

    router
      .post("/stream")
      .handler(ctx -> {
        var body = ctx.getBody();
        log.info("{}", body);
        fs.createFile("D://favicon.ico")
          .onSuccess(v -> {
            fs.writeFile("D://favicon.ico", body);
          });
        ctx.end("server stream");
      });

    router
      .post("/form")
      .handler(ctx -> {
        var firstName = ctx.request().getFormAttribute("firstName");
        var lastName = ctx.request().getFormAttribute("lastName");
        log.info("firstName: {}, lastName: {}", firstName, lastName);
        ctx.end("server form");
      });

    router
      .get("/conf")
      .handler(ctx -> {
        var jsonObject = new JsonObject();
        jsonObject.put("name", "tom")
          .put("age", 18);
        ctx.response().end(jsonObject.toString());
      });

    router
      .post("/multipart/form")
      .handler(ctx -> {
        var imageDescription = ctx.request().getFormAttribute("imageDescription");
        log.info(imageDescription);
        var fileUploads = ctx.fileUploads();
        var fileUpload = fileUploads.toArray(new FileUpload[0])[0];
        log.info("name:{}", fileUpload.name());
        log.info("size:{}", fileUpload.size());
        log.info("uploadedFileName:{}", fileUpload.uploadedFileName());
        log.info("contentType:{}", fileUpload.contentType());
        ctx.end("server multipartform");
      });

    httpServer.requestHandler(router).listen(8080, ar -> {
      if (ar.succeeded()) {
        var server = ar.result();
        log.info("server is start in port: {}", server.actualPort());
      } else {
        startPromise.fail(ar.cause());
      }
    });
    startPromise.complete();
  }
}
