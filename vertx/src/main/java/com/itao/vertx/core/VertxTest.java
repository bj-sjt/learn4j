package com.itao.vertx.core;

import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class VertxTest {

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        Future<String> f = vertx.executeBlocking(p -> {
            System.out.println("vertx..");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            p.complete("success");
        });
        while (!f.isComplete()) {
            System.out.println("waiting..");
            Thread.sleep(500);
        }
        System.out.println(f.result());
        vertx.close();
    }
}
