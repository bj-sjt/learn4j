package com.itao.hutool;

import cn.hutool.core.convert.Convert;

public class ConvertTest {
    public static void main(String[] args) {
        String hex = Convert.toHex("tao".getBytes());
        int number = Convert.chineseToNumber("一百");
        System.out.println(number);
        System.out.println(hex);
    }
}
