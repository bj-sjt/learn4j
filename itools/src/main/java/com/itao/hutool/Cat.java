package com.itao.hutool;

import cn.hutool.core.clone.Cloneable;
import lombok.Data;

@Data
public class Cat implements Cloneable<Cat> {
    private String name;

    @Override
    public Cat clone() {
        try {
            return (Cat)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Cat cat1 =new Cat();
        cat1.setName("cat1");
        Cat cat2 = cat1.clone();
        System.out.println(cat2==cat1);
    }
}
