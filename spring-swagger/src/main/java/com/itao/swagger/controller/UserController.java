package com.itao.swagger.controller;

import com.itao.swagger.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理模块")
public class UserController {

    @ApiOperation(value = "根据id查询用户")
    @GetMapping("/{id}")
    public User getUserId(
            @ApiParam(name = "id",value = "用户id",required = true)
            @PathVariable String id){

        return new User("sjt", "北京", 18);
    }
}
