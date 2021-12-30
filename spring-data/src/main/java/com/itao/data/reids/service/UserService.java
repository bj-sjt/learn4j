package com.itao.data.reids.service;


import com.itao.data.reids.bean.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("all")
@CacheConfig(cacheNames = {"user"}, cacheManager = "userCacheManager")
public class UserService {

    @Cacheable(key = "#id")
    public User getUser(int id) {
        System.out.println("getUser...");
        return new User(1, "sjt", 18);
    }
}
