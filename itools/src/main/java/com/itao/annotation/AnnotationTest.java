package com.itao.annotation;

public class AnnotationTest {
    public static void main(String[] args) {
        Class<A> aClass = A.class;
        System.out.println(aClass.isAssignableFrom(A.class));
        System.out.println(int.class.isPrimitive());
    }
}
