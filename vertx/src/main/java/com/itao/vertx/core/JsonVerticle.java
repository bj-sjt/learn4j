package com.itao.vertx.core;

import com.itao.vertx.bean.User;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class JsonVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var jsonObject = new JsonObject();
    jsonObject.put("name","tomcat");
    jsonObject.put("age",18);
    var user = jsonObject.mapTo(User.class);
    System.out.println(user);
    var jsonArray = new JsonArray();
    jsonArray.add(user).add(jsonObject);
    JsonObject jsonObject1 = jsonArray.getJsonObject(1);
    System.out.println(jsonObject1.encodePrettily());
    Object value = jsonArray.getValue(0);
    System.out.println(value);
  }
}
