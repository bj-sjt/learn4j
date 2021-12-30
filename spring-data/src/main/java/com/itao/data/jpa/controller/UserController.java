package com.itao.data.jpa.controller;

import com.itao.data.jpa.bean.Person;
import com.itao.data.jpa.bean.User;
import com.itao.data.jpa.repostiory.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserRepository userRepository;

    @GetMapping("/user")
    public User saveUser(@RequestBody User user){
        return userRepository.save(user);
    }

    @GetMapping("/httpGet")
    public String httpGet(@RequestParam String userName, @RequestParam String password){
        log.info("userName: {}, password: {}", userName, password);
        return "httpGet";
    }

    @PostMapping("/httpPost")
    public String httpPost(Person person){
        log.info("{}", person);
        return "httpPost";
    }

    @PostMapping("/httpPost1")
    public String httpPost1(@RequestBody Person person){
        log.info("{}", person);
        return "httpPost1";
    }

    @PostMapping("/httpPost2")
    public String httpPost1(MultipartFile file, Person person){
        log.info("file name: {}", file.getOriginalFilename());
        log.info("file name: {}", file.getName());
        log.info("{}", person);
        return "httpPost1";
    }

}
