package com.itao.vertx.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.Router;

public class CookiesVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var httpServer = vertx.createHttpServer();
    var router = Router.router(vertx);
    router.get("/setCookies").handler(ctx -> {
      ctx.response().addCookie(Cookie.cookie("name","tom"));
      ctx.response().end("add cookies");
    });

    router.get("/getCookies").handler(ctx -> {
      Cookie cookie = ctx.request().getCookie("name");
      ctx.response().end("cookie:" + cookie.getValue());
    });
    httpServer.requestHandler(router).listen(8080);
  }
}
