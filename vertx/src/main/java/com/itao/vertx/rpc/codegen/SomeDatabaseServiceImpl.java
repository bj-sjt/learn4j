package com.itao.vertx.rpc.codegen;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class SomeDatabaseServiceImpl implements SomeDatabaseService{

  public SomeDatabaseServiceImpl(Vertx vertx) {
  }

  @Override
  public void save(String collection, JsonObject document) {
    System.out.println(document.encodePrettily());
    System.out.println(collection);
  }
}
