package com.itao.vertx.core;

import com.itao.vertx.codec.User;
import com.itao.vertx.codec.UserCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class EventBus2Verticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var eventBus = vertx.eventBus();
        eventBus.<User>consumer("itao.eventbus", mes -> {
            User body = mes.body();
            log.info("{}", body);
            mes.reply(new User("cat", 20), new DeliveryOptions().setCodecName("userCodec"));
        });
        startPromise.complete();
    }
}
