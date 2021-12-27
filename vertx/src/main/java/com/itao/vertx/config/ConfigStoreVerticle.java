package com.itao.vertx.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigStoreVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    ConfigStoreOptions httpStore = new ConfigStoreOptions() //GET 请求
      .setType("http")
      //.setOptional(true)
      .setConfig(new JsonObject()
        .put("host", "localhost")
        .put("port", 8080)
        .put("path", "/conf"));

    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", "D://config.json"));

    ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys");

    ConfigStoreOptions propertiesStore = new ConfigStoreOptions()
      .setFormat("properties")
      .setType("file")
      .setConfig(new JsonObject().put("path", "config/config.properties").put("hierarchical", true)
      );

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
       .addStore(httpStore).addStore(fileStore).addStore(sysPropsStore).addStore(propertiesStore);

    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        log.info(ar.cause().getMessage());
      } else {
        JsonObject config = ar.result();
        log.info("{}", config.encodePrettily());
      }
    });
  }
}
