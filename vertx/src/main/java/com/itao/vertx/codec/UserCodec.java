package com.itao.vertx.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class UserCodec implements MessageCodec<User, User> {
    @Override
    public void encodeToWire(Buffer buffer, User user) {

    }

    @Override
    public User decodeFromWire(int pos, Buffer buffer) {
        return null;
    }

    @Override
    public User transform(User user) {
        return user;
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
