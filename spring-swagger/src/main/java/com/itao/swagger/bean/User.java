package com.itao.swagger.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("用户")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("用户地址")
    private String address;
    @ApiModelProperty("用户年龄")
    private int age;
}
