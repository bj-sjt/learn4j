package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;


public class TemplateVerticle extends AbstractVerticle {

  public void start(Promise<Void> startPromise) throws Exception {
    ThymeleafTemplateEngine engine = ThymeleafTemplateEngine.create(vertx);
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    SessionStore store = SessionStore.create(vertx);
    router.route().handler(SessionHandler.create(store));
    router.route("/static/*").handler(StaticHandler.create());
    router.route("/favicon.ico").handler(FaviconHandler.create(vertx));
    router.route("/template").handler(ctx -> {
      JsonObject jsonObject = new JsonObject()
        .put("name", "tom")
        .put("age", 18)
        .put("url", "https://www.baidu.com");
      engine.render(jsonObject, "index.html", ar -> {
        if (ar.succeeded()) {
          ctx.session().put("abc", jsonObject);
          var result = ar.result();
          ctx.response().end(result);
        }
      });
    });

    server.requestHandler(router).listen(8080, ar -> {
      if (ar.succeeded()) {
        System.out.println("server start");
      }
    });
  }
}
