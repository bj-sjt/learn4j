package com.itao.hutool;

import cn.hutool.core.annotation.AnnotationUtil;

public class IOTest {
    public static void main(String[] args) {
        Token token = AnnotationUtil.getAnnotation(AnnotationTest.class, Token.class);
        Cookies cookies = AnnotationUtil.getAnnotation(AnnotationTest.class, Cookies.class);
        System.out.println(token);
        System.out.println(cookies);
    }

}
