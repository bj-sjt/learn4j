package com.itao.data.mybatis.bean;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user")
public class Employee extends BaseBean{


    private String name;
    private Integer age;

    public Employee(String id, String name, Integer age, boolean deleted, LocalDateTime createTime, LocalDateTime modifyTime){
        super(id, deleted, createTime, modifyTime);
        this.name = name;
        this.age = age;

    }
}
