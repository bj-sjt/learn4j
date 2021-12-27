package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;

public class BufferVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var buffer = Buffer.buffer();
    buffer.appendInt(20);
    buffer.appendInt(40);
    buffer.appendInt(60);
    buffer.appendInt(80);
    var copy = buffer.copy();
    copy.setInt(1,30);
    System.out.println(buffer.getInt(Integer.BYTES)); // 40
    System.out.println(buffer.getByte(3)); //20   00000000 00000000 00000000 00010100
    startPromise.complete();
  }
}
