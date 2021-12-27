package com.itao.vertx.rpc;

import com.itao.vertx.rpc.codegen.SomeDatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.serviceproxy.ServiceBinder;

public class Rpc1Verticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    SomeDatabaseService someDatabaseService = SomeDatabaseService.create(vertx);
    ServiceBinder binder = new ServiceBinder(vertx);
    binder.setAddress("rpc.verticle")
      .register(SomeDatabaseService.class, someDatabaseService);
  }
}
