package com.itao.vertx.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.ReadStream;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StreamVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var webClient = WebClient.create(vertx);
    var fs = vertx.fileSystem();
    fs.open("favicon.ico", new OpenOptions(), fileRes -> {
      if (fileRes.succeeded()) {
        var asyncFile = fileRes.result();
        asyncFile.size().onSuccess(size -> {
          // 用POST方法发送文件
          webClient
            .post(8080, "localhost", "/stream")
            .putHeader("content-length", String.valueOf(size))
            .sendStream(asyncFile)
            .onSuccess(res -> {
              log.info(res.bodyAsString());
            });
        });
      }
    });
  }
}
