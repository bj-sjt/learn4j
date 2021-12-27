package com.itao.vertx.rpc;

import com.itao.vertx.rpc.codegen.SomeDatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;

public class Rpc2Verticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    SomeDatabaseService service = SomeDatabaseService.createProxy(vertx, "rpc.verticle");
    service.save("rpc.verticle", new JsonObject().put("rpc", "verticle"));
  }
}
