package com.itao.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CaffeineTest {
    public static void main(String[] args) {
        Cache<String, String> cache = Caffeine
                .newBuilder()
                .maximumSize(1)
                .removalListener((key, value, cause) -> {
                    System.out.println("remove" + key);
                })
                .build();
        cache.put("name", "tom");
        System.out.println(cache.getIfPresent("name"));
        cache.put("age", "10");
        System.out.println(cache.getIfPresent("age"));
        cache.put("address", "beijing");
        System.out.println(cache.getIfPresent("address"));
        System.out.println(cache.getIfPresent("name"));
    }
}
