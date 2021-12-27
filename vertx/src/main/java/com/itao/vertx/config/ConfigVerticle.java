package com.itao.vertx.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigStoreOptions propertiesStore = new ConfigStoreOptions()
      .setFormat("properties")
      .setType("file")
      .setConfig(new JsonObject().put("path", "conf/config.properties").put("hierarchical", true)
      );

    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(propertiesStore);

    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        log.info(ar.cause().getMessage());
      } else {
        JsonObject config = ar.result();
        var spring = config.getJsonObject("spring");
        var datasource = spring.getJsonObject("datasource");
        log.info("{}", datasource.encodePrettily());
      }
    });
  }
}
