package com.itao.security.controller;

import com.itao.security.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
public class SessionController {

    @GetMapping("/session")
    @PreAuthorize("hasAnyAuthority('admin')")
    public User user(HttpServletRequest request){

        User user = new User("user", "123456");
        request.getSession().setAttribute("user",user);
        log.info("{}", user);
        return user;
    }
    @GetMapping("/getSession")
    @PreAuthorize("hasAnyRole('user')")
    public User getSession(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        log.info("{}", user);
        return user;
    }

    @GetMapping("/getUser")
    @Secured({"ROLE_user"})  //只能限定角色
    public org.springframework.security.core.userdetails.User getUser(){
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
    }

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
