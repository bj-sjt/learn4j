package com.itao.vertx.cluster;


import com.itao.vertx.util.IpUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SenderVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        String localIp = IpUtil.getLocalIp();
        JsonObject msg = new JsonObject().put("message_from_sender_verticle", localIp + " :: " +"Hello, Consumer !");
        vertx.setPeriodic(10000, index -> {
            eventBus.request("receiver", msg, res -> {
                if (res.succeeded()) {
                    JsonObject reply = (JsonObject) res.result().body();
                    log.info("received reply: " + reply.getValue("reply"));
                }
            });
        });
    }
}
