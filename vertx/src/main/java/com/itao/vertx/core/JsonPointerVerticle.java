package com.itao.vertx.core;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.pointer.JsonPointer;

public class JsonPointerVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    JsonPointer pointer1 = JsonPointer.from("/hello/world");
    // Build a pointer manually
    JsonPointer pointer2 = JsonPointer.create()
      .append("hello")
      .append("world");

    var jsonArray = new JsonArray("hello");
    Object o = pointer1.queryJson(jsonArray);
    System.out.println(o);
  }
}
