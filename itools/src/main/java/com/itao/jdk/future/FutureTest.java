package com.itao.jdk.future;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FutureTest {

    public static void main(String[] args) throws Exception {
        CompletableFuture.runAsync(() -> {
            System.out.println("runAsync..." + Thread.currentThread().getName());
        });
        CompletableFuture<Integer> future =
                CompletableFuture
                        .supplyAsync(() -> {
                            System.out.println("supplyAsync..." + Thread.currentThread().getName());
                            return 10;
                        }).thenApply(i -> {
                            System.out.println("thenApply..." + Thread.currentThread().getName());
                            return i + 10;
                        });
        System.out.println(future.get());
        System.in.read();
    }
}
