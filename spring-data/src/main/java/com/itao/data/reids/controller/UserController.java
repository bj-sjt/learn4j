package com.itao.data.reids.controller;


import com.itao.data.reids.bean.User;
import com.itao.data.reids.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/user")
    public User getUser() {
        return userService.getUser(1);
    }
}
