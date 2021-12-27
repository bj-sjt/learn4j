package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.file.OpenOptions;

public class FileSystemVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var fs = vertx.fileSystem();
    fs.open("1.pdf", new OpenOptions());
  }
}
