package com.itao.vertx.cluster;


import com.itao.vertx.util.IpUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class ReceiverVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();

        MessageConsumer<JsonObject> consumer = eventBus.consumer("receiver");
        consumer.handler(message -> {
            String localIp = IpUtil.getLocalIp();;
            JsonObject jsonMessage = message.body();
            log.info("{}", jsonMessage.getValue("message_from_sender_verticle"));
            JsonObject jsonReply = new JsonObject().put("reply", localIp + " :: " + LocalDateTime.now());
            message.reply(jsonReply);
        });
    }
}
