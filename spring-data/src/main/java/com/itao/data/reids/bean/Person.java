package com.itao.data.reids.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person implements Serializable {
    /**
     * name : sjt
     * age : 18
     * gender : 1
     */

    private String name;
    private Integer age;
    private Integer gender;
}
