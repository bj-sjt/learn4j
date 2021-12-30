package com.itao.vertx.redis;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedisVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    RedisOptions redisOptions = new RedisOptions();
    redisOptions.setEndpoints(Collections.singletonList("redis://localhost:6379"));
    Redis.createClient(vertx, "redis://localhost:6379")
      .connect(ar -> {
        if (ar.succeeded()) {
          var connection = ar.result();
          RedisAPI redis = RedisAPI.api(connection);
          //redis.get("name").onSuccess(System.out::println);
          List<String> command = new ArrayList<>();
          command.add("age");
          command.add("18");
          redis.set(command).onSuccess(System.out::println)
            .onFailure(Throwable::printStackTrace);
        }else {
          ar.cause().printStackTrace();
        }
      });
    startPromise.complete();
  }

}
