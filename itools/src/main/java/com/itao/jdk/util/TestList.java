package com.itao.jdk.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestList {
    public static void main(String[] args) {
        var list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(1, 3);
        new ArrayList<>(list);
        System.out.println(list);
    }
}
