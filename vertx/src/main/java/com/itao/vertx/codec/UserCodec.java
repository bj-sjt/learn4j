package com.itao.vertx.codec;

import com.itao.vertx.bean.User;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class UserCodec implements MessageCodec<User, JsonObject> {
  @Override
  public void encodeToWire(Buffer buffer, User user) {

  }

  @Override
  public JsonObject decodeFromWire(int pos, Buffer buffer) {
    System.out.println(pos);
    System.out.println(buffer);
    return null;
  }

  @Override
  public JsonObject transform(User user) {
    return JsonObject.mapFrom(user);
  }

  @Override
  public String name() {
    return "userCodec";
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
