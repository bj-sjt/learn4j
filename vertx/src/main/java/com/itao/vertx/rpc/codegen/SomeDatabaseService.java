package com.itao.vertx.rpc.codegen;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
public interface SomeDatabaseService {

  // 几个工厂方法用于创建实例和代理
  static SomeDatabaseService create(Vertx vertx) {
    return new SomeDatabaseServiceImpl(vertx);
  }

  static SomeDatabaseService createProxy(Vertx vertx, String address) {
    return new SomeDatabaseServiceVertxEBProxy(vertx, address);
  }

  // 此处是实际的服务操作……
  void save(String collection, JsonObject document);
}
