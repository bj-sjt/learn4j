package com.itao.data.reids.controller;


import com.itao.data.reids.bean.User;
import com.itao.data.reids.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@AllArgsConstructor
public class RedisController {

    private UserService userService;

    @GetMapping("/user/id")
    public User getUser() {
        return userService.getUser(1);
    }
}
