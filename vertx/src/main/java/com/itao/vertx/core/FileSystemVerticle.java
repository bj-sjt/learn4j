package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

public class FileSystemVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var fs = vertx.fileSystem();
    fs.open("d:/1.txt", new OpenOptions(), ar -> {
      if (ar.succeeded()) {
        AsyncFile file = ar.result();
        file.write(Buffer.buffer("hello world"), 10);
      }
    });
  }
}
